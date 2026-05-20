import SwiftUI

struct RemindersView: View {
    var onOpenReminderDetail: () -> Void = {}
    var onAddReminder: () -> Void = {}

    @State private var selectedFilter: ReminderFilter = .all

    var body: some View {
        ScrollView {
            VStack(alignment: .leading, spacing: 0) {
                RemindersHeader(onAddReminder: onAddReminder)

                FilterChips(selected: $selectedFilter)
                    .padding(.horizontal, 20)
                    .padding(.bottom, 14)

                AdBannerSlot()
                    .padding(.bottom, 14)

                ReminderGroupHeader(label: "Telat — segera servis", accent: .sgDanger, count: 1)
                VStack(spacing: 8) {
                    ReminderListCard(
                        iconUnicode: "\u{f613}",
                        title: "Ganti Oli Mesin",
                        vehicle: "Beat Hitam",
                        dueDate: "20 Apr 2026",
                        daysLeftLabel: "+16h",
                        progress: 1.0,
                        urgency: .overdue,
                        onTap: onOpenReminderDetail
                    )
                }
                .padding(.horizontal, 16)
                .padding(.bottom, 18)

                ReminderGroupHeader(label: "Akan datang", accent: .sgWarning, count: 3)
                VStack(spacing: 8) {
                    ReminderListCard(
                        iconUnicode: "\u{f1ce}",
                        title: "Kampas Rem",
                        vehicle: "Beat Hitam",
                        dueDate: "25 Mei 2026",
                        daysLeftLabel: "19h",
                        progress: 0.97,
                        urgency: .soon,
                        onTap: onOpenReminderDetail
                    )
                    ReminderListCard(
                        iconUnicode: "\u{f613}",
                        title: "Ganti Oli Mesin",
                        vehicle: "Vario Merah",
                        dueDate: "12 Mei 2026",
                        daysLeftLabel: "6h",
                        progress: 0.9,
                        urgency: .soon,
                        onTap: onOpenReminderDetail
                    )
                    ReminderListCard(
                        iconUnicode: "\u{f0ad}",
                        title: "Servis Berkala",
                        vehicle: "Brio Biru",
                        dueDate: "9 Mei 2026",
                        daysLeftLabel: "3h",
                        progress: 0.98,
                        urgency: .soon,
                        onTap: onOpenReminderDetail
                    )
                }
                .padding(.horizontal, 16)
                .padding(.bottom, 18)

                ReminderGroupHeader(label: "Aman", accent: .sgPrimary, count: 2)
                VStack(spacing: 8) {
                    ReminderListCard(
                        iconUnicode: "\u{f0b0}",
                        title: "Filter Oli & Udara",
                        vehicle: "Avanza Putih",
                        dueDate: "10 Jul 2026",
                        daysLeftLabel: "65h",
                        progress: 0.95,
                        urgency: .ok,
                        onTap: onOpenReminderDetail
                    )
                    ReminderListCard(
                        iconUnicode: "\u{f5df}",
                        title: "Aki",
                        vehicle: "Avanza Putih",
                        dueDate: "1 Sep 2026",
                        daysLeftLabel: "118h",
                        progress: 0.4,
                        urgency: .ok,
                        onTap: onOpenReminderDetail
                    )
                }
                .padding(.horizontal, 16)
                .padding(.bottom, 24)
            }
        }
    }
}

enum ReminderFilter: String, CaseIterable {
    case all = "Semua"
    case overdue = "Telat"
    case soon = "Soon"
    case ok = "Aman"
}

private struct RemindersHeader: View {
    let onAddReminder: () -> Void

