import SwiftUI

struct OnboardingAddVehicleView: View {
    let vehicleType: String
    let onBack: () -> Void
    let onComplete: (VehicleFormState) -> Void

    @State private var formState: VehicleFormState

    init(vehicleType: String, onBack: @escaping () -> Void, onComplete: @escaping (VehicleFormState) -> Void) {
        self.vehicleType = vehicleType
        self.onBack = onBack
        self.onComplete = onComplete
        _formState = State(initialValue: VehicleFormState(type: vehicleType))
    }

    var body: some View {
        ZStack {
            Color.sgBgWarm.ignoresSafeArea()

            ScrollView {
                VStack(alignment: .leading, spacing: 0) {
                    OnboardingStepHeader(
                        stepLabel: "Langkah 3 dari 4",
                        title: vehicleType == "motor" ? "Detail motormu" : "Detail mobilmu",
                        subtitle: "Isi data kendaraan supaya pengingat servis lebih akurat.",
                        onBack: onBack
                    )

                    VStack(alignment: .leading, spacing: 0) {
                        Spacer().frame(height: 28)
                        VehicleForm(state: $formState, showTypeSelector: false)
                        Spacer().frame(height: 24)
                        AppInfoBanner(text: "Kamu bisa ubah data ini kapan saja nanti.")
                        Spacer().frame(height: 16)
                        AppButton(
                            title: "Lanjut",
                            action: { onComplete(formState) },
                            isEnabled: formState.isValid
                        )
                        Spacer().frame(height: 32)
                    }
                    .padding(.horizontal, 24)
                }
            }
        }
        .navigationBarHidden(true)
    }
}

#Preview {
    OnboardingAddVehicleView(vehicleType: "motor", onBack: {}, onComplete: { _ in })
}
