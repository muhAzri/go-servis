package com.zrifapps.goservice.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.toArgb
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
import com.zrifapps.goservice.ui.service.InterstitialAdScreen
import com.zrifapps.goservice.ui.service.ServiceDetailScreen
import com.zrifapps.goservice.ui.service.ServiceSavedScreen
import com.zrifapps.goservice.ui.splash.SplashScreen
import com.zrifapps.goservice.ui.test.TestScreen
import com.zrifapps.goservice.ui.theme.AppColors
import com.zrifapps.goservice.ui.tips.TipsDetailScreen
import com.zrifapps.goservice.ui.tips.TipsScreen
import com.zrifapps.goservice.ui.vehicle.AddCustomComponentScreen
import com.zrifapps.goservice.ui.vehicle.AddVehicleScreen
import com.zrifapps.goservice.ui.vehicle.ComponentDetailScreen
import com.zrifapps.goservice.ui.vehicle.EditVehicleScreen
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
                userColorArgb = AppColors.Primary.toArgb(),
                onAddService = { navController.navigate(Screen.AddService) },
                onAddVehicle = { navController.navigate(Screen.AddVehicleForm) },
                onUpdateOdometer = { navController.navigate(Screen.UpdateOdometer) },
                onOpenTips = { navController.navigate(Screen.Tips) },
                onOpenReminderDetail = { navController.navigate(Screen.ReminderDetail) },
                onOpenVehicleDetail = { navController.navigate(Screen.VehicleDetail) },
                onOpenServiceDetail = { navController.navigate(Screen.ServiceDetail) },
                onOpenAddReminder = { navController.navigate(Screen.AddReminder) },
                onOpenTestScreen = { navController.navigate(Screen.Test) },
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
                initialName = onboardingState.persistedName.orEmpty(),
                initialColorArgb = AppColors.Primary.toArgb(),
                onBack = { navController.popBackStack() },
                onSave = { _, _ ->
                    navController.popBackStack()
                },
            )
        }

        composable<Screen.Test> {
            TestScreen(
                onBack = { navController.popBackStack() },
                onAddVehicle = { navController.navigate(Screen.AddVehicleForm) },
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

        composable<Screen.ReminderDetail> {
            ReminderDetailScreen(
                onBack = { navController.popBackStack() },
                onMarkServiced = { navController.navigate(Screen.AddService) },
                onEdit = { navController.navigate(Screen.EditReminder) },
                onDelete = { navController.popBackStack() },
            )
        }

        composable<Screen.AddService> {
            AddServiceScreen(
                onClose = { navController.popBackStack() },
                onSaved = { navController.navigate(Screen.InterstitialAd) },
            )
        }

        composable<Screen.InterstitialAd> {
            InterstitialAdScreen(
                onClose = { navController.navigate(Screen.ServiceSaved) },
            )
        }

        composable<Screen.ServiceSaved> {
            ServiceSavedScreen(
                onBackToHome = {
                    navController.popBackStack(Screen.Main, inclusive = false)
                },
                onOpenHistory = {
                    navController.popBackStack(Screen.Main, inclusive = false)
                },
                onOpenServiceDetail = {
                    navController.navigate(Screen.ServiceDetail) {
                        popUpTo(Screen.Main) { inclusive = false }
                    }
                },
                onAddReminderFromContext = {
                    navController.navigate(Screen.AddReminderFromContext(fromContext = true)) {
                        popUpTo(Screen.Main) { inclusive = false }
                    }
                },
            )
        }

        composable<Screen.VehicleDetail> {
            VehicleDetailScreen(
                onBack = { navController.popBackStack() },
                onManageComponents = { navController.navigate(Screen.VehicleComponents) },
                onOpenComponent = { id -> navController.navigate(Screen.ComponentDetail(id)) },
                onAddComponent = { navController.navigate(Screen.AddCustomComponent) },
                onEdit = { navController.navigate(Screen.EditVehicle) },
            )
        }

        composable<Screen.VehicleComponents> {
            VehicleComponentsScreen(
                onBack = { navController.popBackStack() },
                onOpenComponent = { id -> navController.navigate(Screen.ComponentDetail(id)) },
                onAdd = { navController.navigate(Screen.AddCustomComponent) },
            )
        }

        composable<Screen.ComponentDetail> { backStackEntry ->
            val args = backStackEntry.toRoute<Screen.ComponentDetail>()
            ComponentDetailScreen(
                componentId = args.componentId,
                onBack = { navController.popBackStack() },
                onSave = { navController.popBackStack() },
                onStopMonitoring = { navController.popBackStack() },
                onLogServiceForComponent = { navController.navigate(Screen.AddService) },
                onCreateReminderForComponent = { navController.navigate(Screen.AddReminder) },
            )
        }

        composable<Screen.AddCustomComponent> {
            AddCustomComponentScreen(
                onBack = { navController.popBackStack() },
                onAdd = { _ -> navController.popBackStack() },
            )
        }

        composable<Screen.EditVehicle> {
            EditVehicleScreen(
                onBack = { navController.popBackStack() },
                onSave = { navController.popBackStack() },
                onDelete = {
                    navController.popBackStack(Screen.Main, inclusive = false)
                },
            )
        }

        composable<Screen.EditReminder> {
            EditReminderScreen(
                onBack = { navController.popBackStack() },
                onSave = { navController.popBackStack() },
                onDelete = {
                    navController.popBackStack(Screen.Main, inclusive = false)
                },
            )
        }

        composable<Screen.VehicleList> {
            VehicleListScreen(
                onBack = { navController.popBackStack() },
                onOpenVehicle = { navController.navigate(Screen.VehicleDetail) },
                onAddVehicle = { navController.navigate(Screen.AddVehicleForm) },
            )
        }

        composable<Screen.UpdateOdometer> {
            UpdateOdometerScreen(
                onClose = { navController.popBackStack() },
                onSave = { navController.popBackStack() },
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
                onOpenAddService = { navController.navigate(Screen.AddService) },
            )
        }

        composable<Screen.ServiceDetail> {
            ServiceDetailScreen(
                onBack = { navController.popBackStack() },
                onEdit = { navController.navigate(Screen.AddService) },
                onDelete = { navController.popBackStack() },
                onOpenNextReminder = { navController.navigate(Screen.ReminderDetail) },
            )
        }

        composable<Screen.AddReminder> {
            AddReminderScreen(
                onClose = { navController.popBackStack() },
                onSaved = { navController.popBackStack() },
            )
        }

        composable<Screen.AddReminderFromContext> { backStackEntry ->
            val args = backStackEntry.toRoute<Screen.AddReminderFromContext>()
            AddReminderScreen(
                onClose = { navController.popBackStack() },
                onSaved = { navController.popBackStack() },
                fromContext = args.fromContext,
            )
        }
    }
}
