import SwiftUI
import Shared

struct HomeView: View {
    var userName: String = ""
    var onOpenReminders: () -> Void = {}
    var onOpenReminderDetail: (String) -> Void = { _ in }
    var onOpenVehicleDetail: () -> Void = {}
    var onOpenVehicleList: () -> Void = {}
    var onAddService: () -> Void = {}
    var onAddVehicle: () -> Void = {}
    var onUpdateOdometer: () -> Void = {}

    @ObservedObject private var vehicles = VehicleListModel.shared
    @ObservedObject private var reminders = ReminderListModel.shared

    private var isLoading: Bool { vehicles.state.isLoading || reminders.state.isLoading }
    private var isEmpty: Bool { vehicles.state.isEmpty }

    var body: some View {
        Group {
            if isLoading {
                VStack(alignment: .leading, spacing: 0) {
                    HomeHeader(userName: userName, onOpenReminders: onOpenReminders)
                    Skeleton.Card(height: 150)
                    Skeleton.Row(leading: .icon)
                    Skeleton.Row(leading: .icon)
                    Skeleton.Row(leading: .icon)
                    Spacer()
                }
            } else if isEmpty {
                VStack(alignment: .leading, spacing: 0) {
                    HomeHeader(userName: userName, onOpenReminders: onOpenReminders)
                    EmptyState(
                        iconUnicode: "\u{f21c}",
                        title: "Belum ada kendaraan",
                        body: "Tambah motor atau mobilmu untuk mulai catat servis & dapat pengingat.",
                        ctaLabel: "+ Tambah Kendaraan",
                        onCta: onAddVehicle
                    )
                    .frame(maxWidth: .infinity, maxHeight: .infinity)
                }
            } else {
                scrollContent
            }
        }
    }

    private var scrollContent: some View {
        ScrollView {
            VStack(alignment: .leading, spacing: 0) {
                HomeHeader(userName: userName, onOpenReminders: onOpenReminders)

                let vehicleById = Dictionary(uniqueKeysWithValues: vehicles.state.vehicles.map { ($0.id, $0) })
                if let hero = pickHeroVehicle(vehicles.state.vehicles, reminders.state.reminders, vehicleById: vehicleById) {
                    let overdueCount = reminders.state.reminders
                        .filter { $0.vehicleId == hero.id && $0.urgency == .overdue }
                        .count
                    HeroStatusCard(
                        vehicle: hero,
                        overdueCount: Int(overdueCount),
                        onOpenVehicleDetail: onOpenVehicleDetail
                    )
                    .padding(.horizontal, 16)
                    .padding(.bottom, 16)
                }

                AdBannerSlot()
                    .padding(.bottom, 8)

                QuickActionsGrid(
                    onAddService: onAddService,
                    onUpdateOdometer: onUpdateOdometer,
                    onAddVehicle: onAddVehicle
                )
                .padding(.horizontal, 16)
                .padding(.bottom, 16)

                SectionHeading(title: "Pengingat aktif", actionLabel: "Lihat semua", action: onOpenReminders)

                VStack(spacing: 8) {
                    let topReminders = Array(reminders.state.reminders.prefix(3))
                    if topReminders.isEmpty {
                        ReminderEmptyHint()
                    } else {
                        ForEach(topReminders, id: \.id) { reminder in
                            ReminderRow(
                                title: reminder.title,
                                subtitle: reminderSubtitle(reminder, vehicle: vehicleById[reminder.vehicleId]),
                                urgency: toUiUrgency(reminder.urgency),
                                onTap: { onOpenReminderDetail(reminder.id) }
                            )
                        }
                    }
                }
                .padding(.horizontal, 16)
                .padding(.bottom, 12)

                NativeAdCard()
                    .padding(.bottom, 12)

                SectionHeading(title: "Kendaraan saya", actionLabel: "Lihat semua", action: onOpenVehicleList)

                VStack(spacing: 8) {
                    ForEach(Array(vehicles.state.vehicles.prefix(4)), id: \.id) { vehicle in
                        VehicleSummaryRow(vehicle: vehicle, onTap: onOpenVehicleDetail)
                    }
                }
                .padding(.horizontal, 16)
                .padding(.bottom, 16)
            }
        }
    }
}

