import SwiftUI

struct ServiceSavedView: View {
    let onBackToHome: () -> Void
    var onOpenHistory: () -> Void = {}
    var onOpenServiceDetail: () -> Void = {}
    var onAddReminderFromContext: () -> Void = {}

    var body: some View {
        VStack(spacing: 0) {
            Spacer()

            SuccessIcon()
                .padding(.bottom, 20)

            Text("Servis tercatat 🎉")
                .font(.custom("PlusJakartaSans-ExtraBold", size: 26))
                .foregroundColor(.sgTextPrimary)
                .kerning(-0.5)
                .multilineTextAlignment(.center)
                .padding(.bottom, 8)

            VStack(spacing: 0) {
                Text("Pengingat berikutnya: ganti oli pada")
                    .font(.custom("PlusJakartaSans-Medium", size: 14))
                    .foregroundColor(.sgTextMuted)
                Text("20.420 km / 6 Juli 2026")
                    .font(.custom("PlusJakartaSans-Bold", size: 14))
                    .foregroundColor(.sgTextPrimary)
            }
            .multilineTextAlignment(.center)
            .padding(.horizontal, 24)

            Button(action: onOpenServiceDetail) {
                SavedSummaryCard()
            }
            .buttonStyle(.plain)
            .padding(.horizontal, 24)
            .padding(.top, 24)

            Button(action: onAddReminderFromContext) {
                Text("+ Buat pengingat berikutnya")
                    .font(.custom("PlusJakartaSans-Bold", size: 13))
                    .foregroundColor(.sgPrimary)
            }
            .buttonStyle(.plain)
            .padding(.top, 12)

            Spacer()

            VStack(spacing: 8) {
                AppButton(title: "Kembali ke Beranda", action: onBackToHome)
                HStack(spacing: 16) {
                    Button(action: onOpenServiceDetail) {
                        Text("Lihat Detail")
                            .font(.custom("PlusJakartaSans-SemiBold", size: 14))
                            .foregroundColor(.sgTextMuted)
                            .frame(maxWidth: .infinity)
                            .frame(height: 50)
                    }
                    .buttonStyle(.plain)
                    Button(action: onOpenHistory) {
                        Text("Lihat Riwayat")
                            .font(.custom("PlusJakartaSans-SemiBold", size: 14))
                            .foregroundColor(.sgTextMuted)
                            .frame(maxWidth: .infinity)
                            .frame(height: 50)
                    }
                    .buttonStyle(.plain)
                }
            }
            .padding(.horizontal, 24)
            .padding(.bottom, 24)
        }
        .frame(maxWidth: .infinity, maxHeight: .infinity)
        .background(Color.sgBgWarm)
        .navigationBarBackButtonHidden(true)
        .toolbar(.hidden, for: .navigationBar)
    }
}

private struct SuccessIcon: View {
    var body: some View {
        ZStack {
            Circle()
                .fill(Color.sgPrimary)
                .frame(width: 96, height: 96)
                .shadow(color: Color.sgPrimary.opacity(0.33), radius: 20, x: 0, y: 16)
            Text("\u{f00c}")
                .font(.custom("FontAwesome6Free-Solid", size: 44))
                .foregroundColor(.white)
        }
    }
}

private struct SavedSummaryCard: View {
    var body: some View {
        HStack(spacing: 10) {
            IconBadge(
                iconUnicode: "\u{f613}",
                foreground: .sgWarning,
                background: .sgWarningSoft,
                size: 36, iconSize: 18, corner: 10
            )
            VStack(alignment: .leading, spacing: 2) {
                Text("Ganti Oli Mesin")
                    .font(.custom("PlusJakartaSans-Bold", size: 13))
                    .foregroundColor(.sgTextPrimary)
                Text("Beat Hitam · Rp 65.000")
                    .font(.custom("PlusJakartaSans-Medium", size: 12))
                    .foregroundColor(.sgTextMuted)
            }
            Spacer()
        }
        .padding(18)
        .background(Color.sgSurface)
        .clipShape(RoundedRectangle(cornerRadius: 18))
        .overlay(
            RoundedRectangle(cornerRadius: 18)
                .strokeBorder(Color.sgBorder, lineWidth: 1)
        )
    }
}

#Preview {
    ServiceSavedView(onBackToHome: {})
}
