import SwiftUI
import Shared

enum AddServiceContext { case manual, fromReminder, fromComponent }

private struct TrackedComponent: Identifiable, Hashable {
    let id: String
    let label: String
    let subtitle: String
    let iconUnicode: String
    let color: Color
}

private func iconForKey(_ key: String) -> String {
    switch key {
    case "OIL_CAN":          return "\u{f613}"
    case "BOLT":             return "\u{f0e7}"
    case "CAR_BATTERY":      return "\u{f5df}"
    case "CIRCLE_NOTCH":     return "\u{f1ce}"
    case "LIFE_RING":        return "\u{f1cd}"
    case "FILTER":           return "\u{f0b0}"
    case "TEMPERATURE_HALF": return "\u{f2c9}"
    case "GEAR":             return "\u{f013}"
    case "GEARS":            return "\u{f085}"
    case "WRENCH":           return "\u{f0ad}"
    default:                 return "\u{f0ad}"
    }
}

private func colorFromHex(_ hex: String, fallback: Color = .sgPrimary) -> Color {
    var clean = hex.trimmingCharacters(in: .whitespacesAndNewlines)
    if clean.hasPrefix("#") { clean.removeFirst() }
    guard clean.count == 6, let value = UInt32(clean, radix: 16) else { return fallback }
    let r = Double((value >> 16) & 0xFF) / 255.0
    let g = Double((value >> 8) & 0xFF) / 255.0
    let b = Double(value & 0xFF) / 255.0
    return Color(red: r, green: g, blue: b)
}

private extension AddServiceViewModelComponentOption {
    func toLocal() -> TrackedComponent {
        TrackedComponent(
            id: id,
            label: label,
            subtitle: intervalLabel,
            iconUnicode: iconForKey(iconKey),
            color: colorFromHex(colorHex)
        )
    }
}

struct AddServiceView: View {
    var onSaved: (String) -> Void = { _ in }
    var vehicleId: String? = nil
    var sourceReminderId: String? = nil
    var trackedComponentId: String? = nil

    @StateObject private var model = AddServiceModel()
    @State private var contextDismissed: Bool = false
    @State private var showVehiclePicker: Bool = false
    @State private var showDatePicker: Bool = false
    @State private var showComponentPicker: Bool = false

    private var context: AddServiceContext {
        if sourceReminderId != nil { return .fromReminder }
        if trackedComponentId != nil { return .fromComponent }
        return .manual
    }
    private var contextActive: Bool { !contextDismissed && context != .manual }

    private static let dateFormatter: DateFormatter = {
        let df = DateFormatter()
        df.locale = Locale(identifier: "id_ID")
        df.dateFormat = "d MMM yyyy"
        return df
    }()

    private var vehicleOptions: [VehicleOption] {
        model.state.vehicles.map { $0.toVehicleOption() }
    }
    private var selectedOption: VehicleOption? {
        let id = model.state.selectedVehicleId
        return vehicleOptions.first { $0.id == id } ?? vehicleOptions.first
    }
    private var serviceDate: Date {
        Date(timeIntervalSince1970: TimeInterval(model.state.serviceDateMillis) / 1000.0)
    }
    private var kmText: Binding<String> {
        Binding(
            get: { model.state.odometerKm.map { String(Int64(truncating: $0)) } ?? "" },
            set: { newValue in
                let digits = newValue.filter(\.isNumber)
                model.setOdometer(km: Int64(digits))
            }
        )
    }
    private var workshopText: Binding<String> {
        Binding(get: { model.state.workshop }, set: { model.setWorkshop($0) })
    }
    private var costText: Binding<String> {
        Binding(
            get: { model.state.costIdr > 0 ? String(model.state.costIdr) : "" },
            set: { newValue in
                let digits = newValue.filter(\.isNumber)
                model.setCost(Int64(digits) ?? 0)
            }
        )
    }
    private var noteText: Binding<String> {
        Binding(get: { model.state.note }, set: { model.setNote($0) })
    }
    private var availableComponents: [TrackedComponent] {
        (model.state.availableComponents as [AddServiceViewModelComponentOption]).map { $0.toLocal() }
    }

