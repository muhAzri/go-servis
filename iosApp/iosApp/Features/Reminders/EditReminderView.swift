import SwiftUI

// A2 — Edit Pengingat
// Mirrors AddReminderView; adds Status segmented (Aktif/Nonaktif) and DangerZoneCard.
struct EditReminderView: View {
    var onSave: () -> Void = {}
    var onDelete: () -> Void = {}

    @State private var selectedVehicle: VehicleOption = VehicleOptions.defaults[0]
    @State private var selectedService: String = "oli"
    @State private var trigger: TriggerMode = .km
    @State private var targetKm: String = "20420"
    @State private var targetDate: Date = Date()
    @State private var note: String = ""
    @State private var isActive: Bool = true

    @State private var showVehiclePicker: Bool = false
    @State private var showDatePicker: Bool = false
    @State private var showDeleteConfirm: Bool = false

    private static let dateFormatter: DateFormatter = {
        let df = DateFormatter()
        df.locale = Locale(identifier: "id_ID")
        df.dateFormat = "d MMM yyyy"
        return df
    }()

    var body: some View {
        ZStack {
            Color.sgBgWarm.ignoresSafeArea()

            VStack(spacing: 0) {
                ScrollView {
                    VStack(alignment: .leading, spacing: 0) {
                        ERFieldLabel(text: "Kendaraan")
                        VehiclePickerRow(selected: selectedVehicle) {
                            showVehiclePicker = true
                        }
                        .padding(.bottom, 18)

                        ERFieldLabel(text: "Jenis servis")
                        ERServiceTypeGrid(selected: $selectedService)
                            .padding(.bottom, 18)

                        ERFieldLabel(text: "Status")
                        ERStatusSegmented(isActive: $isActive)
                            .padding(.bottom, 18)

                        ERFieldLabel(text: "Picu pengingat")
                        ERTriggerSegmented(selected: $trigger)
                            .padding(.bottom, 14)

                        if trigger == .km || trigger == .both {
                            ERNumberInputField(
                                label: "Target KM",
                                value: $targetKm,
                                helper: "Interval pabrikan: 2.000 km · KM saat ini 18.420"
                            )
                        }

                        if trigger == .date || trigger == .both {
                            ERDateInputField(
                                label: "Tanggal",
                                valueText: Self.dateFormatter.string(from: targetDate),
                                helper: "Pengingat dimulai: 7 hari sebelum",
                                onTap: { showDatePicker = true }
                            )
                            .padding(.top, trigger == .both ? 12 : 0)
                        }

                        ERFieldLabel(text: "Catatan (opsional)")
                            .padding(.top, 18)
                        ERNoteField(value: $note)

                        EditReminderDangerZone(
                            onDelete: { showDeleteConfirm = true }
                        )
                        .padding(.top, 20)

                        Spacer().frame(height: 24)
                    }
                    .padding(.horizontal, 20)
                    .padding(.top, 8)
                }
            }
        }
        .navigationTitle("Edit Pengingat")
        .navigationBarTitleDisplayMode(.inline)
        .toolbar {
            ToolbarItem(placement: .topBarTrailing) {
                Button(action: onSave) {
                    Text("Simpan")
                        .font(.custom("PlusJakartaSans-Bold", size: 15))
                        .foregroundColor(.sgPrimary)
                }
            }
        }
        .sheet(isPresented: $showVehiclePicker) {
            VehiclePickerSheet(
                options: VehicleOptions.defaults,
                selectedId: selectedVehicle.id,
                onPick: { selectedVehicle = $0 }
            )
            .presentationDetents([.medium])
            .presentationDragIndicator(.visible)
        }
        .sheet(isPresented: $showDatePicker) {
            DatePickerSheet(date: $targetDate, onDone: { showDatePicker = false })
                .presentationDetents([.medium])
        }
        .confirmDialog(
            isPresented: $showDeleteConfirm,
            config: .init(
                title: "Hapus pengingat?",
                message: "Pengingat ini akan dihapus dan tidak muncul lagi.",
                confirmLabel: "Hapus pengingat",
                isDanger: true
            ),
            onConfirm: onDelete
        )
    }
}

// MARK: - Local subviews (prefixed to avoid conflicts with AddReminderView's private ones)

private struct ERFieldLabel: View {
    let text: String
    var body: some View {
        Text(text.uppercased())
            .font(.custom("PlusJakartaSans-ExtraBold", size: 11))
            .kerning(1)
            .foregroundColor(.sgTextMuted)
            .padding(.bottom, 8)
            .frame(maxWidth: .infinity, alignment: .leading)
    }
}

private struct ERStatusSegmented: View {
    @Binding var isActive: Bool

