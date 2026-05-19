import SwiftUI

struct UpdateOdometerVehicle: Identifiable, Equatable {
    let id: String
    let name: String
    let plate: String
    let iconUnicode: String
    let lastKm: Int
    let accent: Color
}

private let defaultVehicles: [UpdateOdometerVehicle] = [
    UpdateOdometerVehicle(
        id: "v1",
        name: "Beat Hitam",
        plate: "B 4521 KZA",
        iconUnicode: "\u{f21c}",
        lastKm: 17890,
        accent: .sgPrimary
    ),
    UpdateOdometerVehicle(
        id: "v2",
        name: "Brio Biru",
        plate: "B 1234 ABC",
        iconUnicode: "\u{f1b9}",
        lastKm: 42100,
        accent: Color(red: 0.25, green: 0.69, blue: 0.84)
    ),
]

struct UpdateOdometerView: View {
    var onSave: () -> Void = {}
    var vehicles: [UpdateOdometerVehicle] = defaultVehicles

    @State private var selectedId: String = defaultVehicles.first?.id ?? ""
    @State private var odometer: String = ""

    private var list: [UpdateOdometerVehicle] {
        vehicles.isEmpty ? defaultVehicles : vehicles
    }

    private var selected: UpdateOdometerVehicle {
        list.first(where: { $0.id == selectedId }) ?? list[0]
    }

    var body: some View {
        VStack(spacing: 0) {
            VStack(alignment: .leading, spacing: 0) {
                if list.count > 1 {
                    Text("PILIH KENDARAAN")
                        .font(.custom("PlusJakartaSans-ExtraBold", size: 11))
                        .kerning(1)
                        .foregroundColor(.sgTextMuted)
                        .padding(.top, 12)
                        .padding(.bottom, 8)

                    ScrollView(.horizontal, showsIndicators: false) {
                        HStack(spacing: 8) {
                            ForEach(list) { v in
                                VehicleChip(
                                    vehicle: v,
                                    active: v.id == selectedId,
                                    onTap: { selectedId = v.id }
                                )
                            }
                        }
                        .padding(.bottom, 4)
                    }
                    Spacer().frame(height: 10)
                } else {
                    Text("\(selected.name) · \(selected.plate)")
                        .font(.custom("PlusJakartaSans-Medium", size: 12))
                        .foregroundColor(.sgTextMuted)
                        .padding(.top, 12)
                        .padding(.bottom, 6)
                }

                Text("KM terakhir tercatat: \(formatted(selected.lastKm)) km (4 hari lalu)")
                    .font(.custom("PlusJakartaSans-Medium", size: 14))
                    .foregroundColor(.sgTextMuted)
                    .padding(.bottom, 20)

                OdometerDisplay(value: odometer, lastKm: selected.lastKm)

                Spacer().frame(height: 20)

                NumericKeypad(value: $odometer)
            }
            .padding(.horizontal, 24)
            .padding(.bottom, 12)

            Spacer()

            VStack {
                AppButton(title: "Simpan KM", action: onSave)
            }
            .padding(EdgeInsets(top: 10, leading: 16, bottom: 24, trailing: 16))
            .background(
                Color.sgSurface
                    .overlay(alignment: .top) {
                        Rectangle().fill(Color.sgBorder).frame(height: 1)
                    }
            )
        }
        .background(Color.sgBgWarm)
        .navigationTitle("Update KM")
        .navigationBarTitleDisplayMode(.inline)
        .onAppear {
            if odometer.isEmpty {
                odometer = String(selected.lastKm + 530)
            }
        }
        .onChange(of: selectedId) { _, _ in
            odometer = String(selected.lastKm + 530)
        }
    }

    private func formatted(_ n: Int) -> String {
        let f = NumberFormatter()
        f.numberStyle = .decimal
        f.groupingSeparator = "."
        return f.string(from: NSNumber(value: n)) ?? String(n)
    }
}

private struct VehicleChip: View {
    let vehicle: UpdateOdometerVehicle
    let active: Bool
    let onTap: () -> Void

