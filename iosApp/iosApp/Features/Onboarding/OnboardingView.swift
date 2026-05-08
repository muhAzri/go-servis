import SwiftUI

private struct OnboardingPage {
    let title: String
    let subtitle: String
}

private let pages: [OnboardingPage] = [
    OnboardingPage(
        title: "Catat Semua\nKendaraanmu",
        subtitle: "Tambah motor atau mobil dan kelola semua servisnya dalam satu tempat."
    ),
    OnboardingPage(
        title: "Pengingat Servis\nOtomatis",
        subtitle: "Kami ingatkan kapan ganti oli, servis berkala, dan perawatan lainnya tepat waktu."
    ),
    OnboardingPage(
        title: "Riwayat Servis\nLengkap",
        subtitle: "Catat setiap servis — biaya, bengkel, dan kilometer. Semua tersimpan rapi."
    ),
]

struct OnboardingView: View {
    let onSkip: () -> Void
    let onFinish: () -> Void

    @State private var currentPage = 0

    var body: some View {
        ZStack(alignment: .topTrailing) {
            Color.sgBgWarm.ignoresSafeArea()

            VStack(spacing: 0) {
                // Illustration pager
                TabView(selection: $currentPage) {
                    IllustWelcomeView().tag(0)
                    IllustReminderView().tag(1)
                    IllustHistoryView().tag(2)
                }
                .tabViewStyle(.page(indexDisplayMode: .never))
                .frame(maxHeight: .infinity)

                // Bottom section
                VStack(alignment: .leading, spacing: 0) {
                    // Dot indicators
                    HStack(spacing: 6) {
                        ForEach(0..<pages.count, id: \.self) { idx in
                            Capsule()
                                .fill(idx == currentPage ? Color.sgPrimary : Color.black.opacity(0.08))
                                .frame(width: idx == currentPage ? 28 : 8, height: 8)
                                .animation(.spring(response: 0.3), value: currentPage)
                        }
                    }
                    .frame(maxWidth: .infinity, alignment: .center)
                    .padding(.bottom, 24)

                    Text(pages[currentPage].title)
                        .font(.custom("PlusJakartaSans-Bold", size: 28))
                        .foregroundColor(.sgTextPrimary)
                        .lineSpacing(4)
                        .fixedSize(horizontal: false, vertical: true)
                        .animation(.easeInOut(duration: 0.2), value: currentPage)

                    Spacer().frame(height: 10)

                    Text(pages[currentPage].subtitle)
                        .font(.custom("PlusJakartaSans-Regular", size: 15))
                        .foregroundColor(.sgTextMuted)
                        .lineSpacing(3)
                        .fixedSize(horizontal: false, vertical: true)
                        .animation(.easeInOut(duration: 0.2), value: currentPage)

                    Spacer().frame(height: 28)

                    Button {
                        withAnimation(.easeInOut(duration: 0.3)) {
                            if currentPage < pages.count - 1 {
                                currentPage += 1
                            } else {
                                onFinish()
                            }
                        }
                    } label: {
                        HStack(spacing: 8) {
                            Text(currentPage == pages.count - 1 ? "Mulai Sekarang" : "Lanjut")
                                .font(.custom("PlusJakartaSans-Bold", size: 16))
                                .foregroundColor(.white)
                            Text(currentPage == pages.count - 1 ? "\u{f061}" : "\u{f054}")
                                .font(.custom("FontAwesome6Free-Solid", size: 14))
                                .foregroundColor(.white)
                        }
                        .frame(maxWidth: .infinity)
                        .frame(height: 56)
                        .background(Color.sgPrimary)
                        .clipShape(RoundedRectangle(cornerRadius: 18))
                    }
                    .buttonStyle(.plain)
                }
                .padding(.horizontal, 24)
                .padding(.bottom, 36)
            }

            // Skip button
            Button(action: onSkip) {
                Text("Lewati")
                    .font(.custom("PlusJakartaSans-SemiBold", size: 14))
                    .foregroundColor(.sgTextMuted)
                    .padding(.horizontal, 16)
                    .padding(.vertical, 10)
            }
            .padding(.top, 56)
            .padding(.trailing, 8)
        }
    }
}

// MARK: – Illustrations