    var body: some View {
        VStack(spacing: 0) {
            ScrollView {
                VStack(alignment: .leading, spacing: 0) {
                    if contextActive {
                        ContextBanner(
                            title: context == .fromReminder ? "Dari reminder" : "Untuk komponen yang dipantau",
                            body: context == .fromReminder
                                ? "Reminder akan otomatis ditandai selesai setelah kamu simpan."
                                : "Servis ini akan tercatat sebagai update komponen yang dipantau.",
                            iconUnicode: context == .fromReminder ? "\u{f0f3}" : "\u{f0ad}",
                            tone: .info,
                            onDismiss: { contextDismissed = true }
                        )
                        .padding(.bottom, 14)
                    }

                    FieldLabel(text: "Kendaraan")
                    if let option = selectedOption {
                        VehiclePickerRow(selected: option, locked: contextActive) {
                            if !contextActive { showVehiclePicker = true }
                        }
                        .padding(.bottom, 18)
                    }

                    FieldLabel(text: "Jenis catatan")
                    ModeSelector(
                        selected: model.state.mode,
                        onSelect: { model.setMode($0) }
                    )
                    .padding(.bottom, 18)

                    switch model.state.mode {
                    case ServiceKind.komponen:
                        FieldLabel(text: "Komponen yang diservis · \(model.state.selectedComponentIds.count)")
                        if model.state.hasTrackedComponents {
                            ComponentChipsRow(
                                selectedIds: Set(model.state.selectedComponentIds),
                                allItems: availableComponents,
                                onRemove: { id in model.toggleComponent(id) },
                                onAdd: { showComponentPicker = true }
                            )
                            Text("Daftar diambil dari komponen yang kamu pantau. Reminder berikutnya dibuat otomatis per komponen.")
                                .font(.custom("PlusJakartaSans-Medium", size: 11))
                                .foregroundColor(.sgTextSubtle)
                                .lineSpacing(2)
                                .padding(.top, 6)
                                .padding(.bottom, 18)
                        } else {
                            ContextBanner(
                                title: "Belum ada komponen dipantau",
                                body: "Tambah komponen di Detail Kendaraan → Komponen dulu, atau pakai mode Rutin / Manual.",
                                iconUnicode: "\u{f0ad}",
                                tone: .warning,
                                onDismiss: nil
                            )
                            .padding(.bottom, 18)
                        }
                    case ServiceKind.rutin:
                        let rutinKm = (model.state.selectedVehicle?.type == VehicleType.mobil) ? "10.000" : "4.000"
                        ContextBanner(
                            title: "Servis Rutin / Berkala",
                            body: "Cocok untuk servis berkala umum di bengkel (tidak tahu komponen apa saja yang diganti). Kami buat reminder otomatis ~6 bulan / \(rutinKm) km kedepan.",
                            iconUnicode: "\u{f05a}",
                            tone: .info,
                            onDismiss: nil
                        )
                        .padding(.bottom, 18)
                    case ServiceKind.manual:
                        EditableRowField(
                            label: "Apa yang diservis?",
                            text: Binding(
                                get: { model.state.customTitle },
                                set: { model.setCustomTitle($0) }
                            ),
                            placeholder: "cth. Ganti spion, jok baru, klakson",
                            iconUnicode: "\u{f0ad}"
                        )
                        Text("Sekali catat, tidak dibuat reminder otomatis. Cocok untuk perbaikan satu kali.")
                            .font(.custom("PlusJakartaSans-Medium", size: 11))
                            .foregroundColor(.sgTextSubtle)
                            .lineSpacing(2)
                            .padding(.top, 2)
                            .padding(.bottom, 18)
                    default:
                        EmptyView()
                    }

                    DateRowField(
                        label: "Tanggal servis",
                        valueText: Self.dateFormatter.string(from: serviceDate),
                        onTap: { showDatePicker = true }
                    )

                    EditableRowField(
                        label: "KM saat servis",
                        text: kmText,
                        placeholder: "cth. 18420",
                        iconUnicode: "\u{f625}",
                        keyboardType: .numberPad,
                        monospaced: true
                    )

                    EditableRowField(
                        label: "Bengkel",
                        text: workshopText,
                        placeholder: "cth. AHASS Kebon Jeruk",
                        iconUnicode: "\u{f3c5}"
                    )

                    EditableRowField(
                        label: "Biaya",
                        text: costText,
                        placeholder: "cth. 65000",
                        iconUnicode: nil,
                        keyboardType: .numberPad,
                        monospaced: true,
                        prefix: "Rp "
                    )

                    EditableRowField(
                        label: "Catatan",
                        text: noteText,
                        placeholder: "cth. AHM MPX2 0.8L",
                        iconUnicode: nil,
                        axis: .vertical
                    )

                    if model.state.mode != ServiceKind.manual {
                        AutoReminderInfoCard()
                            .padding(.top, 4)
                    }
                }
                .padding(.horizontal, 20)
                .padding(.vertical, 8)
                .padding(.bottom, 20)
            }

            AddServiceSaveBar(
                onSave: { model.submit() },
                enabled: model.state.canSave
            )
        }
        .background(Color.sgBgWarm)
        .navigationTitle("Catat Servis")
        .navigationBarTitleDisplayMode(.inline)
        .onAppear {
            model.preselect(
                vehicleId: vehicleId,
                sourceReminderId: sourceReminderId,
                trackedComponentId: trackedComponentId
            )
            model.onSaved = onSaved
        }
        .onDisappear { model.onSaved = nil }
        .sheet(isPresented: $showVehiclePicker) {
            if let option = selectedOption {
                VehiclePickerSheet(
                    options: vehicleOptions,
                    selectedId: option.id,
                    onPick: { picked in model.selectVehicle(picked.id) }
                )
                .presentationDetents([.medium])
                .presentationDragIndicator(.visible)
            }
        }
        .sheet(isPresented: $showDatePicker) {
            DatePickerSheet(
                date: Binding(
                    get: { serviceDate },
                    set: { newDate in
                        model.setServiceDate(millis: Int64(newDate.timeIntervalSince1970 * 1000))
                    }
                ),
                onDone: { showDatePicker = false }
            )
            .presentationDetents([.medium])
        }
        .sheet(isPresented: $showComponentPicker) {
            ComponentPickerSheet(
                allItems: availableComponents,
                initiallySelected: Set(model.state.selectedComponentIds),
                onApply: { picked in
                    model.setComponents(picked)
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

private struct ModeSelector: View {
    let selected: ServiceKind
    let onSelect: (ServiceKind) -> Void

    private let options: [(ServiceKind, String)] = [
        (.komponen, "Komponen"),
        (.rutin, "Rutin"),
        (.manual, "Manual"),
    ]

    var body: some View {
        HStack(spacing: 8) {
            ForEach(options, id: \.0) { (mode, label) in
                Button(action: { onSelect(mode) }) {
                    Text(label)
                        .font(.custom("PlusJakartaSans-Bold", size: 13))
                        .foregroundColor(mode == selected ? .sgPrimary : .sgTextPrimary)
                        .frame(maxWidth: .infinity)
                        .padding(.vertical, 12)
                        .background(mode == selected ? Color.sgPrimarySoft : Color.sgSurface)
                        .clipShape(RoundedRectangle(cornerRadius: 12))
                        .overlay(
                            RoundedRectangle(cornerRadius: 12)
                                .strokeBorder(mode == selected ? Color.sgPrimary : Color.sgBorder, lineWidth: 1.5)
                        )
                }
                .buttonStyle(.plain)
            }
        }
    }
}

extension ServiceKind: Identifiable {
    public var id: String { key }
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
    var enabled: Bool = true

    var body: some View {
        VStack {
            AppButton(title: "Simpan Servis", action: onSave, isEnabled: enabled)
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