// MARK: - Subviews

private struct HomeHeader: View {
    let userName: String
    let onOpenReminders: () -> Void

    private var greetingName: String { userName.isEmpty ? "Kamu" : userName }

    var body: some View {
        HStack(alignment: .center) {
            VStack(alignment: .leading, spacing: 4) {
                Text("Halo, \(greetingName) 👋")
                    .font(.custom("PlusJakartaSans-Medium", size: 13))
                    .foregroundColor(.sgTextMuted)
                Text("Garasi Saya")
                    .font(.custom("PlusJakartaSans-ExtraBold", size: 22))
                    .foregroundColor(.sgTextPrimary)
                    .kerning(-0.4)
            }
            Spacer()
            Button(action: onOpenReminders) {
                ZStack {
                    Text("\u{f0f3}")
                        .font(.custom("FontAwesome6Free-Solid", size: 18))
                        .foregroundColor(.sgTextPrimary)
                        .frame(width: 44, height: 44)
                        .background(Color.sgSurface)
                        .clipShape(RoundedRectangle(cornerRadius: 14))
                        .overlay(
                            RoundedRectangle(cornerRadius: 14)
                                .strokeBorder(Color.sgBorder, lineWidth: 1)
                        )
                    Circle()
                        .fill(Color.sgDanger)
                        .frame(width: 10, height: 10)
                        .overlay(Circle().stroke(Color.sgSurface, lineWidth: 2))
                        .offset(x: 10, y: -10)
                }
            }
            .buttonStyle(.plain)
        }
        .padding(EdgeInsets(top: 12, leading: 20, bottom: 20, trailing: 20))
    }
}

private struct HeroStatusCard: View {
    let vehicle: Vehicle
    let overdueCount: Int
    let onOpenVehicleDetail: () -> Void

    private var icon: String {
        vehicle.type == VehicleType.mobil ? "\u{f1b9}" : "\u{f21c}"
    }
    private var headline: String {
        switch overdueCount {
        case 0: return "Servis terpantau — semua aman"
        case 1: return "1 servis telat — segera bawa ke bengkel"
        default: return "\(overdueCount) servis telat — segera bawa ke bengkel"
        }
    }
    private var tone: Color { overdueCount > 0 ? .sgDanger : .sgPrimary }

    var body: some View {
        ZStack(alignment: .topTrailing) {
            VStack(alignment: .leading, spacing: 0) {
                HStack(spacing: 8) {
                    Text(icon)
                        .font(.custom("FontAwesome6Free-Solid", size: 14))
                        .foregroundColor(.white.opacity(0.95))
                    Text(vehicle.displayTitle)
                        .font(.custom("PlusJakartaSans-SemiBold", size: 12))
                        .foregroundColor(.white.opacity(0.9))
                }

                Text(headline)
                    .font(.custom("PlusJakartaSans-ExtraBold", size: 22))
                    .foregroundColor(.white)
                    .kerning(-0.4)
                    .lineSpacing(2)
                    .padding(.top, 6)

                Divider().background(Color.white.opacity(0.25)).padding(.top, 14)

                HStack(alignment: .bottom, spacing: 16) {
                    VStack(alignment: .leading, spacing: 2) {
                        Text("KM saat ini")
                            .font(.custom("PlusJakartaSans-Regular", size: 11))
                            .foregroundColor(.white.opacity(0.85))
                        Text(formatKm(vehicle.odometer))
                            .font(.system(size: 18, weight: .bold, design: .monospaced))
                            .foregroundColor(.white)
                    }
                    VStack(alignment: .leading, spacing: 2) {
                        Text("Plat")
                            .font(.custom("PlusJakartaSans-Regular", size: 11))
                            .foregroundColor(.white.opacity(0.85))
                        Text(vehicle.plateNumber.isEmpty ? "—" : vehicle.plateNumber)
                            .font(.system(size: 16, weight: .bold, design: .monospaced))
                            .foregroundColor(.white)
                    }
                    Spacer()
                    Button(action: onOpenVehicleDetail) {
                        Text("Detail →")
                            .font(.custom("PlusJakartaSans-Bold", size: 12))
                            .foregroundColor(.white)
                            .padding(.horizontal, 14)
                            .padding(.vertical, 8)
                            .background(Color.white.opacity(0.22))
                            .clipShape(Capsule())
                    }
                    .buttonStyle(.plain)
                }
                .padding(.top, 14)
            }
            .padding(20)
            .frame(maxWidth: .infinity, alignment: .leading)
            .background(tone)
            .clipShape(RoundedRectangle(cornerRadius: 24))
        }
    }
}

