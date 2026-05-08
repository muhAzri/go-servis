import SwiftUI

struct PickVehicleTypeView: View {
    let onBack: () -> Void
    let onPickType: (String) -> Void

    var body: some View {
        ZStack {
            Color.sgBgWarm.ignoresSafeArea()

            VStack(alignment: .leading, spacing: 0) {
                OnboardingStepHeader(
                    stepLabel: "Langkah 1 dari 3",
                    title: "Kendaraan kamu apa?",
                    subtitle: "Bisa tambah lebih dari satu nanti.",
                    onBack: onBack
                )

                VStack(alignment: .leading, spacing: 0) {
                    Spacer().frame(height: 28)

                    VehicleCard(
                        iconUnicode: "\u{f21c}",
                        label: "Motor",
                        sub: "Bebek, matic, sport"
                    ) { onPickType("motor") }

                    Spacer().frame(height: 14)

                    VehicleCard(
                        iconUnicode: "\u{f1b9}",
                        label: "Mobil",
                        sub: "MPV, SUV, sedan, hatchback"
                    ) { onPickType("mobil") }

                    Spacer()

                    AppInfoBanner(text: "Data disimpan lokal di HP. Tidak butuh login.")

                    Spacer().frame(height: 32)
                }
                .padding(.horizontal, 24)
            }
        }
        .navigationBarHidden(true)
    }
}

private struct VehicleCard: View {
    let iconUnicode: String
    let label: String
    let sub: String
    let action: () -> Void

    var body: some View {
        Button(action: action) {
            HStack(spacing: 16) {
                ZStack {
                    RoundedRectangle(cornerRadius: 18)
                        .fill(Color.sgPrimarySoft)
                        .frame(width: 64, height: 64)
                    Text(iconUnicode)
                        .font(.custom("FontAwesome6Free-Solid", size: 30))
                        .foregroundColor(.sgPrimary)
                }

                VStack(alignment: .leading, spacing: 3) {
                    Text(label)
                        .font(.custom("PlusJakartaSans-Bold", size: 18))
                        .foregroundColor(.sgTextPrimary)
                    Text(sub)
                        .font(.custom("PlusJakartaSans-Regular", size: 13))
                        .foregroundColor(.sgTextMuted)
                }

                Spacer()

                Text("\u{f054}")
                    .font(.custom("FontAwesome6Free-Solid", size: 14))
                    .foregroundColor(.sgTextSubtle)
            }
            .padding(20)
            .background(Color.white)
            .clipShape(RoundedRectangle(cornerRadius: 22))
            .overlay(
                RoundedRectangle(cornerRadius: 22)
                    .strokeBorder(Color.sgSurfaceAlt, lineWidth: 1.5)
            )
        }
        .buttonStyle(.plain)
    }
}

#Preview {
    PickVehicleTypeView(onBack: {}, onPickType: { _ in })
}
