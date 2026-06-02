import SwiftUI
import Shared
import UniformTypeIdentifiers
import UserNotifications

struct SettingsView: View {
    var onOpenPrivacy: () -> Void = {}
    var onOpenTerms: () -> Void = {}
    var onOpenAbout: () -> Void = {}
    var onOpenHelp: () -> Void = {}
    var onOpenEditProfile: () -> Void = {}

    @ObservedObject private var profile = ProfileModel.shared
    @ObservedObject private var vehicles = VehicleListModel.shared
    @ObservedObject private var services = ServiceHistoryModel.shared
    @ObservedObject private var settings = SettingsModel.shared

    @State private var showExportSheet = false
    @State private var showWipeFlow = false
    @State private var showImporter = false
    @State private var showImportAlert = false
    @State private var importMessage = ""
    @State private var showNotifBlockedAlert = false

    private var displayName: String { profile.state.displayName }
    private var avatarHex: String? {
        guard let p = profile.state.profile else { return nil }
        return PresentationFactory.shared.profileAvatarHex(profile: p)
    }
    private var vehicleCount: Int { vehicles.state.vehicles.count }
    private var serviceCount: Int { services.state.records.count }

    var body: some View {
        ScrollView {
            VStack(alignment: .leading, spacing: 0) {
                TabHeader(subtitle: nil, title: "Pengaturan")

                ProfileHeaderCard(
                    userName: displayName,
                    avatarHex: avatarHex,
                    vehicleCount: vehicleCount,
                    serviceCount: serviceCount,
                    onTap: onOpenEditProfile
                )
                .padding(.horizontal, 16)
                .padding(.bottom, 8)

                SettingsSection(title: "Akun & Data") {
                    SettingsRow(
                        icon: "\u{f15b}",
                        label: "Ekspor Data (CSV)",
                        action: { showExportSheet = true }
                    )
                    SectionDivider()
                    SettingsRow(
                        icon: "\u{f56f}",
                        label: "Impor Data (CSV)",
                        action: { showImporter = true }
                    )
                    SectionDivider()
                    SettingsRow(
                        icon: "\u{f1f8}",
                        label: "Hapus Semua Data",
                        isDanger: true,
                        action: { showWipeFlow = true }
                    )
                }

                SettingsSection(title: "Notifikasi") {
                    ToggleRow(
                        icon: "\u{f0f3}",
                        label: "Pengingat servis",
                        isOn: Binding(
                            get: { settings.state.serviceReminderNotificationsEnabled },
                            set: { settings.setServiceReminderNotificationsEnabled($0) }
                        )
                    )
                    SectionDivider()
                    ToggleRow(
                        icon: "\u{f625}",
                        label: "Pengingat update KM",
                        isOn: Binding(
                            get: { settings.state.odometerReminderNotificationsEnabled },
                            set: { settings.setOdometerReminderNotificationsEnabled($0) }
                        )
                    )
                    SectionDivider()
                    SettingsRow(
                        icon: "\u{f0f3}",
                        label: "Tes notif servis",
                        action: {
                            let firstId = vehicles.state.vehicles.first?.id
                            fireTestNotification(
                                identifier: "test:reminder:\(UUID().uuidString)",
                                title: "Tes pengingat servis",
                                body: "Kalau ini muncul, kategori \"SERVICE_REMINDER\" sudah aktif.",
                                userInfo: ["reminderId": firstId ?? "test"],
                                categoryId: "SERVICE_REMINDER"
                            )
                        }
                    )
                    SectionDivider()
                    SettingsRow(
                        icon: "\u{f625}",
                        label: "Tes notif update KM",
                        action: {
                            guard let v = vehicles.state.vehicles.first else { return }
                            fireTestNotification(
                                identifier: "test:odo:\(UUID().uuidString)",
                                title: "Tes update KM \(v.displayTitle)",
                                body: "Kalau ini muncul, kategori \"ODOMETER_REMINDER\" sudah aktif. Tap untuk buka layar Update KM.",
                                userInfo: ["vehicleId": v.id],
                                categoryId: "ODOMETER_REMINDER"
                            )
                        }
                    )
                }

                SettingsSection(title: "Legal & Bantuan") {
                    SettingsRow(icon: "\u{f023}", label: "Kebijakan Privasi", action: onOpenPrivacy)
                    SectionDivider()
                    SettingsRow(icon: "\u{f15b}", label: "Syarat & Ketentuan", action: onOpenTerms)
                    SectionDivider()
                    SettingsRow(icon: "\u{f05a}", label: "Tentang ServisGo", action: onOpenAbout)
                    SectionDivider()
                    SettingsRow(icon: "\u{f05a}", label: "Bantuan & FAQ", action: onOpenHelp)
                    SectionDivider()
                    SettingsRow(icon: "\u{f005}", label: "Beri Rating ⭐", action: {})
                }

                SettingsFooter()
            }
        }
        .sheet(isPresented: $showExportSheet) {
            ExportCsvSheet(
                onDismiss: { showExportSheet = false },
                onShare: { showExportSheet = false }
            )
            .presentationDetents([.large])
        }
        .overlay {
            if showWipeFlow {
                WipeDataFlow(
                    isPresented: $showWipeFlow,
                    onConfirmed: { showWipeFlow = false }
                )
            }
        }
        .fileImporter(
            isPresented: $showImporter,
            allowedContentTypes: [.commaSeparatedText, .plainText, .text, .data],
            allowsMultipleSelection: false
        ) { result in
            handleImport(result)
        }
        .alert("Impor Data", isPresented: $showImportAlert) {
            Button("OK", role: .cancel) {}
        } message: {
            Text(importMessage)
        }
        .alert("Izin notifikasi dimatikan", isPresented: $showNotifBlockedAlert) {
            Button("Batal", role: .cancel) {}
            Button("Buka Setelan") {
                if let url = URL(string: UIApplication.openSettingsURLString) {
                    UIApplication.shared.open(url)
                }
            }
        } message: {
            Text("Aktifkan notifikasi ServisGo di Setelan iOS agar pengingat bisa muncul.")
        }
    }

