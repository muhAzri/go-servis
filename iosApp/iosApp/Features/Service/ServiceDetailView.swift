import SwiftUI

struct ServiceDetailView: View {
    var onEdit: () -> Void = {}
    var onDelete: () -> Void = {}
    var onOpenNextReminder: () -> Void = {}
    var onShare: () -> Void = {}

    @State private var showDeleteConfirm: Bool = false

    var body: some View {
        ScrollView {
            VStack(alignment: .leading, spacing: 0) {
                HeaderRow()
                    .padding(.horizontal, 20)
                    .padding(.top, 8)

                FactsCard()
                    .padding(.horizontal, 16)
                    .padding(.top, 16)

                SectionLabel(text: "Catatan")
                    .padding(.top, 20)
                NoteBlock(text: "AHM MPX2 0.8L. Mekanik bilang filter masih bagus, tidak perlu ganti.")
                    .padding(.horizontal, 16)

                SectionLabel(text: "Komponen yang diservis")
                    .padding(.top, 20)
                ComponentList()
                    .padding(.horizontal, 16)

                SectionLabel(text: "Pengingat berikutnya")
                    .padding(.top, 20)
                NextReminderCard(onTap: onOpenNextReminder)
                    .padding(.horizontal, 16)

                ActionRow(onEdit: onEdit, onDelete: { showDeleteConfirm = true })
                    .padding(.horizontal, 16)
                    .padding(.top, 24)
                    .padding(.bottom, 24)
            }
        }
        .background(Color.sgBgWarm)
        .navigationTitle("Detail Servis")
        .navigationBarTitleDisplayMode(.inline)
        .toolbar {
            ToolbarItemGroup(placement: .topBarTrailing) {
                Button(action: onShare) {
                    Image(systemName: "square.and.arrow.up")
                }
            }
        }
        .confirmationDialog(
            "Hapus servis?",
            isPresented: $showDeleteConfirm,
            titleVisibility: .visible
        ) {
            Button("Hapus servis", role: .destructive, action: onDelete)
            Button("Batal", role: .cancel) { }
        } message: {
            Text("Catatan servis ini akan dihapus permanen. Aksi ini tidak bisa dibatalkan.")
        }
    }
}

// MARK: - Subviews

private struct HeaderRow: View {
    var body: some View {
        HStack(spacing: 14) {
            IconBadge(
                iconUnicode: "\u{f613}",
                foreground: Color(red: 0.91, green: 0.61, blue: 0.18),
                background: Color(red: 0.91, green: 0.61, blue: 0.18).opacity(0.15),
                size: 56,
                iconSize: 28,
                corner: 16
            )
            VStack(alignment: .leading, spacing: 2) {
                Text("Ganti Oli Mesin")
                    .font(.custom("PlusJakartaSans-ExtraBold", size: 22))
                    .foregroundColor(.sgTextPrimary)
                    .kerning(-0.3)
                Text("Beat Hitam · B 4521 KZA")
                    .font(.custom("PlusJakartaSans-Medium", size: 13))
                    .foregroundColor(.sgTextMuted)
            }
            Spacer()
        }
    }
}

private struct FactsCard: View {
    private let rows: [(String, String, Bool)] = [
        ("\u{f783}", "20 Februari 2026", false),
        ("\u{f624}", "16.000 km", true),
        ("\u{f3c5}", "AHASS Kebon Jeruk", false),
        ("\u{f613}", "Rp 65.000", true),
    ]

    var body: some View {
        VStack(spacing: 0) {
            ForEach(rows.indices, id: \.self) { idx in
                HStack(spacing: 14) {
                    Text(rows[idx].0)
                        .font(.custom("FontAwesome6Free-Solid", size: 16))
                        .foregroundColor(.sgTextMuted)
                        .frame(width: 18)
                    if rows[idx].2 {
                        Text(rows[idx].1)
                            .font(.system(size: 14, weight: .semibold, design: .monospaced))
                            .foregroundColor(.sgTextPrimary)
                    } else {
                        Text(rows[idx].1)
                            .font(.custom("PlusJakartaSans-SemiBold", size: 14))
                            .foregroundColor(.sgTextPrimary)
                    }
                    Spacer()
                }
                .padding(.horizontal, 16)
                .padding(.vertical, 14)

                if idx < rows.count - 1 {
                    Rectangle().fill(Color.sgBorder)
                        .frame(height: 1)
                        .padding(.horizontal, 16)
                }
            }
        }
        .background(Color.sgSurface)
        .clipShape(RoundedRectangle(cornerRadius: 18))
        .overlay(
            RoundedRectangle(cornerRadius: 18)
                .strokeBorder(Color.sgBorder, lineWidth: 1)
        )
    }
}

private struct SectionLabel: View {
    let text: String
    var body: some View {
        Text(text.uppercased())
            .font(.custom("PlusJakartaSans-ExtraBold", size: 11))
            .kerning(1)
            .foregroundColor(.sgTextMuted)
            .padding(.horizontal, 20)
            .padding(.bottom, 8)
            .frame(maxWidth: .infinity, alignment: .leading)
    }
}