    var body: some View {
        HStack(spacing: 4) {
            segment(label: "Aktif", selected: isActive) { isActive = true }
            segment(label: "Nonaktif", selected: !isActive) { isActive = false }
        }
        .padding(4)
        .background(Color.sgSurfaceAlt)
        .clipShape(RoundedRectangle(cornerRadius: 12))
    }

    @ViewBuilder
    private func segment(label: String, selected: Bool, action: @escaping () -> Void) -> some View {
        Button(action: action) {
            Text(label)
                .font(.custom("PlusJakartaSans-Bold", size: 12))
                .foregroundColor(selected ? .sgTextPrimary : .sgTextMuted)
                .frame(maxWidth: .infinity)
                .padding(.vertical, 10)
                .background(selected ? Color.sgSurface : Color.clear)
                .clipShape(RoundedRectangle(cornerRadius: 9))
        }
        .buttonStyle(.plain)
    }
}

private struct ERServiceTypeGrid: View {
    @Binding var selected: String

    private let services: [(id: String, label: String, icon: String, color: Color)] = [
        ("oli", "Ganti Oli", "\u{f613}", Color(red: 0.91, green: 0.61, blue: 0.18)),
        ("filter", "Filter", "\u{f0b0}", Color(red: 0.48, green: 0.44, blue: 0.91)),
        ("rem", "Kampas Rem", "\u{f1ce}", Color(red: 0.18, green: 0.55, blue: 0.34)),
        ("ban", "Ban", "\u{f1cd}", Color(red: 0.25, green: 0.30, blue: 0.36)),
        ("aki", "Aki", "\u{f5df}", Color(red: 0.84, green: 0.27, blue: 0.23)),
        ("radiator", "Coolant", "\u{f2c9}", Color(red: 0.25, green: 0.69, blue: 0.84)),
    ]

    var body: some View {
        LazyVGrid(
            columns: [.init(.flexible(), spacing: 8), .init(.flexible(), spacing: 8), .init(.flexible(), spacing: 8)],
            spacing: 8
        ) {
            ForEach(services, id: \.id) { s in
                Button { selected = s.id } label: {
                    VStack(spacing: 6) {
                        Text(s.icon)
                            .font(.custom("FontAwesome6Free-Solid", size: 22))
                            .foregroundColor(selected == s.id ? .sgPrimary : s.color)
                        Text(s.label)
                            .font(.custom("PlusJakartaSans-SemiBold", size: 11))
                            .foregroundColor(.sgTextPrimary)
                    }
                    .frame(maxWidth: .infinity)
                    .padding(.vertical, 14)
                    .background(selected == s.id ? Color.sgPrimarySoft : Color.sgSurface)
                    .clipShape(RoundedRectangle(cornerRadius: 14))
                    .overlay(
                        RoundedRectangle(cornerRadius: 14)
                            .strokeBorder(
                                selected == s.id ? Color.sgPrimary : Color.sgBorder,
                                lineWidth: 1.5
                            )
                    )
                }
                .buttonStyle(.plain)
            }
        }
    }
}

private struct ERTriggerSegmented: View {
    @Binding var selected: TriggerMode

    private let options: [(mode: TriggerMode, label: String)] = [
        (.km, "Per KM"),
        (.date, "Per Tanggal"),
        (.both, "Keduanya"),
    ]

    var body: some View {
        HStack(spacing: 4) {
            ForEach(options, id: \.mode) { o in
                Button { selected = o.mode } label: {
                    Text(o.label)
                        .font(.custom("PlusJakartaSans-Bold", size: 12))
                        .foregroundColor(selected == o.mode ? .sgTextPrimary : .sgTextMuted)
                        .frame(maxWidth: .infinity)
                        .padding(.vertical, 10)
                        .background(selected == o.mode ? Color.sgSurface : Color.clear)
                        .clipShape(RoundedRectangle(cornerRadius: 9))
                }
                .buttonStyle(.plain)
            }
        }
        .padding(4)
        .background(Color.sgSurfaceAlt)
        .clipShape(RoundedRectangle(cornerRadius: 12))
    }
}

private struct ERNumberInputField: View {
    let label: String
    @Binding var value: String
    let helper: String

    @FocusState private var focused: Bool