    private func handleImport(_ result: Result<[URL], Error>) {
        guard case let .success(urls) = result, let url = urls.first else { return }
        let accessed = url.startAccessingSecurityScopedResource()
        defer { if accessed { url.stopAccessingSecurityScopedResource() } }
        guard let data = try? Data(contentsOf: url),
              let text = String(data: data, encoding: .utf8) else {
            importMessage = "Gagal membaca file."
            showImportAlert = true
            return
        }
        Task {
            do {
                let outcome = try await BackupModel.shared.importCsv(text)
                importMessage = outcome.malformed
                    ? "File CSV tidak dikenali."
                    : "Impor selesai: \(outcome.imported) ditambah · \(outcome.skipped) dilewati."
            } catch {
                importMessage = "Gagal mengimpor data."
            }
            showImportAlert = true
        }
    }

    private func fireTestNotification(
        identifier: String,
        title: String,
        body: String,
        userInfo: [String: Any],
        categoryId: String
    ) {
        let center = UNUserNotificationCenter.current()
        center.getNotificationSettings { settings in
            switch settings.authorizationStatus {
            case .authorized, .provisional, .ephemeral:
                postTestNotification(
                    identifier: identifier,
                    title: title,
                    body: body,
                    userInfo: userInfo,
                    categoryId: categoryId
                )
            case .notDetermined:
                center.requestAuthorization(options: [.alert, .sound, .badge]) { granted, _ in
                    if granted {
                        postTestNotification(
                            identifier: identifier,
                            title: title,
                            body: body,
                            userInfo: userInfo,
                            categoryId: categoryId
                        )
                    } else {
                        DispatchQueue.main.async { showNotifBlockedAlert = true }
                    }
                }
            case .denied:
                DispatchQueue.main.async { showNotifBlockedAlert = true }
            @unknown default:
                DispatchQueue.main.async { showNotifBlockedAlert = true }
            }
        }
    }

    private func postTestNotification(
        identifier: String,
        title: String,
        body: String,
        userInfo: [String: Any],
        categoryId: String
    ) {
        let content = UNMutableNotificationContent()
        content.title = title
        content.body = body
        content.sound = .default
        content.userInfo = userInfo
        content.categoryIdentifier = categoryId
        let trigger = UNTimeIntervalNotificationTrigger(timeInterval: 1, repeats: false)
        let request = UNNotificationRequest(
            identifier: identifier,
            content: content,
            trigger: trigger
        )
        UNUserNotificationCenter.current().add(request, withCompletionHandler: nil)
    }
}

private struct ProfileHeaderCard: View {
    let userName: String
    let avatarHex: String?
    let vehicleCount: Int
    let serviceCount: Int
    let onTap: () -> Void

    private var displayName: String { userName.isEmpty ? "Kamu" : userName }
    private var initial: String {
        let trimmed = userName.trimmingCharacters(in: .whitespacesAndNewlines)
        return String(trimmed.first ?? "K").uppercased()
    }
    private var avatarColor: Color {
        if let hex = avatarHex, let color = hexColor(hex) { return color }
        return .sgPrimary
    }

