import SwiftUI

struct PengaturanView: View {
    var onOpenPrivacy: () -> Void = {}
    var onOpenTerms: () -> Void = {}
    var onOpenAbout: () -> Void = {}
    var onOpenHelp: () -> Void = {}
    var onOpenTestScreen: () -> Void = {}

    @State private var notifPengingatOn = true

    var body: some View {
        ScrollView {
            VStack(alignment: .leading, spacing: 0) {
                TabHeader(subtitle: nil, title: "Pengaturan")

                ProfileHeaderCard(onTap: onOpenTestScreen)
                    .padding(.horizontal, 16)
                    .padding(.bottom, 8)

                SettingsSection(title: "Akun & Data") {
                    SettingsRow(
                        icon: "\u{f15b}",
                        label: "Ekspor Data (CSV)",
                        action: {}
                    )
                    SectionDivider()
                    SettingsRow(
                        icon: "\u{f1f8}",
                        label: "Hapus Semua Data",
                        isDanger: true,
                        action: {}
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

                AppFooter()
            }
        }
    }
}

private struct ProfileHeaderCard: View {
    let onTap: () -> Void

    var body: some View {
        Button(action: onTap) {
            HStack(spacing: 12) {
                ZStack {
                    RoundedRectangle(cornerRadius: 16, style: .continuous)
                        .fill(Color.sgPrimary)
                    Text("A")
                        .font(.custom("PlusJakartaSans-ExtraBold", size: 20))
                        .foregroundColor(.white)
                }
                .frame(width: 52, height: 52)

                VStack(alignment: .leading, spacing: 2) {
                    Text("Profil Lokal")
                        .font(.custom("PlusJakartaSans-Bold", size: 15))
                        .foregroundColor(.sgTextPrimary)
                    Text("Data tersimpan di perangkat ini")
                        .font(.custom("PlusJakartaSans-Medium", size: 12))
                        .foregroundColor(.sgTextMuted)
                }
                .frame(maxWidth: .infinity, alignment: .leading)

                Text("\u{f303}")
                    .font(.custom("FontAwesome6Free-Solid", size: 14))
                    .foregroundColor(.sgTextMuted)
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

private struct AppFooter: View {
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
    PengaturanView()
        .background(Color.sgBgWarm)
}