    var body: some View {
        VStack(alignment: .leading, spacing: 6) {
            Text(label.uppercased())
                .font(.custom("PlusJakartaSans-ExtraBold", size: 11))
                .kerning(1)
                .foregroundColor(.sgTextMuted)
                .padding(.bottom, 2)
            HStack(spacing: 10) {
                Text("\u{f625}")
                    .font(.custom("FontAwesome6Free-Solid", size: 16))
                    .foregroundColor(.sgTextMuted)
                ZStack(alignment: .leading) {
                    if value.isEmpty {
                        Text("cth. 20420")
                            .font(.custom("PlusJakartaSans-Medium", size: 15))
                            .foregroundColor(.sgTextSubtle)
                            .allowsHitTesting(false)
                    }
                    TextField("", text: $value)
                        .focused($focused)
                        .font(.system(size: 15, weight: .semibold, design: .monospaced))
                        .foregroundColor(.sgTextPrimary)
                        .keyboardType(.numberPad)
                        .onChange(of: value) { _, newValue in
                            value = newValue.filter { $0.isNumber }
                        }
                }
                Spacer(minLength: 0)
                Text("km")
                    .font(.custom("PlusJakartaSans-SemiBold", size: 13))
                    .foregroundColor(.sgTextSubtle)
            }
            .padding(.horizontal, 16)
            .padding(.vertical, 16)
            .background(Color.sgSurface)
            .clipShape(RoundedRectangle(cornerRadius: 14))
            .overlay(
                RoundedRectangle(cornerRadius: 14)
                    .strokeBorder(focused ? Color.sgPrimary : Color.sgBorder, lineWidth: 1.5)
            )
            Text(helper)
                .font(.custom("PlusJakartaSans-Medium", size: 11))
                .foregroundColor(.sgTextSubtle)
                .padding(.leading, 4)
        }
    }
}

private struct ERDateInputField: View {
    let label: String
    let valueText: String
    let helper: String
    let onTap: () -> Void

    var body: some View {
        VStack(alignment: .leading, spacing: 6) {
            Text(label.uppercased())
                .font(.custom("PlusJakartaSans-ExtraBold", size: 11))
                .kerning(1)
                .foregroundColor(.sgTextMuted)
                .padding(.bottom, 2)
            Button(action: onTap) {
                HStack(spacing: 10) {
                    Text("\u{f783}")
                        .font(.custom("FontAwesome6Free-Solid", size: 16))
                        .foregroundColor(.sgTextMuted)
                    Text(valueText)
                        .font(.custom("PlusJakartaSans-SemiBold", size: 15))
                        .foregroundColor(.sgTextPrimary)
                    Spacer()
                    Text("\u{f078}")
                        .font(.custom("FontAwesome6Free-Solid", size: 12))
                        .foregroundColor(.sgTextSubtle)
                }
                .padding(.horizontal, 16)
                .padding(.vertical, 16)
                .frame(maxWidth: .infinity, alignment: .leading)
                .background(Color.sgSurface)
                .clipShape(RoundedRectangle(cornerRadius: 14))
                .overlay(
                    RoundedRectangle(cornerRadius: 14)
                        .strokeBorder(Color.sgBorder, lineWidth: 1.5)
                )
            }
            .buttonStyle(.plain)
            Text(helper)
                .font(.custom("PlusJakartaSans-Medium", size: 11))
                .foregroundColor(.sgTextSubtle)
                .padding(.leading, 4)
        }
    }
}

private struct ERNoteField: View {
    @Binding var value: String

    @FocusState private var focused: Bool

    var body: some View {
        ZStack(alignment: .topLeading) {
            if value.isEmpty {
                Text("Tambah catatan…")
                    .font(.custom("PlusJakartaSans-Medium", size: 14))
                    .foregroundColor(.sgTextSubtle)
                    .padding(.horizontal, 16)
                    .padding(.vertical, 14)
                    .allowsHitTesting(false)
            }
            TextField("", text: $value, axis: .vertical)
                .focused($focused)
                .font(.custom("PlusJakartaSans-Medium", size: 14))
                .foregroundColor(.sgTextPrimary)
                .lineLimit(3...6)
                .padding(.horizontal, 16)
                .padding(.vertical, 14)
        }
        .frame(maxWidth: .infinity, minHeight: 96, alignment: .topLeading)
        .background(Color.sgSurface)
        .clipShape(RoundedRectangle(cornerRadius: 14))
        .overlay(
            RoundedRectangle(cornerRadius: 14)
                .strokeBorder(focused ? Color.sgPrimary : Color.sgBorder, lineWidth: 1.5)
        )
    }
}

private struct EditReminderDangerZone: View {
    let onDelete: () -> Void

    var body: some View {
        VStack(alignment: .leading, spacing: 10) {
            Text("ZONA BERBAHAYA")
                .font(.custom("PlusJakartaSans-ExtraBold", size: 11))
                .kerning(1)
                .foregroundColor(.sgDanger)

            Text("Menghapus pengingat akan menghilangkan notifikasi mendatang. Riwayat servis tidak ikut terhapus.")
                .font(.custom("PlusJakartaSans-Medium", size: 13))
                .foregroundColor(.sgTextMuted)
                .lineSpacing(3)
                .fixedSize(horizontal: false, vertical: true)

            Button(action: onDelete) {
                HStack(spacing: 8) {
                    Text("\u{f2ed}")
                        .font(.custom("FontAwesome6Free-Solid", size: 14))
                    Text("Hapus pengingat")
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
    NavigationStack { EditReminderView() }
}
