import SwiftUI

enum AddServiceContext { case manual, fromReminder, fromComponent }

private struct TrackedComponent: Identifiable, Hashable {
    let id: String
    let label: String
    let subtitle: String
    let iconUnicode: String
    let color: Color
}

private let trackedComponents: [TrackedComponent] = [
    .init(id: "oli_mesin",    label: "Oli mesin",     subtitle: "2.000 km", iconUnicode: "\u{f613}", color: Color(red: 0.91, green: 0.61, blue: 0.18)),
    .init(id: "filter_oli",   label: "Filter oli",    subtitle: "4.000 km", iconUnicode: "\u{f0b0}", color: Color(red: 0.48, green: 0.44, blue: 0.91)),
    .init(id: "filter_udara", label: "Filter udara",  subtitle: "8.000 km", iconUnicode: "\u{f0b0}", color: Color(red: 0.48, green: 0.44, blue: 0.91)),
    .init(id: "busi",         label: "Busi & tune-up",subtitle: "6.000 km", iconUnicode: "\u{f0e7}", color: Color(red: 0.91, green: 0.71, blue: 0.18)),
    .init(id: "aki",          label: "Aki",            subtitle: "1–2 tahun", iconUnicode: "\u{f5df}", color: Color(red: 0.84, green: 0.27, blue: 0.23)),
    .init(id: "kampas_rem",   label: "Kampas rem",     subtitle: "8.000 km", iconUnicode: "\u{f1ce}", color: .sgPrimary),
    .init(id: "ban",          label: "Ban",            subtitle: "10.000 km", iconUnicode: "\u{f1cd}", color: Color(red: 0.25, green: 0.30, blue: 0.36)),
    .init(id: "radiator",     label: "Radiator",       subtitle: "tahunan",   iconUnicode: "\u{f2c9}", color: Color(red: 0.25, green: 0.69, blue: 0.84)),
]

struct AddServiceView: View {
    var onSaved: () -> Void = {}
    var context: AddServiceContext = .manual

    @State private var selectedVehicle: VehicleOption = VehicleOptions.defaults[0]
    @State private var selectedService: String = "oli"
    @State private var serviceDate: Date = Date()
    @State private var kmText: String = ""
    @State private var workshop: String = ""
    @State private var costText: String = ""
    @State private var note: String = ""
    @State private var selectedComponents: Set<String> = ["oli_mesin", "filter_oli"]
    @State private var contextDismissed: Bool = false

    @State private var showVehiclePicker: Bool = false
    @State private var showDatePicker: Bool = false
    @State private var showComponentPicker: Bool = false

    private var contextActive: Bool {
        !contextDismissed && context != .manual
    }

    private static let dateFormatter: DateFormatter = {
        let df = DateFormatter()
        df.locale = Locale(identifier: "id_ID")
        df.dateFormat = "d MMM yyyy"
        return df
    }()

