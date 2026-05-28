import SwiftUI
import Shared

struct AddCustomComponentView: View {
    let vehicleId: String
    var onOpenComponent: (String) -> Void = { _ in }
    var onCreateCustom: (String) -> Void = { _ in }

    @StateObject private var model = AddTrackedComponentModel()
    @FocusState private var searchFocused: Bool

    private var state: AddTrackedComponentViewModel.UiState { model.state }
    private var vehicleType: VehicleType { state.vehicle?.type ?? VehicleType.motor }
    private var vehicleTypeKey: String { vehicleType == .mobil ? "mobil" : "motor" }
    private var subtypeId: String {
        let raw = state.vehicle?.subtypeId ?? ""
        if raw.isEmpty || raw == "*" {
            return VehicleSubtypes.defaultId(for: vehicleTypeKey)
        }
        return raw
    }
    private var subLabel: String { VehicleSubtypes.label(for: vehicleTypeKey, id: subtypeId) }

    var body: some View {
        ScrollView {
            VStack(alignment: .leading, spacing: 0) {
                SearchField(
                    query: Binding(get: { state.query }, set: { model.setQuery($0) }),
                    focused: $searchFocused
                )
                .padding(.horizontal, 16)
                .padding(.top, 8)

                SectionLabel(text: state.query.isEmpty ? "Rekomendasi untuk \(subLabel)" : "\(state.items.count) hasil")
                    .padding(.top, 14)

                if state.items.isEmpty && !state.showCustomPrompt {
                    Text(state.isLoading ? "Memuat…" : "Tidak ada komponen.")
                        .font(.custom("PlusJakartaSans-Medium", size: 13))
                        .foregroundColor(.sgTextMuted)
                        .padding(20)
                } else {
                    VStack(spacing: 8) {
                        ForEach(state.items, id: \.component.id) { item in
                            ComponentPickRow(
                                item: item,
                                vehicleType: vehicleType,
                                onTap: { if !item.alreadyTracked { onOpenComponent(item.component.id) } }
                            )
                        }
                    }
                    .padding(.horizontal, 16)
                }

                if state.showCustomPrompt {
                    CustomPromptRow(query: state.query, onAdd: { onCreateCustom(state.query) })
                        .padding(.horizontal, 16)
                        .padding(.top, 8)
                }

                FreeformHint()
                    .padding(.horizontal, 16)
                    .padding(.top, 16)
                    .padding(.bottom, 24)
            }
        }
        .background(Color.sgBgWarm)
        .navigationTitle("Tambah komponen")
        .navigationBarTitleDisplayMode(.inline)
        .onAppear {
            model.load(vehicleId: vehicleId)
        }
    }
}

private struct SearchField: View {
    @Binding var query: String
    var focused: FocusState<Bool>.Binding

