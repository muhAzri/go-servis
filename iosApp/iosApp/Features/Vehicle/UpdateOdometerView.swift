import SwiftUI

struct UpdateOdometerView: View {
    var onSave: () -> Void = {}

    @State private var odometer: String = "18420"

    var body: some View {
        VStack(spacing: 0) {
            VStack(alignment: .leading, spacing: 0) {
                Text("Beat Hitam · B 4521 KZA")
                    .font(.custom("PlusJakartaSans-Medium", size: 12))
                    .foregroundColor(.sgTextMuted)
                    .padding(.top, 12)
                    .padding(.bottom, 6)

                Text("KM terakhir tercatat: 17.890 km (4 hari lalu)")
                    .font(.custom("PlusJakartaSans-Medium", size: 14))
                    .foregroundColor(.sgTextMuted)
                    .padding(.bottom, 24)

                OdometerDisplay(value: odometer)

                Spacer().frame(height: 24)

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
    }
}

private struct OdometerDisplay: View {
    let value: String

    private var formattedValue: String {
        guard let n = Int(value) else { return value }
        let f = NumberFormatter()
        f.numberStyle = .decimal
        f.groupingSeparator = "."
        return f.string(from: NSNumber(value: n)) ?? value
    }

    private var delta: Int {
        (Int(value) ?? 0) - 17890
    }

    var body: some View {
        VStack(spacing: 6) {
            Text("KM SAAT INI")
                .font(.custom("PlusJakartaSans-Bold", size: 11))
                .kerning(1)
                .foregroundColor(.sgTextMuted)

            Text(formattedValue)
                .font(.system(size: 56, weight: .bold, design: .monospaced))
                .foregroundColor(.sgTextPrimary)
                .kerning(-1)

            Text("+\(delta) km dari terakhir")
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
                        .frame(height: 56)
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