    var body: some View {
        VStack(spacing: 0) {
            ScrollView {
                VStack(alignment: .leading, spacing: 0) {
                    if contextActive {
                        ContextBanner(
                            title: context == .fromReminder ? "Dari reminder" : "Untuk komponen: Oli mesin",
                            body: context == .fromReminder
                                ? "Ganti Oli Mesin · Beat Hitam — field di bawah sudah diisi otomatis."
                                : "Servis ini akan tercatat sebagai update komponen yang dipantau.",
                            iconUnicode: context == .fromReminder ? "\u{f0f3}" : "\u{f0ad}",
                            tone: .info,
                            onDismiss: { contextDismissed = true }
                        )
                        .padding(.bottom, 14)
                    }

                    FieldLabel(text: "Kendaraan")
                    VehiclePickerRow(selected: selectedVehicle, locked: contextActive) {
                        if !contextActive { showVehiclePicker = true }
                    }
                    .padding(.bottom, 18)

                    FieldLabel(text: "Jenis servis")
                    ServiceTypeGrid(selected: $selectedService)
                        .padding(.bottom, 18)

                    FieldLabel(text: "Komponen yang diservis · \(selectedComponents.count)")
                    ComponentChipsRow(
                        selectedIds: selectedComponents,
                        allItems: trackedComponents,
                        onRemove: { id in selectedComponents.remove(id) },
                        onAdd: { showComponentPicker = true }
                    )
                    Text("Daftar diambil dari komponen yang kamu pantau. Tambah di Detail Kendaraan → Komponen.")
                        .font(.custom("PlusJakartaSans-Medium", size: 11))
                        .foregroundColor(.sgTextSubtle)
                        .lineSpacing(2)
                        .padding(.top, 6)
                        .padding(.bottom, 18)

                    DateRowField(
                        label: "Tanggal servis",
                        valueText: Self.dateFormatter.string(from: serviceDate),
                        onTap: { showDatePicker = true }
                    )

                    EditableRowField(
                        label: "KM saat servis",
                        text: $kmText,
                        placeholder: "cth. 18420",
                        iconUnicode: "\u{f625}",
                        keyboardType: .numberPad,
                        monospaced: true
                    )

                    EditableRowField(
                        label: "Bengkel",
                        text: $workshop,
                        placeholder: "cth. AHASS Kebon Jeruk",
                        iconUnicode: "\u{f3c5}"
                    )

                    EditableRowField(
                        label: "Biaya",
                        text: $costText,
                        placeholder: "cth. 65000",
                        iconUnicode: nil,
                        keyboardType: .numberPad,
                        monospaced: true,
                        prefix: "Rp "
                    )

                    EditableRowField(
                        label: "Catatan",
                        text: $note,
                        placeholder: "cth. AHM MPX2 0.8L",
                        iconUnicode: nil,
                        axis: .vertical
                    )

                    AutoReminderInfoCard()
                        .padding(.top, 4)
                }
                .padding(.horizontal, 20)
                .padding(.vertical, 8)
                .padding(.bottom, 20)
            }

            AddServiceSaveBar(onSave: onSaved)
        }
        .background(Color.sgBgWarm)
        .navigationTitle("Catat Servis")
        .navigationBarTitleDisplayMode(.inline)
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
            DatePickerSheet(date: $serviceDate, onDone: { showDatePicker = false })
                .presentationDetents([.medium])
        }
        .sheet(isPresented: $showComponentPicker) {
            ComponentPickerSheet(
                allItems: trackedComponents,
                initiallySelected: selectedComponents,
                onApply: { picked in
                    selectedComponents = picked
                    showComponentPicker = false
                },
                onDismiss: { showComponentPicker = false }
            )
            .presentationDetents([.large])
        }
    }
}

private struct ComponentChipsRow: View {
    let selectedIds: Set<String>
    let allItems: [TrackedComponent]
    let onRemove: (String) -> Void
    let onAdd: () -> Void

    private var pickedItems: [TrackedComponent] {
        allItems.filter { selectedIds.contains($0.id) }
    }

    var body: some View {
        VStack(alignment: .leading, spacing: 8) {
            if !pickedItems.isEmpty {
                FlowLayout(spacing: 6) {
                    ForEach(pickedItems) { item in
                        HStack(spacing: 6) {
                            Text(item.iconUnicode)
                                .font(.custom("FontAwesome6Free-Solid", size: 11))
                                .foregroundColor(.sgPrimary)
                            Text(item.label)
                                .font(.custom("PlusJakartaSans-Bold", size: 11))
                                .foregroundColor(.sgPrimary)
                            Button(action: { onRemove(item.id) }) {
                                Text("\u{f00d}")
                                    .font(.custom("FontAwesome6Free-Solid", size: 9))
                                    .foregroundColor(.sgPrimary)
                                    .frame(width: 16, height: 16)
                                    .background(Color.black.opacity(0.06))
                                    .clipShape(Circle())
                            }
                            .buttonStyle(.plain)
                        }
                        .padding(.leading, 10)
                        .padding(.trailing, 4)
                        .padding(.vertical, 4)
                        .background(Color.sgPrimarySoft)
                        .clipShape(Capsule())
                    }
                }
            }
            Button(action: onAdd) {
                HStack(spacing: 8) {
                    Text("\u{2b}")
                        .font(.custom("FontAwesome6Free-Solid", size: 12))
                        .foregroundColor(.sgPrimary)
                    Text("Pilih komponen (\(selectedIds.count) / \(allItems.count) dipantau)")
                        .font(.custom("PlusJakartaSans-Bold", size: 13))
                        .foregroundColor(.sgPrimary)
                }
                .padding(.horizontal, 4)
                .padding(.vertical, 6)
            }
            .buttonStyle(.plain)
        }
        .padding(10)
        .frame(maxWidth: .infinity, alignment: .leading)
        .background(Color.sgSurface)
        .clipShape(RoundedRectangle(cornerRadius: 14))
        .overlay(
            RoundedRectangle(cornerRadius: 14)
                .strokeBorder(Color.sgBorder, lineWidth: 1.5)
        )
    }
}

