import SwiftUI

private enum IntervalMode { case preset, custom }
private enum IntervalUnit: String, CaseIterable { case km, bulan, keduanya
    var label: String {
        switch self {
        case .km:       return "KM"
        case .bulan:    return "Bulan"
        case .keduanya: return "Keduanya"
        }
    }
}

struct ComponentDetailView: View {
    let componentId: String
    var vehicleType: String = "motor"
    var onSave: () -> Void = {}
    var onStopMonitoring: () -> Void = {}

    @State private var mode: IntervalMode = .preset
    @State private var unit: IntervalUnit = .km
    @State private var kmVal: Int = 2000
    @State private var monthVal: Int = 2

    private var component: ComponentInfo {
        ComponentsCatalog.byId(componentId) ?? ComponentsCatalog.all[0]
    }

    private var intervalStr: String {
        component.interval(for: vehicleType)
    }

    var body: some View {
        VStack(spacing: 0) {
            ScrollView {
                VStack(alignment: .leading, spacing: 0) {
                    HeroCard(component: component)
                        .padding(.horizontal, 16)
                        .padding(.top, 8)

                    SectionLabel(text: "Interval pengingat")
                        .padding(.top, 16)

                    HStack(spacing: 8) {
                        ModeCard(
                            label: "Pakai rekomendasi",
                            sub: intervalStr,
                            subMonospaced: true,
                            selected: mode == .preset,
                            onTap: { mode = .preset }
                        )
                        ModeCard(
                            label: "Atur sendiri",
                            sub: "KM atau tanggal",
                            subMonospaced: false,
                            selected: mode == .custom,
                            onTap: { mode = .custom }
                        )
                    }
                    .padding(.horizontal, 16)

                    if mode == .custom {
                        CustomIntervalCard(
                            unit: $unit,
                            kmVal: $kmVal,
                            monthVal: $monthVal
                        )
                        .padding(.horizontal, 16)
                        .padding(.top, 10)
                    }

                    SectionLabel(text: "Terakhir diservis")
                        .padding(.top, 16)
                    LastServiceCard()
                        .padding(.horizontal, 16)

                    StopMonitoringButton(onTap: onStopMonitoring)
                        .padding(.horizontal, 16)
                        .padding(.top, 16)
                        .padding(.bottom, 24)
                }
            }

            VStack(spacing: 0) {
                AppButton(title: "Simpan perubahan", action: onSave)
                    .padding(.horizontal, 16)
                    .padding(.vertical, 12)
            }
            .background(Color.sgSurface)
        }
        .background(Color.sgBgWarm)
        .navigationTitle("Detail komponen")
        .navigationBarTitleDisplayMode(.inline)
    }
}

private struct HeroCard: View {
    let component: ComponentInfo

    var body: some View {
        HStack(spacing: 14) {
            ZStack {
                RoundedRectangle(cornerRadius: 14)
                    .fill(component.color.opacity(0.2))
                    .frame(width: 56, height: 56)
                Text(component.iconUnicode)
                    .font(.custom("FontAwesome6Free-Solid", size: 28))
                    .foregroundColor(component.color)
            }
            VStack(alignment: .leading, spacing: 2) {
                Text("KOMPONEN DIPANTAU")
                    .font(.custom("PlusJakartaSans-Bold", size: 11))
                    .kerning(0.5)
                    .foregroundColor(.sgTextMuted)
                Text(component.label)
                    .font(.custom("PlusJakartaSans-ExtraBold", size: 20))
                    .kerning(-0.3)
                    .foregroundColor(.sgTextPrimary)
                Text(component.why)
                    .font(.custom("PlusJakartaSans-Medium", size: 12))
                    .foregroundColor(.sgTextMuted)
            }
            Spacer(minLength: 0)
        }
        .padding(18)
        .background(
            LinearGradient(
                colors: [component.color.opacity(0.13), component.color.opacity(0.03)],
                startPoint: .topLeading,
                endPoint: .bottomTrailing
            )
        )
        .clipShape(RoundedRectangle(cornerRadius: 20))
        .overlay(
            RoundedRectangle(cornerRadius: 20)
                .strokeBorder(component.color.opacity(0.2), lineWidth: 1)
        )
    }
}

