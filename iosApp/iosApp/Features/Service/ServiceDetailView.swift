import SwiftUI
import Shared

struct ServiceDetailView: View {
    let recordId: String
    var onEdit: () -> Void = {}
    var onOpenNextReminder: () -> Void = {}

    @StateObject private var model = ServiceDetailModel()

    private static let dateFormatter: DateFormatter = {
        let df = DateFormatter()
        df.locale = Locale(identifier: "id_ID")
        df.dateFormat = "d MMM yyyy"
        return df
    }()

    var body: some View {
        ScrollView {
            VStack(alignment: .leading, spacing: 0) {
                if let record = model.state.record {
                    let meta = ServiceTypeMeta.for(key: record.serviceType.key)
                    let vehicleLine: String = {
                        guard let v = model.state.vehicle else { return "Kendaraan dihapus" }
                        let plate = v.plateNumber.isEmpty ? nil : v.plateNumber
                        return plate.map { "\(v.displayTitle) · \($0)" } ?? v.displayTitle
                    }()

                    HStack(spacing: 14) {
                        IconBadge(
                            iconUnicode: meta.icon,
                            foreground: meta.color,
                            background: meta.color.opacity(0.15),
                            size: 56, iconSize: 28, corner: 16
                        )
                        VStack(alignment: .leading, spacing: 2) {
                            Text(meta.label.replacingOccurrences(of: "\n", with: " "))
                                .font(.custom("PlusJakartaSans-ExtraBold", size: 22))
                                .foregroundColor(.sgTextPrimary)
                                .kerning(-0.3)
                            Text(vehicleLine)
                                .font(.custom("PlusJakartaSans-Medium", size: 13))
                                .foregroundColor(.sgTextMuted)
                        }
                        Spacer()
                    }
                    .padding(.horizontal, 20)
                    .padding(.top, 12)

                    let dateText = Self.dateFormatter.string(from: Date(timeIntervalSince1970: TimeInterval(record.serviceDate) / 1000.0))
                    let kmText = "\(formatGrouped(record.odometer)) km"
                    let workshopText = (record.workshop?.isEmpty ?? true) ? "—" : (record.workshop ?? "—")
                    let costText = "Rp \(formatGrouped(record.cost))"

                    VStack(spacing: 0) {
                        FactRow(icon: "\u{f783}", value: dateText, monospaced: false)
                        Divider().padding(.horizontal, 16)
                        FactRow(icon: "\u{f624}", value: kmText, monospaced: true)
                        Divider().padding(.horizontal, 16)
                        FactRow(icon: "\u{f3c5}", value: workshopText, monospaced: false)
                        Divider().padding(.horizontal, 16)
                        FactRow(icon: "\u{f613}", value: costText, monospaced: true)
                    }
                    .background(Color.sgSurface)
                    .clipShape(RoundedRectangle(cornerRadius: 18))
                    .overlay(
                        RoundedRectangle(cornerRadius: 18)
                            .strokeBorder(Color.sgBorder, lineWidth: 1)
                    )
                    .padding(.horizontal, 16)
                    .padding(.top, 16)

                    if let note = record.note, !note.isEmpty {
                        Text("CATATAN")
                            .font(.custom("PlusJakartaSans-ExtraBold", size: 11))
                            .kerning(1)
                            .foregroundColor(.sgTextMuted)
                            .padding(.horizontal, 20)
                            .padding(.top, 20)
                            .padding(.bottom, 8)
                            .frame(maxWidth: .infinity, alignment: .leading)
                        Text(note)
                            .font(.custom("PlusJakartaSans-Medium", size: 14))
                            .foregroundColor(.sgTextPrimary)
                            .lineSpacing(5)
                            .frame(maxWidth: .infinity, alignment: .leading)
                            .padding(.horizontal, 16)
                            .padding(.vertical, 14)
                            .background(Color.sgSurface)
                            .clipShape(RoundedRectangle(cornerRadius: 18))
                            .overlay(
                                RoundedRectangle(cornerRadius: 18)
                                    .strokeBorder(Color.sgBorder, lineWidth: 1)
                            )
                            .padding(.horizontal, 16)
                    }

                    if !record.componentIds.isEmpty {
                        Text("KOMPONEN YANG DISERVIS")
                            .font(.custom("PlusJakartaSans-ExtraBold", size: 11))
                            .kerning(1)
                            .foregroundColor(.sgTextMuted)
                            .padding(.horizontal, 20)
                            .padding(.top, 20)
                            .padding(.bottom, 8)
                            .frame(maxWidth: .infinity, alignment: .leading)
                        VStack(spacing: 0) {
                            ForEach(Array(record.componentIds.enumerated()), id: \.offset) { _, id in
                                let item = componentMetaLookup.first { $0.id == id }
                                HStack(spacing: 12) {
                                    IconBadge(
                                        iconUnicode: item?.iconUnicode ?? "\u{f0ad}",
                                        foreground: item?.color ?? .sgPrimary,
                                        background: (item?.color ?? .sgPrimary).opacity(0.13),
                                        size: 36, iconSize: 16, corner: 10
                                    )
                                    Text(item?.label ?? id)
                                        .font(.custom("PlusJakartaSans-SemiBold", size: 14))
                                        .foregroundColor(.sgTextPrimary)
                                    Spacer()
                                }
                                .padding(.horizontal, 14)
                                .padding(.vertical, 12)
                            }
                        }
                        .background(Color.sgSurface)
                        .clipShape(RoundedRectangle(cornerRadius: 18))
                        .overlay(
                            RoundedRectangle(cornerRadius: 18)
                                .strokeBorder(Color.sgBorder, lineWidth: 1)
                        )
                        .padding(.horizontal, 16)
                    }

                    Button(action: onEdit) {
                        HStack(spacing: 8) {
                            Text("\u{f044}")
                                .font(.custom("FontAwesome6Free-Solid", size: 13))
                            Text("Edit servis")
                                .font(.custom("PlusJakartaSans-Bold", size: 14))
                        }
                        .foregroundColor(.sgTextPrimary)
                        .frame(maxWidth: .infinity)
                        .frame(height: 50)
                        .background(Color.sgSurface)
                        .clipShape(RoundedRectangle(cornerRadius: 14))
                        .overlay(
                            RoundedRectangle(cornerRadius: 14)
                                .strokeBorder(Color.sgBorder, lineWidth: 1.5)
                        )
                    }
                    .buttonStyle(.plain)
                    .padding(.horizontal, 16)
                    .padding(.top, 24)
                    .padding(.bottom, 24)
                } else {
                    Text(model.state.isLoading ? "Memuat…" : "Catatan tidak ditemukan")
                        .font(.custom("PlusJakartaSans-Medium", size: 14))
                        .foregroundColor(.sgTextMuted)
                        .padding(40)
                        .frame(maxWidth: .infinity)
                }
            }
        }
        .background(Color.sgBgWarm)
        .navigationTitle("Detail Servis")
        .navigationBarTitleDisplayMode(.inline)
        .onAppear { model.load(recordId: recordId) }
    }