    var body: some View {
        Button(action: onTap) {
            HStack(spacing: 12) {
                ZStack {
                    RoundedRectangle(cornerRadius: 16, style: .continuous)
                        .fill(avatarColor)
                    Text(initial)
                        .font(.custom("PlusJakartaSans-ExtraBold", size: 20))
                        .foregroundColor(.white)
                }
                .frame(width: 52, height: 52)

                VStack(alignment: .leading, spacing: 2) {
                    Text(displayName)
                        .font(.custom("PlusJakartaSans-Bold", size: 15))
                        .foregroundColor(.sgTextPrimary)
                    Text("Profil lokal · \(vehicleCount) kendaraan · \(serviceCount) servis tercatat")
                        .font(.custom("PlusJakartaSans-Medium", size: 12))
                        .foregroundColor(.sgTextMuted)
                }
                .frame(maxWidth: .infinity, alignment: .leading)

                HStack(spacing: 6) {
                    Text("\u{f303}")
                        .font(.custom("FontAwesome6Free-Solid", size: 12))
                        .foregroundColor(.sgPrimary)
                    Text("Edit")
                        .font(.custom("PlusJakartaSans-Bold", size: 12))
                        .foregroundColor(.sgPrimary)
                }
                .padding(.horizontal, 10)
                .padding(.vertical, 6)
                .background(Color.sgPrimarySoft)
                .clipShape(Capsule())
            }
            .padding(16)
            .frame(maxWidth: .infinity)
            .background(
                RoundedRectangle(cornerRadius: 18, style: .continuous)
                    .fill(Color.sgSurface)
            )
            .overlay(
                RoundedRectangle(cornerRadius: 18, style: .continuous)
                    .strokeBorder(Color.sgBorder, lineWidth: 1)
            )
        }
        .buttonStyle(.plain)
    }
}

private struct SettingsSection<Content: View>: View {
    let title: String
    @ViewBuilder let content: () -> Content

    var body: some View {
        VStack(alignment: .leading, spacing: 0) {
            Text(title.uppercased())
                .font(.custom("PlusJakartaSans-ExtraBold", size: 11))
                .foregroundColor(.sgTextMuted)
                .kerning(1)
                .padding(EdgeInsets(top: 4, leading: 24, bottom: 6, trailing: 24))

            VStack(spacing: 0) {
                content()
            }
            .background(
                RoundedRectangle(cornerRadius: 18, style: .continuous)
                    .fill(Color.sgSurface)
            )
            .overlay(
                RoundedRectangle(cornerRadius: 18, style: .continuous)
                    .strokeBorder(Color.sgBorder, lineWidth: 1)
            )
            .padding(.horizontal, 16)
            .padding(.bottom, 16)
        }
    }
}

private struct SettingsRow: View {
    let icon: String
    let label: String
    var isDanger: Bool = false
    let action: () -> Void

    var body: some View {
        Button(action: action) {
            HStack(spacing: 12) {
                Text(icon)
                    .font(.custom("FontAwesome6Free-Solid", size: 16))
                    .foregroundColor(isDanger ? .sgDanger : .sgTextMuted)
                    .frame(width: 20)
                Text(label)
                    .font(.custom("PlusJakartaSans-SemiBold", size: 14))
                    .foregroundColor(isDanger ? .sgDanger : .sgTextPrimary)
                    .frame(maxWidth: .infinity, alignment: .leading)
                Text("\u{f054}")
                    .font(.custom("FontAwesome6Free-Solid", size: 12))
                    .foregroundColor(.sgTextSubtle)
            }
            .padding(EdgeInsets(top: 14, leading: 16, bottom: 14, trailing: 16))
            .contentShape(Rectangle())
        }
        .buttonStyle(.plain)
    }
}

private struct ToggleRow: View {
    let icon: String
    let label: String
    @Binding var isOn: Bool

    var body: some View {
        HStack(spacing: 12) {
            Text(icon)
                .font(.custom("FontAwesome6Free-Solid", size: 16))
                .foregroundColor(.sgTextMuted)
                .frame(width: 20)
            Text(label)
                .font(.custom("PlusJakartaSans-SemiBold", size: 14))
                .foregroundColor(.sgTextPrimary)
                .frame(maxWidth: .infinity, alignment: .leading)
            Toggle("", isOn: $isOn)
                .labelsHidden()
                .toggleStyle(SwitchToggleStyle(tint: .sgPrimary))
        }
        .padding(EdgeInsets(top: 10, leading: 16, bottom: 10, trailing: 16))
    }
}

private struct SectionDivider: View {
    var body: some View {
        Rectangle()
            .fill(Color.sgBorder)
            .frame(height: 1)
    }
}

private struct SettingsFooter: View {
    var body: some View {
        Text("ServisGo v1.0.0 · build 2026.05.06\n© 2026 Muhammad Azri Fatihah Susanto")
            .font(.custom("PlusJakartaSans-Medium", size: 11))
            .foregroundColor(.sgTextSubtle)
            .multilineTextAlignment(.center)
            .frame(maxWidth: .infinity)
            .padding(.horizontal, 20)
            .padding(.top, 12)
            .padding(.bottom, 24)
    }
}

#Preview {
    SettingsView()
        .background(Color.sgBgWarm)
}
