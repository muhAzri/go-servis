import SwiftUI

struct SettingsView: View {
    var userName: String = ""
    var userColorId: String = "primary"
    var onOpenPrivacy: () -> Void = {}
    var onOpenTerms: () -> Void = {}
    var onOpenAbout: () -> Void = {}
    var onOpenHelp: () -> Void = {}
    var onOpenTestScreen: () -> Void = {}
    var onOpenEditProfile: () -> Void = {}

    @State private var notifPengingatOn = true
    @State private var showExportSheet = false
    @State private var showWipeFlow = false

    var body: some View {
        ScrollView {
            VStack(alignment: .leading, spacing: 0) {
                TabHeader(subtitle: nil, title: "Pengaturan")

                ProfileHeaderCard(
                    userName: userName,
                    colorId: userColorId,
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
                        isOn: $notifPengingatOn
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
    }
}

private struct ProfileHeaderCard: View {
    let userName: String
    let colorId: String
    let onTap: () -> Void

    private var displayName: String { userName.isEmpty ? "Kamu" : userName }
    private var initial: String {
        let trimmed = userName.trimmingCharacters(in: .whitespacesAndNewlines)
        return String(trimmed.first ?? "K").uppercased()
    }
    private var avatarColor: Color {
        switch colorId {
        case "danger": return Color(red: 0.84, green: 0.27, blue: 0.23)
        case "cyan":   return Color(red: 0.25, green: 0.69, blue: 0.84)
        case "amber":  return Color(red: 0.91, green: 0.61, blue: 0.18)
        case "violet": return Color(red: 0.48, green: 0.44, blue: 0.91)
        case "ink":    return Color(red: 0.10, green: 0.14, blue: 0.09)
        default:       return .sgPrimary
        }
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
                    Text("Profil lokal · 4 kendaraan · 12 servis tercatat")
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