private struct ComponentPickerSheet: View {
    let allItems: [TrackedComponent]
    let initiallySelected: Set<String>
    let onApply: (Set<String>) -> Void
    let onDismiss: () -> Void

    @State private var draft: Set<String> = []
    @State private var query: String = ""

    private var filtered: [TrackedComponent] {
        query.isEmpty ? allItems : allItems.filter { $0.label.localizedCaseInsensitiveContains(query) }
    }

    var body: some View {
        VStack(alignment: .leading, spacing: 0) {
            Text("Pilih komponen yang diservis")
                .font(.custom("PlusJakartaSans-ExtraBold", size: 18))
                .foregroundColor(.sgTextPrimary)
                .padding(.horizontal, 20)
                .padding(.top, 18)
            Text("\(draft.count) terpilih dari \(allItems.count) dipantau")
                .font(.custom("PlusJakartaSans-Medium", size: 12))
                .foregroundColor(.sgTextMuted)
                .padding(.horizontal, 20)
                .padding(.top, 2)
                .padding(.bottom, 12)

            StickySearchHeader(placeholder: "Cari komponen…", text: $query)

            ScrollView {
                VStack(spacing: 0) {
                    ForEach(filtered) { item in
                        Button(action: {
                            if draft.contains(item.id) { draft.remove(item.id) } else { draft.insert(item.id) }
                        }) {
                            HStack(spacing: 12) {
                                ZStack {
                                    RoundedRectangle(cornerRadius: 6)
                                        .strokeBorder(draft.contains(item.id) ? Color.sgPrimary : Color.sgBorder, lineWidth: 2)
                                        .background(
                                            RoundedRectangle(cornerRadius: 6)
                                                .fill(draft.contains(item.id) ? Color.sgPrimary : Color.clear)
                                        )
                                        .frame(width: 22, height: 22)
                                    if draft.contains(item.id) {
                                        Text("\u{f00c}")
                                            .font(.custom("FontAwesome6Free-Solid", size: 12))
                                            .foregroundColor(.white)
                                    }
                                }
                                Text(item.iconUnicode)
                                    .font(.custom("FontAwesome6Free-Solid", size: 18))
                                    .foregroundColor(item.color)
                                VStack(alignment: .leading, spacing: 2) {
                                    Text(item.label)
                                        .font(.custom("PlusJakartaSans-SemiBold", size: 14))
                                        .foregroundColor(.sgTextPrimary)
                                    Text(item.subtitle)
                                        .font(.system(size: 11, weight: .regular, design: .monospaced))
                                        .foregroundColor(.sgTextSubtle)
                                }
                                Spacer()
                            }
                            .padding(.horizontal, 20)
                            .padding(.vertical, 10)
                        }
                        .buttonStyle(.plain)
                    }
                }
                .padding(.bottom, 12)
            }

            Button(action: { onApply(draft) }) {
                Text("Pilih (\(draft.count))")
                    .font(.custom("PlusJakartaSans-Bold", size: 15))
                    .foregroundColor(.white)
                    .frame(maxWidth: .infinity)
                    .frame(height: 50)
                    .background(Color.sgPrimary)
                    .clipShape(RoundedRectangle(cornerRadius: 14))
            }
            .buttonStyle(.plain)
            .padding(.horizontal, 16)
            .padding(.bottom, 24)
        }
        .background(Color.sgSurface)
        .onAppear { draft = initiallySelected }
    }
}

