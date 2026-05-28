import SwiftUI
import Shared

struct TrackedComponentDetailView: View {
    let trackedId: String
    var onStopped: () -> Void = {}
    var onLogServiceForComponent: () -> Void = {}
    var onCreateReminderForComponent: () -> Void = {}

    @StateObject private var model = TrackedComponentDetailModel()
    @State private var showStopConfirm: Bool = false

    private var state: TrackedComponentDetailViewModel.UiState { model.state }
    private var tracked: TrackedComponent? { state.tracked }
    private var catalog: Shared.Component? { state.catalog }
    private var mode: TrackedComponentDetailViewModel.IntervalMode { state.mode }

    var body: some View {
        VStack(spacing: 0) {
            ScrollView {
                VStack(alignment: .leading, spacing: 0) {
                    if let tracked = tracked {
                        HeroCard(tracked: tracked, catalog: catalog)
                            .padding(.horizontal, 16)
                            .padding(.top, 12)

                        SectionLabel(text: "Interval pengingat")
                            .padding(.top, 16)
                        HStack(spacing: 8) {
                            ModeCard(
                                label: "Pakai rekomendasi",
                                sub: presetLabel(),
                                subMonospaced: true,
                                selected: mode == .preset,
                                onTap: { model.setMode(.preset) }
                            )
                            ModeCard(
                                label: "Atur sendiri",
                                sub: "KM atau bulan",
                                subMonospaced: false,
                                selected: mode == .custom,
                                onTap: { model.setMode(.custom) }
                            )
                        }
                        .padding(.horizontal, 16)

                        if mode == .custom {
                            CustomIntervalCard(
                                kmVal: state.customKmOverride?.int64Value,
                                monthVal: state.customDaysOverride.map { Int(truncating: $0) / 30 },
                                onKm: { model.setCustomKm($0) },
                                onMonth: { months in
                                    if let m = months {
                                        model.setCustomDays(Int32(m * 30))
                                    } else {
                                        model.setCustomDays(nil)
                                    }
                                }
                            )
                            .padding(.horizontal, 16)
                            .padding(.top, 10)
                        }

                        SectionLabel(text: "Terakhir diservis")
                            .padding(.top, 16)
                        LastServiceCard(tracked: tracked)
                            .padding(.horizontal, 16)

                        CtaSection(
                            onLogService: onLogServiceForComponent,
                            onCreateReminder: onCreateReminderForComponent
                        )
                        .padding(.horizontal, 16)
                        .padding(.top, 20)

                        StopButton { showStopConfirm = true }
                            .padding(.horizontal, 16)
                            .padding(.top, 20)
                            .padding(.bottom, 24)
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
            saveBar
        }
        .background(Color.sgBgWarm)
        .navigationTitle("Detail komponen")
        .navigationBarTitleDisplayMode(.inline)
        .onAppear {
            model.onStopped = { onStopped() }
            model.load(trackedId: trackedId)
        }
        .alert("Berhenti pantau \(catalog?.label ?? "komponen")?", isPresented: $showStopConfirm) {
            Button("Berhenti pantau", role: .destructive) { model.stopMonitoring() }
            Button("Batal", role: .cancel) {}
        } message: {
            Text("Pengingat untuk komponen ini akan dimatikan. Kamu masih bisa mengaktifkannya lagi nanti.")
        }
    }

    private func presetLabel() -> String {
        catalog?.intervalMotor?.displayLabel ?? catalog?.intervalMobil?.displayLabel ?? "—"
    }

    private var saveBar: some View {
        Button(action: { model.save() }) {
            Text(state.isSaving ? "Menyimpan…" : "Simpan perubahan")
                .font(.custom("PlusJakartaSans-Bold", size: 15))
                .foregroundColor(.white)
                .frame(maxWidth: .infinity)
                .frame(height: 52)
                .background(state.isSaving ? Color.sgPrimary.opacity(0.5) : Color.sgPrimary)
                .clipShape(RoundedRectangle(cornerRadius: 14))
        }
        .buttonStyle(.plain)
        .disabled(state.isSaving)
        .padding(.horizontal, 16)
        .padding(.vertical, 12)
        .background(Color.sgSurface)
    }
}

private struct HeroCard: View {
    let tracked: TrackedComponent
    let catalog: Shared.Component?

    private var color: Color { catalog?.uiColor ?? .sgPrimary }
    private var icon: String { catalog?.iconUnicode ?? "\u{f0ad}" }

    var body: some View {
        HStack(spacing: 14) {
            ZStack {
                RoundedRectangle(cornerRadius: 14)
                    .fill(color.opacity(0.2))
                    .frame(width: 56, height: 56)
                Text(icon)
                    .font(.custom("FontAwesome6Free-Solid", size: 28))
                    .foregroundColor(color)
            }
            VStack(alignment: .leading, spacing: 2) {
                Text("KOMPONEN DIPANTAU")
                    .font(.custom("PlusJakartaSans-Bold", size: 11))
                    .kerning(0.5)
                    .foregroundColor(.sgTextMuted)
                Text(tracked.displayName(catalog: catalog))
                    .font(.custom("PlusJakartaSans-ExtraBold", size: 20))
                    .foregroundColor(.sgTextPrimary)
                if let why = catalog?.why, !why.isEmpty {
                    Text(why)
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
                colors: [color.opacity(0.13), color.opacity(0.03)],
                startPoint: .topLeading, endPoint: .bottomTrailing
            )
        )
        .clipShape(RoundedRectangle(cornerRadius: 20))
        .overlay(
            RoundedRectangle(cornerRadius: 20)
                .strokeBorder(color.opacity(0.2), lineWidth: 1)
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

private struct CustomIntervalCard: View {
    let kmVal: Int64?
    let monthVal: Int?
    let onKm: (Int64?) -> Void
    let onMonth: (Int?) -> Void

    var body: some View {
        VStack(spacing: 12) {
            StepperField(
                label: "Setiap KM (kosongkan untuk tidak pakai)",
                valueText: kmVal.map { "\(formatThousands(Int($0))) km" } ?? "—",
                onMinus: {
                    let next = max(0, (kmVal ?? 0) - 500)
                    onKm(next == 0 ? nil : next)
                },
                onPlus: { onKm((kmVal ?? 0) + 500) }
            )
            StepperField(
                label: "Setiap bulan (opsional)",
                valueText: monthVal.map { "\($0) bulan" } ?? "—",
                onMinus: {
                    let next = max(0, (monthVal ?? 0) - 1)
                    onMonth(next == 0 ? nil : next)
                },
                onPlus: { onMonth((monthVal ?? 0) + 1) }
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

private struct LastServiceCard: View {
    let tracked: TrackedComponent

    private static let dateFormatter: DateFormatter = {
        let df = DateFormatter()
        df.locale = Locale(identifier: "id_ID")
        df.dateFormat = "d MMM yyyy"
        return df
    }()

    var body: some View {
        VStack(alignment: .leading, spacing: 2) {
            Text(dateLabel)
                .font(.custom("PlusJakartaSans-Bold", size: 13))
                .foregroundColor(.sgTextPrimary)
            if let km = kmLabel {
                Text("pada \(km)")
                    .font(.system(size: 11, weight: .medium, design: .monospaced))
                    .foregroundColor(.sgTextMuted)
            }
        }
        .frame(maxWidth: .infinity, alignment: .leading)
        .padding(14)
        .background(Color.sgSurface)
        .clipShape(RoundedRectangle(cornerRadius: 16))
        .overlay(
            RoundedRectangle(cornerRadius: 16)
                .strokeBorder(Color.sgBorder, lineWidth: 1)
        )
    }

    private var dateLabel: String {
        guard let raw = tracked.lastServiceDate else { return "belum tercatat" }
        let date = Date(timeIntervalSince1970: TimeInterval(truncating: raw) / 1000.0)
        return Self.dateFormatter.string(from: date)
    }

    private var kmLabel: String? {
        guard let dist = tracked.lastServiceOdometer as? KotlinLong else { return nil }
        return "\(formatThousands(Int(truncating: dist))) km"
    }
}

private struct CtaSection: View {
    let onLogService: () -> Void
    let onCreateReminder: () -> Void

    var body: some View {
        VStack(spacing: 12) {
            Button(action: onLogService) {
                HStack(spacing: 8) {
                    Text("\u{2b}")
                        .font(.custom("FontAwesome6Free-Solid", size: 14))
                        .foregroundColor(.white)
                    Text("Catat servis untuk komponen ini")
                        .font(.custom("PlusJakartaSans-Bold", size: 14))
                        .foregroundColor(.white)
                }
                .frame(maxWidth: .infinity)
                .frame(height: 52)
                .background(Color.sgPrimary)
                .clipShape(RoundedRectangle(cornerRadius: 14))
            }
            .buttonStyle(.plain)

            HStack(spacing: 10) {
                Rectangle().fill(Color.sgBorder).frame(height: 1)
                Text("atau")
                    .font(.custom("PlusJakartaSans-SemiBold", size: 11))
                    .foregroundColor(.sgTextSubtle)
                Rectangle().fill(Color.sgBorder).frame(height: 1)
            }

            Button(action: onCreateReminder) {
                HStack(spacing: 8) {
                    Text("\u{f0f3}")
                        .font(.custom("FontAwesome6Free-Solid", size: 14))
                        .foregroundColor(.sgPrimary)
                    Text("Buat pengingat manual")
                        .font(.custom("PlusJakartaSans-Bold", size: 14))
                        .foregroundColor(.sgPrimary)
                }
                .frame(maxWidth: .infinity)
                .frame(height: 48)
                .overlay(
                    RoundedRectangle(cornerRadius: 14)
                        .strokeBorder(Color.sgPrimary, lineWidth: 1.5)
                )
            }
            .buttonStyle(.plain)
        }
    }
}

private struct StopButton: View {
    let onTap: () -> Void
    var body: some View {
        Button(action: onTap) {
            HStack(spacing: 10) {
                Text("\u{f1f8}")
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
    if value == 0 { return "0" }
    let formatter = NumberFormatter()
    formatter.numberStyle = .decimal
    formatter.groupingSeparator = "."
    formatter.locale = Locale(identifier: "id_ID")
    return formatter.string(from: NSNumber(value: value)) ?? "\(value)"
}
