import SwiftUI

struct ReminderDetailView: View {
    var onMarkServiced: () -> Void = {}
    var onEdit: () -> Void = {}
    var onDelete: () -> Void = {}

    @State private var isSnoozeSheetPresented = false
    @State private var showDeleteConfirm: Bool = false

    var body: some View {
        VStack(spacing: 0) {
            ScrollView {
                VStack(alignment: .leading, spacing: 0) {
                    VStack(alignment: .leading, spacing: 12) {
                        StatusPill(urgency: .overdue)
                        Text("Ganti Oli Mesin")
                            .font(.custom("PlusJakartaSans-ExtraBold", size: 28))
                            .foregroundColor(.sgTextPrimary)
                            .kerning(-0.5)
                        Text("Beat Hitam · Honda BeAT 110 2022")
                            .font(.custom("PlusJakartaSans-Medium", size: 14))
                            .foregroundColor(.sgTextMuted)
                    }
                    .padding(.horizontal, 20)
                    .padding(.top, 8)
                    .padding(.bottom, 16)

                    OverdueStatsCard()
                        .padding(.horizontal, 16)
                        .padding(.bottom, 16)

                    DetailSectionLabel(text: "Detail servis")
                    ServiceDetailRows()

                    NativeAdCard()
                        .padding(.top, 8)
                        .padding(.bottom, 24)
                }
            }

            ReminderActionBar(
                onSnooze: { isSnoozeSheetPresented = true },
                onMarkServiced: onMarkServiced
            )
        }
        .background(Color.sgBgWarm)
        .navigationBarTitleDisplayMode(.inline)
        .toolbar {
            ToolbarItemGroup(placement: .topBarTrailing) {
                Button(action: onEdit) {
                    Image(systemName: "square.and.pencil")
                }
                Button(action: { showDeleteConfirm = true }) {
                    Image(systemName: "trash")
                }
                .tint(.sgDanger)
            }
        }
        .sheet(isPresented: $isSnoozeSheetPresented) {
            SnoozeSheet(onDismiss: { isSnoozeSheetPresented = false })
                .presentationDetents([.fraction(0.55)])
                .presentationDragIndicator(.hidden)
        }
        .confirmationDialog(
            "Hapus pengingat?",
            isPresented: $showDeleteConfirm,
            titleVisibility: .visible
        ) {
            Button("Hapus pengingat", role: .destructive, action: onDelete)
            Button("Batal", role: .cancel) { }
        } message: {
            Text("Pengingat ini akan dihapus dan tidak akan muncul lagi di lock screen.")
        }
    }
}

// MARK: - Subviews

private struct OverdueStatsCard: View {
    var body: some View {
        VStack(alignment: .leading, spacing: 14) {
            Text("TELAT 16 HARI · 420 KM")
                .font(.custom("PlusJakartaSans-ExtraBold", size: 12))
                .kerning(0.5)
                .foregroundColor(.sgDanger)

            HStack(alignment: .top, spacing: 14) {
                VStack(alignment: .leading, spacing: 4) {
                    Text("Target servis")
                        .font(.custom("PlusJakartaSans-Medium", size: 11))
                        .foregroundColor(.sgTextMuted)
                    Text("18.000 km")
                        .font(.system(size: 20, weight: .bold, design: .monospaced))
                        .foregroundColor(.sgTextPrimary)
                    Text("20 Apr 2026")
                        .font(.custom("PlusJakartaSans-Medium", size: 11))
                        .foregroundColor(.sgTextMuted)
                }
                .frame(maxWidth: .infinity, alignment: .leading)

                VStack(alignment: .leading, spacing: 4) {
                    Text("KM sekarang")
                        .font(.custom("PlusJakartaSans-Medium", size: 11))
                        .foregroundColor(.sgTextMuted)
                    Text("18.420 km")
                        .font(.system(size: 20, weight: .bold, design: .monospaced))
                        .foregroundColor(.sgDanger)
                    Text("diperbarui 2h lalu")
                        .font(.custom("PlusJakartaSans-Medium", size: 11))
                        .foregroundColor(.sgTextMuted)
                }
                .frame(maxWidth: .infinity, alignment: .leading)
            }

            Capsule()
                .fill(Color.sgDanger)
                .frame(height: 8)
                .background(Color.black.opacity(0.06))
                .clipShape(Capsule())
        }
        .padding(20)
        .background(Color.sgDangerSoft)
        .clipShape(RoundedRectangle(cornerRadius: 22))
        .overlay(
            RoundedRectangle(cornerRadius: 22)
                .strokeBorder(Color.sgDanger.opacity(0.19), lineWidth: 1)
        )
    }
}

