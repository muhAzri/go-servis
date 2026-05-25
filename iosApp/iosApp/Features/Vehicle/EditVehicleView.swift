import SwiftUI
import Shared

// A1 — Edit Kendaraan
// Pre-filled VehicleForm + sub-type picker (ActionSheetView in `.sheet`) + DangerZoneCard.
struct EditVehicleView: View {
    let vehicleId: String?
    var onBack: () -> Void = {}
    var onSaved: () -> Void = {}
    var onDeleted: () -> Void = {}

    @StateObject private var model = EditVehicleModel()
    @State private var formState: VehicleFormState = VehicleFormState()
    @State private var subtype: String = "matic"
    @State private var hydratedVehicleId: String? = nil
    @State private var showSubtypePicker: Bool = false
    @State private var showDeleteConfirm: Bool = false

    init(
        vehicleId: String? = nil,
        onBack: @escaping () -> Void = {},
        onSaved: @escaping () -> Void = {},
        onDeleted: @escaping () -> Void = {}
    ) {
        self.vehicleId = vehicleId
        self.onBack = onBack
        self.onSaved = onSaved
        self.onDeleted = onDeleted
    }

    private var subtypeLabel: String {
        VehicleSubtypes.label(for: formState.type, id: subtype)
    }

    private var subtypeOptions: [ActionSheetOption] {
        VehicleSubtypes.list(for: formState.type).map { sub in
            ActionSheetOption(
                id: sub.id,
                iconUnicode: formState.type == "mobil" ? "\u{f1b9}" : "\u{f21c}",
                label: sub.label,
                subtitle: sub.desc,
                value: sub.id
            )
        }
    }

    var body: some View {
        ZStack {
            Color.sgBgWarm.ignoresSafeArea()

            VStack(spacing: 0) {
                HStack(spacing: 12) {
                    AppBackButton(action: onBack)
                    Text("Edit Kendaraan")
                        .font(.custom("PlusJakartaSans-Bold", size: 18))
                        .foregroundColor(.sgTextPrimary)
                    Spacer()
                }
                .padding(.horizontal, 16)
                .padding(.vertical, 12)

                ScrollView {
                    VStack(alignment: .leading, spacing: 18) {
                        VehicleForm(state: $formState, showTypeSelector: true)

                        // Sub-type picker row
                        VStack(alignment: .leading, spacing: 6) {
                            Text("Sub-tipe")
                                .font(.custom("PlusJakartaSans-SemiBold", size: 13))
                                .foregroundColor(.sgTextMuted)
                            Button {
                                showSubtypePicker = true
                            } label: {
                                HStack(spacing: 10) {
                                    Text(formState.type == "mobil" ? "\u{f1b9}" : "\u{f21c}")
                                        .font(.custom("FontAwesome6Free-Solid", size: 16))
                                        .foregroundColor(.sgTextMuted)
                                    Text(subtypeLabel)
                                        .font(.custom("PlusJakartaSans-SemiBold", size: 15))
                                        .foregroundColor(.sgTextPrimary)
                                    Spacer()
                                    Text("\u{f078}")
                                        .font(.custom("FontAwesome6Free-Solid", size: 12))
                                        .foregroundColor(.sgTextSubtle)
                                }
                                .padding(.horizontal, 16)
                                .padding(.vertical, 14)
                                .background(Color.white)
                                .clipShape(RoundedRectangle(cornerRadius: 14))
                                .overlay(
                                    RoundedRectangle(cornerRadius: 14)
                                        .strokeBorder(Color.black.opacity(0.08), lineWidth: 1.5)
                                )
                            }
                            .buttonStyle(.plain)
                        }

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
        .navigationBarHidden(true)
        .sheet(isPresented: $showSubtypePicker) {
            ActionSheetView(
                title: "Sub-tipe kendaraan",
                subtitle: "Pilih yang paling mendekati",
                options: subtypeOptions,
                selectionMode: .radio,
                selectedValue: subtype,
                onSelect: { value in
                    subtype = value
                }
            )
            .presentationDetents([.medium, .large])
            .presentationDragIndicator(.visible)
        }
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
    EditVehicleView()
}