private struct FieldLabel: View {
    let text: String
    var body: some View {
        Text(text)
            .font(.custom("PlusJakartaSans-Bold", size: 12))
            .foregroundColor(.sgTextMuted)
            .padding(.bottom, 8)
    }
}

private struct ServiceTypeGrid: View {
    @Binding var selected: String

    private let services: [(id: String, label: String, icon: String, color: Color)] = [
        ("oli", "Ganti Oli\nMesin", "\u{f613}", Color(red: 0.91, green: 0.61, blue: 0.18)),
        ("filter", "Filter Oli\n& Udara", "\u{f0b0}", Color(red: 0.48, green: 0.44, blue: 0.91)),
        ("ban", "Rotasi/\nGanti Ban", "\u{f1cd}", Color(red: 0.25, green: 0.30, blue: 0.36)),
        ("aki", "Aki", "\u{f5df}", Color(red: 0.84, green: 0.27, blue: 0.23)),
        ("rem", "Kampas\nRem", "\u{f1ce}", Color(red: 0.18, green: 0.55, blue: 0.34)),
        ("radiator", "Radiator/\nCoolant", "\u{f2c9}", Color(red: 0.25, green: 0.69, blue: 0.84)),
    ]

    var body: some View {
        LazyVGrid(columns: [.init(.flexible(), spacing: 8), .init(.flexible(), spacing: 8), .init(.flexible(), spacing: 8)], spacing: 8) {
            ForEach(services, id: \.id) { s in
                Button { selected = s.id } label: {
                    VStack(spacing: 6) {
                        Text(s.icon)
                            .font(.custom("FontAwesome6Free-Solid", size: 22))
                            .foregroundColor(selected == s.id ? .sgPrimary : s.color)
                        Text(s.label)
                            .font(.custom("PlusJakartaSans-SemiBold", size: 10))
                            .foregroundColor(.sgTextPrimary)
                            .multilineTextAlignment(.center)
                            .lineLimit(2)
                    }
                    .frame(maxWidth: .infinity)
                    .padding(.vertical, 12)
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

private struct EditableRowField: View {
    let label: String
    @Binding var text: String
    let placeholder: String
    let iconUnicode: String?
    var keyboardType: UIKeyboardType = .default
    var monospaced: Bool = false
    var prefix: String = ""
    var axis: Axis = .horizontal

    @FocusState private var focused: Bool

    var body: some View {
        VStack(alignment: .leading, spacing: 6) {
            Text(label)
                .font(.custom("PlusJakartaSans-Bold", size: 12))
                .foregroundColor(.sgTextMuted)
            HStack(alignment: axis == .vertical ? .top : .center, spacing: 10) {
                if let iconUnicode {
                    Text(iconUnicode)
                        .font(.custom("FontAwesome6Free-Solid", size: 16))
                        .foregroundColor(.sgTextMuted)
                }
                if !prefix.isEmpty && !text.isEmpty {
                    valueText(prefix)
                }
                ZStack(alignment: .topLeading) {
                    if text.isEmpty {
                        Text(placeholder)
                            .font(.custom("PlusJakartaSans-Medium", size: 15))
                            .foregroundColor(.sgTextSubtle)
                            .allowsHitTesting(false)
                    }
                    if axis == .vertical {
                        TextField("", text: $text, axis: .vertical)
                            .focused($focused)
                            .font(monospaced
                                  ? .system(size: 15, weight: .semibold, design: .monospaced)
                                  : .custom("PlusJakartaSans-SemiBold", size: 15))
                            .foregroundColor(.sgTextPrimary)
                            .keyboardType(keyboardType)
                            .lineLimit(2...4)
                    } else {
                        TextField("", text: $text)
                            .focused($focused)
                            .font(monospaced
                                  ? .system(size: 15, weight: .semibold, design: .monospaced)
                                  : .custom("PlusJakartaSans-SemiBold", size: 15))
                            .foregroundColor(.sgTextPrimary)
                            .keyboardType(keyboardType)
                    }
                }
                Spacer(minLength: 0)
            }
            .padding(.horizontal, 16)
            .padding(.vertical, axis == .vertical ? 14 : 14)
            .frame(maxWidth: .infinity, minHeight: axis == .vertical ? 56 : nil, alignment: .leading)
            .background(Color.sgSurface)
            .clipShape(RoundedRectangle(cornerRadius: 14))
            .overlay(
                RoundedRectangle(cornerRadius: 14)
                    .strokeBorder(focused ? Color.sgPrimary : Color.sgBorder, lineWidth: 1.5)
            )
        }
        .padding(.bottom, 12)
    }

    @ViewBuilder
    private func valueText(_ value: String) -> some View {
        if monospaced {
            Text(value)
                .font(.system(size: 15, weight: .semibold, design: .monospaced))
                .foregroundColor(.sgTextPrimary)
        } else {
            Text(value)
                .font(.custom("PlusJakartaSans-SemiBold", size: 15))
                .foregroundColor(.sgTextPrimary)
        }
    }
}

private struct DateRowField: View {
    let label: String
    let valueText: String
    let onTap: () -> Void

    var body: some View {
        VStack(alignment: .leading, spacing: 6) {
            Text(label)
                .font(.custom("PlusJakartaSans-Bold", size: 12))
                .foregroundColor(.sgTextMuted)
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
                .padding(.vertical, 14)
                .frame(maxWidth: .infinity, alignment: .leading)
                .background(Color.sgSurface)
                .clipShape(RoundedRectangle(cornerRadius: 14))
                .overlay(
                    RoundedRectangle(cornerRadius: 14)
                        .strokeBorder(Color.sgBorder, lineWidth: 1.5)
                )
            }
            .buttonStyle(.plain)
        }
        .padding(.bottom, 12)
    }
}

struct DatePickerSheet: View {
    @Binding var date: Date
    let onDone: () -> Void

    var body: some View {
        VStack(spacing: 0) {
            HStack {
                Text("Pilih tanggal")
                    .font(.custom("PlusJakartaSans-ExtraBold", size: 18))
                    .foregroundColor(.sgTextPrimary)
                Spacer()
                Button("Selesai", action: onDone)
                    .font(.custom("PlusJakartaSans-Bold", size: 14))
                    .foregroundColor(.sgPrimary)
            }
            .padding(.horizontal, 20)
            .padding(.top, 20)
            .padding(.bottom, 8)

            DatePicker(
                "",
                selection: $date,
                displayedComponents: .date
            )
            .datePickerStyle(.graphical)
            .padding(.horizontal, 12)
            Spacer()
        }
        .background(Color.sgSurface)
    }
}

private struct AutoReminderInfoCard: View {
    var body: some View {
        VStack(alignment: .leading, spacing: 4) {
            HStack(spacing: 8) {
                Text("\u{f05a}")
                    .font(.custom("FontAwesome6Free-Solid", size: 14))
                    .foregroundColor(.sgPrimary)
                Text("Pengingat berikutnya akan diset otomatis")
                    .font(.custom("PlusJakartaSans-Bold", size: 12))
                    .foregroundColor(.sgPrimary)
            }
            Text("Berdasarkan interval pabrikan: target ganti oli berikutnya 20.420 km atau 6 Juli 2026.")
                .font(.custom("PlusJakartaSans-Medium", size: 12))
                .foregroundColor(.sgTextMuted)
                .lineSpacing(2)
        }
        .padding(14)
        .frame(maxWidth: .infinity, alignment: .leading)
        .background(Color.sgPrimarySofter)
        .clipShape(RoundedRectangle(cornerRadius: 14))
        .overlay(
            RoundedRectangle(cornerRadius: 14)
                .strokeBorder(Color.sgPrimary.opacity(0.19), lineWidth: 1)
        )
    }
}

private struct AddServiceSaveBar: View {
    let onSave: () -> Void

    var body: some View {
        VStack {
            AppButton(title: "Simpan Servis", action: onSave)
        }
        .padding(EdgeInsets(top: 10, leading: 16, bottom: 24, trailing: 16))
        .background(
            Color.sgSurface
                .overlay(alignment: .top) {
                    Rectangle().fill(Color.sgBorder).frame(height: 1)
                }
        )
    }
}

#Preview {
    NavigationStack { AddServiceView() }
}