private struct DetailSectionLabel: View {
    let text: String
    var trailing: String? = nil

    var body: some View {
        HStack(spacing: 6) {
            Text(text.uppercased())
                .font(.custom("PlusJakartaSans-ExtraBold", size: 11))
                .kerning(1)
                .foregroundColor(.sgTextMuted)
            if let trailing {
                Text(trailing)
                    .font(.custom("PlusJakartaSans-Bold", size: 11))
                    .foregroundColor(.sgWarning)
            }
        }
        .padding(.horizontal, 20)
        .padding(.top, 8)
        .padding(.bottom, 6)
    }
}

private struct ServiceDetailRows: View {
    private let rows: [(String, String)] = [
        ("Interval", "2.000 km / 2 bln"),
        ("Servis terakhir", "20 Feb 2026 · 16.000 km"),
        ("Bengkel terakhir", "AHASS Kebon Jeruk"),
        ("Biaya terakhir", "Rp 65.000"),
    ]

    var body: some View {
        VStack(spacing: 0) {
            ForEach(rows.indices, id: \.self) { idx in
                HStack {
                    Text(rows[idx].0)
                        .font(.custom("PlusJakartaSans-Medium", size: 13))
                        .foregroundColor(.sgTextMuted)
                    Spacer()
                    Text(rows[idx].1)
                        .font(.custom("PlusJakartaSans-SemiBold", size: 13))
                        .foregroundColor(.sgTextPrimary)
                }
                .padding(.vertical, 14)

                if idx < rows.count - 1 {
                    Rectangle().fill(Color.sgBorder).frame(height: 1)
                }
            }
        }
        .padding(.horizontal, 16)
        .background(Color.sgSurface)
        .clipShape(RoundedRectangle(cornerRadius: 18))
        .overlay(
            RoundedRectangle(cornerRadius: 18)
                .strokeBorder(Color.sgBorder, lineWidth: 1)
        )
        .padding(.horizontal, 16)
        .padding(.bottom, 12)
    }
}

private struct ReminderActionBar: View {
    let onSnooze: () -> Void
    let onMarkServiced: () -> Void

    var body: some View {
        HStack(spacing: 10) {
            Button(action: onSnooze) {
                Text("Tunda")
                    .font(.custom("PlusJakartaSans-Bold", size: 14))
                    .foregroundColor(.sgTextPrimary)
                    .frame(maxWidth: .infinity)
                    .frame(height: 50)
                    .background(Color.sgSurfaceAlt)
                    .clipShape(RoundedRectangle(cornerRadius: 14))
            }
            .buttonStyle(.plain)
            .frame(maxWidth: .infinity)

            Button(action: onMarkServiced) {
                HStack(spacing: 6) {
                    Text("\u{f00c}")
                        .font(.custom("FontAwesome6Free-Solid", size: 14))
                    Text("Tandai Sudah Servis")
                        .font(.custom("PlusJakartaSans-Bold", size: 14))
                }
                .foregroundColor(.white)
                .frame(maxWidth: .infinity)
                .frame(height: 50)
                .background(Color.sgPrimary)
                .clipShape(RoundedRectangle(cornerRadius: 14))
            }
            .buttonStyle(.plain)
            .frame(maxWidth: .infinity)
            .layoutPriority(1)
        }
        .padding(EdgeInsets(top: 10, leading: 16, bottom: 24, trailing: 16))
        .background(
            Color.sgSurface
                .overlay(alignment: .top) {
                    Rectangle().fill(Color.sgBorder).frame(height: 1)
                }
        )
    }
}

#Preview {
    NavigationStack { ReminderDetailView() }
}
