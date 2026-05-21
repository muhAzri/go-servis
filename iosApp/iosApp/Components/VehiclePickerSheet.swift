import SwiftUI

struct VehicleOption: Identifiable, Hashable {
    let id: String
    let name: String
    let plate: String
    let iconUnicode: String
    let accent: Color
}

enum VehicleOptions {
    static let defaults: [VehicleOption] = [
        .init(id: "v1", name: "Beat Hitam",   plate: "B 4521 KZA", iconUnicode: "\u{f21c}", accent: .sgPrimary),
        .init(id: "v2", name: "Vario Merah",  plate: "B 6789 SKR", iconUnicode: "\u{f21c}", accent: Color(red: 0.84, green: 0.27, blue: 0.23)),
        .init(id: "v3", name: "Avanza Putih", plate: "B 1234 ABC", iconUnicode: "\u{f1b9}", accent: Color(red: 0.25, green: 0.30, blue: 0.36)),
        .init(id: "v4", name: "Brio Biru",    plate: "B 9876 XYZ", iconUnicode: "\u{f1b9}", accent: Color(red: 0.25, green: 0.69, blue: 0.84)),
    ]
}

struct VehiclePickerRow: View {
    let selected: VehicleOption
    var locked: Bool = false
    let onTap: () -> Void

    var body: some View {
        Button {
            if !locked { onTap() }
        } label: {
            HStack(spacing: 12) {
                IconBadge(
                    iconUnicode: selected.iconUnicode,
                    foreground: selected.accent,
                    background: selected.accent.opacity(0.13),
                    size: 40, iconSize: 22, corner: 10
                )
                VStack(alignment: .leading, spacing: 2) {
                    Text(selected.name)
                        .font(.custom("PlusJakartaSans-Bold", size: 14))
                        .foregroundColor(.sgTextPrimary)
                    Text(selected.plate)
                        .font(.system(size: 12, weight: .medium, design: .monospaced))
                        .foregroundColor(.sgTextMuted)
                }
                Spacer()
                Text(locked ? "\u{f023}" : "\u{f078}")
                    .font(.custom("FontAwesome6Free-Solid", size: 14))
                    .foregroundColor(.sgTextSubtle)
            }
            .padding(14)
            .background(locked ? Color.sgSurfaceAlt : Color.sgSurface)
            .clipShape(RoundedRectangle(cornerRadius: 14))
            .overlay(
                RoundedRectangle(cornerRadius: 14)
                    .strokeBorder(Color.sgBorder, lineWidth: 1.5)
            )
        }
        .buttonStyle(.plain)
        .disabled(locked)
    }
}

struct VehiclePickerSheet: View {
    let options: [VehicleOption]
    let selectedId: String
    let onPick: (VehicleOption) -> Void

    @Environment(\.dismiss) private var dismiss

    var body: some View {
        VStack(alignment: .leading, spacing: 0) {
            Text("Pilih kendaraan")
                .font(.custom("PlusJakartaSans-ExtraBold", size: 20))
                .foregroundColor(.sgTextPrimary)
                .kerning(-0.3)
                .padding(.bottom, 14)

            VStack(spacing: 8) {
                ForEach(options) { option in
                    OptionRow(
                        option: option,
                        active: option.id == selectedId,
                        onTap: {
                            onPick(option)
                            dismiss()
                        }
                    )
                }
            }
        }
        .frame(maxWidth: .infinity, alignment: .leading)
        .padding(.horizontal, 20)
        .padding(.top, 24)
        .padding(.bottom, 32)
        .background(Color.sgSurface)
    }

    private struct OptionRow: View {
        let option: VehicleOption
        let active: Bool
        let onTap: () -> Void

        var body: some View {
            Button(action: onTap) {
                HStack(spacing: 12) {
                    IconBadge(
                        iconUnicode: option.iconUnicode,
                        foreground: option.accent,
                        background: option.accent.opacity(0.13),
                        size: 40, iconSize: 22, corner: 10
                    )
                    VStack(alignment: .leading, spacing: 2) {
                        Text(option.name)
                            .font(.custom("PlusJakartaSans-Bold", size: 14))
                            .foregroundColor(.sgTextPrimary)
                        Text(option.plate)
                            .font(.system(size: 12, weight: .medium, design: .monospaced))
                            .foregroundColor(.sgTextMuted)
                    }
                    Spacer()
                    if active {
                        Text("\u{f00c}")
                            .font(.custom("FontAwesome6Free-Solid", size: 16))
                            .foregroundColor(.sgPrimary)
                    }
                }
                .padding(14)
                .background(active ? Color.sgPrimarySoft : Color.sgSurfaceAlt)
                .clipShape(RoundedRectangle(cornerRadius: 14))
                .overlay(
                    RoundedRectangle(cornerRadius: 14)
                        .strokeBorder(active ? Color.sgPrimary : Color.clear, lineWidth: 1.5)
                )
            }
            .buttonStyle(.plain)
        }
    }
}
