import SwiftUI
import Shared

struct AddReminderView: View {
    var onSaved: () -> Void = {}
    var vehicleId: String? = nil
    var trackedComponentId: String? = nil
    var fromContext: Bool = false

    @StateObject private var model = AddReminderModel()
    @State private var contextDismissed = false

    private var contextActive: Bool {
        !contextDismissed && (fromContext || vehicleId != nil || trackedComponentId != nil)
    }

    private var targetDate: Date {
        Date(timeIntervalSince1970: TimeInterval(model.state.targetDateMillis) / 1000.0)
    }

    var body: some View {
        Form {
            if contextActive {
                Section {
                    HStack(alignment: .top, spacing: 10) {
                        Text("\u{f0f3}")
                            .font(.custom("FontAwesome6Free-Solid", size: 16))
                            .foregroundColor(.sgPrimary)
                        VStack(alignment: .leading, spacing: 4) {
                            Text("Dari konteks")
                                .font(.custom("PlusJakartaSans-Bold", size: 13))
                                .foregroundColor(.sgPrimary)
                            Text("Reminder ini terhubung ke kendaraan/komponen yang dipilih.")
                                .font(.custom("PlusJakartaSans-Medium", size: 12))
                                .foregroundColor(.sgTextMuted)
                        }
                        Spacer()
                        Button("Tutup") { contextDismissed = true }
                            .font(.custom("PlusJakartaSans-Bold", size: 12))
                            .foregroundColor(.sgPrimary)
                    }
                }
            }

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
                .disabled(vehicleId != nil)
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
        }
        .navigationTitle("Buat Pengingat")
        .navigationBarTitleDisplayMode(.inline)
        .toolbar {
            ToolbarItem(placement: .confirmationAction) {
                Button("Simpan") { model.submit() }
                    .disabled(!model.state.canSave)
            }
        }
        .onAppear {
            model.preselect(vehicleId: vehicleId, trackedComponentId: trackedComponentId)
            model.onSaved = { _ in onSaved() }
        }
        .onDisappear { model.onSaved = nil }
    }
}