private struct SectionLabel: View {
    let text: String

    var body: some View {
        Text(text.uppercased())
            .font(.custom("PlusJakartaSans-ExtraBold", size: 11))
            .kerning(0.8)
            .foregroundColor(.sgTextMuted)
            .frame(maxWidth: .infinity, alignment: .leading)
            .padding(.horizontal, 20)
            .padding(.bottom, 8)
    }
}

private struct ModeCard: View {
    let label: String
    let sub: String
    let subMonospaced: Bool
    let selected: Bool
    let onTap: () -> Void

    var body: some View {
        Button(action: onTap) {
            VStack(alignment: .leading, spacing: 2) {
                Text(label)
                    .font(.custom("PlusJakartaSans-Bold", size: 13))
                    .foregroundColor(.sgTextPrimary)
                if subMonospaced {
                    Text(sub)
                        .font(.system(size: 11, weight: .medium, design: .monospaced))
                        .foregroundColor(.sgTextMuted)
                } else {
                    Text(sub)
                        .font(.custom("PlusJakartaSans-Medium", size: 11))
                        .foregroundColor(.sgTextMuted)
                }
            }
            .frame(maxWidth: .infinity, alignment: .leading)
            .padding(14)
            .background(selected ? Color.sgPrimarySofter : Color.sgSurface)
            .clipShape(RoundedRectangle(cornerRadius: 14))
            .overlay(
                RoundedRectangle(cornerRadius: 14)
                    .strokeBorder(selected ? Color.sgPrimary : Color.sgBorder, lineWidth: 1.5)
            )
        }
        .buttonStyle(.plain)
    }
}

private struct CustomIntervalCard: View {
    @Binding var unit: IntervalUnit
    @Binding var kmVal: Int
    @Binding var monthVal: Int

    var body: some View {
        VStack(spacing: 12) {
            HStack(spacing: 6) {
                ForEach(IntervalUnit.allCases, id: \.self) { u in
                    UnitTab(label: u.label, selected: unit == u, onTap: { unit = u })
                }
            }
            if unit == .km || unit == .keduanya {
                StepperField(
                    label: "Setiap KM",
                    valueText: "\(formatThousands(kmVal)) km",
                    onMinus: { kmVal = max(500, kmVal - 500) },
                    onPlus:  { kmVal += 500 }
                )
            }
            if unit == .bulan || unit == .keduanya {
                StepperField(
                    label: "Setiap bulan",
                    valueText: "\(monthVal) bulan",
                    onMinus: { monthVal = max(1, monthVal - 1) },
                    onPlus:  { monthVal += 1 }
                )
            }
        }
        .padding(14)
        .background(Color.sgSurface)
        .clipShape(RoundedRectangle(cornerRadius: 16))
        .overlay(
            RoundedRectangle(cornerRadius: 16)
                .strokeBorder(Color.sgBorder, lineWidth: 1)
        )
    }
}

private struct UnitTab: View {
    let label: String
    let selected: Bool
    let onTap: () -> Void

    var body: some View {
        Button(action: onTap) {
            Text(label)
                .font(.custom("PlusJakartaSans-Bold", size: 12))
                .foregroundColor(selected ? .white : .sgTextPrimary)
                .frame(maxWidth: .infinity)
                .padding(.vertical, 8)
                .background(selected ? Color.sgPrimary : Color.sgSurfaceAlt)
                .clipShape(RoundedRectangle(cornerRadius: 10))
        }
        .buttonStyle(.plain)
    }
}

private struct StepperField: View {
    let label: String
    let valueText: String
    let onMinus: () -> Void
    let onPlus: () -> Void

