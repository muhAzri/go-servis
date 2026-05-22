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

            NextReminderCtaCard(onCustomize: onAddReminderFromContext)
                .padding(.horizontal, 24)
                .padding(.top, 16)

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

private struct NextReminderCtaCard: View {
    let onCustomize: () -> Void

    @State private var state: CtaState = .cta

    enum CtaState { case cta, animating, saved }

    var body: some View {
        VStack(alignment: .leading, spacing: 12) {
            HStack(spacing: 10) {
                ZStack {
                    RoundedRectangle(cornerRadius: 10)
                        .fill(Color.sgPrimary)
                        .frame(width: 36, height: 36)
                    Text("\u{f0f3}")
                        .font(.custom("FontAwesome6Free-Solid", size: 16))
                        .foregroundColor(.white)
                }
                VStack(alignment: .leading, spacing: 2) {
                    Text("Pengingat berikutnya")
                        .font(.custom("PlusJakartaSans-Bold", size: 13))
                        .foregroundColor(.sgTextPrimary)
                    Text("20.420 km · 6 Juli 2026")
                        .font(.system(size: 11, weight: .medium, design: .monospaced))
                        .foregroundColor(.sgTextMuted)
                }
                Spacer()
            }

            switch state {
            case .cta:
                HStack(spacing: 8) {
                    Button(action: { triggerSave() }) {
                        Text("Set otomatis")
                            .font(.custom("PlusJakartaSans-Bold", size: 12))
                            .foregroundColor(.white)
                            .frame(maxWidth: .infinity)
                            .frame(height: 40)
                            .background(Color.sgPrimary)
                            .clipShape(RoundedRectangle(cornerRadius: 10))
                    }
                    .buttonStyle(.plain)
                    Button(action: onCustomize) {
                        Text("Ubah dulu")
                            .font(.custom("PlusJakartaSans-Bold", size: 12))
                            .foregroundColor(.sgPrimary)
                            .frame(maxWidth: .infinity)
                            .frame(height: 40)
                            .overlay(
                                RoundedRectangle(cornerRadius: 10)
                                    .strokeBorder(Color.sgPrimary, lineWidth: 1.5)
                            )
                    }
                    .buttonStyle(.plain)
                }
            case .animating:
                HStack {
                    Spacer()
                    ProgressView().progressViewStyle(.circular).tint(.sgPrimary)
                    Spacer()
                }
                .frame(height: 40)
                .background(Color.sgPrimarySoft)
                .clipShape(RoundedRectangle(cornerRadius: 10))
            case .saved:
                HStack(spacing: 8) {
                    Text("\u{f00c}")
                        .font(.custom("FontAwesome6Free-Solid", size: 13))
                        .foregroundColor(.sgPrimary)
                    Text("Reminder dibuat: 5.000 km / 6 bulan")
                        .font(.custom("PlusJakartaSans-Bold", size: 12))
                        .foregroundColor(.sgPrimary)
                    Spacer()
                }
                .padding(.horizontal, 12)
                .padding(.vertical, 10)
                .background(Color.sgPrimarySoft)
                .clipShape(RoundedRectangle(cornerRadius: 10))
            }
        }
        .padding(16)
        .background(Color.sgPrimarySofter)
        .clipShape(RoundedRectangle(cornerRadius: 18))
        .overlay(
            RoundedRectangle(cornerRadius: 18)
                .strokeBorder(Color.sgPrimary.opacity(0.2), lineWidth: 1)
        )
    }

    private func triggerSave() {
        state = .animating
        DispatchQueue.main.asyncAfter(deadline: .now() + 0.4) {
            state = .saved
        }
    }
}

#Preview {
    ServiceSavedView(onBackToHome: {})
}
