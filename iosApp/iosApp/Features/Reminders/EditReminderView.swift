import SwiftUI
import Shared

struct EditReminderView: View {
    let reminderId: String
    var onSave: () -> Void = {}
    var onDelete: () -> Void = {}

    @StateObject private var model = EditReminderModel()
    @State private var showDeleteConfirm = false

    private var targetDate: Date {
        Date(timeIntervalSince1970: TimeInterval(model.state.targetDateMillis) / 1000.0)
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

            Section("Judul") {
                TextField("Judul reminder", text: Binding(
                    get: { model.state.title },
                    set: { model.setTitle($0) }
                ))
            }

            Section("Picu pengingat") {
                Picker(
                    "Mode",
                    selection: Binding<ReminderTriggerMode>(
                        get: { model.state.triggerMode },
                        set: { model.setTriggerMode($0) }
                    )
                ) {
                    Text("Per KM").tag(ReminderTriggerMode.km)
                    Text("Per Tanggal").tag(ReminderTriggerMode.date)
                    Text("Keduanya").tag(ReminderTriggerMode.both)
                }
                .pickerStyle(.segmented)

                if model.state.triggerMode != ReminderTriggerMode.date {
                    HStack {
                        Text("Target KM")
                        Spacer()
                        TextField(
                            "km",
                            text: Binding(
                                get: { model.state.targetKm.map { String(Int64(truncating: $0)) } ?? "" },
                                set: { model.setTargetKm(Int64($0.filter(\.isNumber))) }
                            )
                        )
                        .multilineTextAlignment(.trailing)
                        .keyboardType(.numberPad)
                    }
                }

                if model.state.triggerMode != ReminderTriggerMode.km {
                    DatePicker(
                        "Tanggal",
                        selection: Binding(
                            get: { targetDate },
                            set: { model.setTargetDate(millis: Int64($0.timeIntervalSince1970 * 1000)) }
                        ),
                        displayedComponents: .date
                    )
                }

                Stepper(
                    "Notif \(model.state.notifyDaysBefore) hari sebelum",
                    value: Binding(
                        get: { Int(model.state.notifyDaysBefore) },
                        set: { model.setNotifyDaysBefore(Int32($0)) }
                    ),
                    in: 0...30
                )
            }

            Section("Catatan") {
                TextField("Catatan", text: Binding(
                    get: { model.state.note },
                    set: { model.setNote($0) }
                ), axis: .vertical)
                .lineLimit(2...5)
            }

            Section {
                Button("Hapus pengingat", role: .destructive) { showDeleteConfirm = true }
            }
        }
        .navigationTitle("Edit Pengingat")
        .navigationBarTitleDisplayMode(.inline)
        .toolbar {
            ToolbarItem(placement: .confirmationAction) {
                Button("Simpan") { model.save() }
                    .disabled(!model.state.canSave)
            }
        }
        .onAppear {
            model.load(reminderId: reminderId)
            model.onSaved = onSave
            model.onDeleted = onDelete
        }
        .onDisappear {
            model.onSaved = nil
            model.onDeleted = nil
        }
        .confirmationDialog(
            "Hapus pengingat?",
            isPresented: $showDeleteConfirm,
            titleVisibility: .visible
        ) {
            Button("Hapus", role: .destructive) { model.delete() }
            Button("Batal", role: .cancel) { }
        } message: {
            Text("Pengingat ini akan dihapus permanen.")
        }
    }
}