private struct QuickActionsGrid: View {
    let onAddService: () -> Void
    let onUpdateOdometer: () -> Void
    let onAddVehicle: () -> Void

    var body: some View {
        HStack(spacing: 10) {
            QuickActionTile(
                icon: "+",
                label: "Catat Servis",
                primary: true,
                action: onAddService
            )
            QuickActionTile(
                icon: "\u{f625}",
                label: "Update KM",
                primary: false,
                action: onUpdateOdometer
            )
            QuickActionTile(
                icon: "\u{f1b9}",
                label: "Tambah",
                primary: false,
                action: onAddVehicle
            )
        }
    }
}

private struct QuickActionTile: View {
    let icon: String
    let label: String
    let primary: Bool
    let action: () -> Void

    var body: some View {
        Button(action: action) {
            VStack(spacing: 6) {
                if icon == "+" {
                    Text("+")
                        .font(.custom("PlusJakartaSans-ExtraBold", size: 22))
                        .foregroundColor(primary ? .white : .sgTextPrimary)
                } else {
                    Text(icon)
                        .font(.custom("FontAwesome6Free-Solid", size: 20))
                        .foregroundColor(primary ? .white : .sgTextPrimary)
                }
                Text(label)
                    .font(.custom("PlusJakartaSans-SemiBold", size: 11))
                    .foregroundColor(primary ? .white : .sgTextPrimary)
                    .multilineTextAlignment(.center)
            }
            .frame(maxWidth: .infinity)
            .padding(.vertical, 12)
            .background(primary ? Color.sgPrimary : Color.sgSurface)
            .clipShape(RoundedRectangle(cornerRadius: 16))
            .overlay(
                RoundedRectangle(cornerRadius: 16)
                    .strokeBorder(primary ? Color.clear : Color.sgBorder, lineWidth: 1)
            )
        }
        .buttonStyle(.plain)
    }
}

private struct SectionHeading: View {
    let title: String
    let actionLabel: String?
    let action: () -> Void

    var body: some View {
        HStack(alignment: .center) {
            Text(title)
                .font(.custom("PlusJakartaSans-ExtraBold", size: 16))
                .foregroundColor(.sgTextPrimary)
            Spacer()
            if let label = actionLabel {
                Button(action: action) {
                    Text(label)
                        .font(.custom("PlusJakartaSans-Bold", size: 13))
                        .foregroundColor(.sgPrimary)
                }
                .buttonStyle(.plain)
            }
        }
        .padding(.horizontal, 20)
        .padding(.bottom, 8)
    }
}

private struct ReminderRow: View {
    let title: String
    let subtitle: String
    let urgency: ReminderUrgency
    let onTap: () -> Void