    private func formatGrouped(_ value: Int64) -> String {
        let formatter = NumberFormatter()
        formatter.numberStyle = .decimal
        formatter.groupingSeparator = "."
        return formatter.string(from: NSNumber(value: value)) ?? "\(value)"
    }
}

private struct FactRow: View {
    let icon: String
    let value: String
    let monospaced: Bool

    var body: some View {
        HStack(spacing: 14) {
            Text(icon)
                .font(.custom("FontAwesome6Free-Solid", size: 16))
                .foregroundColor(.sgTextMuted)
                .frame(width: 18)
            if monospaced {
                Text(value)
                    .font(.system(size: 14, weight: .semibold, design: .monospaced))
                    .foregroundColor(.sgTextPrimary)
            } else {
                Text(value)
                    .font(.custom("PlusJakartaSans-SemiBold", size: 14))
                    .foregroundColor(.sgTextPrimary)
            }
            Spacer()
        }
        .padding(.horizontal, 16)
        .padding(.vertical, 14)
    }
}

struct ServiceTypeMeta {
    let icon: String
    let label: String
    let color: Color

    static func `for`(key: String) -> ServiceTypeMeta {
        switch key {
        case "oli": return ServiceTypeMeta(icon: "\u{f613}", label: "Ganti Oli Mesin", color: Color(red: 0.91, green: 0.61, blue: 0.18))
        case "filter": return ServiceTypeMeta(icon: "\u{f0b0}", label: "Filter Oli & Udara", color: Color(red: 0.48, green: 0.44, blue: 0.91))
        case "ban": return ServiceTypeMeta(icon: "\u{f1cd}", label: "Rotasi/Ganti Ban", color: Color(red: 0.25, green: 0.30, blue: 0.36))
        case "aki": return ServiceTypeMeta(icon: "\u{f5df}", label: "Aki", color: Color(red: 0.84, green: 0.27, blue: 0.23))
        case "rem": return ServiceTypeMeta(icon: "\u{f1ce}", label: "Kampas Rem", color: Color(red: 0.18, green: 0.55, blue: 0.34))
        case "radiator": return ServiceTypeMeta(icon: "\u{f2c9}", label: "Radiator/Coolant", color: Color(red: 0.25, green: 0.69, blue: 0.84))
        case "tune_up": return ServiceTypeMeta(icon: "\u{f0e7}", label: "Tune-up", color: Color(red: 0.91, green: 0.71, blue: 0.18))
        default: return ServiceTypeMeta(icon: "\u{f0ad}", label: "Servis", color: .sgPrimary)
        }
    }
}

struct TrackedComponentMeta {
    let id: String
    let label: String
    let iconUnicode: String
    let color: Color
}

let componentMetaLookup: [TrackedComponentMeta] = [
    .init(id: "oli_mesin", label: "Oli mesin", iconUnicode: "\u{f613}", color: Color(red: 0.91, green: 0.61, blue: 0.18)),
    .init(id: "filter_oli", label: "Filter oli", iconUnicode: "\u{f0b0}", color: Color(red: 0.48, green: 0.44, blue: 0.91)),
    .init(id: "filter_udara", label: "Filter udara", iconUnicode: "\u{f0b0}", color: Color(red: 0.48, green: 0.44, blue: 0.91)),
    .init(id: "busi", label: "Busi & tune-up", iconUnicode: "\u{f0e7}", color: Color(red: 0.91, green: 0.71, blue: 0.18)),
    .init(id: "aki", label: "Aki", iconUnicode: "\u{f5df}", color: Color(red: 0.84, green: 0.27, blue: 0.23)),
    .init(id: "kampas_rem", label: "Kampas rem", iconUnicode: "\u{f1ce}", color: .sgPrimary),
    .init(id: "ban", label: "Ban", iconUnicode: "\u{f1cd}", color: Color(red: 0.25, green: 0.30, blue: 0.36)),
    .init(id: "radiator", label: "Radiator", iconUnicode: "\u{f2c9}", color: Color(red: 0.25, green: 0.69, blue: 0.84)),
]
