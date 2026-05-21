import SwiftUI

struct AddServiceView: View {
    var onSaved: () -> Void = {}

    @State private var selectedVehicle: VehicleOption = VehicleOptions.defaults[0]
    @State private var selectedService: String = "oli"
    @State private var serviceDate: Date = Date()
    @State private var kmText: String = ""
    @State private var workshop: String = ""
    @State private var costText: String = ""
    @State private var note: String = ""

    @State private var showVehiclePicker: Bool = false
    @State private var showDatePicker: Bool = false

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
                    FieldLabel(text: "Kendaraan")
                    VehiclePickerRow(selected: selectedVehicle) {
                        showVehiclePicker = true
                    }
                    .padding(.bottom, 18)

                    FieldLabel(text: "Jenis servis")
                    ServiceTypeGrid(selected: $selectedService)
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