    var body: some View {
        Button(action: onTap) {
            HStack(spacing: 12) {
                IconBadge(
                    iconUnicode: "\u{f0f3}",
                    foreground: urgency.color,
                    background: urgency.softColor
                )
                VStack(alignment: .leading, spacing: 2) {
                    Text(title)
                        .font(.custom("PlusJakartaSans-Bold", size: 14))
                        .foregroundColor(.sgTextPrimary)
                    Text(subtitle)
                        .font(.custom("PlusJakartaSans-Medium", size: 12))
                        .foregroundColor(.sgTextMuted)
                }
                Spacer()
                StatusPill(urgency: urgency)
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

private struct VehicleSummaryRow: View {
    let vehicle: Vehicle
    let onTap: () -> Void

    private var icon: String { vehicle.type == VehicleType.mobil ? "\u{f1b9}" : "\u{f21c}" }
    private var accent: Color {
        hexColor(PresentationFactory.shared.vehicleColorHex(vehicle: vehicle)) ?? .sgPrimary
    }
    private var plate: String { vehicle.plateNumber.isEmpty ? "—" : vehicle.plateNumber }
    private var plateAndKm: String { "\(plate) · \(formatKm(vehicle.odometer)) km" }

    var body: some View {
        Button(action: onTap) {
            HStack(spacing: 12) {
                IconBadge(
                    iconUnicode: icon,
                    foreground: accent,
                    background: accent.opacity(0.13),
                    size: 48,
                    iconSize: 24
                )
                VStack(alignment: .leading, spacing: 2) {
                    Text(vehicle.displayTitle)
                        .font(.custom("PlusJakartaSans-Bold", size: 14))
                        .foregroundColor(.sgTextPrimary)
                    Text(plateAndKm)
                        .font(.system(size: 12, weight: .medium, design: .monospaced))
                        .foregroundColor(.sgTextMuted)
                }
                Spacer()
                Text("\u{f054}")
                    .font(.custom("FontAwesome6Free-Solid", size: 14))
                    .foregroundColor(.sgTextSubtle)
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

private struct ReminderEmptyHint: View {
    var body: some View {
        Text("Belum ada pengingat aktif. Tambah dari halaman Pengingat.")
            .font(.custom("PlusJakartaSans-Medium", size: 12))
            .foregroundColor(.sgTextMuted)
            .frame(maxWidth: .infinity, alignment: .leading)
            .padding(14)
            .background(Color.sgSurface)
            .clipShape(RoundedRectangle(cornerRadius: 18))
            .overlay(
                RoundedRectangle(cornerRadius: 18)
                    .strokeBorder(Color.sgBorder, lineWidth: 1)
            )
    }
}

func pickHeroVehicle(
    _ vehicles: [Vehicle],
    _ reminders: [Reminder],
    vehicleById: [String: Vehicle]
) -> Vehicle? {
    if let overdue = reminders.first(where: { $0.urgency == .overdue }),
       let vehicle = vehicleById[overdue.vehicleId] {
        return vehicle
    }
    return vehicles.first
}

func reminderSubtitle(_ reminder: Reminder, vehicle: Vehicle?) -> String {
    let nick = vehicle?.displayTitle ?? "—"
    let urgency: String
    switch reminder.urgency {
    case .overdue: urgency = "Telat"
    case .soon: urgency = "Segera"
    case .ok: urgency = "Aman"
    default: urgency = "Aman"
    }
    return "\(nick) · \(urgency)"
}

func toUiUrgency(_ domain: Shared.ReminderUrgency) -> ReminderUrgency {
    switch domain {
    case .overdue: return .overdue
    case .soon: return .soon
    case .ok: return .ok
    default: return .ok
    }
}

func formatKm(_ km: Int64) -> String {
    formatGroupedLong(km)
}

func formatGroupedLong(_ value: Int64) -> String {
    if value == 0 { return "0" }
    var parts: [String] = []
    var n = value
    while n > 0 {
        let chunk = n % 1000
        n /= 1000
        if n > 0 {
            parts.insert(String(format: "%03d", chunk), at: 0)
        } else {
            parts.insert(String(chunk), at: 0)
        }
    }
    return parts.joined(separator: ".")
}

func hexColor(_ hex: String) -> Color? {
    let trimmed = hex.trimmingCharacters(in: CharacterSet(charactersIn: "#"))
    guard trimmed.count == 6 || trimmed.count == 8 else { return nil }
    var int: UInt64 = 0
    Scanner(string: trimmed).scanHexInt64(&int)
    let r, g, b: Double
    if trimmed.count == 6 {
        r = Double((int >> 16) & 0xFF) / 255
        g = Double((int >> 8) & 0xFF) / 255
        b = Double(int & 0xFF) / 255
    } else {
        r = Double((int >> 24) & 0xFF) / 255
        g = Double((int >> 16) & 0xFF) / 255
        b = Double((int >> 8) & 0xFF) / 255
    }
    return Color(red: r, green: g, blue: b)
}

#Preview {
    HomeView()
        .background(Color.sgBgWarm)
}