    var body: some View {
        HStack(alignment: .center) {
            VStack(alignment: .leading, spacing: 4) {
                Text("6 pengingat aktif")
                    .font(.custom("PlusJakartaSans-Medium", size: 13))
                    .foregroundColor(.sgTextMuted)
                Text("Pengingat Servis")
                    .font(.custom("PlusJakartaSans-ExtraBold", size: 26))
                    .foregroundColor(.sgTextPrimary)
                    .kerning(-0.4)
            }
            Spacer()
            Button(action: onAddReminder) {
                Text("\u{2b}")
                    .font(.custom("FontAwesome6Free-Solid", size: 16))
                    .foregroundColor(.sgPrimary)
                    .frame(width: 40, height: 40)
                    .background(Color.sgSurface)
                    .clipShape(RoundedRectangle(cornerRadius: 12))
                    .overlay(
                        RoundedRectangle(cornerRadius: 12)
                            .strokeBorder(Color.sgBorder, lineWidth: 1)
                    )
            }
            .buttonStyle(.plain)
        }
        .padding(EdgeInsets(top: 12, leading: 20, bottom: 16, trailing: 16))
    }
}

private struct FilterChips: View {
    @Binding var selected: ReminderFilter

    var body: some View {
        HStack(spacing: 8) {
            ForEach(ReminderFilter.allCases, id: \.self) { f in
                Button { selected = f } label: {
                    Text(f.rawValue)
                        .font(.custom("PlusJakartaSans-Bold", size: 12))
                        .foregroundColor(selected == f ? .white : .sgTextMuted)
                        .padding(.horizontal, 14)
                        .padding(.vertical, 8)
                        .background(selected == f ? Color.sgPrimary : Color.sgSurface)
                        .clipShape(Capsule())
                        .overlay(
                            Capsule().stroke(
                                selected == f ? .clear : Color.sgBorder,
                                lineWidth: 1
                            )
                        )
                }
                .buttonStyle(.plain)
            }
            Spacer()
        }
    }
}

private struct ReminderGroupHeader: View {
    let label: String
    let accent: Color
    let count: Int

    var body: some View {
        HStack(spacing: 8) {
            Circle().fill(accent).frame(width: 6, height: 6)
            Text("\(label.uppercased()) (\(count))")
                .font(.custom("PlusJakartaSans-ExtraBold", size: 11))
                .kerning(1)
                .foregroundColor(.sgTextMuted)
        }
        .padding(.horizontal, 24)
        .padding(.bottom, 8)
        .padding(.top, 8)
    }
}

private struct ReminderListCard: View {
    let iconUnicode: String
    let title: String
    let vehicle: String
    let dueDate: String
    let daysLeftLabel: String
    let progress: Double
    let urgency: ReminderUrgency
    let onTap: () -> Void

    var body: some View {
        Button(action: onTap) {
            HStack(spacing: 12) {
                IconBadge(
                    iconUnicode: iconUnicode,
                    foreground: urgency.color,
                    background: urgency.softColor
                )

                VStack(alignment: .leading, spacing: 2) {
                    HStack(alignment: .firstTextBaseline) {
                        Text(title)
                            .font(.custom("PlusJakartaSans-Bold", size: 14))
                            .foregroundColor(.sgTextPrimary)
                        Spacer()
                        Text(daysLeftLabel)
                            .font(.system(size: 11, weight: .bold, design: .monospaced))
                            .foregroundColor(urgency.color)
                    }
                    Text("\(vehicle) · \(dueDate)")
                        .font(.custom("PlusJakartaSans-Medium", size: 12))
                        .foregroundColor(.sgTextMuted)
                    GeometryReader { geo in
                        ZStack(alignment: .leading) {
                            Capsule()
                                .fill(Color.sgSurfaceAlt)
                                .frame(height: 4)
                            Capsule()
                                .fill(urgency.color)
                                .frame(width: geo.size.width * progress, height: 4)
                        }
                    }
                    .frame(height: 4)
                    .padding(.top, 6)
                }
            }
            .padding(14)
            .background(Color.sgSurface)
            .clipShape(RoundedRectangle(cornerRadius: 18))
            .overlay(
                RoundedRectangle(cornerRadius: 18)
                    .strokeBorder(Color.sgBorder, lineWidth: 1)
            )
        }
        .buttonStyle(.plain)
    }
}

#Preview {
    RemindersView()
        .background(Color.sgBgWarm)
}
