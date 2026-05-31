import SwiftUI
import Shared

struct ExportCsvSheet: View {
    var onDismiss: () -> Void = {}
    var onShare: () -> Void = {}

    @ObservedObject private var backup = BackupModel.shared

    @State private var picks: [String: Bool] = [
        "veh": true, "hist": true, "rem": true, "comp": true,
    ]
    @State private var range: String = "all"

    private var items: [(String, String, String)] {
        [
            ("veh", "Kendaraan", "\(backup.counts.vehicles) kendaraan"),
            ("hist", "Riwayat servis", "\(backup.counts.services) entri"),
            ("rem", "Pengingat", "\(backup.counts.reminders) pengingat"),
            ("comp", "Komponen dipantau", "\(backup.counts.components) komponen"),
        ]
    }

    private let ranges: [(String, String)] = [
        ("all", "Semua waktu"),
        ("month", "Bulan ini"),
        ("3m", "3 bulan"),
        ("year", "1 tahun"),
    ]

    var body: some View {
        VStack(alignment: .leading, spacing: 0) {
            VStack(alignment: .leading, spacing: 4) {
                Text("Ekspor Data")
                    .font(.custom("PlusJakartaSans-ExtraBold", size: 20))
                    .foregroundColor(.sgTextPrimary)
                Text("\(backup.counts.vehicles) kendaraan · \(backup.counts.services) servis · \(backup.counts.reminders) pengingat")
                    .font(.custom("PlusJakartaSans-Medium", size: 12))
                    .foregroundColor(.sgTextMuted)
            }
            .padding(.horizontal, 20)
            .padding(.top, 18)
            .padding(.bottom, 12)

            Divider().background(Color.sgBorder)

            ScrollView {
                VStack(alignment: .leading, spacing: 16) {
                    sectionLabel("Pilih data")
                    VStack(spacing: 0) {
                        ForEach(items, id: \.0) { row in
                            Button(action: { picks[row.0] = !(picks[row.0] ?? false) }) {
                                HStack(spacing: 12) {
                                    checkbox(on: picks[row.0] ?? false)
                                    VStack(alignment: .leading, spacing: 2) {
                                        Text(row.1)
                                            .font(.custom("PlusJakartaSans-Bold", size: 14))
                                            .foregroundColor(.sgTextPrimary)
                                        Text(row.2)
                                            .font(.custom("PlusJakartaSans-Medium", size: 11))
                                            .foregroundColor(.sgTextMuted)
                                    }
                                    Spacer()
                                }
                                .padding(.vertical, 10)
                            }
                            .buttonStyle(.plain)
                            if row.0 != items.last?.0 {
                                Divider().background(Color.sgBorder)
                            }
                        }
                    }
                    .padding(.horizontal, 12)
                    .background(Color.sgSurfaceAlt)
                    .clipShape(RoundedRectangle(cornerRadius: 14))

                    sectionLabel("Rentang tanggal")
                    FlowLayout(spacing: 6) {
                        ForEach(ranges, id: \.0) { r in
                            Button(action: { range = r.0 }) {
                                Text(r.1)
                                    .font(.custom("PlusJakartaSans-Bold", size: 12))
                                    .foregroundColor(range == r.0 ? .white : .sgTextPrimary)
                                    .padding(.horizontal, 12)
                                    .padding(.vertical, 7)
                                    .background(range == r.0 ? Color.sgPrimary : Color.sgSurfaceAlt)
                                    .clipShape(Capsule())
                            }
                            .buttonStyle(.plain)
                        }
                    }

                    HStack(spacing: 10) {
                        Text("\u{f15c}")
                            .font(.custom("FontAwesome6Free-Solid", size: 18))
                            .foregroundColor(.sgTextMuted)
                        VStack(alignment: .leading, spacing: 2) {
                            Text("servisgo-backup-….csv")
                                .font(.custom("PlusJakartaSans-Bold", size: 12))
                                .foregroundColor(.sgTextPrimary)
                            Text("Format CSV (comma-separated)")
                                .font(.system(size: 10, weight: .regular, design: .monospaced))
                                .foregroundColor(.sgTextSubtle)
                        }
                        Spacer()
                    }
                    .padding(12)
                    .frame(maxWidth: .infinity, alignment: .leading)
                    .background(Color.sgSurfaceAlt)
                    .clipShape(RoundedRectangle(cornerRadius: 12))
                }
                .padding(.horizontal, 20)
                .padding(.vertical, 16)
            }

            Divider().background(Color.sgBorder)

            VStack(spacing: 4) {
                Button(action: shareCsv) {
                    HStack(spacing: 8) {
                        Text("\u{f1e0}")
                            .font(.custom("FontAwesome6Free-Solid", size: 16))
                            .foregroundColor(.white)
                        Text("Bagikan CSV")
                            .font(.custom("PlusJakartaSans-Bold", size: 15))
                            .foregroundColor(.white)
                    }
                    .frame(maxWidth: .infinity)
                    .frame(height: 50)
                    .background(Color.sgPrimary)
                    .clipShape(RoundedRectangle(cornerRadius: 14))
                }
                .buttonStyle(.plain)
                Button(action: onDismiss) {
                    Text("Batal")
                        .font(.custom("PlusJakartaSans-SemiBold", size: 13))
                        .foregroundColor(.sgTextMuted)
                        .frame(maxWidth: .infinity)
                        .frame(height: 44)
                }
                .buttonStyle(.plain)
            }
            .padding(.horizontal, 16)
            .padding(.bottom, 24)
        }
        .background(Color.sgSurface)
    }

