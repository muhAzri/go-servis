import SwiftUI

enum TriggerMode: Hashable {
    case km, date, both
}

private struct ServiceChoice: Identifiable, Hashable {
    let id: String
    let label: String
    let iconUnicode: String
    let color: Color
}

private let serviceChoices: [ServiceChoice] = [
    .init(id: "oli",      label: "Ganti Oli",  iconUnicode: "\u{f613}", color: Color(red: 0.91, green: 0.61, blue: 0.18)),
    .init(id: "filter",   label: "Filter",     iconUnicode: "\u{f0b0}", color: Color(red: 0.48, green: 0.44, blue: 0.91)),
    .init(id: "rem",      label: "Kampas Rem", iconUnicode: "\u{f1ce}", color: Color(red: 0.18, green: 0.55, blue: 0.34)),
    .init(id: "ban",      label: "Ban",        iconUnicode: "\u{f1cd}", color: Color(red: 0.25, green: 0.30, blue: 0.36)),
    .init(id: "aki",      label: "Aki",        iconUnicode: "\u{f5df}", color: Color(red: 0.84, green: 0.27, blue: 0.23)),
    .init(id: "radiator", label: "Coolant",    iconUnicode: "\u{f2c9}", color: Color(red: 0.25, green: 0.69, blue: 0.84)),
]

struct AddReminderView: View {
    var onSaved: () -> Void = {}
    var onOpenNotifSettings: () -> Void = {}
    var fromContext: Bool = false
    var notifPermissionGranted: Bool = true

    @State private var selectedService: String = "oli"
    @State private var trigger: TriggerMode = .km
    @State private var targetKm: String = "20.420"
    @State private var targetDate: String = "6 Jul 2026"
    @State private var note: String = ""
    @State private var showCtxBanner: Bool

    init(
        onSaved: @escaping () -> Void = {},
        onOpenNotifSettings: @escaping () -> Void = {},
        fromContext: Bool = false,
        notifPermissionGranted: Bool = true
    ) {
        self.onSaved = onSaved
        self.onOpenNotifSettings = onOpenNotifSettings
        self.fromContext = fromContext
        self.notifPermissionGranted = notifPermissionGranted
        _showCtxBanner = State(initialValue: fromContext)
    }

    var body: some View {
        ScrollView {
            VStack(alignment: .leading, spacing: 0) {
                if showCtxBanner {
                    ContextBanner(
                        title: "Dari servis tercatat",
                        body: "Ganti Oli Mesin · Beat Hitam — field di bawah sudah diisi otomatis.",
                        iconUnicode: "\u{f0f3}",
                        tone: .info,
                        onDismiss: { showCtxBanner = false }
                    )
                    .padding(.bottom, 16)
                }

                FieldLabel(text: "Kendaraan")
                VehiclePickerRow(locked: showCtxBanner)
                    .padding(.bottom, 18)

                FieldLabel(text: "Jenis servis")
                ServiceTypeGrid(selected: $selectedService, locked: showCtxBanner)
                    .padding(.bottom, 18)

                FieldLabel(text: "Picu pengingat")
                TriggerSegmented(selected: $trigger)
                    .padding(.bottom, 14)

                if trigger == .km || trigger == .both {
                    NumberField(
                        label: "Target KM",
                        value: $targetKm,
                        helper: "Interval pabrikan: 2.000 km · KM saat ini 18.420"
                    )
                }
                if trigger == .date || trigger == .both {
                    DateField(
                        label: "Tanggal",
                        value: targetDate,
                        helper: "Pengingat dimulai: 7 hari sebelum"
                    )
                    .padding(.top, trigger == .both ? 12 : 0)
                }

                FieldLabel(text: "Catatan (opsional)")
                    .padding(.top, 18)
                NoteField(value: $note)

                if !notifPermissionGranted {
                    ContextBanner(
                        title: "Notif belum aktif",
                        body: "Pengingat servis tidak akan muncul di lock screen. Aktifkan supaya tidak kelewat.",
                        iconUnicode: "\u{f1f6}",
                        tone: .warning,
                        ctaLabel: "Aktifkan →",
                        onCta: onOpenNotifSettings
                    )
                    .padding(.top, 20)
                }

                Spacer().frame(height: 24)
            }
            .padding(.horizontal, 20)
            .padding(.top, 8)
        }
        .background(Color.sgBgWarm)
        .navigationTitle("Buat Pengingat")
        .navigationBarTitleDisplayMode(.inline)
        .toolbar {
            ToolbarItem(placement: .topBarTrailing) {
                Button(action: onSaved) {
                    Text("Simpan")
                        .font(.custom("PlusJakartaSans-Bold", size: 15))
                        .foregroundColor(.sgPrimary)
                }
            }
        }
    }
}

