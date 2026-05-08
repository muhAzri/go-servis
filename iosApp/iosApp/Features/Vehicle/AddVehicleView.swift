import SwiftUI

struct AddVehicleView: View {
    let onBack: () -> Void
    let onSaved: (VehicleFormState) -> Void

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
                        action: { onSaved(formState) },
                        isEnabled: formState.isValid
                    )
                    .padding(.horizontal, 16)
                    .padding(.top, 10)
                    .padding(.bottom, 24)
                }
                .background(Color.white)
            }
        }
        .navigationBarHidden(true)
    }
}

#Preview {
    AddVehicleView(onBack: {}, onSaved: { _ in })
}