struct IllustWelcomeView: View {
    var body: some View {
        // ZStack centered — offsets are from the visual center of the 280×260 container
        ZStack {
            RoundedRectangle(cornerRadius: 100)
                .fill(Color.sgPrimarySoft)
                .frame(width: 280, height: 260)

            // Motorcycle — hero, center-left and slightly below center
            Text("\u{f21c}")
                .font(.custom("FontAwesome6Free-Solid", size: 108))
                .foregroundColor(.sgPrimary)
                .offset(x: -16, y: 18)

            // Car — upper-right quadrant, clearly separated
            Text("\u{f1b9}")
                .font(.custom("FontAwesome6Free-Solid", size: 66))
                .foregroundColor(.sgPrimaryDark)
                .rotationEffect(.degrees(10))
                .offset(x: 72, y: -68)

            // Bubbles
            Circle().fill(Color.sgWarning.opacity(0.7)).frame(width: 16, height: 16)
                .offset(x: -110, y: -96)
            Circle().fill(Color.sgPrimaryDark.opacity(0.4)).frame(width: 22, height: 22)
                .offset(x: -104, y: 82)
            Circle().fill(Color.sgDanger.opacity(0.5)).frame(width: 12, height: 12)
                .offset(x: 110, y: 90)
        }
        .frame(width: 280, height: 260)
        .clipped()
    }
}

struct IllustReminderView: View {
    var body: some View {
        ZStack {
            RoundedRectangle(cornerRadius: 100)
                .fill(Color.sgWarningSoft)
                .frame(width: 280, height: 260)

            // Bell — perfectly centered
            Text("\u{f0f3}")
                .font(.custom("FontAwesome6Free-Solid", size: 112))
                .foregroundColor(.sgWarning)

            // Toast card 1 — upper-right, slightly overlapping bell
            toastCard(dot: .sgDanger, text: "Ganti oli telat", time: "sekarang")
                .offset(x: 52, y: -82)

            // Toast card 2 — lower-left
            toastCard(dot: .sgWarning, text: "Servis dalam 3 hari", time: "3j lalu")
                .offset(x: -46, y: 84)
        }
        .frame(width: 280, height: 260)
        .clipped()
    }

    private func toastCard(dot: Color, text: String, time: String) -> some View {
        HStack(spacing: 8) {
            Circle().fill(dot).frame(width: 8, height: 8)
            Text(text)
                .font(.custom("PlusJakartaSans-SemiBold", size: 11))
                .foregroundColor(.sgTextPrimary)
            Spacer()
            Text(time)
                .font(.custom("PlusJakartaSans-Regular", size: 10))
                .foregroundColor(.sgTextMuted)
        }
        .padding(.horizontal, 12)
        .padding(.vertical, 10)
        .frame(width: 162)
        .background(Color.white)
        .clipShape(RoundedRectangle(cornerRadius: 12))
        .shadow(color: .black.opacity(0.08), radius: 8, x: 0, y: 4)
    }
}

struct IllustHistoryView: View {
    var body: some View {
        // ZStack centered — offsets from center of 280×260
        // Stack: 3 cards (200×60 each) stepped 16pt apart = 92pt total height
        // Vertical center offset: stack center at y=0, so top card at y ≈ -32, bottom at y ≈ +32
        ZStack {
            RoundedRectangle(cornerRadius: 100)
                .fill(Color.sgPrimarySofter)
                .frame(width: 280, height: 260)

            // Card 0 — back (drawn first = behind)
            serviceCard(iconChar: "\u{f613}", iconBg: .sgWarningSoft, iconColor: .sgWarning, rotation: -2)
                .offset(x: -6, y: -26)

            // Card 1 — middle
            serviceCard(iconChar: "\u{f5df}", iconBg: .sgDangerSoft, iconColor: .sgDanger, rotation: 0)
                .offset(x: 0, y: -10)

            // Card 2 — front (drawn last = on top)
            serviceCard(iconChar: "\u{f0ad}", iconBg: .sgPrimarySoft, iconColor: .sgPrimary, rotation: 2)
                .offset(x: 6, y: 6)
        }
        .frame(width: 280, height: 260)
        .clipped()
    }

    private func serviceCard(
        iconChar: String,
        iconBg: Color,
        iconColor: Color,
        rotation: Double
    ) -> some View {
        HStack(spacing: 12) {
            ZStack {
                RoundedRectangle(cornerRadius: 10)
                    .fill(iconBg)
                    .frame(width: 36, height: 36)
                Text(iconChar)
                    .font(.custom("FontAwesome6Free-Solid", size: 17))
                    .foregroundColor(iconColor)
            }
            VStack(alignment: .leading, spacing: 6) {
                RoundedRectangle(cornerRadius: 4)
                    .fill(Color.sgSurfaceAlt)
                    .frame(width: 100, height: 8)
                RoundedRectangle(cornerRadius: 3)
                    .fill(Color.sgSurfaceAlt)
                    .frame(width: 64, height: 6)
            }
            Spacer()
        }
        .padding(.horizontal, 12)
        .frame(width: 200, height: 60)
        .background(Color.white)
        .clipShape(RoundedRectangle(cornerRadius: 14))
        .shadow(color: .black.opacity(0.06), radius: 8, x: 0, y: 4)
        .rotationEffect(.degrees(rotation))
    }
}

#Preview {
    OnboardingView(onSkip: {}, onFinish: {})
}
