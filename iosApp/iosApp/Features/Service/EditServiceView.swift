import SwiftUI
import Shared

struct EditServiceView: View {
    let recordId: String
    var onSaved: () -> Void = {}
    var onDeleted: () -> Void = {}

    @StateObject private var model = EditServiceModel()
    @State private var showDeleteConfirm = false

    private static let dateFormatter: DateFormatter = {
        let df = DateFormatter()
        df.locale = Locale(identifier: "id_ID")
        df.dateFormat = "d MMM yyyy"
        return df
    }()

    private var serviceDate: Date {
        Date(timeIntervalSince1970: TimeInterval(model.state.serviceDateMillis) / 1000.0)
    }

    var body: some View {
        Form {
            Section("Kendaraan") {
                Picker(
                    "Kendaraan",
                    selection: Binding(
                        get: { model.state.selectedVehicleId ?? "" },
                        set: { id in if !id.isEmpty { model.selectVehicle(id) } }
                    )
                ) {
                    ForEach(model.state.vehicles, id: \.id) { vehicle in
                        Text(vehicle.displayTitle).tag(vehicle.id)
                    }
                }
            }

            Section("Jenis servis") {
                Picker(
                    "Jenis",
                    selection: Binding(
                        get: { model.state.serviceType.key },
                        set: { model.setServiceType(ServiceType.companion.fromKey(key: $0)) }
                    )
                ) {
                    Text("Ganti Oli").tag("oli")
                    Text("Filter").tag("filter")
                    Text("Ban").tag("ban")
                    Text("Aki").tag("aki")
                    Text("Kampas Rem").tag("rem")
                    Text("Radiator").tag("radiator")
                    Text("Tune-up").tag("tune_up")
                    Text("Lainnya").tag("other")
                }
            }

            Section("Detail") {
                DatePicker(
                    "Tanggal",
                    selection: Binding(
                        get: { serviceDate },
                        set: { model.setServiceDate(millis: Int64($0.timeIntervalSince1970 * 1000)) }
                    ),
                    displayedComponents: .date
                )

                HStack {
                    Text("KM")
                    Spacer()
                    TextField(
                        "km",
                        text: Binding(
                            get: { model.state.odometerKm.map { String(Int64(truncating: $0)) } ?? "" },
                            set: { model.setOdometer(km: Int64($0.filter(\.isNumber))) }
                        )
                    )
                    .multilineTextAlignment(.trailing)
                    .keyboardType(.numberPad)
                }

                HStack {
                    Text("Bengkel")
                    Spacer()
                    TextField("Nama bengkel", text: Binding(
                        get: { model.state.workshop },
                        set: { model.setWorkshop($0) }
                    ))
                    .multilineTextAlignment(.trailing)
                }

                HStack {
                    Text("Biaya")
                    Spacer()
                    TextField("Rupiah", text: Binding(
                        get: { model.state.costIdr > 0 ? String(model.state.costIdr) : "" },
                        set: { model.setCost(Int64($0.filter(\.isNumber)) ?? 0) }
                    ))
                    .multilineTextAlignment(.trailing)
                    .keyboardType(.numberPad)
                }
            }

            Section("Catatan") {
                TextField("Catatan", text: Binding(
                    get: { model.state.note },
                    set: { model.setNote($0) }
                ), axis: .vertical)
                .lineLimit(2...5)
            }

            Section {
                Button("Hapus servis", role: .destructive) {
                    showDeleteConfirm = true
                }
            }
        }
        .navigationTitle("Edit Servis")
        .navigationBarTitleDisplayMode(.inline)
        .toolbar {
            ToolbarItem(placement: .confirmationAction) {
                Button("Simpan") { model.save() }
                    .disabled(!model.state.canSave)
            }
        }
        .onAppear {
            model.load(recordId: recordId)
            model.onSaved = onSaved
            model.onDeleted = onDeleted
        }
        .onDisappear {
            model.onSaved = nil
            model.onDeleted = nil
        }
        .confirmationDialog(
            "Hapus servis?",
            isPresented: $showDeleteConfirm,
            titleVisibility: .visible
        ) {
            Button("Hapus", role: .destructive) { model.delete() }
            Button("Batal", role: .cancel) { }
        } message: {
            Text("Catatan servis ini akan dihapus permanen.")
        }
    }
}
