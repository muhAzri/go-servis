import SwiftUI
import Shared

struct ComponentInfoView: View {
    let vehicleId: String
    var catalogId: String? = nil
    var customName: String? = nil
    var onTracked: (String) -> Void = { _ in }

    @StateObject private var model = ComponentInfoModel()

    private var state: ComponentInfoViewModel.UiState { model.state }
    private var catalog: Shared.Component? { state.catalog }
    private var existingTrackedId: String? { state.existingTrackedId }
    private var isSubmitting: Bool { state.isSubmitting }
    private var alreadyTracked: Bool { existingTrackedId != nil }

    private func presetSub(_ catalog: Shared.Component) -> String {
        let motor = catalog.intervalLabel(for: VehicleType.motor)
        if motor != "—" { return motor }
        return catalog.intervalLabel(for: VehicleType.mobil)
    }

    var body: some View {
        VStack(spacing: 0) {
            ScrollView {
                VStack(alignment: .leading, spacing: 0) {
                    if let catalog = catalog {
                        HeroCard(catalog: catalog)
                            .padding(.horizontal, 16)
                            .padding(.top, 12)

                        if !state.requiresCustomInterval {
                            SectionLabel(text: "Interval pabrikan")
                                .padding(.top, 18)
                            IntervalCard(
                                motor: catalog.intervalLabel(for: VehicleType.motor),
                                mobil: catalog.intervalLabel(for: VehicleType.mobil)
                            )
                            .padding(.horizontal, 16)
                        }

                        if !alreadyTracked {
                            if state.requiresCustomInterval {
                                SectionLabel(text: "Atur interval pengingat")
                                    .padding(.top, 16)
                                Text("Komponen kustom belum punya interval pabrikan — tentukan sendiri kapan harus diingatkan.")
                                    .font(.custom("PlusJakartaSans-Medium", size: 12))
                                    .foregroundColor(.sgTextMuted)
                                    .padding(.horizontal, 20)
                                    .padding(.bottom, 8)
                            } else {
                                SectionLabel(text: "Interval pengingat")
                                    .padding(.top, 16)
                            }
                            IntervalModePicker(
                                mode: state.mode,
                                presetSub: presetSub(catalog),
                                showModeToggle: !state.requiresCustomInterval,
                                customKm: state.customKm?.int64Value,
                                customMonths: state.customDays.map { Int(truncating: $0) / 30 },
                                onMode: { model.setMode($0) },
                                onKm: { model.setCustomKm($0) },
                                onMonth: { months in
                                    if let m = months { model.setCustomDays(Int32(m * 30)) }
                                    else { model.setCustomDays(nil) }
                                }
                            )
                            .padding(.horizontal, 16)
                        }
                        Spacer().frame(height: 24)
                    } else if state.isLoading {
                        Text("Memuat…")
                            .font(.custom("PlusJakartaSans-Medium", size: 13))
                            .foregroundColor(.sgTextMuted)
                            .padding(20)
                    } else {
                        Text("Komponen tidak ditemukan")
                            .font(.custom("PlusJakartaSans-Medium", size: 13))
                            .foregroundColor(.sgTextMuted)
                            .padding(20)
                    }
                }
            }
            bottomBar
        }
        .background(Color.sgBgWarm)
        .navigationTitle("Info komponen")
        .navigationBarTitleDisplayMode(.inline)
        .onAppear {
            model.onTracked = { id in onTracked(id) }
            if let name = customName {
                model.loadNewCustom(name: name, vehicleId: vehicleId)
            } else if let id = catalogId {
                model.load(catalogId: id, vehicleId: vehicleId)
            }
        }
    }

    @ViewBuilder
    private var bottomBar: some View {
        let enabled = !isSubmitting && (alreadyTracked || state.canTrack)
        let label = alreadyTracked ? "Buka pengaturan komponen"
            : isSubmitting ? "Menyimpan…"
            : !state.canTrack ? "Atur interval dulu" : "Pantau komponen ini"
        let icon = alreadyTracked ? "\u{f013}" : "\u{2b}"
        Button(action: {
            if let id = existingTrackedId {
                onTracked(id)
            } else {
                model.track()
            }
        }) {
            HStack(spacing: 8) {
                Text(icon)
                    .font(.custom("FontAwesome6Free-Solid", size: 16))
                    .foregroundColor(.white)
                Text(label)
                    .font(.custom("PlusJakartaSans-Bold", size: 15))
                    .foregroundColor(.white)
            }
            .frame(maxWidth: .infinity)
            .frame(height: 52)
            .background(enabled ? Color.sgPrimary : Color.sgPrimary.opacity(0.35))
            .clipShape(RoundedRectangle(cornerRadius: 14))
        }
        .buttonStyle(.plain)
        .disabled(!enabled)
        .padding(.horizontal, 16)
        .padding(.vertical, 12)
        .background(Color.sgSurface)
    }
}

