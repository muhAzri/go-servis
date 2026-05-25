import SwiftUI
import Shared

// A1 — Edit Kendaraan
// Pre-filled VehicleForm (with embedded sub-type picker) + DangerZoneCard.
struct EditVehicleView: View {
    let vehicleId: String?
    var onSaved: () -> Void = {}
    var onDeleted: () -> Void = {}

    @StateObject private var model = EditVehicleModel()
    @State private var formState: VehicleFormState = VehicleFormState()
    @State private var hydratedVehicleId: String? = nil
    @State private var showDeleteConfirm: Bool = false

    init(
        vehicleId: String? = nil,
        onSaved: @escaping () -> Void = {},
        onDeleted: @escaping () -> Void = {}
    ) {
        self.vehicleId = vehicleId
        self.onSaved = onSaved
        self.onDeleted = onDeleted
    }

    var body: some View {
        ZStack {
            Color.sgBgWarm.ignoresSafeArea()

            VStack(spacing: 0) {
                ScrollView {
                    VStack(alignment: .leading, spacing: 18) {
                        VehicleForm(state: $formState, showTypeSelector: true)

                        EditVehicleDangerZone(
                            onDelete: { showDeleteConfirm = true }
                        )
                        .padding(.top, 8)
                    }
                    .padding(.horizontal, 20)
                    .padding(.top, 8)
                    .padding(.bottom, 24)
                }

                VStack(spacing: 0) {
                    Divider()
                    AppButton(
                        title: "Simpan Perubahan",
                        action: { model.save(input: formState.toOnboardingInput()) },
                        isEnabled: formState.isValid && !model.state.isLoading && !model.state.isSaving && model.state.vehicle != nil
                    )
                    .padding(.horizontal, 16)
                    .padding(.top, 10)
                    .padding(.bottom, 24)
                }
                .background(Color.white)
            }
        }
        .navigationTitle("Edit Kendaraan")
        .navigationBarTitleDisplayMode(.inline)
        .confirmDialog(
            isPresented: $showDeleteConfirm,
            config: .init(
                title: "Hapus kendaraan?",
                message: "Semua data servis & pengingat untuk \(formState.nama.isEmpty ? "kendaraan ini" : formState.nama) akan ikut terhapus permanen.",
                confirmLabel: "Hapus kendaraan",
                isDanger: true
            ),
            onConfirm: { model.delete() }
        )
        .onAppear {
            model.onSaved = onSaved
            model.onDeleted = onDeleted
            model.load(vehicleId: vehicleId)
        }
        .onDisappear {
            model.onSaved = nil
            model.onDeleted = nil
        }
        .onReceive(model.$state) { newState in
            if let vehicle = newState.vehicle, vehicle.id != hydratedVehicleId {
                hydratedVehicleId = vehicle.id
                formState = VehicleFormState(vehicle: vehicle)
            }
        }
    }
}

private struct EditVehicleDangerZone: View {
    let onDelete: () -> Void

    var body: some View {
        VStack(alignment: .leading, spacing: 10) {
            Text("ZONA BERBAHAYA")
                .font(.custom("PlusJakartaSans-ExtraBold", size: 11))
                .kerning(1)
                .foregroundColor(.sgDanger)

            Text("Menghapus kendaraan akan menghapus semua riwayat servis dan pengingat terkait. Aksi ini tidak bisa dibatalkan.")
                .font(.custom("PlusJakartaSans-Medium", size: 13))
                .foregroundColor(.sgTextMuted)
                .lineSpacing(3)
                .fixedSize(horizontal: false, vertical: true)

            Button(action: onDelete) {
                HStack(spacing: 8) {
                    Text("\u{f2ed}")
                        .font(.custom("FontAwesome6Free-Solid", size: 14))
                    Text("Hapus kendaraan")
                        .font(.custom("PlusJakartaSans-Bold", size: 14))
                }
                .foregroundColor(.sgDanger)
                .frame(maxWidth: .infinity)
                .frame(height: 48)
                .background(Color.sgSurface)
                .clipShape(RoundedRectangle(cornerRadius: 14))
                .overlay(
                    RoundedRectangle(cornerRadius: 14)
                        .strokeBorder(Color.sgDanger.opacity(0.4), lineWidth: 1.5)
                )
            }
            .buttonStyle(.plain)
            .padding(.top, 4)
        }
        .padding(16)
        .frame(maxWidth: .infinity, alignment: .leading)
        .background(Color.sgDangerSoft.opacity(0.5))
        .clipShape(RoundedRectangle(cornerRadius: 16))
        .overlay(
            RoundedRectangle(cornerRadius: 16)
                .strokeBorder(Color.sgDanger.opacity(0.2), lineWidth: 1)
        )
    }
}

#Preview {
    NavigationStack { EditVehicleView() }
}