    private func sectionLabel(_ text: String) -> some View {
        Text(text.uppercased())
            .font(.custom("PlusJakartaSans-ExtraBold", size: 11))
            .kerning(1)
            .foregroundColor(.sgTextMuted)
    }

    private func checkbox(on: Bool) -> some View {
        ZStack {
            RoundedRectangle(cornerRadius: 6)
                .strokeBorder(on ? Color.sgPrimary : Color.sgBorder, lineWidth: 2)
                .background(
                    RoundedRectangle(cornerRadius: 6)
                        .fill(on ? Color.sgPrimary : Color.clear)
                )
                .frame(width: 22, height: 22)
            if on {
                Text("\u{f00c}")
                    .font(.custom("FontAwesome6Free-Solid", size: 12))
                    .foregroundColor(.white)
            }
        }
    }

    private func shareCsv() {
        let selected = Set(picks.filter { $0.value }.keys.compactMap(Self.sectionKey))
        let rangeKey = Self.rangeKey(range)
        Task {
            do {
                let export = try await backup.export(sections: selected, range: rangeKey)
                if let url = writeBackupTempFile(export) {
                    onShare()
                    presentBackupShareSheet(items: [url]) { onDismiss() }
                } else {
                    onDismiss()
                }
            } catch {
                onDismiss()
            }
        }
    }

    private static func sectionKey(_ id: String) -> String? {
        switch id {
        case "veh": return "vehicles"
        case "hist": return "services"
        case "rem": return "reminders"
        case "comp": return "components"
        default: return nil
        }
    }

    private static func rangeKey(_ r: String) -> String {
        r == "year" ? "1y" : r
    }
}

// Simple FlowLayout for chip wrapping
struct FlowLayout: Layout {
    var spacing: CGFloat = 8

    func sizeThatFits(proposal: ProposedViewSize, subviews: Subviews, cache: inout ()) -> CGSize {
        let result = layout(proposal: proposal, subviews: subviews)
        return result.size
    }

    func placeSubviews(in bounds: CGRect, proposal: ProposedViewSize, subviews: Subviews, cache: inout ()) {
        let result = layout(proposal: proposal, subviews: subviews)
        for (offset, item) in result.positions.enumerated() {
            let sub = subviews[offset]
            sub.place(at: CGPoint(x: bounds.minX + item.x, y: bounds.minY + item.y), proposal: .unspecified)
        }
    }

    private func layout(proposal: ProposedViewSize, subviews: Subviews) -> (positions: [CGPoint], size: CGSize) {
        let maxWidth = proposal.width ?? .infinity
        var positions: [CGPoint] = []
        var rowWidth: CGFloat = 0
        var rowHeight: CGFloat = 0
        var totalHeight: CGFloat = 0

        for sub in subviews {
            let size = sub.sizeThatFits(.unspecified)
            if rowWidth + size.width > maxWidth && rowWidth > 0 {
                totalHeight += rowHeight + spacing
                rowWidth = 0
                rowHeight = 0
            }
            positions.append(CGPoint(x: rowWidth, y: totalHeight))
            rowWidth += size.width + spacing
            rowHeight = max(rowHeight, size.height)
        }
        totalHeight += rowHeight
        return (positions, CGSize(width: maxWidth, height: totalHeight))
    }
}
