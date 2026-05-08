import SwiftUI

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

struct VehicleForm: View {
    @Binding var state: VehicleFormState
    var showTypeSelector: Bool = true

    var body: some View {
        VStack(spacing: 16) {
            if showTypeSelector {
                HStack(spacing: 10) {
                    ForEach([("motor", "\u{f21c}"), ("mobil", "\u{f1b9}")], id: \.0) { tp, icon in
                        let selected = state.type == tp
                        Button {
                            state.type = tp
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
    }
}
