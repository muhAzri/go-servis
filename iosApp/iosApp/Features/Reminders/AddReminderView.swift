import SwiftUI
import Shared

struct AddReminderView: View {
    var onSaved: () -> Void = {}
    var vehicleId: String? = nil
    var trackedComponentId: String? = nil
    var fromContext: Bool = false

    @StateObject private var model = AddReminderModel()
    @State private var contextDismissed = false
    @State private var showComponentPicker = false

    private var contextActive: Bool {
        !contextDismissed && (fromContext || vehicleId != nil || trackedComponentId != nil)
    }

    private var targetDate: Date {
        Date(timeIntervalSince1970: TimeInterval(model.state.targetDateMillis) / 1000.0)
    }

    private var componentOptions: [AddReminderViewModelComponentOption] {
        model.state.availableComponents as [AddReminderViewModelComponentOption]
    }

    private var selectedComponent: AddReminderViewModelComponentOption? {
        let id = model.state.selectedComponentTrackedId
        return componentOptions.first { $0.trackedId == id }
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

            Section("Jenis pengingat") {
                Picker(
                    "Mode",
                    selection: Binding<AddReminderViewModelMode>(
                        get: { model.state.mode },
                        set: { model.setMode($0) }
                    )
                ) {
                    Text("Komponen").tag(AddReminderViewModelMode.komponen)
                    Text("Manual").tag(AddReminderViewModelMode.manual)
                }
                .pickerStyle(.segmented)
            }

            switch model.state.mode {
            case AddReminderViewModelMode.komponen:
                Section("Komponen") {
                    if model.state.hasTrackedComponents {
                        Button(action: { showComponentPicker = true }) {
                            HStack {
                                VStack(alignment: .leading, spacing: 2) {
                                    Text(selectedComponent?.label ?? "Pilih komponen…")
                                        .font(.custom("PlusJakartaSans-SemiBold", size: 15))
                                        .foregroundColor(selectedComponent != nil ? .sgTextPrimary : .sgTextSubtle)
                                    if let opt = selectedComponent {
                                        Text("Interval: \(opt.intervalLabel)")
                                            .font(.custom("PlusJakartaSans-Medium", size: 12))
                                            .foregroundColor(.sgTextMuted)
                                    }
                                }
                                Spacer()
                                Text("Ubah")
                                    .font(.custom("PlusJakartaSans-Bold", size: 13))
                                    .foregroundColor(.sgPrimary)
                            }
                        }
                        .buttonStyle(.plain)
                    } else {
                        Text("Belum ada komponen dipantau untuk kendaraan ini. Tambah dulu di Detail Kendaraan → Komponen, atau pakai mode Manual.")
                            .font(.custom("PlusJakartaSans-Medium", size: 12))
                            .foregroundColor(.sgTextMuted)
                    }
                }
            case AddReminderViewModelMode.manual:
                Section("Judul pengingat") {
                    TextField("cth. Ganti spion, perpanjang STNK", text: Binding(
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
                }
            default:
                EmptyView()
            }

            let showKm = model.state.mode == AddReminderViewModelMode.komponen
                || model.state.triggerMode != ReminderTriggerMode.date
            let showDate = model.state.mode == AddReminderViewModelMode.komponen
                || model.state.triggerMode != ReminderTriggerMode.km

            Section("Target") {
                if showKm {
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

                if showDate {
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
        .sheet(isPresented: $showComponentPicker) {
            NavigationStack {
                List {
                    ForEach(componentOptions, id: \.trackedId) { opt in
                        Button(action: {
                            model.selectComponent(opt.trackedId)
                            showComponentPicker = false
                        }) {
                            HStack {
                                VStack(alignment: .leading, spacing: 2) {
                                    Text(opt.label)
                                        .font(.custom("PlusJakartaSans-SemiBold", size: 15))
                                        .foregroundColor(.sgTextPrimary)
                                    Text(opt.intervalLabel)
                                        .font(.custom("PlusJakartaSans-Medium", size: 12))
                                        .foregroundColor(.sgTextMuted)
                                }
                                Spacer()
                                if opt.trackedId == model.state.selectedComponentTrackedId {
                                    Text("\u{f00c}")
                                        .font(.custom("FontAwesome6Free-Solid", size: 13))
                                        .foregroundColor(.sgPrimary)
                                }
                            }
                        }
                        .buttonStyle(.plain)
                    }
                }
                .navigationTitle("Pilih komponen")
                .navigationBarTitleDisplayMode(.inline)
                .toolbar {
                    ToolbarItem(placement: .cancellationAction) {
                        Button("Batal") { showComponentPicker = false }
                    }
                }
            }
            .presentationDetents([.medium, .large])
        }
        .onAppear {
            model.preselect(vehicleId: vehicleId, trackedComponentId: trackedComponentId)
            model.onSaved = { _ in onSaved() }
        }
        .onDisappear { model.onSaved = nil }
    }
}
