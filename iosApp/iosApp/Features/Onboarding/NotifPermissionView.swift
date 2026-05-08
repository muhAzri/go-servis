import SwiftUI
import UserNotifications

struct NotifPermissionView: View {
    let onBack: () -> Void
    let onComplete: () -> Void

    var body: some View {
        ZStack {
            Color.sgBgWarm.ignoresSafeArea()

            VStack(alignment: .leading, spacing: 0) {
                OnboardingStepHeader(
                    stepLabel: "Langkah 3 dari 3",
                    title: "Aktifkan pengingat",
                    subtitle: "Kami kirim notif saat servis hampir tiba — supaya tidak kelupaan.",
                    onBack: onBack
                )

                VStack(alignment: .leading, spacing: 0) {
                    Spacer().frame(height: 24)

                    NotifPreviewCard(
                        accentColor: .sgDanger,
                        iconUnicode: "\u{f0ad}",
                        title: "Beat Hitam — Ganti oli telat 16 hari",
                        subtitle: "Sudah 18.420 km, target 18.000 km",
                        time: "sekarang",
                        opacity: 1.0
                    )

                    Spacer().frame(height: 8)

                    NotifPreviewCard(
                        accentColor: .sgWarning,
                        iconUnicode: "\u{f0f3}",
                        title: "Brio Biru — Servis berkala 3 hari lagi",
                        subtitle: "9 Mei 2026 · Auto2000 Cikarang",
                        time: "3j yang lalu",
                        opacity: 0.85
                    )

                    Spacer()

                    AppButton(title: "Aktifkan Notifikasi", action: {
                        UNUserNotificationCenter.current().requestAuthorization(
                            options: [.alert, .badge, .sound]
                        ) { _, _ in
                            DispatchQueue.main.async { onComplete() }
                        }
                    })

                    Spacer().frame(height: 4)

                    AppTextButton(title: "Nanti saja", action: onComplete)

                    Spacer().frame(height: 24)
                }
                .padding(.horizontal, 24)
            }
        }
        .navigationBarHidden(true)
    }
}

private struct NotifPreviewCard: View {
    let accentColor: Color
    let iconUnicode: String
    let title: String
    let subtitle: String
    let time: String
    let opacity: Double

    var body: some View {
        HStack(alignment: .top, spacing: 10) {

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
        .background(.regularMaterial, in: RoundedRectangle(cornerRadius: 24, style: .continuous))
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