    var body: some View {
        Button(action: onTap) {
            HStack(spacing: 10) {
                ZStack {
                    RoundedRectangle(cornerRadius: 9)
                        .fill(active ? Color.white.opacity(0.22) : vehicle.accent.opacity(0.14))
                        .frame(width: 32, height: 32)
                    Text(vehicle.iconUnicode)
                        .font(.custom("FontAwesome6Free-Solid", size: 14))
                        .foregroundColor(active ? .white : vehicle.accent)
                }
                VStack(alignment: .leading, spacing: 2) {
                    Text(vehicle.name)
                        .font(.custom("PlusJakartaSans-Bold", size: 13))
                        .foregroundColor(active ? .white : .sgTextPrimary)
                    Text(vehicle.plate)
                        .font(.system(size: 10, weight: .medium, design: .monospaced))
                        .foregroundColor(active ? .white.opacity(0.8) : .sgTextMuted)
                }
            }
            .padding(.horizontal, 14)
            .padding(.vertical, 10)
            .background(active ? Color.sgPrimary : Color.white)
            .clipShape(RoundedRectangle(cornerRadius: 14))
            .overlay(
                RoundedRectangle(cornerRadius: 14)
                    .strokeBorder(active ? Color.sgPrimary : Color.sgBorder, lineWidth: 1.5)
            )
            .shadow(color: active ? Color.sgPrimary.opacity(0.2) : Color.clear, radius: 8, x: 0, y: 4)
        }
        .buttonStyle(.plain)
    }
}

private struct OdometerDisplay: View {
    let value: String
    let lastKm: Int

    private var formattedValue: String {
        guard let n = Int(value) else { return value }
        let f = NumberFormatter()
        f.numberStyle = .decimal
        f.groupingSeparator = "."
        return f.string(from: NSNumber(value: n)) ?? value
    }

    private var delta: Int {
        (Int(value) ?? 0) - lastKm
    }

    private var deltaText: String {
        let f = NumberFormatter()
        f.numberStyle = .decimal
        f.groupingSeparator = "."
        let abs = f.string(from: NSNumber(value: Swift.abs(delta))) ?? "\(Swift.abs(delta))"
        return delta >= 0 ? "+\(abs) km dari terakhir" : "-\(abs) km dari terakhir"
    }

    var body: some View {
        VStack(spacing: 6) {
            Text("KM SAAT INI")
                .font(.custom("PlusJakartaSans-Bold", size: 11))
                .kerning(1)
                .foregroundColor(.sgTextMuted)

            Text(formattedValue)
                .font(.system(size: 50, weight: .bold, design: .monospaced))
                .foregroundColor(.sgTextPrimary)
                .kerning(-1)

            Text(deltaText)
                .font(.custom("PlusJakartaSans-Bold", size: 13))
                .foregroundColor(.sgPrimary)
        }
        .frame(maxWidth: .infinity)
        .padding(30)
        .background(Color.sgSurface)
        .clipShape(RoundedRectangle(cornerRadius: 22))
        .overlay(
            RoundedRectangle(cornerRadius: 22)
                .strokeBorder(Color.sgPrimary, lineWidth: 2)
        )
    }
}

private struct NumericKeypad: View {
    @Binding var value: String

    private let keys: [String] = ["1","2","3","4","5","6","7","8","9",".","0","⌫"]

    var body: some View {
        LazyVGrid(columns: [.init(.flexible(), spacing: 8), .init(.flexible(), spacing: 8), .init(.flexible(), spacing: 8)], spacing: 8) {
            ForEach(keys, id: \.self) { k in
                Button { tap(k) } label: {
                    Text(k)
                        .font(.system(size: 22, weight: .semibold, design: .monospaced))
                        .foregroundColor(.sgTextPrimary)
                        .frame(maxWidth: .infinity)
                        .frame(height: 52)
                        .background(Color.sgSurface)
                        .clipShape(RoundedRectangle(cornerRadius: 14))
                        .overlay(
                            RoundedRectangle(cornerRadius: 14)
                                .strokeBorder(Color.sgBorder, lineWidth: 1)
                        )
                }
                .buttonStyle(.plain)
            }
        }
    }

    private func tap(_ k: String) {
        switch k {
        case "⌫":
            if !value.isEmpty { value.removeLast() }
        case ".":
            if !value.contains(".") { value.append(".") }
        default:
            value.append(k)
        }
    }
}

#Preview {
    NavigationStack { UpdateOdometerView() }
}
