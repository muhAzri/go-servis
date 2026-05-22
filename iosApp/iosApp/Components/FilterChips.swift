import SwiftUI

// MARK: - FilterChipBar

struct FilterChipItem: Identifiable, Equatable {
    let id: String
    let label: String
    var iconUnicode: String? = nil
    var count: Int? = nil
}

struct FilterChipBar: View {
    let items: [FilterChipItem]
    @Binding var activeId: String

    var body: some View {
        ScrollView(.horizontal, showsIndicators: false) {
            HStack(spacing: 8) {
                ForEach(items) { item in
                    let active = item.id == activeId
                    Button {
                        activeId = item.id
                    } label: {
                        HStack(spacing: 6) {
                            if let icon = item.iconUnicode {
                                Text(icon)
                                    .font(.custom("FontAwesome6Free-Solid", size: 11))
                            }
                            Text(item.label)
                                .font(.custom("PlusJakartaSans-Bold", size: 13))
                            if let count = item.count {
                                Text("\(count)")
                                    .font(.custom("PlusJakartaSans-Bold", size: 11))
                                    .foregroundColor(active ? .sgPrimary : .sgTextMuted)
                                    .padding(.horizontal, 6)
                                    .padding(.vertical, 1)
                                    .background(active ? Color.white : Color.sgSurfaceAlt)
                                    .clipShape(Capsule())
                            }
                        }
                        .foregroundColor(active ? .white : .sgTextPrimary)
                        .padding(.horizontal, 12)
                        .padding(.vertical, 8)
                        .background(active ? Color.sgPrimary : Color.sgSurface)
                        .clipShape(Capsule())
                        .overlay(
                            Capsule()
                                .strokeBorder(active ? Color.clear : Color.sgBorder, lineWidth: 1)
                        )
                    }
                    .buttonStyle(.plain)
                }
            }
            .padding(.horizontal, 16)
            .padding(.vertical, 4)
        }
    }
}

// MARK: - ActiveFilterChips

struct ActiveFilterChip: Identifiable, Equatable {
    let id: String
    let label: String
}

struct ActiveFilterChips: View {
    let chips: [ActiveFilterChip]
    let onRemove: (String) -> Void
    let onReset: () -> Void
    var resetLabel: String = "Reset"

    var body: some View {
        if chips.isEmpty {
            EmptyView()
        } else {
            ScrollView(.horizontal, showsIndicators: false) {
                HStack(spacing: 8) {
                    ForEach(chips) { chip in
                        HStack(spacing: 6) {
                            Text(chip.label)
                                .font(.custom("PlusJakartaSans-SemiBold", size: 12))
                                .foregroundColor(.sgPrimary)
                            Button {
                                onRemove(chip.id)
                            } label: {
                                Text("\u{f00d}")
                                    .font(.custom("FontAwesome6Free-Solid", size: 10))
                                    .foregroundColor(.sgPrimary)
                            }
                            .buttonStyle(.plain)
                        }
                        .padding(.horizontal, 10)
                        .padding(.vertical, 6)
                        .background(Color.sgPrimarySoft)
                        .clipShape(Capsule())
                        .overlay(
                            Capsule()
                                .strokeBorder(Color.sgPrimary.opacity(0.2), lineWidth: 1)
                        )
                    }

                    Button(action: onReset) {
                        Text(resetLabel)
                            .font(.custom("PlusJakartaSans-Bold", size: 12))
                            .foregroundColor(.sgTextMuted)
                            .underline()
                            .padding(.horizontal, 6)
                    }
                    .buttonStyle(.plain)
                }
                .padding(.horizontal, 16)
                .padding(.vertical, 4)
            }
        }
    }
}

#Preview {
    StatefulFC()
}

private struct StatefulFC: View {
    @State var active = "all"
    var body: some View {
        VStack(spacing: 12) {
            FilterChipBar(
                items: [
                    .init(id: "all", label: "Semua", iconUnicode: "\u{f0b0}", count: 24),
                    .init(id: "oli", label: "Oli", iconUnicode: "\u{f613}", count: 8),
                    .init(id: "rem", label: "Rem", iconUnicode: "\u{f1cd}", count: 4),
                    .init(id: "aki", label: "Aki", iconUnicode: "\u{f5df}", count: 1),
                ],
                activeId: $active
            )
            ActiveFilterChips(
                chips: [
                    .init(id: "v1", label: "Beat Hitam"),
                    .init(id: "t1", label: "Bulan ini"),
                ],
                onRemove: { _ in },
                onReset: {}
            )
        }
        .padding(.vertical, 8)
        .background(Color.sgBgWarm)
    }
}
