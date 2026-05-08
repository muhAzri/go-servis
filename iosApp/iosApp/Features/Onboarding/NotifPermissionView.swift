import SwiftUI
import UserNotifications

struct NotifPermissionView: View {
    let onBack: () -> Void
    let onComplete: () -> Void

    var body: some View {
        ZStack {
            Color.sgBgWarm.ignoresSafeArea()

            VStack(alignment: .leading, spacing: 0) {
                // Back button
                Button(action: onBack) {
                    Text("\u{f060}")
                        .font(.custom("FontAwesome6Free-Solid", size: 18))
                        .foregroundColor(.sgTextPrimary)
                        .frame(width: 44, height: 44)
                }
                .buttonStyle(.plain)
                .padding(.leading, 12)
                .padding(.top, 8)

                VStack(alignment: .leading, spacing: 0) {
                    Text("Langkah 3 dari 3")
                        .font(.custom("PlusJakartaSans-SemiBold", size: 13))
                        .foregroundColor(.sgTextMuted)

                    Spacer().frame(height: 6)

                    Text("Aktifkan pengingat")
                        .font(.custom("PlusJakartaSans-Bold", size: 26))
                        .foregroundColor(.sgTextPrimary)

                    Spacer().frame(height: 6)

                    Text("Kami kirim notif saat servis hampir tiba — supaya tidak kelupaan.")
                        .font(.custom("PlusJakartaSans-Regular", size: 14))
                        .foregroundColor(.sgTextMuted)
                        .lineSpacing(3)

                    Spacer().frame(height: 24)

                    // Mock notification card 1 — overdue
                    NotifPreviewCard(
                        accentColor: .sgDanger,
                        iconUnicode: "\u{f0ad}",
                        title: "Beat Hitam — Ganti oli telat 16 hari",
                        subtitle: "Sudah 18.420 km, target 18.000 km",
                        time: "sekarang",
                        opacity: 1.0
                    )

                    Spacer().frame(height: 8)

                    // Mock notification card 2 — upcoming
                    NotifPreviewCard(
                        accentColor: .sgWarning,
                        iconUnicode: "\u{f0f3}",
                        title: "Brio Biru — Servis berkala 3 hari lagi",
                        subtitle: "9 Mei 2026 · Auto2000 Cikarang",
                        time: "3j yang lalu",
                        opacity: 0.85
                    )

                    Spacer()

                    // Primary button
                    Button {
                        UNUserNotificationCenter.current().requestAuthorization(
                            options: [.alert, .badge, .sound]
                        ) { _, _ in
                            DispatchQueue.main.async { onComplete() }
                        }
                    } label: {
                        Text("Aktifkan Notifikasi")
                            .font(.custom("PlusJakartaSans-Bold", size: 16))
                            .foregroundColor(.white)
                            .frame(maxWidth: .infinity)
                            .frame(height: 56)
                            .background(Color.sgPrimary)
                            .clipShape(RoundedRectangle(cornerRadius: 18))
                    }
                    .buttonStyle(.plain)

                    Spacer().frame(height: 4)

                    // Secondary button
                    Button(action: onComplete) {
                        Text("Nanti saja")
                            .font(.custom("PlusJakartaSans-SemiBold", size: 14))
                            .foregroundColor(.sgTextMuted)
                            .frame(maxWidth: .infinity)
                            .frame(height: 50)
                    }
                    .buttonStyle(.plain)

                    Spacer().frame(height: 24)
                }
                .padding(.horizontal, 24)
            }
        }
        .navigationBarHidden(true)
    }
}

/// iOS 26 Liquid Glass notification style.
/// Key traits: .regularMaterial frosted background, top-edge specular highlight,
/// circular app icon, system rounded font, 24pt corner radius.
private struct NotifPreviewCard: View {
    let accentColor: Color
    let iconUnicode: String
    let title: String
    let subtitle: String
    let time: String
    let opacity: Double

    var body: some View {
        HStack(alignment: .top, spacing: 10) {

            // App icon — circular in iOS 26 notification banners
            ZStack {
                Circle()
                    .fill(
                        LinearGradient(
                            colors: [accentColor.opacity(0.9), accentColor],
                            startPoint: .topLeading,
                            endPoint: .bottomTrailing
                        )
                    )
                    .frame(width: 36, height: 36)
                // Glass sheen on icon — small top-left highlight
                Circle()
                    .fill(
                        LinearGradient(
                            colors: [.white.opacity(0.35), .clear],
                            startPoint: .topLeading,
                            endPoint: .center
                        )
                    )
                    .frame(width: 36, height: 36)
                Text(iconUnicode)
                    .font(.custom("FontAwesome6Free-Solid", size: 16))
                    .foregroundColor(.white)
            }

            // Text — system font (it's an OS notification, not app UI)
            VStack(alignment: .leading, spacing: 2) {
                HStack(alignment: .firstTextBaseline) {
                    Text("ServisGo")
                        .font(.system(size: 12, weight: .semibold, design: .rounded))
                        .foregroundStyle(.primary)
                    Spacer()
                    Text(time)
                        .font(.system(size: 11, weight: .regular, design: .rounded))
                        .foregroundStyle(.secondary)
                }
                Text(title)
                    .font(.system(size: 13, weight: .semibold, design: .rounded))
                    .foregroundStyle(.primary)
                    .lineSpacing(1)
                    .fixedSize(horizontal: false, vertical: true)
                Text(subtitle)
                    .font(.system(size: 12, weight: .regular, design: .rounded))
                    .foregroundStyle(.secondary)
                    .lineSpacing(0.5)
            }
        }
        .padding(.horizontal, 14)
        .padding(.vertical, 13)
        // ── Liquid Glass material ──
        .background(.regularMaterial, in: RoundedRectangle(cornerRadius: 24, style: .continuous))
        // Top-edge specular highlight — the signature Liquid Glass shine
        .overlay(alignment: .top) {
            RoundedRectangle(cornerRadius: 24, style: .continuous)
                .strokeBorder(
                    LinearGradient(
                        colors: [.white.opacity(0.55), .white.opacity(0.0)],
                        startPoint: .top,
                        endPoint: .init(x: 0.5, y: 0.55)
                    ),
                    lineWidth: 1
                )
        }
        .shadow(color: .black.opacity(0.12), radius: 18, x: 0, y: 6)
        .shadow(color: .black.opacity(0.05), radius: 4,  x: 0, y: 1)
        .opacity(opacity)
    }
}

#Preview {
    NotifPermissionView(onBack: {}, onComplete: {})
}
