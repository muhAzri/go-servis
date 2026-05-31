import SwiftUI
import Shared

struct ReminderDetailView: View {
    let reminderId: String
    var onMarkServiced: (String, String) -> Void = { _, _ in }
    var onEdit: (String) -> Void = { _ in }
    var onDeleted: () -> Void = {}

    @StateObject private var model = ReminderDetailModel()
    @State private var showSnoozeSheet = false
    @State private var showDeleteConfirm = false

    private static let dateFormatter: DateFormatter = {
        let df = DateFormatter()
        df.locale = Locale(identifier: "id_ID")
        df.dateFormat = "d MMM yyyy"
        return df
    }()

    var body: some View {
        ScrollView {
            VStack(alignment: .leading, spacing: 16) {
                if let reminder = model.state.reminder {
                    let meta = ServiceTypeMeta.for(key: reminder.serviceType.key)
                    let vehicleLine: String = {
                        guard let v = model.state.vehicle else { return "Kendaraan dihapus" }
                        let plate = v.plateNumber.isEmpty ? nil : v.plateNumber
                        return plate.map { "\(v.displayTitle) · \($0)" } ?? v.displayTitle
                    }()

                    HStack(spacing: 14) {
                        IconBadge(
                            iconUnicode: meta.icon,
                            foreground: meta.color,
                            background: meta.color.opacity(0.15),
                            size: 56, iconSize: 28, corner: 16
                        )
                        VStack(alignment: .leading, spacing: 2) {
                            Text(reminder.title)
                                .font(.custom("PlusJakartaSans-ExtraBold", size: 22))
                                .foregroundColor(.sgTextPrimary)
                                .kerning(-0.3)
                            Text(vehicleLine)
                                .font(.custom("PlusJakartaSans-Medium", size: 13))
                                .foregroundColor(.sgTextMuted)
                        }
                        Spacer()
                        StatusPill(urgency: toUiUrgency(reminder.urgency))
                    }
                    .padding(.horizontal, 20)
                    .padding(.top, 12)

                    VStack(alignment: .leading, spacing: 8) {
                        ForEach(triggerLines(for: reminder.trigger), id: \.0) { (_, label, mono) in
                            Text(label)
                                .font(mono
                                      ? .system(size: 14, weight: .semibold, design: .monospaced)
                                      : .custom("PlusJakartaSans-SemiBold", size: 14))
                                .foregroundColor(.sgTextPrimary)
                        }
                        Text("Notif \(reminder.notifyDaysBefore) hari sebelumnya")
                            .font(.custom("PlusJakartaSans-SemiBold", size: 14))
                            .foregroundColor(.sgTextMuted)
                    }
                    .frame(maxWidth: .infinity, alignment: .leading)
                    .padding(16)
                    .background(Color.sgSurface)
                    .clipShape(RoundedRectangle(cornerRadius: 18))
                    .overlay(
                        RoundedRectangle(cornerRadius: 18)
                            .strokeBorder(Color.sgBorder, lineWidth: 1)
                    )
                    .padding(.horizontal, 16)

                    if let note = reminder.note, !note.isEmpty {
                        Text(note)
                            .font(.custom("PlusJakartaSans-Medium", size: 14))
                            .foregroundColor(.sgTextPrimary)
                            .frame(maxWidth: .infinity, alignment: .leading)
                            .padding(16)
                            .background(Color.sgSurface)
                            .clipShape(RoundedRectangle(cornerRadius: 18))
                            .overlay(
                                RoundedRectangle(cornerRadius: 18)
                                    .strokeBorder(Color.sgBorder, lineWidth: 1)
                            )
                            .padding(.horizontal, 16)
                    }

                    let status = reminder.status
                    if status == ReminderStatus.active || status == ReminderStatus.snoozed {
                        VStack(spacing: 8) {
                            Button(action: { onMarkServiced(reminder.id, reminder.vehicleId) }) {
                                HStack(spacing: 8) {
                                    Text("\u{f00c}")
                                        .font(.custom("FontAwesome6Free-Solid", size: 14))
                                    Text("Tandai sudah servis")
                                        .font(.custom("PlusJakartaSans-Bold", size: 15))
                                }
                                .foregroundColor(.white)
                                .frame(maxWidth: .infinity, minHeight: 54)
                                .background(Color.sgPrimary)
                                .clipShape(RoundedRectangle(cornerRadius: 16))
                            }
                            .buttonStyle(.plain)
                            .disabled(model.state.isBusy)

                            Button(action: { showSnoozeSheet = true }) {
                                HStack(spacing: 8) {
                                    Text("\u{f017}")
                                        .font(.custom("FontAwesome6Free-Solid", size: 13))
                                    Text("Tunda pengingat")
                                        .font(.custom("PlusJakartaSans-Bold", size: 14))
                                }
                                .foregroundColor(.sgTextPrimary)
                                .frame(maxWidth: .infinity, minHeight: 50)
                                .background(Color.sgSurface)
                                .clipShape(RoundedRectangle(cornerRadius: 14))
                                .overlay(
                                    RoundedRectangle(cornerRadius: 14)
                                        .strokeBorder(Color.sgBorder, lineWidth: 1.5)
                                )
                            }
                            .buttonStyle(.plain)
                            .disabled(model.state.isBusy)
                        }
                        .padding(.horizontal, 16)
                    } else {
                        Text(status == ReminderStatus.completed ? "Sudah selesai" : "Dilewatkan")
                            .font(.custom("PlusJakartaSans-Bold", size: 14))
                            .foregroundColor(.sgPrimary)
                            .frame(maxWidth: .infinity)
                            .padding(.vertical, 14)
                            .background(Color.sgPrimarySoft)
                            .clipShape(RoundedRectangle(cornerRadius: 14))
                            .padding(.horizontal, 16)
                    }
                } else {
                    Text(model.state.isLoading ? "Memuat…" : "Pengingat tidak ditemukan")
                        .font(.custom("PlusJakartaSans-Medium", size: 14))
                        .foregroundColor(.sgTextMuted)
                        .padding(40)
                        .frame(maxWidth: .infinity)
                }
            }
            .padding(.bottom, 24)
        }
        .background(Color.sgBgWarm)
        .navigationTitle("Detail Pengingat")
        .navigationBarTitleDisplayMode(.inline)
        .toolbar {
            ToolbarItem(placement: .navigationBarTrailing) {
                Menu {
                    Button("Edit") { onEdit(reminderId) }
                    Button("Hapus", role: .destructive) { showDeleteConfirm = true }
                } label: {
                    Image(systemName: "ellipsis.circle")
                }
            }
        }
        .onAppear {
            model.load(reminderId: reminderId)
            model.onDeleted = onDeleted
            model.onDismissed = onDeleted
        }
        .onDisappear {
            model.onDeleted = nil
            model.onDismissed = nil
        }
        .confirmationDialog(
            "Hapus pengingat?",
            isPresented: $showDeleteConfirm,
            titleVisibility: .visible
        ) {
            Button("Hapus", role: .destructive) { model.delete() }
            Button("Batal", role: .cancel) { }
        } message: {
            Text("Pengingat ini akan dihapus permanen.")
        }
        .sheet(isPresented: $showSnoozeSheet) {
            SnoozeSheet(
                onPick: { duration in
                    model.snooze(duration)
                    showSnoozeSheet = false
                }
            )
            .presentationDetents([.medium])
        }
    }

    private func triggerLines(for trigger: ReminderTrigger) -> [(String, String, Bool)] {
        var result: [(String, String, Bool)] = []
        if let km = trigger.targetKm {
            let kmInt = Int64(truncating: km)
            let formatter = NumberFormatter()
            formatter.numberStyle = .decimal
            formatter.groupingSeparator = "."
            let s = formatter.string(from: NSNumber(value: kmInt)) ?? "\(kmInt)"
            result.append(("km", "\(s) km", true))
        }
        if let date = trigger.targetDateMillis {
            let d = Date(timeIntervalSince1970: TimeInterval(Int64(truncating: date)) / 1000.0)
            result.append(("date", Self.dateFormatter.string(from: d), false))
        }
        return result
    }
}