    var body: some View {
        VStack(alignment: .leading, spacing: 6) {
            Text(label)
                .font(.custom("PlusJakartaSans-Bold", size: 11))
                .foregroundColor(.sgTextMuted)
            HStack {
                Text(valueText)
                    .font(.system(size: 16, weight: .bold, design: .monospaced))
                    .foregroundColor(.sgTextPrimary)
                Spacer()
                HStack(spacing: 6) {
                    StepperButton(symbol: "−", action: onMinus)
                    StepperButton(symbol: "+", action: onPlus)
                }
            }
            .padding(.horizontal, 14)
            .padding(.vertical, 10)
            .background(Color.sgSurfaceAlt)
            .clipShape(RoundedRectangle(cornerRadius: 12))
        }
    }
}

private struct StepperButton: View {
    let symbol: String
    let action: () -> Void

    var body: some View {
        Button(action: action) {
            Text(symbol)
                .font(.custom("PlusJakartaSans-Bold", size: 16))
                .foregroundColor(.sgTextPrimary)
                .frame(width: 30, height: 30)
                .background(Color.sgSurface)
                .clipShape(RoundedRectangle(cornerRadius: 8))
                .overlay(
                    RoundedRectangle(cornerRadius: 8)
                        .strokeBorder(Color.sgBorder, lineWidth: 1)
                )
        }
        .buttonStyle(.plain)
    }
}

private struct LastServiceCard: View {
    var body: some View {
        VStack(spacing: 12) {
            HStack {
                VStack(alignment: .leading, spacing: 2) {
                    Text("20 Feb 2026")
                        .font(.custom("PlusJakartaSans-Bold", size: 13))
                        .foregroundColor(.sgTextPrimary)
                    Text("pada 16.000 km · 2.420 km lalu")
                        .font(.system(size: 11, weight: .medium, design: .monospaced))
                        .foregroundColor(.sgTextMuted)
                }
                Spacer()
                Text("AHASS Kebon Jeruk")
                    .font(.custom("PlusJakartaSans-Bold", size: 11))
                    .foregroundColor(.sgTextPrimary)
                    .padding(.horizontal, 12)
                    .padding(.vertical, 6)
                    .background(Color.sgSurfaceAlt)
                    .clipShape(Capsule())
            }
            Rectangle().fill(Color.sgBorder).frame(height: 1)
            HStack(spacing: 8) {
                Text("\u{f0f3}")
                    .font(.custom("FontAwesome6Free-Solid", size: 12))
                    .foregroundColor(.sgPrimary)
                Text("Berikutnya: 20.420 km / 6 Juli 2026")
                    .font(.custom("PlusJakartaSans-SemiBold", size: 12))
                    .foregroundColor(.sgTextPrimary)
                Spacer()
            }
        }
        .padding(14)
        .background(Color.sgSurface)
        .clipShape(RoundedRectangle(cornerRadius: 16))
        .overlay(
            RoundedRectangle(cornerRadius: 16)
                .strokeBorder(Color.sgBorder, lineWidth: 1)
        )
    }
}

private struct StopMonitoringButton: View {
    let onTap: () -> Void

    var body: some View {
        Button(action: onTap) {
            HStack(spacing: 10) {
                Text("\u{f2ed}")
                    .font(.custom("FontAwesome6Free-Solid", size: 14))
                    .foregroundColor(.sgDanger)
                Text("Berhenti pantau komponen ini")
                    .font(.custom("PlusJakartaSans-Bold", size: 13))
                    .foregroundColor(.sgDanger)
                Spacer()
            }
            .padding(.horizontal, 16)
            .padding(.vertical, 14)
            .overlay(
                RoundedRectangle(cornerRadius: 14)
                    .strokeBorder(Color.sgDanger.opacity(0.4), lineWidth: 1)
            )
        }
        .buttonStyle(.plain)
    }
}

private func formatThousands(_ value: Int) -> String {
    let formatter = NumberFormatter()
    formatter.numberStyle = .decimal
    formatter.groupingSeparator = "."
    return formatter.string(from: NSNumber(value: value)) ?? String(value)
}

#Preview {
    NavigationStack {
        ComponentDetailView(componentId: "oli_mesin")
    }
}