private struct HeroCard: View {
    let catalog: Shared.Component
    var body: some View {
        HStack(spacing: 14) {
            ZStack {
                RoundedRectangle(cornerRadius: 14)
                    .fill(catalog.uiColor.opacity(0.2))
                    .frame(width: 56, height: 56)
                Text(catalog.iconUnicode)
                    .font(.custom("FontAwesome6Free-Solid", size: 28))
                    .foregroundColor(catalog.uiColor)
            }
            VStack(alignment: .leading, spacing: 2) {
                Text("KOMPONEN")
                    .font(.custom("PlusJakartaSans-Bold", size: 11))
                    .kerning(0.5)
                    .foregroundColor(.sgTextMuted)
                Text(catalog.label)
                    .font(.custom("PlusJakartaSans-ExtraBold", size: 20))
                    .foregroundColor(.sgTextPrimary)
                if !catalog.why.isEmpty {
                    Text(catalog.why)
                        .font(.custom("PlusJakartaSans-Medium", size: 12))
                        .foregroundColor(.sgTextMuted)
                        .padding(.top, 2)
                }
            }
            Spacer()
        }
        .padding(18)
        .background(
            LinearGradient(
                colors: [catalog.uiColor.opacity(0.13), catalog.uiColor.opacity(0.03)],
                startPoint: .topLeading, endPoint: .bottomTrailing
            )
        )
        .clipShape(RoundedRectangle(cornerRadius: 20))
        .overlay(
            RoundedRectangle(cornerRadius: 20)
                .strokeBorder(catalog.uiColor.opacity(0.2), lineWidth: 1)
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
            .padding(.horizontal, 20)
            .padding(.bottom, 8)
    }
}

private struct IntervalCard: View {
    let motor: String
    let mobil: String
    var body: some View {
        VStack(spacing: 10) {
            HStack {
                Text("Motor")
                    .font(.custom("PlusJakartaSans-SemiBold", size: 12))
                    .foregroundColor(.sgTextMuted)
                Spacer()
                Text(motor)
                    .font(.system(size: 13, weight: .bold, design: .monospaced))
                    .foregroundColor(.sgTextPrimary)
            }
            Rectangle().fill(Color.sgBorder).frame(height: 1)
            HStack {
                Text("Mobil")
                    .font(.custom("PlusJakartaSans-SemiBold", size: 12))
                    .foregroundColor(.sgTextMuted)
                Spacer()
                Text(mobil)
                    .font(.system(size: 13, weight: .bold, design: .monospaced))
                    .foregroundColor(.sgTextPrimary)
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

private struct IntervalModePicker: View {
    let mode: ComponentInfoViewModel.IntervalMode
    let presetSub: String
    var showModeToggle: Bool = true
    let customKm: Int64?
    let customMonths: Int?
    let onMode: (ComponentInfoViewModel.IntervalMode) -> Void
    let onKm: (Int64?) -> Void
    let onMonth: (Int?) -> Void

    var body: some View {
        VStack(spacing: 10) {
            if showModeToggle {
                HStack(spacing: 8) {
                    ModeCard(
                        label: "Pakai rekomendasi",
                        sub: presetSub,
                        subMonospaced: true,
                        selected: mode == .preset,
                        onTap: { onMode(.preset) }
                    )
                    ModeCard(
                        label: "Atur sendiri",
                        sub: "KM atau bulan",
                        subMonospaced: false,
                        selected: mode == .custom,
                        onTap: { onMode(.custom) }
                    )
                }
            }
            if mode == .custom {
                VStack(spacing: 12) {
                    StepperField(
                        label: "Setiap KM",
                        valueText: customKm.map { "\(formatThousands(Int($0))) km" } ?? "—",
                        onMinus: {
                            let next = max(0, (customKm ?? 0) - 500)
                            onKm(next == 0 ? nil : next)
                        },
                        onPlus: { onKm((customKm ?? 0) + 500) }
                    )
                    StepperField(
                        label: "Setiap bulan (opsional)",
                        valueText: customMonths.map { "\($0) bulan" } ?? "—",
                        onMinus: {
                            let next = max(0, (customMonths ?? 0) - 1)
                            onMonth(next == 0 ? nil : next)
                        },
                        onPlus: { onMonth((customMonths ?? 0) + 1) }
                    )
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
                Text(sub)
                    .font(subMonospaced ? .system(size: 11, weight: .medium, design: .monospaced)
                                        : .custom("PlusJakartaSans-Medium", size: 11))
                    .foregroundColor(.sgTextMuted)
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
                StepperButton(symbol: "−", action: onMinus)
                Spacer().frame(width: 6)
                StepperButton(symbol: "+", action: onPlus)
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

private func formatThousands(_ value: Int) -> String {
    if value == 0 { return "0" }
    let formatter = NumberFormatter()
    formatter.numberStyle = .decimal
    formatter.groupingSeparator = "."
    formatter.locale = Locale(identifier: "id_ID")
    return formatter.string(from: NSNumber(value: value)) ?? "\(value)"
}
