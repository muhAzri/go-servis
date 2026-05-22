import SwiftUI

struct MultiPickerItem: Identifiable, Hashable {
    let id: String
    let label: String
    var subtitle: String? = nil
    var iconUnicode: String? = nil
    var color: Color? = nil
}

struct MultiPickerGroup: Identifiable {
    let id: String
    let title: String?
    let items: [MultiPickerItem]
}

struct MultiPicker: View {
    let title: String
    var subtitle: String? = nil
    let groups: [MultiPickerGroup]
    @Binding var selected: Set<String>
    var searchPlaceholder: String = "Cari..."
    var primaryLabel: String = "Pilih"
    let onConfirm: () -> Void

    @State private var query: String = ""
    @Environment(\.dismiss) private var dismiss

    private func filteredGroups() -> [MultiPickerGroup] {
        let q = query.trimmingCharacters(in: .whitespaces).lowercased()
        guard !q.isEmpty else { return groups }
        return groups.compactMap { g in
            let items = g.items.filter {
                $0.label.lowercased().contains(q)
                    || ($0.subtitle?.lowercased().contains(q) ?? false)
            }
            return items.isEmpty ? nil : MultiPickerGroup(id: g.id, title: g.title, items: items)
        }
    }

    var body: some View {
        VStack(spacing: 0) {
            Capsule()
                .fill(Color.sgBorder)
                .frame(width: 40, height: 4)
                .padding(.top, 8)
                .padding(.bottom, 12)

            VStack(alignment: .leading, spacing: 4) {
                Text(title)
                    .font(.custom("PlusJakartaSans-ExtraBold", size: 18))
                    .foregroundColor(.sgTextPrimary)
                if let subtitle, !subtitle.isEmpty {
                    Text(subtitle)
                        .font(.custom("PlusJakartaSans-Medium", size: 13))
                        .foregroundColor(.sgTextMuted)
                }
            }
            .frame(maxWidth: .infinity, alignment: .leading)
            .padding(.horizontal, 20)

            // Search
            HStack(spacing: 8) {
                Text("\u{f002}")
                    .font(.custom("FontAwesome6Free-Solid", size: 13))
                    .foregroundColor(.sgTextSubtle)
                TextField(searchPlaceholder, text: $query)
                    .font(.custom("PlusJakartaSans-Medium", size: 14))
                    .foregroundColor(.sgTextPrimary)
                    .autocorrectionDisabled(true)
                if !query.isEmpty {
                    Button { query = "" } label: {
                        Text("\u{f00d}")
                            .font(.custom("FontAwesome6Free-Solid", size: 12))
                            .foregroundColor(.sgTextSubtle)
                    }
                    .buttonStyle(.plain)
                }
            }
            .padding(.horizontal, 12)
            .padding(.vertical, 10)
            .background(Color.sgSurfaceAlt)
            .clipShape(RoundedRectangle(cornerRadius: 12))
            .padding(.horizontal, 16)
            .padding(.top, 12)
            .padding(.bottom, 8)

            ScrollView {
                LazyVStack(alignment: .leading, spacing: 18) {
                    ForEach(filteredGroups()) { group in
                        VStack(alignment: .leading, spacing: 8) {
                            if let gt = group.title, !gt.isEmpty {
                                Text(gt.uppercased())
                                    .font(.custom("PlusJakartaSans-Bold", size: 11))
                                    .kerning(0.6)
                                    .foregroundColor(.sgTextMuted)
                                    .padding(.horizontal, 4)
                            }
                            VStack(spacing: 6) {
                                ForEach(group.items) { item in
                                    row(item)
                                }
                            }
                        }
                    }
                }
                .padding(.horizontal, 16)
                .padding(.bottom, 12)
            }

            // Footer
            Button {
                onConfirm()
                dismiss()
            } label: {
                Text("\(primaryLabel) (\(selected.count))")
                    .font(.custom("PlusJakartaSans-Bold", size: 15))
                    .foregroundColor(.white)
                    .frame(maxWidth: .infinity)
                    .frame(height: 52)
                    .background(Color.sgPrimary.opacity(selected.isEmpty ? 0.5 : 1.0))
                    .clipShape(RoundedRectangle(cornerRadius: 14))
            }
            .buttonStyle(.plain)
            .disabled(selected.isEmpty)
            .padding(.horizontal, 16)
            .padding(.top, 6)
            .padding(.bottom, 24)
        }
        .background(Color.sgSurface)
    }

    private func row(_ item: MultiPickerItem) -> some View {
        let active = selected.contains(item.id)
        return Button {
            if active { selected.remove(item.id) } else { selected.insert(item.id) }
        } label: {
            HStack(spacing: 12) {
                if let icon = item.iconUnicode {
                    ZStack {
                        RoundedRectangle(cornerRadius: 10)
                            .fill((item.color ?? .sgPrimary).opacity(0.13))
                            .frame(width: 36, height: 36)
                        Text(icon)
                            .font(.custom("FontAwesome6Free-Solid", size: 16))
                            .foregroundColor(item.color ?? .sgPrimary)
                    }
                }

                VStack(alignment: .leading, spacing: 2) {
                    Text(item.label)
                        .font(.custom("PlusJakartaSans-Bold", size: 14))
                        .foregroundColor(.sgTextPrimary)
                    if let st = item.subtitle {
                        Text(st)
                            .font(.custom("PlusJakartaSans-Medium", size: 12))
                            .foregroundColor(.sgTextMuted)
                    }
                }
                .frame(maxWidth: .infinity, alignment: .leading)

                ZStack {
                    RoundedRectangle(cornerRadius: 6)
                        .strokeBorder(active ? Color.sgPrimary : Color.sgBorder, lineWidth: 1.5)
                        .background(
                            RoundedRectangle(cornerRadius: 6)
                                .fill(active ? Color.sgPrimary : Color.clear)
                        )
                        .frame(width: 22, height: 22)
                    if active {
                        Text("\u{f00c}")
                            .font(.custom("FontAwesome6Free-Solid", size: 11))
                            .foregroundColor(.white)
                    }
                }
            }
            .padding(.horizontal, 12)
            .padding(.vertical, 10)
            .background(active ? Color.sgPrimarySofter : Color.sgSurfaceAlt.opacity(0.5))
            .clipShape(RoundedRectangle(cornerRadius: 12))
            .overlay(
                RoundedRectangle(cornerRadius: 12)
                    .strokeBorder(active ? Color.sgPrimary.opacity(0.35) : Color.clear, lineWidth: 1)
            )
        }
        .buttonStyle(.plain)
    }
}

#Preview {
    StatefulMP() {
        EmptyView()
    }
}

private struct StatefulMP<C: View>: View {
    @State var selected: Set<String> = ["oli"]
    let content: () -> C
    var body: some View {
        Color.sgBgWarm.sheet(isPresented: .constant(true)) {
            MultiPicker(
                title: "Pilih jenis servis",
                subtitle: "Boleh pilih beberapa",
                groups: [
                    .init(id: "motor", title: "Motor", items: [
                        .init(id: "oli", label: "Ganti Oli Mesin", subtitle: "Tiap 2.000–3.000 km", iconUnicode: "\u{f613}", color: .sgPrimary),
                        .init(id: "rem", label: "Servis Rem", iconUnicode: "\u{f1cd}", color: .sgWarning),
                    ]),
                    .init(id: "mobil", title: "Mobil", items: [
                        .init(id: "aki", label: "Cek Aki", iconUnicode: "\u{f5df}", color: .sgDanger),
                    ]),
                ],
                selected: $selected,
                onConfirm: {}
            )
            .presentationDetents([.medium, .large])
        }
    }
}