private struct NoteBlock: View {
    let text: String
    var body: some View {
        Text(text)
            .font(.custom("PlusJakartaSans-Medium", size: 14))
            .foregroundColor(.sgTextPrimary)
            .lineSpacing(5)
            .frame(maxWidth: .infinity, alignment: .leading)
            .padding(.horizontal, 16)
            .padding(.vertical, 14)
            .background(Color.sgSurface)
            .clipShape(RoundedRectangle(cornerRadius: 18))
            .overlay(
                RoundedRectangle(cornerRadius: 18)
                    .strokeBorder(Color.sgBorder, lineWidth: 1)
            )
    }
}

private struct ComponentList: View {
    private let items: [(String, String, String, Color)] = [
        ("\u{f613}", "Oli mesin",  "4.420 km lalu", Color(red: 0.91, green: 0.61, blue: 0.18)),
        ("\u{f0b0}", "Filter oli", "4.420 km lalu", Color(red: 0.48, green: 0.44, blue: 0.91)),
    ]

    var body: some View {
        VStack(spacing: 0) {
            ForEach(items.indices, id: \.self) { idx in
                let item = items[idx]
                HStack(spacing: 12) {
                    IconBadge(
                        iconUnicode: item.0,
                        foreground: item.3,
                        background: item.3.opacity(0.13),
                        size: 36, iconSize: 16, corner: 10
                    )
                    Text(item.1)
                        .font(.custom("PlusJakartaSans-SemiBold", size: 14))
                        .foregroundColor(.sgTextPrimary)
                    Spacer()
                    Text(item.2)
                        .font(.system(size: 12, weight: .medium, design: .monospaced))
                        .foregroundColor(.sgTextMuted)
                }
                .padding(.horizontal, 14)
                .padding(.vertical, 12)
                if idx < items.count - 1 {
                    Rectangle().fill(Color.sgBorder)
                        .frame(height: 1)
                        .padding(.horizontal, 16)
                }
            }
        }
        .background(Color.sgSurface)
        .clipShape(RoundedRectangle(cornerRadius: 18))
        .overlay(
            RoundedRectangle(cornerRadius: 18)
                .strokeBorder(Color.sgBorder, lineWidth: 1)
        )
    }
}

private struct NextReminderCard: View {
    let onTap: () -> Void

    var body: some View {
        Button(action: onTap) {
            HStack(spacing: 12) {
                IconBadge(
                    iconUnicode: "\u{f0f3}",
                    foreground: .sgPrimary,
                    background: .sgPrimarySoft,
                    size: 36, iconSize: 16, corner: 10
                )
                VStack(alignment: .leading, spacing: 2) {
                    Text("20.420 km · 6 Jul 2026")
                        .font(.system(size: 14, weight: .bold, design: .monospaced))
                        .foregroundColor(.sgTextPrimary)
                    Text("Pengingat aktif untuk servis berikutnya")
                        .font(.custom("PlusJakartaSans-Medium", size: 12))
                        .foregroundColor(.sgTextMuted)
                }
                Spacer()
                StatusPill(urgency: .ok)
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

private struct ActionRow: View {
    let onEdit: () -> Void
    let onDelete: () -> Void

    var body: some View {
        HStack(spacing: 10) {
            Button(action: onEdit) {
                HStack(spacing: 8) {
                    Text("\u{f044}")
                        .font(.custom("FontAwesome6Free-Solid", size: 13))
                    Text("Edit")
                        .font(.custom("PlusJakartaSans-Bold", size: 14))
                }
                .foregroundColor(.sgTextPrimary)
                .frame(maxWidth: .infinity)
                .frame(height: 50)
                .background(Color.sgSurface)
                .clipShape(RoundedRectangle(cornerRadius: 14))
                .overlay(
                    RoundedRectangle(cornerRadius: 14)
                        .strokeBorder(Color.sgBorder, lineWidth: 1.5)
                )
            }
            .buttonStyle(.plain)

            Button(action: onDelete) {
                HStack(spacing: 8) {
                    Text("\u{f2ed}")
                        .font(.custom("FontAwesome6Free-Solid", size: 13))
                    Text("Hapus servis")
                        .font(.custom("PlusJakartaSans-Bold", size: 14))
                }
                .foregroundColor(.sgDanger)
                .frame(maxWidth: .infinity)
                .frame(height: 50)
                .background(Color.sgSurface)
                .clipShape(RoundedRectangle(cornerRadius: 14))
                .overlay(
                    RoundedRectangle(cornerRadius: 14)
                        .strokeBorder(Color.sgDanger.opacity(0.4), lineWidth: 1.5)
                )
            }
            .buttonStyle(.plain)
        }
    }
}

#Preview {
    NavigationStack { ServiceDetailView() }
}
