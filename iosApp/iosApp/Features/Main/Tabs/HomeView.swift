import SwiftUI

struct HomeView: View {
    var onOpenReminders: () -> Void = {}
    var onOpenReminderDetail: () -> Void = {}
    var onOpenVehicleDetail: () -> Void = {}
    var onAddService: () -> Void = {}
    var onAddVehicle: () -> Void = {}
    var onUpdateOdometer: () -> Void = {}
    var onOpenTips: () -> Void = {}

    var body: some View {
        ScrollView {
            VStack(alignment: .leading, spacing: 0) {
                HomeHeader(onOpenReminders: onOpenReminders)

                HeroStatusCard(onOpenVehicleDetail: onOpenVehicleDetail)
                    .padding(.horizontal, 16)
                    .padding(.bottom, 16)

                AdBannerSlot()
                    .padding(.bottom, 8)

                QuickActionsGrid(
                    onAddService: onAddService,
                    onUpdateOdometer: onUpdateOdometer,
                    onAddVehicle: onAddVehicle,
                    onOpenTips: onOpenTips
                )
                .padding(.horizontal, 16)
                .padding(.bottom, 16)

                SectionHeading(title: "Pengingat aktif", actionLabel: "Lihat semua", action: onOpenReminders)

                VStack(spacing: 8) {
                    ReminderRow(
                        iconUnicode: "\u{f613}",
                        title: "Ganti Oli Mesin",
                        subtitle: "Beat Hitam · Telat 16 hari",
                        urgency: .overdue,
                        onTap: onOpenReminderDetail
                    )
                    ReminderRow(
                        iconUnicode: "\u{f1ce}",
                        title: "Kampas Rem",
                        subtitle: "Beat Hitam · 19 hari lagi",
                        urgency: .soon,
                        onTap: onOpenReminderDetail
                    )
                    ReminderRow(
                        iconUnicode: "\u{f0b0}",
                        title: "Filter Oli & Udara",
                        subtitle: "Avanza Putih · 65 hari lagi",
                        urgency: .ok,
                        onTap: onOpenReminderDetail
                    )
                }
                .padding(.horizontal, 16)
                .padding(.bottom, 12)

                NativeAdCard()
                    .padding(.bottom, 12)

                SectionHeading(title: "Kendaraan saya", actionLabel: nil, action: {})

                VStack(spacing: 8) {
                    VehicleSummaryRow(
                        iconUnicode: "\u{f21c}",
                        accent: Color(red: 0.18, green: 0.55, blue: 0.34),
                        title: "Beat Hitam",
                        plateAndKm: "B 4521 KZA · 18.420 km",
                        onTap: onOpenVehicleDetail
                    )
                    VehicleSummaryRow(
                        iconUnicode: "\u{f21c}",
                        accent: Color(red: 0.84, green: 0.27, blue: 0.23),
                        title: "Vario Merah",
                        plateAndKm: "B 6789 SKR · 8.100 km",
                        onTap: onOpenVehicleDetail
                    )
                    VehicleSummaryRow(
                        iconUnicode: "\u{f1b9}",
                        accent: Color(red: 0.25, green: 0.30, blue: 0.36),
                        title: "Avanza Putih",
                        plateAndKm: "B 1234 ABC · 62.300 km",
                        onTap: onOpenVehicleDetail
                    )
                    VehicleSummaryRow(
                        iconUnicode: "\u{f1b9}",
                        accent: Color(red: 0.25, green: 0.69, blue: 0.84),
                        title: "Brio Biru",
                        plateAndKm: "B 9876 XYZ · 24.500 km",
                        onTap: onOpenVehicleDetail
                    )
                }
                .padding(.horizontal, 16)
                .padding(.bottom, 16)
            }
        }
    }
}

// MARK: - Subviews

private struct HomeHeader: View {
    let onOpenReminders: () -> Void

    var body: some View {
        HStack(alignment: .center) {
            VStack(alignment: .leading, spacing: 4) {
                Text("Halo, Budi 👋")
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
    let onOpenVehicleDetail: () -> Void

    var body: some View {
        ZStack(alignment: .topTrailing) {
            VStack(alignment: .leading, spacing: 0) {
                HStack(spacing: 8) {
                    Text("\u{f1b9}")
                        .font(.custom("FontAwesome6Free-Solid", size: 14))
                        .foregroundColor(.white.opacity(0.95))
                    Text("Toyota Avanza Veloz")
                        .font(.custom("PlusJakartaSans-SemiBold", size: 12))
                        .foregroundColor(.white.opacity(0.9))
                }

                Text("1 servis telat — segera bawa ke bengkel")
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
                        Text("62.300")
                            .font(.system(size: 18, weight: .bold, design: .monospaced))
                            .foregroundColor(.white)
                    }
                    VStack(alignment: .leading, spacing: 2) {
                        Text("Plat")
                            .font(.custom("PlusJakartaSans-Regular", size: 11))
                            .foregroundColor(.white.opacity(0.85))
                        Text("B 1234 ABC")
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
            .background(Color.sgDanger)
            .clipShape(RoundedRectangle(cornerRadius: 24))
        }
    }
}

private struct QuickActionsGrid: View {
    let onAddService: () -> Void
    let onUpdateOdometer: () -> Void
    let onAddVehicle: () -> Void
    let onOpenTips: () -> Void

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
            QuickActionTile(
                icon: "\u{f0eb}",
                label: "Tips",
                primary: false,
                action: onOpenTips
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
    let iconUnicode: String
    let title: String
    let subtitle: String
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
    let iconUnicode: String
    let accent: Color
    let title: String
    let plateAndKm: String
    let onTap: () -> Void

    var body: some View {
        Button(action: onTap) {
            HStack(spacing: 12) {
                IconBadge(
                    iconUnicode: iconUnicode,
                    foreground: accent,
                    background: accent.opacity(0.13),
                    size: 48,
                    iconSize: 24
                )
                VStack(alignment: .leading, spacing: 2) {
                    Text(title)
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

#Preview {
    HomeView()
        .background(Color.sgBgWarm)
}
