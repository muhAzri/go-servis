package com.zrifapps.goservice.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.zrifapps.goservice.ads.AdsManager
import com.zrifapps.goservice.feature.onboarding.domain.model.OnboardingStep
import com.zrifapps.goservice.feature.onboarding.presentation.AppGate
import com.zrifapps.goservice.feature.onboarding.presentation.AppGateViewModel
import com.zrifapps.goservice.feature.onboarding.presentation.OnboardingEvent
import com.zrifapps.goservice.feature.onboarding.presentation.OnboardingFlowViewModel
import com.zrifapps.goservice.feature.vehicle.domain.model.VehicleType
import com.zrifapps.goservice.ui.legal.AboutScreen
import com.zrifapps.goservice.ui.legal.HelpScreen
import com.zrifapps.goservice.ui.legal.PrivacyScreen
import com.zrifapps.goservice.ui.legal.TermsScreen
import com.zrifapps.goservice.ui.main.MainTabsScreen
import com.zrifapps.goservice.ui.onboarding.NameScreen
import com.zrifapps.goservice.ui.onboarding.NotifPermScreen
import com.zrifapps.goservice.ui.onboarding.OnboardingAddVehicleScreen
import com.zrifapps.goservice.ui.onboarding.OnboardingScreen
import com.zrifapps.goservice.ui.onboarding.PickVehicleTypeScreen
import com.zrifapps.goservice.ui.profile.EditProfileScreen
import com.zrifapps.goservice.ui.reminders.AddReminderScreen
import com.zrifapps.goservice.ui.reminders.EditReminderScreen
import com.zrifapps.goservice.ui.reminders.ReminderDetailScreen
import com.zrifapps.goservice.ui.service.AddServiceScreen
import com.zrifapps.goservice.ui.service.EditServiceScreen
import com.zrifapps.goservice.ui.service.InterstitialAdScreen
import com.zrifapps.goservice.ui.service.ServiceDetailScreen
import com.zrifapps.goservice.ui.service.ServiceSavedScreen
import com.zrifapps.goservice.ui.splash.SplashScreen
import com.zrifapps.goservice.ui.tips.TipsDetailScreen
import com.zrifapps.goservice.ui.tips.TipsScreen
import com.zrifapps.goservice.ui.vehicle.AddCustomComponentScreen
import com.zrifapps.goservice.ui.vehicle.AddVehicleScreen
import com.zrifapps.goservice.ui.vehicle.ComponentInfoScreen
import com.zrifapps.goservice.ui.vehicle.EditVehicleScreen
import com.zrifapps.goservice.ui.vehicle.TrackedComponentDetailScreen
import com.zrifapps.goservice.ui.vehicle.UpdateOdometerScreen
import com.zrifapps.goservice.ui.vehicle.VehicleComponentsScreen
import com.zrifapps.goservice.ui.vehicle.VehicleDetailScreen
import com.zrifapps.goservice.ui.vehicle.VehicleListScreen
import org.koin.androidx.compose.koinViewModel

