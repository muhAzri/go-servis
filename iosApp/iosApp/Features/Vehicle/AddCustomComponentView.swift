import SwiftUI

struct AddCustomComponentView: View {
    var vehicleType: String = "motor"
    var subtype: String = "matic"
    var onAdd: (String) -> Void = { _ in }

    @State private var query: String = ""
    @FocusState private var searchFocused: Bool

    private var subtypeList: [ComponentInfo] {
        ComponentsCatalog.forSubtype(subtype)
    }

    private var results: [ComponentInfo] {
        let q = query.trimmingCharacters(in: .whitespaces).lowercased()
        if q.isEmpty {
            return subtypeList.filter { $0.tag != .core }
        }
        return ComponentsCatalog.all.filter { $0.label.lowercased().contains(q) }
    }

    private var suggestions: [ComponentInfo] {
        Array(subtypeList.filter { $0.tag == .plus || $0.tag == .pro }.prefix(4))
    }

    private var subLabel: String {
        VehicleSubtypes.label(for: vehicleType, id: subtype)
    }

    var body: some View {
        ScrollView {
            VStack(alignment: .leading, spacing: 0) {
                SearchField(query: $query, focused: $searchFocused)
                    .padding(.horizontal, 16)
                    .padding(.top, 8)

                if query.isEmpty && !suggestions.isEmpty {
                    SectionLabel(text: "Saran untuk kamu")
                        .padding(.top, 14)
                    SuggestionChips(suggestions: suggestions, onPick: { onAdd($0.id) })
                        .padding(.horizontal, 16)
                }

                SectionLabel(text: query.isEmpty ? "Dari katalog" : "\(results.count) hasil")
                    .padding(.top, 14)

                if results.isEmpty {
                    EmptyResults(query: query, onAddNew: {
                        onAdd("custom:\(query)")
                    })
                    .padding(.horizontal, 16)
                } else {
                    ResultsList(
                        results: Array(results.prefix(7)),
                        vehicleType: vehicleType,
                        subtype: subtype,
                        subLabel: subLabel,
                        onPick: { onAdd($0.id) }
                    )
                    .padding(.horizontal, 16)
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
                    Text("Ketik nama komponen…")
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
                .strokeBorder(focused.wrappedValue ? Color.sgPrimary : Color.sgBorder, lineWidth: 1.5)
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

private struct SuggestionChips: View {
    let suggestions: [ComponentInfo]
    let onPick: (ComponentInfo) -> Void

    private let columns = [GridItem(.adaptive(minimum: 130), spacing: 6)]

    var body: some View {
        LazyVGrid(columns: columns, alignment: .leading, spacing: 6) {
            ForEach(suggestions) { s in
                Button(action: { onPick(s) }) {
                    HStack(spacing: 6) {
                        Text(s.iconUnicode)
                            .font(.custom("FontAwesome6Free-Solid", size: 12))
                            .foregroundColor(s.color)
                        Text(s.label)
                            .font(.custom("PlusJakartaSans-SemiBold", size: 12))
                            .foregroundColor(.sgTextPrimary)
                        Text("\u{2b}")
                            .font(.custom("FontAwesome6Free-Solid", size: 10))
                            .foregroundColor(.sgTextMuted)
                    }
                    .padding(.horizontal, 12)
                    .padding(.vertical, 8)
                    .background(Color.sgSurface)
                    .clipShape(Capsule())
                    .overlay(Capsule().strokeBorder(Color.sgBorder, lineWidth: 1))
                }
                .buttonStyle(.plain)
            }
        }
    }
}

private struct ResultsList: View {
    let results: [ComponentInfo]
    let vehicleType: String
    let subtype: String
    let subLabel: String
    let onPick: (ComponentInfo) -> Void

    var body: some View {
        VStack(spacing: 0) {
            ForEach(Array(results.enumerated()), id: \.element.id) { index, c in
                if index > 0 {
                    Rectangle().fill(Color.sgBorder).frame(height: 1)
                }
                let inSubtype = c.matches(subtype: subtype)
                Button(action: { onPick(c) }) {
                    ResultRow(
                        component: c,
                        interval: c.interval(for: vehicleType),
                        outOfSubtype: !inSubtype,
                        subLabel: subLabel
                    )
                }
                .buttonStyle(.plain)
            }
        }
        .background(Color.sgSurface)
        .clipShape(RoundedRectangle(cornerRadius: 16))
        .overlay(
            RoundedRectangle(cornerRadius: 16)
                .strokeBorder(Color.sgBorder, lineWidth: 1)
        )
    }
}

private struct ResultRow: View {
    let component: ComponentInfo
    let interval: String
    let outOfSubtype: Bool
    let subLabel: String

    var body: some View {
        HStack(spacing: 12) {
            IconBadge(
                iconUnicode: component.iconUnicode,
                foreground: component.color,
                background: component.color.opacity(0.13),
                size: 36, iconSize: 18, corner: 10
            )
            VStack(alignment: .leading, spacing: 2) {
                Text(component.label)
                    .font(.custom("PlusJakartaSans-Bold", size: 14))
                    .foregroundColor(.sgTextPrimary)
                HStack(spacing: 6) {
                    Text(interval)
                        .font(.custom("PlusJakartaSans-Medium", size: 11))
                        .foregroundColor(.sgTextMuted)
                    if outOfSubtype {
                        Text("· bukan tipikal \(subLabel)")
                            .font(.custom("PlusJakartaSans-Bold", size: 11))
                            .foregroundColor(.sgWarning)
                    }
                }
            }
            Spacer()
            ZStack {
                Circle()
                    .fill(Color.sgPrimarySoft)
                    .frame(width: 32, height: 32)
                Text("\u{2b}")
                    .font(.custom("FontAwesome6Free-Solid", size: 14))
                    .foregroundColor(.sgPrimary)
            }
        }
        .padding(.horizontal, 14)
        .padding(.vertical, 12)
        .contentShape(Rectangle())
    }
}

private struct EmptyResults: View {
    let query: String
    let onAddNew: () -> Void

    var body: some View {
        VStack(spacing: 12) {
            Text("Tidak ada hasil untuk \"\(query)\"")
                .font(.custom("PlusJakartaSans-Medium", size: 13))
                .foregroundColor(.sgTextMuted)
                .multilineTextAlignment(.center)
            Button(action: onAddNew) {
                HStack(spacing: 6) {
                    Text("\u{2b}")
                        .font(.custom("FontAwesome6Free-Solid", size: 14))
                        .foregroundColor(.white)
                    Text("Tambahkan \"\(query)\" sebagai baru")
                        .font(.custom("PlusJakartaSans-Bold", size: 13))
                        .foregroundColor(.white)
                }
                .padding(.horizontal, 18)
                .padding(.vertical, 10)
                .background(Color.sgPrimary)
                .clipShape(Capsule())
            }
            .buttonStyle(.plain)
        }
        .frame(maxWidth: .infinity)
        .padding(18)
        .background(Color.sgSurface)
        .clipShape(RoundedRectangle(cornerRadius: 16))
        .overlay(
            RoundedRectangle(cornerRadius: 16)
                .strokeBorder(Color.sgBorder, lineWidth: 1)
        )
    }
}

private struct FreeformHint: View {
    var body: some View {
        HStack(alignment: .top, spacing: 12) {
            Text("\u{f303}")
                .font(.custom("FontAwesome6Free-Solid", size: 14))
                .foregroundColor(.sgTextMuted)
            VStack(alignment: .leading, spacing: 2) {
                Text("Tidak ketemu?")
                    .font(.custom("PlusJakartaSans-Bold", size: 13))
                    .foregroundColor(.sgTextPrimary)
                Text("Ketik nama komponen apapun di kolom pencarian — kamu bisa atur intervalnya sendiri.")
                    .font(.custom("PlusJakartaSans-Medium", size: 12))
                    .foregroundColor(.sgTextMuted)
            }
            Spacer(minLength: 0)
        }
        .padding(16)
        .background(Color.sgSurfaceAlt)
        .clipShape(RoundedRectangle(cornerRadius: 14))
    }
}

#Preview {
    NavigationStack { AddCustomComponentView() }
}
