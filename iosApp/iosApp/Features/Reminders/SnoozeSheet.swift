import SwiftUI

struct SnoozeSheet: View {
    let onDismiss: () -> Void
    var onConfirm: () -> Void = {}

    @State private var selected: SnoozeOption = .threeDays

    enum SnoozeOption: String, CaseIterable {
        case tomorrow = "Besok pagi"
        case threeDays = "3 hari lagi"
        case oneWeek = "1 minggu lagi"
        case km100 = "Saat KM bertambah 100"
        case pickDate = "Pilih tanggal sendiri"
    }

    var body: some View {
        VStack(spacing: 0) {
            Capsule()
                .fill(Color.sgBorder)
                .frame(width: 40, height: 4)
                .padding(.top, 16)
                .padding(.bottom, 14)

            VStack(alignment: .leading, spacing: 6) {
                Text("Tunda pengingat")
                    .font(.custom("PlusJakartaSans-ExtraBold", size: 20))
                    .foregroundColor(.sgTextPrimary)
                    .kerning(-0.3)
                Text("Kapan kamu mau diingatkan lagi?")
                    .font(.custom("PlusJakartaSans-Medium", size: 13))
                    .foregroundColor(.sgTextMuted)
            }
            .frame(maxWidth: .infinity, alignment: .leading)
            .padding(.horizontal, 20)
            .padding(.bottom, 16)

            VStack(spacing: 8) {
                ForEach(SnoozeOption.allCases, id: \.self) { option in
                    SnoozeRow(
                        label: option.rawValue,
                        active: selected == option,
                        onTap: { selected = option }
                    )
                }
            }
            .padding(.horizontal, 20)

            Button(action: {
                onConfirm()
                onDismiss()
            }) {
                Text("Tunda")
                    .font(.custom("PlusJakartaSans-Bold", size: 14))
                    .foregroundColor(.white)
                    .frame(maxWidth: .infinity)
                    .frame(height: 50)
                    .background(Color.sgPrimary)
                    .clipShape(RoundedRectangle(cornerRadius: 14))
            }
            .buttonStyle(.plain)
            .padding(.horizontal, 20)
            .padding(.top, 8)
            .padding(.bottom, 36)
        }
        .frame(maxWidth: .infinity)
        .background(Color.sgSurface)
        .clipShape(RoundedRectangle(cornerRadius: 24, style: .continuous))
    }
}

private struct SnoozeRow: View {
    let label: String
    let active: Bool
    let onTap: () -> Void

    var body: some View {
        Button(action: onTap) {
            HStack {
                Text(label)
                    .font(.custom("PlusJakartaSans-SemiBold", size: 14))
                    .foregroundColor(.sgTextPrimary)
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
                    .strokeBorder(active ? Color.sgPrimary : .clear, lineWidth: 1.5)
            )
        }
        .buttonStyle(.plain)
    }
}

#Preview {
    SnoozeSheet(onDismiss: {})
        .background(Color.black.opacity(0.45))
}
