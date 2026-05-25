import SwiftUI
import Shared

private let vehicleColors = ["#1A2418", "#D6453A", "#3FB1D6", "#E89C2E", "#F2EEE6", "#7B6FE8"]

private extension Color {
    init(hex: String) {
        let hex = hex.trimmingCharacters(in: CharacterSet.alphanumerics.inverted)
        var int: UInt64 = 0
        Scanner(string: hex).scanHexInt64(&int)
        let r = Double((int >> 16) & 0xFF) / 255
        let g = Double((int >> 8) & 0xFF) / 255
        let b = Double(int & 0xFF) / 255
        self.init(red: r, green: g, blue: b)
    }
}

struct VehicleFormState {
    var type: String = "motor"
    var subtype: String = VehicleSubtypes.defaultFor(vehicleType: "motor")
    var nama: String = ""
    var merek: String = ""
    var model: String = ""
    var tahun: String = ""
    var platNomor: String = ""
    var odometer: String = ""
    var warna: String = "#1A2418"

    var isValid: Bool {
        !nama.trimmingCharacters(in: .whitespaces).isEmpty &&
        !merek.trimmingCharacters(in: .whitespaces).isEmpty
    }
}

extension VehicleFormState {
    func toOnboardingInput(typeKey: String? = nil) -> OnboardingVehicleInput {
        let parsedYear: KotlinInt? = Int32(tahun).map { KotlinInt(value: $0) }
        return PresentationFactory.shared.vehicleInput(
            typeKey: typeKey ?? type,
            nickname: nama,
            brand: merek,
            model: model,
            year: parsedYear,
            plateNumber: platNomor,
            odometerKm: Int64(odometer) ?? 0,
            colorHex: warna,
            subtypeId: subtype.isEmpty ? nil : subtype
        )
    }
}

extension VehicleSubtypes {
    static func defaultFor(vehicleType: String) -> String {
        vehicleType == "mobil" ? "mpv" : "matic"
    }
}

struct VehicleForm: View {
    @Binding var state: VehicleFormState
    var showTypeSelector: Bool = true

    @State private var showSubtypePicker: Bool = false

    private var subtypeIcon: String {
        state.type == "mobil" ? "\u{f1b9}" : "\u{f21c}"
    }

    private var subtypeLabel: String {
        VehicleSubtypes.label(for: state.type, id: state.subtype)
    }

    private var subtypeOptions: [ActionSheetOption] {
        VehicleSubtypes.list(for: state.type).map { sub in
            ActionSheetOption(
                id: sub.id,
                iconUnicode: subtypeIcon,
                label: sub.label,
                subtitle: sub.desc,
                value: sub.id
            )
        }
    }

    var body: some View {
        VStack(spacing: 16) {
            if showTypeSelector {
                HStack(spacing: 10) {
                    ForEach([("motor", "\u{f21c}"), ("mobil", "\u{f1b9}")], id: \.0) { tp, icon in
                        let selected = state.type == tp
                        Button {
                            if state.type != tp {
                                state.type = tp
                                state.subtype = VehicleSubtypes.defaultFor(vehicleType: tp)
                            }
                        } label: {
                            VStack(spacing: 6) {
                                Text(icon)
                                    .font(.custom("FontAwesome6Free-Solid", size: 28))
                                    .foregroundColor(selected ? .white : .sgTextPrimary)
                                Text(tp == "motor" ? "Motor" : "Mobil")
                                    .font(.custom("PlusJakartaSans-Bold", size: 13))
                                    .foregroundColor(selected ? .white : .sgTextPrimary)
                            }
                            .frame(maxWidth: .infinity)
                            .padding(16)
                            .background(selected ? Color.sgPrimary : Color.white)
                            .clipShape(RoundedRectangle(cornerRadius: 18))
                            .overlay(
                                RoundedRectangle(cornerRadius: 18)
                                    .strokeBorder(selected ? Color.clear : Color.black.opacity(0.08), lineWidth: 1.5)
                            )
                        }
                        .buttonStyle(.plain)
                    }
                }
            }

            VStack(alignment: .leading, spacing: 6) {
                Text("Sub-tipe")
                    .font(.custom("PlusJakartaSans-SemiBold", size: 13))
                    .foregroundColor(.sgTextMuted)
                Button {
                    showSubtypePicker = true
                } label: {
                    HStack(spacing: 10) {
                        Text(subtypeIcon)
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

            AppTextField(label: "Nama panggilan", placeholder: "cth. Beat Hitam", text: $state.nama)
            AppTextField(label: "Merk", placeholder: "cth. Honda", text: $state.merek)

            HStack(spacing: 12) {
                AppTextField(label: "Model", placeholder: "cth. Beat", text: $state.model)
                AppTextField(label: "Tahun", placeholder: "cth. 2022", text: $state.tahun, keyboardType: .numberPad)
            }

            AppTextField(label: "Plat nomor", placeholder: "cth. B 1234 ABC", text: $state.platNomor)
            AppTextField(label: "KM saat ini", placeholder: "cth. 18000", text: $state.odometer, keyboardType: .numberPad)

            VStack(alignment: .leading, spacing: 8) {
                Text("Warna")
                    .font(.custom("PlusJakartaSans-SemiBold", size: 13))
                    .foregroundColor(.sgTextMuted)
                HStack(spacing: 8) {
                    ForEach(vehicleColors, id: \.self) { hex in
                        let selected = state.warna == hex
                        RoundedRectangle(cornerRadius: 12)
                            .fill(Color(hex: hex))
                            .frame(width: 36, height: 36)
                            .overlay(
                                RoundedRectangle(cornerRadius: 12)
                                    .strokeBorder(
                                        selected ? Color.sgPrimary : Color.black.opacity(0.08),
                                        lineWidth: selected ? 3 : 1
                                    )
                            )
                            .onTapGesture { state.warna = hex }
                    }
                }
            }
            .frame(maxWidth: .infinity, alignment: .leading)
        }
        .sheet(isPresented: $showSubtypePicker) {
            ActionSheetView(
                title: "Sub-tipe kendaraan",
                subtitle: "Pilih yang paling mendekati — menentukan komponen yang relevan.",
                options: subtypeOptions,
                selectionMode: .radio,
                selectedValue: state.subtype,
                onSelect: { value in
                    state.subtype = value
                }
            )
            .presentationDetents([.medium, .large])
            .presentationDragIndicator(.visible)
        }
    }
}
