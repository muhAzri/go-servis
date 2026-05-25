import SwiftUI
import Shared

struct AddVehicleView: View {
    let onBack: () -> Void
    let onSaved: () -> Void

    @StateObject private var model = AddVehicleModel()
    @State private var formState = VehicleFormState()

    var body: some View {
        ZStack {
            Color.sgBgWarm.ignoresSafeArea()

            VStack(spacing: 0) {
                HStack(spacing: 12) {
                    AppBackButton(action: onBack)

                    Text("Tambah Kendaraan")
                        .font(.custom("PlusJakartaSans-Bold", size: 18))
                        .foregroundColor(.sgTextPrimary)

                    Spacer()
                }
                .padding(.horizontal, 16)
                .padding(.vertical, 12)

                ScrollView {
                    VehicleForm(state: $formState, showTypeSelector: true)
                        .padding(.horizontal, 20)
                        .padding(.top, 8)
                        .padding(.bottom, 20)
                }

                VStack(spacing: 0) {
                    Divider()
                    AppButton(
                        title: "Simpan Kendaraan",
                        action: { model.submit(input: formState.toOnboardingInput()) },
                        isEnabled: formState.isValid && !model.state.isSaving
                    )
                    .padding(.horizontal, 16)
                    .padding(.top, 10)
                    .padding(.bottom, 24)
                }
                .background(Color.white)
            }
        }
        .navigationBarHidden(true)
        .onAppear { model.onSaved = onSaved }
        .onDisappear { model.onSaved = nil }
    }
}

#Preview {
    AddVehicleView(onBack: {}, onSaved: {})
}