// MARK: - Subviews

private struct FieldLabel: View {
    let text: String
    var body: some View {
        Text(text.uppercased())
            .font(.custom("PlusJakartaSans-ExtraBold", size: 11))
            .kerning(1)
            .foregroundColor(.sgTextMuted)
            .padding(.bottom, 8)
            .frame(maxWidth: .infinity, alignment: .leading)
    }
}

private struct VehiclePickerRow: View {
    let locked: Bool

    var body: some View {
        HStack(spacing: 12) {
            IconBadge(
                iconUnicode: "\u{f21c}",
                foreground: .sgPrimary,
                background: .sgPrimarySoft,
                size: 40, iconSize: 22, corner: 10
            )
            VStack(alignment: .leading, spacing: 2) {
                Text("Beat Hitam")
                    .font(.custom("PlusJakartaSans-Bold", size: 14))
                    .foregroundColor(.sgTextPrimary)
                Text("B 4521 KZA")
                    .font(.system(size: 12, weight: .medium, design: .monospaced))
                    .foregroundColor(.sgTextMuted)
            }
            Spacer()
            Text(locked ? "\u{f023}" : "\u{f078}")
                .font(.custom("FontAwesome6Free-Solid", size: 14))
                .foregroundColor(.sgTextSubtle)
        }
        .padding(14)
        .background(locked ? Color.sgSurfaceAlt : Color.sgSurface)
        .clipShape(RoundedRectangle(cornerRadius: 14))
        .overlay(
            RoundedRectangle(cornerRadius: 14)
                .strokeBorder(Color.sgBorder, lineWidth: 1.5)
        )
    }
}

private struct ServiceTypeGrid: View {
    @Binding var selected: String
    let locked: Bool

    var body: some View {
        LazyVGrid(
            columns: [.init(.flexible(), spacing: 8), .init(.flexible(), spacing: 8), .init(.flexible(), spacing: 8)],
            spacing: 8
        ) {
            ForEach(serviceChoices) { c in
                Button {
                    if !locked { selected = c.id }
                } label: {
                    VStack(spacing: 6) {
                        Text(c.iconUnicode)
                            .font(.custom("FontAwesome6Free-Solid", size: 22))
                            .foregroundColor(selected == c.id ? .sgPrimary : c.color)
                        Text(c.label)
                            .font(.custom("PlusJakartaSans-SemiBold", size: 11))
                            .foregroundColor(.sgTextPrimary)
                    }
                    .frame(maxWidth: .infinity)
                    .padding(.vertical, 14)
                    .background(selected == c.id ? Color.sgPrimarySoft : Color.sgSurface)
                    .clipShape(RoundedRectangle(cornerRadius: 14))
                    .overlay(
                        RoundedRectangle(cornerRadius: 14)
                            .strokeBorder(
                                selected == c.id ? Color.sgPrimary : Color.sgBorder,
                                lineWidth: 1.5
                            )
                    )
                }
                .buttonStyle(.plain)
                .disabled(locked)
            }
        }
    }
}

private struct TriggerSegmented: View {
    @Binding var selected: TriggerMode

    private let options: [(mode: TriggerMode, label: String)] = [
        (.km, "Per KM"),
        (.date, "Per Tanggal"),
        (.both, "Keduanya"),
    ]

    var body: some View {
        HStack(spacing: 4) {
            ForEach(options, id: \.mode) { o in
                Button {
                    selected = o.mode
                } label: {
                    Text(o.label)
                        .font(.custom("PlusJakartaSans-Bold", size: 12))
                        .foregroundColor(selected == o.mode ? .sgTextPrimary : .sgTextMuted)
                        .frame(maxWidth: .infinity)
                        .padding(.vertical, 10)
                        .background(selected == o.mode ? Color.sgSurface : Color.clear)
                        .clipShape(RoundedRectangle(cornerRadius: 9))
                }
                .buttonStyle(.plain)
            }
        }
        .padding(4)
        .background(Color.sgSurfaceAlt)
        .clipShape(RoundedRectangle(cornerRadius: 12))
    }
}

