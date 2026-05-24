import SwiftUI
import Shared

struct RemindersView: View {
    var onOpenReminderDetail: () -> Void = {}
    var onAddReminder: () -> Void = {}
    var isRefreshing: Bool = false

    @ObservedObject private var reminders = ReminderListModel.shared
    @ObservedObject private var vehicles = VehicleListModel.shared

    private var isLoading: Bool { reminders.state.isLoading }
    private var isEmpty: Bool { reminders.state.isEmpty }
    private var activeCount: Int { reminders.state.reminders.count }

    private var vehicleById: [String: Vehicle] {
        Dictionary(uniqueKeysWithValues: vehicles.state.vehicles.map { ($0.id, $0) })
    }

    private var selectedFilter: ReminderFilterChip {
        let urgencies = reminders.state.urgencyFilter
        if urgencies.count != 1 { return .all }
        let only = urgencies.first
        if only == Shared.ReminderUrgency.overdue { return .overdue }
        if only == Shared.ReminderUrgency.soon { return .soon }
        if only == Shared.ReminderUrgency.ok { return .ok }
        return .all
    }
    private var searchQuery: String { reminders.state.query }

    var body: some View {
        if isLoading {
            VStack(alignment: .leading, spacing: 0) {
                RemindersHeader(activeCount: 0, onAddReminder: onAddReminder)
                Skeleton.Row(leading: .icon)
                Skeleton.Row(leading: .icon)
                Skeleton.Row(leading: .icon)
                Spacer()
            }
        } else if isEmpty {
            VStack(alignment: .leading, spacing: 0) {
                RemindersHeader(activeCount: 0, onAddReminder: onAddReminder)
                EmptyState(
                    iconUnicode: "\u{f0f3}",
                    title: "Belum ada pengingat aktif",
                    body: "Buat pengingat berdasarkan KM atau tanggal supaya servis tepat waktu.",
                    ctaLabel: "+ Buat Pengingat",
                    onCta: onAddReminder
                )
                .frame(maxWidth: .infinity, maxHeight: .infinity)
            }
        } else {
            scrollContent
        }
    }

    private var scrollContent: some View {
        let grouped = Dictionary(grouping: reminders.state.reminders, by: { $0.urgency })
        let overdue = grouped[Shared.ReminderUrgency.overdue] ?? []
        let soon = grouped[Shared.ReminderUrgency.soon] ?? []
        let ok = grouped[Shared.ReminderUrgency.ok] ?? []

        return ScrollView {
            VStack(alignment: .leading, spacing: 0) {
                RemindersHeader(activeCount: activeCount, onAddReminder: onAddReminder)

                StickySearchHeader(
                    placeholder: "Cari pengingat…",
                    text: Binding(
                        get: { searchQuery },
                        set: { reminders.vm.setQuery(query: $0) }
                    )
                )

                if isRefreshing {
                    PullRefreshIndicator(state: .refreshing)
                }

                FilterChips(
                    selected: selectedFilter,
                    onSelect: { chip in
                        let domain: Set<Shared.ReminderUrgency>
                        switch chip {
                        case .all: domain = []
                        case .overdue: domain = [Shared.ReminderUrgency.overdue]
                        case .soon: domain = [Shared.ReminderUrgency.soon]
                        case .ok: domain = [Shared.ReminderUrgency.ok]
                        }
                        reminders.vm.setUrgencyFilter(urgencies: domain)
                    }
                )
                    .padding(.horizontal, 20)
                    .padding(.bottom, 14)

                AdBannerSlot()
                    .padding(.bottom, 14)

                ReminderGroup(
                    label: "Telat — segera servis",
                    accent: .sgDanger,
                    reminders: overdue,
                    vehicleById: vehicleById,
                    onOpenDetail: onOpenReminderDetail
                )
                ReminderGroup(
                    label: "Akan datang",
                    accent: .sgWarning,
                    reminders: soon,
                    vehicleById: vehicleById,
                    onOpenDetail: onOpenReminderDetail
                )
                ReminderGroup(
                    label: "Aman",
                    accent: .sgPrimary,
                    reminders: ok,
                    vehicleById: vehicleById,
                    onOpenDetail: onOpenReminderDetail
                )
                Spacer().frame(height: 24)
            }
        }
    }
}

enum ReminderFilterChip: String, CaseIterable {
    case all = "Semua"
    case overdue = "Telat"
    case soon = "Soon"
    case ok = "Aman"
}

private struct RemindersHeader: View {
    let activeCount: Int
    let onAddReminder: () -> Void

    var body: some View {
        HStack(alignment: .center) {
            VStack(alignment: .leading, spacing: 4) {
                Text("\(activeCount) pengingat aktif")
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
    let selected: ReminderFilterChip
    let onSelect: (ReminderFilterChip) -> Void

    var body: some View {
        HStack(spacing: 8) {
            ForEach(ReminderFilterChip.allCases, id: \.self) { f in
                Button { onSelect(f) } label: {
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

private struct ReminderGroup: View {
    let label: String
    let accent: Color
    let reminders: [Reminder]
    let vehicleById: [String: Vehicle]
    let onOpenDetail: () -> Void

    var body: some View {
        if reminders.isEmpty {
            EmptyView()
        } else {
            VStack(alignment: .leading, spacing: 0) {
                ReminderGroupHeader(label: label, accent: accent, count: reminders.count)
                VStack(spacing: 8) {
                    ForEach(reminders, id: \.id) { reminder in
                        ReminderListCard(
                            reminder: reminder,
                            vehicle: vehicleById[reminder.vehicleId],
                            onTap: onOpenDetail
                        )
                    }
                }
                .padding(.horizontal, 16)
                .padding(.bottom, 18)
            }
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
    let reminder: Reminder
    let vehicle: Vehicle?
    let onTap: () -> Void

    private var urgency: ReminderUrgency { toUiUrgency(reminder.urgency) }
    private var urgencyLabel: String {
        switch reminder.urgency {
        case Shared.ReminderUrgency.overdue: return "Telat"
        case Shared.ReminderUrgency.soon: return "Segera"
        case Shared.ReminderUrgency.ok: return "Aman"
        default: return "Aman"
        }
    }
    private var progress: Double {
        switch reminder.urgency {
        case Shared.ReminderUrgency.overdue: return 1.0
        case Shared.ReminderUrgency.soon: return 0.9
        case Shared.ReminderUrgency.ok: return 0.4
        default: return 0.4
        }
    }
    private var vehicleLabel: String { vehicle?.displayTitle ?? "—" }
    private var triggerLabel: String {
        if let byKm = reminder.trigger as? ReminderTrigger.ByKm {
            return "@ \(byKm.targetOdometer) km"
        } else if let byBoth = reminder.trigger as? ReminderTrigger.ByBoth {
            return "@ \(byBoth.targetOdometer) km / date"
        } else {
            return "due date set"
        }
    }

    var body: some View {
        Button(action: onTap) {
            HStack(spacing: 12) {
                IconBadge(
                    iconUnicode: "\u{f0f3}",
                    foreground: urgency.color,
                    background: urgency.softColor
                )

                VStack(alignment: .leading, spacing: 2) {
                    HStack(alignment: .firstTextBaseline) {
                        Text(reminder.title)
                            .font(.custom("PlusJakartaSans-Bold", size: 14))
                            .foregroundColor(.sgTextPrimary)
                        Spacer()
                        Text(urgencyLabel)
                            .font(.system(size: 11, weight: .bold, design: .monospaced))
                            .foregroundColor(urgency.color)
                    }
                    Text("\(vehicleLabel) · \(triggerLabel)")
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