    var body: some View {
        HStack(spacing: 10) {
            Text("\u{f002}")
                .font(.custom("FontAwesome6Free-Solid", size: 14))
                .foregroundColor(.sgTextMuted)
            ZStack(alignment: .leading) {
                if query.isEmpty {
                    Text("Cari komponen lain…")
                        .font(.custom("PlusJakartaSans-Regular", size: 15))
                        .foregroundColor(.sgTextSubtle)
                }
                TextField("", text: $query)
                    .font(.custom("PlusJakartaSans-SemiBold", size: 15))
                    .foregroundColor(.sgTextPrimary)
                    .focused(focused)
            }
            if !query.isEmpty {
                Button(action: { query = "" }) {
                    Text("\u{f00d}")
                        .font(.custom("FontAwesome6Free-Solid", size: 14))
                        .foregroundColor(.sgTextMuted)
                        .frame(width: 20, height: 20)
                }
                .buttonStyle(.plain)
            }
        }
        .padding(.horizontal, 14)
        .padding(.vertical, 12)
        .background(Color.sgSurface)
        .clipShape(RoundedRectangle(cornerRadius: 14))
        .overlay(
            RoundedRectangle(cornerRadius: 14)
                .strokeBorder(Color.sgBorder, lineWidth: 1.5)
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

private struct ComponentPickRow: View {
    let item: AddTrackedComponentViewModel.Item
    let vehicleType: VehicleType
    let onTap: () -> Void

    private var component: Shared.Component { item.component }
    private var locked: Bool { item.alreadyTracked }

    var body: some View {
        Button(action: { if !locked { onTap() } }) {
            HStack(spacing: 12) {
                IconBadge(
                    iconUnicode: component.iconUnicode,
                    foreground: component.uiColor,
                    background: component.uiColor.opacity(0.13),
                    size: 38, iconSize: 18, corner: 10
                )
                VStack(alignment: .leading, spacing: 2) {
                    HStack(spacing: 6) {
                        Text(component.label)
                            .font(.custom("PlusJakartaSans-Bold", size: 14))
                            .foregroundColor(.sgTextPrimary)
                        TagChip(tag: component.tag)
                    }
                    if locked {
                        Text("Sudah dipantau")
                            .font(.custom("PlusJakartaSans-Medium", size: 11))
                            .foregroundColor(.sgPrimary)
                    } else {
                        Text(component.intervalLabel(for: vehicleType))
                            .font(.system(size: 11, weight: .medium, design: .monospaced))
                            .foregroundColor(.sgTextMuted)
                    }
                }
                Spacer()
                if locked {
                    Text("\u{f00c}")
                        .font(.custom("FontAwesome6Free-Solid", size: 14))
                        .foregroundColor(.sgPrimary)
                } else {
                    ZStack {
                        Circle().fill(Color.sgPrimarySoft).frame(width: 32, height: 32)
                        Text("\u{2b}")
                            .font(.custom("FontAwesome6Free-Solid", size: 14))
                            .foregroundColor(.sgPrimary)
                    }
                }
            }
            .padding(12)
            .background(Color.sgSurface)
            .clipShape(RoundedRectangle(cornerRadius: 14))
            .overlay(
                RoundedRectangle(cornerRadius: 14)
                    .strokeBorder(Color.sgBorder, lineWidth: 1)
            )
        }
        .buttonStyle(.plain)
        .disabled(locked)
    }
}

private struct CustomPromptRow: View {
    let query: String
    let onAdd: () -> Void

    var body: some View {
        Button(action: onAdd) {
            HStack(spacing: 12) {
                ZStack {
                    Circle().fill(Color.sgPrimary).frame(width: 32, height: 32)
                    Text("\u{2b}")
                        .font(.custom("FontAwesome6Free-Solid", size: 14))
                        .foregroundColor(.white)
                }
                Text("Tambahkan \"\(query)\" sebagai komponen baru")
                    .font(.custom("PlusJakartaSans-Bold", size: 13))
                    .foregroundColor(.sgTextPrimary)
                Spacer()
            }
            .padding(14)
            .background(Color.sgSurface)
            .clipShape(RoundedRectangle(cornerRadius: 14))
            .overlay(
                RoundedRectangle(cornerRadius: 14)
                    .strokeBorder(Color.sgBorder, lineWidth: 1)
            )
        }
        .buttonStyle(.plain)
    }
}

private struct TagChip: View {
    let tag: Shared.ComponentTag

    private var colors: (fg: Color, bg: Color) {
        switch tag {
        case .core: return (.sgPrimary, .sgPrimarySoft)
        case .plus: return (.sgWarning, .sgWarningSoft)
        default: return (.sgTextMuted, .sgSurfaceAlt)
        }
    }

    var body: some View {
        Text(tag.displayLabel.uppercased())
            .font(.custom("PlusJakartaSans-ExtraBold", size: 9))
            .kerning(0.5)
            .foregroundColor(colors.fg)
            .padding(.horizontal, 7)
            .padding(.vertical, 2)
            .background(colors.bg)
            .clipShape(Capsule())
    }
}

private struct FreeformHint: View {
    var body: some View {
        HStack(alignment: .top, spacing: 12) {
            Text("\u{f0eb}")
                .font(.custom("FontAwesome6Free-Solid", size: 14))
                .foregroundColor(.sgTextMuted)
            VStack(alignment: .leading, spacing: 2) {
                Text("Pilih satu untuk atur interval")
                    .font(.custom("PlusJakartaSans-Bold", size: 13))
                    .foregroundColor(.sgTextPrimary)
                Text("Tiap komponen bisa kamu atur interval km / bulannya sebelum dipantau.")
                    .font(.custom("PlusJakartaSans-Medium", size: 12))
                    .foregroundColor(.sgTextMuted)
            }
        }
        .padding(16)
        .background(Color.sgSurfaceAlt)
        .clipShape(RoundedRectangle(cornerRadius: 14))
    }
}