private struct NumberField: View {
    let label: String
    @Binding var value: String
    let helper: String

    var body: some View {
        VStack(alignment: .leading, spacing: 6) {
            Text(label.uppercased())
                .font(.custom("PlusJakartaSans-ExtraBold", size: 11))
                .kerning(1)
                .foregroundColor(.sgTextMuted)
                .padding(.bottom, 2)
            HStack(spacing: 10) {
                Text("\u{f625}")
                    .font(.custom("FontAwesome6Free-Solid", size: 16))
                    .foregroundColor(.sgTextMuted)
                Text(value)
                    .font(.system(size: 15, weight: .semibold, design: .monospaced))
                    .foregroundColor(.sgTextPrimary)
                Spacer()
                Text("km")
                    .font(.custom("PlusJakartaSans-SemiBold", size: 13))
                    .foregroundColor(.sgTextSubtle)
            }
            .padding(.horizontal, 16)
            .padding(.vertical, 16)
            .background(Color.sgSurface)
            .clipShape(RoundedRectangle(cornerRadius: 14))
            .overlay(
                RoundedRectangle(cornerRadius: 14)
                    .strokeBorder(Color.sgBorder, lineWidth: 1.5)
            )
            Text(helper)
                .font(.custom("PlusJakartaSans-Medium", size: 11))
                .foregroundColor(.sgTextSubtle)
                .padding(.leading, 4)
        }
    }
}

private struct DateField: View {
    let label: String
    let value: String
    let helper: String

    var body: some View {
        VStack(alignment: .leading, spacing: 6) {
            Text(label.uppercased())
                .font(.custom("PlusJakartaSans-ExtraBold", size: 11))
                .kerning(1)
                .foregroundColor(.sgTextMuted)
                .padding(.bottom, 2)
            HStack(spacing: 10) {
                Text("\u{f783}")
                    .font(.custom("FontAwesome6Free-Solid", size: 16))
                    .foregroundColor(.sgTextMuted)
                Text(value)
                    .font(.custom("PlusJakartaSans-SemiBold", size: 15))
                    .foregroundColor(.sgTextPrimary)
                Spacer()
                Text("\u{f078}")
                    .font(.custom("FontAwesome6Free-Solid", size: 12))
                    .foregroundColor(.sgTextSubtle)
            }
            .padding(.horizontal, 16)
            .padding(.vertical, 16)
            .background(Color.sgSurface)
            .clipShape(RoundedRectangle(cornerRadius: 14))
            .overlay(
                RoundedRectangle(cornerRadius: 14)
                    .strokeBorder(Color.sgBorder, lineWidth: 1.5)
            )
            Text(helper)
                .font(.custom("PlusJakartaSans-Medium", size: 11))
                .foregroundColor(.sgTextSubtle)
                .padding(.leading, 4)
        }
    }
}

private struct NoteField: View {
    @Binding var value: String

    var body: some View {
        TextEditor(text: $value)
            .scrollContentBackground(.hidden)
            .background(Color.sgSurface)
            .font(.custom("PlusJakartaSans-Medium", size: 14))
            .foregroundColor(.sgTextPrimary)
            .frame(height: 96)
            .padding(.horizontal, 10)
            .padding(.vertical, 6)
            .background(Color.sgSurface)
            .clipShape(RoundedRectangle(cornerRadius: 14))
            .overlay(
                RoundedRectangle(cornerRadius: 14)
                    .strokeBorder(Color.sgBorder, lineWidth: 1.5)
            )
            .overlay(alignment: .topLeading) {
                if value.isEmpty {
                    Text("Tambah catatan…")
                        .font(.custom("PlusJakartaSans-Medium", size: 14))
                        .foregroundColor(.sgTextSubtle)
                        .padding(.horizontal, 16)
                        .padding(.vertical, 14)
                        .allowsHitTesting(false)
                }
            }
    }
}

#Preview {
    NavigationStack {
        AddReminderView(fromContext: true)
    }
}