@Composable
fun AppNavGraph() {
    val navController = rememberNavController()
    val appGateVm: AppGateViewModel = koinViewModel()
    val onboardingVm: OnboardingFlowViewModel = koinViewModel()
    val gate by appGateVm.gate.collectAsStateWithLifecycle()
    val onboardingState by onboardingVm.state.collectAsStateWithLifecycle()

    LaunchedEffect(onboardingVm) {
        onboardingVm.events.collect { event ->
            when (event) {
                is OnboardingEvent.GoTo -> when (event.step) {
                    OnboardingStep.Welcome -> Unit
                    OnboardingStep.ProfileName -> navController.navigate(Screen.Name)
                    OnboardingStep.PickVehicleType -> navController.navigate(Screen.PickVehicleType)
                    OnboardingStep.AddVehicle -> {
                        val type = onboardingVm.state.value.pickedVehicleType?.key ?: "motor"
                        navController.navigate(Screen.AddVehicle(type))
                    }
                    OnboardingStep.NotificationPermission -> navController.navigate(Screen.NotifPermission)
                    OnboardingStep.Done -> Unit
                }
                OnboardingEvent.CompletedFlow -> {
                    navController.navigate(Screen.Main) {
                        popUpTo<Screen.Splash> { inclusive = true }
                    }
                }
                is OnboardingEvent.Failed -> Unit
            }
        }
    }

    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = backStackEntry?.destination?.route
    LaunchedEffect(currentRoute) {
        AdsManager.setAppOpenAllowed(currentRoute == Screen.Main::class.qualifiedName)
    }

    // Tapped reminder notification -> open its detail once we're past the gate.
    val pendingDeepLink by ReminderDeepLinks.pending.collectAsStateWithLifecycle()
    LaunchedEffect(pendingDeepLink, gate) {
        val reminderId = pendingDeepLink ?: return@LaunchedEffect
        if (gate is AppGate.Main) {
            navController.navigate(Screen.ReminderDetail(reminderId))
            ReminderDeepLinks.consume()
        }
    }

    NavHost(
        navController = navController,
        startDestination = Screen.Splash,
    ) {
        composable<Screen.Splash> {
            SplashScreen(
                gate = gate,
                onResolved = { resolved ->
                    when (resolved) {
                        is AppGate.Main -> navController.navigate(Screen.Main) {
                            popUpTo<Screen.Splash> { inclusive = true }
                        }
                        is AppGate.Onboarding -> navController.navigate(Screen.Onboarding) {
                            popUpTo<Screen.Splash> { inclusive = true }
                        }
                        AppGate.Loading -> Unit
                    }
                },
            )
        }

        composable<Screen.Onboarding> {
            OnboardingScreen(
                onSkip = { onboardingVm.skipAll() },
                onFinish = { onboardingVm.finishCarousel() },
            )
        }

        composable<Screen.Name> {
            NameScreen(
                initialName = onboardingState.nameInput,
                isSubmitting = onboardingState.isSubmitting,
                onBack = { navController.popBackStack() },
                onNameChange = onboardingVm::setNameInput,
                onNext = { onboardingVm.submitName() },
                onSkip = { onboardingVm.skipName() },
            )
        }

        composable<Screen.PickVehicleType> {
            PickVehicleTypeScreen(
                onBack = { navController.popBackStack() },
                onPickType = { typeKey ->
                    onboardingVm.pickVehicleType(VehicleType.fromKey(typeKey))
                },
            )
        }

        composable<Screen.AddVehicle> { backStackEntry ->
            val screen = backStackEntry.toRoute<Screen.AddVehicle>()
            OnboardingAddVehicleScreen(
                vehicleType = screen.type,
                isSubmitting = onboardingState.isSubmitting,
                onBack = { navController.popBackStack() },
                onSkip = { onboardingVm.skipVehicle() },
                onComplete = { input -> onboardingVm.submitVehicle(input) },
            )
        }

        composable<Screen.NotifPermission> {
            NotifPermScreen(
                onBack = { navController.popBackStack() },
                onComplete = { granted ->
                    onboardingVm.recordNotificationPermission(asked = true, granted = granted)
                },
            )
        }

        composable<Screen.Main> {
            MainTabsScreen(
                userName = onboardingState.persistedName.orEmpty(),
                onAddService = { navController.navigate(Screen.AddService()) },
                onAddVehicle = { navController.navigate(Screen.AddVehicleForm) },
                onUpdateOdometer = { navController.navigate(Screen.UpdateOdometer()) },
                onOpenTips = { navController.navigate(Screen.Tips) },
                onOpenReminderDetail = { reminderId -> navController.navigate(Screen.ReminderDetail(reminderId)) },
                onOpenVehicleDetail = { navController.navigate(Screen.VehicleDetail()) },
                onOpenServiceDetail = { recordId -> navController.navigate(Screen.ServiceDetail(recordId)) },
                onOpenAddReminder = { navController.navigate(Screen.AddReminder()) },
                onOpenPrivacy = { navController.navigate(Screen.Privacy) },
                onOpenTerms = { navController.navigate(Screen.Terms) },
                onOpenAbout = { navController.navigate(Screen.About) },
                onOpenHelp = { navController.navigate(Screen.Help) },
                onOpenEditProfile = { navController.navigate(Screen.EditProfile) },
                onOpenVehicleList = { navController.navigate(Screen.VehicleList) },
            )
        }

        composable<Screen.EditProfile> {
            EditProfileScreen(
                onBack = { navController.popBackStack() },
                onSaved = { navController.popBackStack() },
            )
        }

        composable<Screen.AddVehicleForm> {
            AddVehicleScreen(
                onBack = { navController.popBackStack() },
                onSaved = { navController.popBackStack() },
            )
        }


        composable<Screen.Privacy> {
            PrivacyScreen(onBack = { navController.popBackStack() })
        }

        composable<Screen.Terms> {
            TermsScreen(onBack = { navController.popBackStack() })
        }

        composable<Screen.About> {
            AboutScreen(onBack = { navController.popBackStack() })
        }

        composable<Screen.Help> {
            HelpScreen(onBack = { navController.popBackStack() })
        }

        composable<Screen.ReminderDetail> { backStackEntry ->
            val args = backStackEntry.toRoute<Screen.ReminderDetail>()
            ReminderDetailScreen(
                reminderId = args.reminderId,
                onBack = { navController.popBackStack() },
                onMarkServiced = { reminderId, vehicleId ->
                    navController.navigate(
                        Screen.AddService(
                            vehicleId = vehicleId.takeIf(String::isNotBlank),
                            sourceReminderId = reminderId,
                        ),
                    )
                },
                onEdit = { reminderId -> navController.navigate(Screen.EditReminder(reminderId)) },
                onDeleted = { navController.popBackStack() },
            )
        }

        composable<Screen.AddService> { backStackEntry ->
            val args = backStackEntry.toRoute<Screen.AddService>()
            AddServiceScreen(
                onClose = { navController.popBackStack() },
                onSaved = { recordId ->
                    navController.navigate(Screen.InterstitialAd(recordId)) {
                        popUpTo(Screen.AddService(
                            vehicleId = args.vehicleId,
                            sourceReminderId = args.sourceReminderId,
                            trackedComponentId = args.trackedComponentId,
                        )) { inclusive = true }
                    }
                },
                vehicleId = args.vehicleId,
                sourceReminderId = args.sourceReminderId,
                trackedComponentId = args.trackedComponentId,
            )
        }

        composable<Screen.InterstitialAd> { backStackEntry ->
            val args = backStackEntry.toRoute<Screen.InterstitialAd>()
            InterstitialAdScreen(
                onClose = {
                    navController.navigate(Screen.ServiceSaved(args.recordId)) {
                        popUpTo(Screen.InterstitialAd(args.recordId)) { inclusive = true }
                    }
                },
            )
        }

        composable<Screen.ServiceSaved> { backStackEntry ->
            val args = backStackEntry.toRoute<Screen.ServiceSaved>()
            ServiceSavedScreen(
                onBackToHome = {
                    navController.popBackStack(Screen.Main, inclusive = false)
                },
                onOpenHistory = {
                    navController.popBackStack(Screen.Main, inclusive = false)
                },
                onOpenServiceDetail = {
                    val id = args.recordId
                    if (id != null) {
                        navController.navigate(Screen.ServiceDetail(id)) {
                            popUpTo(Screen.Main) { inclusive = false }
                        }
                    } else {
                        navController.popBackStack(Screen.Main, inclusive = false)
                    }
                },
                onAddReminderFromContext = {
                    navController.navigate(Screen.AddReminderFromContext(fromContext = true)) {
                        popUpTo(Screen.Main) { inclusive = false }
                    }
                },
                recordId = args.recordId,
            )
        }

        composable<Screen.VehicleDetail> { backStackEntry ->
            val args = backStackEntry.toRoute<Screen.VehicleDetail>()
            val vehicleId = args.vehicleId.orEmpty()
            VehicleDetailScreen(
                vehicleId = args.vehicleId,
                onBack = { navController.popBackStack() },
                onManageComponents = {
                    if (vehicleId.isNotBlank()) {
                        navController.navigate(Screen.VehicleComponents(vehicleId))
                    }
                },
                onOpenTracked = { trackedId ->
                    navController.navigate(Screen.TrackedComponentDetail(trackedId))
                },
                onAddComponent = {
                    if (vehicleId.isNotBlank()) {
                        navController.navigate(Screen.AddCustomComponent(vehicleId))
                    }
                },
                onEdit = { id -> navController.navigate(Screen.EditVehicle(id)) },
            )
        }

        composable<Screen.VehicleComponents> { backStackEntry ->
            val args = backStackEntry.toRoute<Screen.VehicleComponents>()
            VehicleComponentsScreen(
                vehicleId = args.vehicleId,
                onBack = { navController.popBackStack() },
                onOpenTracked = { trackedId ->
                    navController.navigate(Screen.TrackedComponentDetail(trackedId))
                },
                onOpenCatalog = { catalogId ->
                    navController.navigate(Screen.ComponentInfo(vehicleId = args.vehicleId, catalogId = catalogId))
                },
                onAdd = { navController.navigate(Screen.AddCustomComponent(args.vehicleId)) },
            )
        }

        composable<Screen.ComponentInfo> { backStackEntry ->
            val args = backStackEntry.toRoute<Screen.ComponentInfo>()
            ComponentInfoScreen(
                catalogId = args.catalogId,
                customName = args.customName,
                vehicleId = args.vehicleId,
                onBack = { navController.popBackStack() },
                onTracked = {
                    // Setelah dipantau, kembali ke daftar tambah komponen.
                    navController.popBackStack(Screen.AddCustomComponent(args.vehicleId), inclusive = false)
                },
            )
        }

        composable<Screen.TrackedComponentDetail> { backStackEntry ->
            val args = backStackEntry.toRoute<Screen.TrackedComponentDetail>()
            TrackedComponentDetailScreen(
                trackedId = args.trackedId,
                onBack = { navController.popBackStack() },
                onStopped = { navController.popBackStack() },
                onLogServiceForComponent = {
                    navController.navigate(Screen.AddService(trackedComponentId = args.trackedId))
                },
                onCreateReminderForComponent = {
                    navController.navigate(Screen.AddReminder(trackedComponentId = args.trackedId))
                },
            )
        }

        composable<Screen.AddCustomComponent> { backStackEntry ->
            val args = backStackEntry.toRoute<Screen.AddCustomComponent>()
            AddCustomComponentScreen(
                vehicleId = args.vehicleId,
                onBack = { navController.popBackStack() },
                onOpenComponent = { catalogId ->
                    navController.navigate(Screen.ComponentInfo(vehicleId = args.vehicleId, catalogId = catalogId))
                },
                onCreateCustom = { name ->
                    navController.navigate(Screen.ComponentInfo(vehicleId = args.vehicleId, customName = name))
                },
            )
        }

        composable<Screen.EditVehicle> { backStackEntry ->
            val args = backStackEntry.toRoute<Screen.EditVehicle>()
            EditVehicleScreen(
                vehicleId = args.vehicleId,
                onBack = { navController.popBackStack() },
                onSaved = { navController.popBackStack() },
                onDeleted = {
                    navController.popBackStack(Screen.Main, inclusive = false)
                },
            )
        }

        composable<Screen.EditReminder> { backStackEntry ->
            val args = backStackEntry.toRoute<Screen.EditReminder>()
            EditReminderScreen(
                reminderId = args.reminderId,
                onBack = { navController.popBackStack() },
                onSave = { navController.popBackStack() },
                onDelete = { navController.popBackStack(Screen.Main, inclusive = false) },
            )
        }

        composable<Screen.VehicleList> {
            VehicleListScreen(
                onBack = { navController.popBackStack() },
                onOpenVehicle = { id -> navController.navigate(Screen.VehicleDetail(id)) },
                onAddVehicle = { navController.navigate(Screen.AddVehicleForm) },
            )
        }

        composable<Screen.UpdateOdometer> { backStackEntry ->
            val args = backStackEntry.toRoute<Screen.UpdateOdometer>()
            UpdateOdometerScreen(
                vehicleId = args.vehicleId,
                onClose = { navController.popBackStack() },
                onSaved = { navController.popBackStack() },
            )
        }

        composable<Screen.Tips> {
            TipsScreen(
                onBack = { navController.popBackStack() },
                onOpenTipDetail = { navController.navigate(Screen.TipsDetail) },
            )
        }

        composable<Screen.TipsDetail> {
            TipsDetailScreen(
                onBack = { navController.popBackStack() },
                onOpenAddService = { navController.navigate(Screen.AddService()) },
            )
        }

        composable<Screen.ServiceDetail> { backStackEntry ->
            val args = backStackEntry.toRoute<Screen.ServiceDetail>()
            ServiceDetailScreen(
                onBack = { navController.popBackStack() },
                onEdit = { navController.navigate(Screen.EditService(args.recordId)) },
                onDelete = { navController.popBackStack() },
                onOpenNextReminder = { /* next-reminder linkage TBD */ },
                recordId = args.recordId,
            )
        }

        composable<Screen.EditService> { backStackEntry ->
            val args = backStackEntry.toRoute<Screen.EditService>()
            EditServiceScreen(
                recordId = args.recordId,
                onBack = { navController.popBackStack() },
                onSaved = { navController.popBackStack() },
                onDeleted = { navController.popBackStack(Screen.Main, inclusive = false) },
            )
        }

        composable<Screen.AddReminder> { backStackEntry ->
            val args = backStackEntry.toRoute<Screen.AddReminder>()
            AddReminderScreen(
                onClose = { navController.popBackStack() },
                onSaved = { navController.popBackStack() },
                vehicleId = args.vehicleId,
                trackedComponentId = args.trackedComponentId,
            )
        }

        composable<Screen.AddReminderFromContext> { backStackEntry ->
            val args = backStackEntry.toRoute<Screen.AddReminderFromContext>()
            AddReminderScreen(
                onClose = { navController.popBackStack() },
                onSaved = { navController.popBackStack() },
                vehicleId = args.vehicleId,
                fromContext = args.fromContext,
            )
        }
    }
}
