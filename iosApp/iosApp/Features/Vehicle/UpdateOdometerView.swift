import SwiftUI
import Shared

struct UpdateOdometerView: View {
    let vehicleId: String?
    var onSaved: () -> Void = {}

    @StateObject private var model = UpdateOdometerModel()

    init(vehicleId: String? = nil, onSaved: @escaping () -> Void = {}) {
        self.vehicleId = vehicleId
        self.onSaved = onSaved
    }

    private var state: UpdateOdometerViewModel.UiState { model.state }
    private var selected: Vehicle? { state.selected }
    private var odometerText: String {
        guard let km = state.odometerKm else { return "" }
        return String(describing: km)
    }
    private var lastKm: Int64 { selected?.odometer ?? 0 }

    var body: some View {
        VStack(spacing: 0) {
            VStack(alignment: .leading, spacing: 0) {
                if state.vehicles.count > 1 {
                    Text("PILIH KENDARAAN")
                        .font(.custom("PlusJakartaSans-ExtraBold", size: 11))
                        .kerning(1)
                        .foregroundColor(.sgTextMuted)
                        .padding(.top, 12)
                        .padding(.bottom, 8)

                    ScrollView(.horizontal, showsIndicators: false) {
                        HStack(spacing: 8) {
                            ForEach(state.vehicles, id: \.id) { v in
                                VehicleChip(
                                    vehicle: v,
                                    active: v.id == state.selectedId,
                                    onTap: { model.select(vehicleId: v.id) }
                                )
                            }
                        }
                        .padding(.bottom, 4)
                    }
                    Spacer().frame(height: 10)
                } else if let v = selected {
                    Text("\(v.displayTitle) · \(v.plateNumber)")
                        .font(.custom("PlusJakartaSans-Medium", size: 12))
                        .foregroundColor(.sgTextMuted)
                        .padding(.top, 12)
                        .padding(.bottom, 6)
                }

                Text("KM terakhir tercatat: \(formatted(Int(lastKm))) km")
                    .font(.custom("PlusJakartaSans-Medium", size: 14))
                    .foregroundColor(.sgTextMuted)
                    .padding(.bottom, 20)

                OdometerDisplay(value: odometerText, lastKm: Int(lastKm))

                Spacer().frame(height: 20)

                NumericKeypad(value: odometerText, onChange: { next in
                    model.setOdometer(km: Int64(next))
                })
            }
            .padding(.horizontal, 24)
            .padding(.bottom, 12)

            Spacer()

            VStack {
                AppButton(
                    title: "Simpan KM",
                    action: { model.save(allowRollback: false) },
                    isEnabled: state.canSave
                )
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
            model.onSaved = onSaved
            model.preselect(vehicleId: vehicleId)
        }
        .onDisappear { model.onSaved = nil }
    }

    private func formatted(_ n: Int) -> String {
        let f = NumberFormatter()
        f.numberStyle = .decimal
        f.groupingSeparator = "."
        return f.string(from: NSNumber(value: n)) ?? String(n)
    }
}

private struct VehicleChip: View {
    let vehicle: Vehicle
    let active: Bool
    let onTap: () -> Void

    private var iconUnicode: String {
        vehicle.type == .mobil ? "\u{f1b9}" : "\u{f21c}"
    }

    private var accent: Color {
        Color(hex: PresentationFactory.shared.vehicleColorHex(vehicle: vehicle)) ?? .sgPrimary
    }

    var body: some View {
        Button(action: onTap) {
            HStack(spacing: 10) {
                ZStack {
                    RoundedRectangle(cornerRadius: 9)
                        .fill(active ? Color.white.opacity(0.22) : accent.opacity(0.14))
                        .frame(width: 32, height: 32)
                    Text(iconUnicode)
                        .font(.custom("FontAwesome6Free-Solid", size: 14))
                        .foregroundColor(active ? .white : accent)
                }
                VStack(alignment: .leading, spacing: 2) {
                    Text(vehicle.displayTitle)
                        .font(.custom("PlusJakartaSans-Bold", size: 13))
                        .foregroundColor(active ? .white : .sgTextPrimary)
                    Text(vehicle.plateNumber)
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
        guard let n = Int(value) else { return value.isEmpty ? "0" : value }
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
    let value: String
    let onChange: (String) -> Void

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
        var next = value
        switch k {
        case "⌫":
            if !next.isEmpty { next.removeLast() }
        case ".":
            if !next.contains(".") { next.append(".") }
        default:
            next.append(k)
        }
        onChange(next)
    }
}

private extension Color {
    init?(hex: String) {
        let trimmed = hex.trimmingCharacters(in: CharacterSet.alphanumerics.inverted)
        var int: UInt64 = 0
        guard Scanner(string: trimmed).scanHexInt64(&int) else { return nil }
        let r = Double((int >> 16) & 0xFF) / 255
        let g = Double((int >> 8) & 0xFF) / 255
        let b = Double(int & 0xFF) / 255
        self.init(red: r, green: g, blue: b)
    }
}

#Preview {
    NavigationStack { UpdateOdometerView() }
}
