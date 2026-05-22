import SwiftUI

private let maxNameLen = 20

private struct PaletteColor: Identifiable, Equatable {
    let id: String
    let color: Color
}

private let palette: [PaletteColor] = [
    PaletteColor(id: "primary", color: .sgPrimary),
    PaletteColor(id: "danger", color: Color(red: 0.84, green: 0.27, blue: 0.23)),
    PaletteColor(id: "cyan", color: Color(red: 0.25, green: 0.69, blue: 0.84)),
    PaletteColor(id: "amber", color: Color(red: 0.91, green: 0.61, blue: 0.18)),
    PaletteColor(id: "violet", color: Color(red: 0.48, green: 0.44, blue: 0.91)),
    PaletteColor(id: "ink", color: Color(red: 0.10, green: 0.14, blue: 0.09)),
]

struct EditProfileView: View {
    let initialName: String
    let initialColorId: String
    let onSave: (_ name: String, _ colorId: String) -> Void
    let onCancel: () -> Void

    @State private var draftName: String = ""
    @State private var draftColorId: String = "primary"
    @State private var draftEmail: String = ""
    @State private var emailError: String = ""
    @FocusState private var isFocused: Bool
    @FocusState private var emailFocused: Bool

    private var selectedColor: Color {
        palette.first(where: { $0.id == draftColorId })?.color ?? .sgPrimary
    }

    private var initial: String {
        String(draftName.trimmingCharacters(in: .whitespacesAndNewlines).first ?? "B").uppercased()
    }

    var body: some View {
        ScrollView {
            VStack(alignment: .leading, spacing: 0) {
                avatarSection
                Spacer().frame(height: 24)
                sectionLabel("Warna avatar")
                Spacer().frame(height: 8)
                colorPickerRow
                Spacer().frame(height: 22)
                nameField
                Spacer().frame(height: 18)
                emailField
                Spacer().frame(height: 22)
                statsCard
                Spacer().frame(height: 16)
                resetButton
            }
            .padding(.horizontal, 20)
            .padding(.top, 8)
            .padding(.bottom, 28)
        }
        .background(Color.sgBgWarm)
        .navigationTitle("Edit Profil")
        .navigationBarTitleDisplayMode(.inline)
        .toolbar {
            ToolbarItem(placement: .topBarTrailing) {
                Button(action: commit) {
                    Text("Simpan")
                        .font(.custom("PlusJakartaSans-Bold", size: 14))
                        .foregroundColor(.sgPrimary)
                }
            }
        }
        .onAppear {
            draftName = initialName
            draftColorId = initialColorId.isEmpty ? "primary" : initialColorId
        }
    }

    private var avatarSection: some View {
        VStack(spacing: 10) {
            ZStack {
                RoundedRectangle(cornerRadius: 28, style: .continuous)
                    .fill(selectedColor)
                    .frame(width: 96, height: 96)
                    .shadow(color: selectedColor.opacity(0.27), radius: 14, x: 0, y: 14)
                Text(initial)
                    .font(.custom("PlusJakartaSans-ExtraBold", size: 42))
                    .foregroundColor(.white)
            }
            Text("Inisial dari nama panggilanmu")
                .font(.custom("PlusJakartaSans-Regular", size: 12))
                .foregroundColor(.sgTextMuted)
        }
        .frame(maxWidth: .infinity)
        .padding(.top, 12)
    }

    private func sectionLabel(_ text: String) -> some View {
        Text(text.uppercased())
            .font(.custom("PlusJakartaSans-ExtraBold", size: 11))
            .kerning(1)
            .foregroundColor(.sgTextMuted)
    }

    private var colorPickerRow: some View {
        HStack(spacing: 10) {
            ForEach(palette) { p in
                Button {
                    draftColorId = p.id
                } label: {
                    RoundedRectangle(cornerRadius: 14)
                        .fill(p.color)
                        .frame(width: 40, height: 40)
                        .overlay(
                            RoundedRectangle(cornerRadius: 14)
                                .strokeBorder(
                                    p.id == draftColorId ? Color.sgTextPrimary : Color.sgBorder,
                                    lineWidth: p.id == draftColorId ? 3 : 1
                                )
                        )
                }
                .buttonStyle(.plain)
            }
            Spacer()
        }
    }

    private var nameField: some View {
        VStack(alignment: .leading, spacing: 6) {
            Text("Nama panggilan")
                .font(.custom("PlusJakartaSans-SemiBold", size: 13))
                .foregroundColor(.sgTextMuted)
            ZStack(alignment: .leading) {
                if draftName.isEmpty {
                    Text("Misal: Budi")
                        .font(.custom("PlusJakartaSans-Regular", size: 15))
                        .foregroundColor(.sgTextSubtle)
                        .padding(.horizontal, 16)
                }
                TextField("", text: $draftName)
                    .font(.custom("PlusJakartaSans-Regular", size: 15))
                    .foregroundColor(.sgTextPrimary)
                    .focused($isFocused)
                    .onChange(of: draftName) { _, newValue in
                        if newValue.count > maxNameLen {
                            draftName = String(newValue.prefix(maxNameLen))
                        }
                    }
                    .padding(.horizontal, 16)
                    .padding(.vertical, 14)
            }
            .background(Color.white)
            .clipShape(RoundedRectangle(cornerRadius: 14))
            .overlay(
                RoundedRectangle(cornerRadius: 14)
                    .strokeBorder(
                        isFocused ? Color.sgPrimary : Color.black.opacity(0.08),
                        lineWidth: 1.5
                    )
            )

            HStack {
                Text("Dipakai untuk sapaan di Beranda. Tidak dikirim ke server.")
                    .font(.custom("PlusJakartaSans-Regular", size: 12))
                    .foregroundColor(.sgTextSubtle)
                Spacer()
                Text("\(draftName.count)/\(maxNameLen)")
                    .font(.custom("PlusJakartaSans-Regular", size: 11))
                    .foregroundColor(.sgTextSubtle)
            }
        }
    }

    private var emailField: some View {
        VStack(alignment: .leading, spacing: 6) {
            sectionLabel("Email (opsional · untuk backup)")
            HStack(spacing: 10) {
                Text("\u{f0e0}")
                    .font(.custom("FontAwesome6Free-Solid", size: 16))
                    .foregroundColor(.sgTextMuted)
                TextField("alamat@email.com", text: $draftEmail)
                    .focused($emailFocused)
                    .keyboardType(.emailAddress)
                    .textInputAutocapitalization(.never)
                    .autocorrectionDisabled()
                    .font(.custom("PlusJakartaSans-SemiBold", size: 15))
                    .foregroundColor(.sgTextPrimary)
                    .onChange(of: draftEmail) { _, newValue in
                        validateEmail(newValue)
                    }
                if !draftEmail.isEmpty && emailError.isEmpty {
                    Text("\u{f00c}")
                        .font(.custom("FontAwesome6Free-Solid", size: 14))
                        .foregroundColor(.sgPrimary)
                }
            }
            .padding(.horizontal, 16)
            .padding(.vertical, 14)
            .frame(maxWidth: .infinity, alignment: .leading)
            .background(Color.sgSurface)
            .clipShape(RoundedRectangle(cornerRadius: 14))
            .overlay(
                RoundedRectangle(cornerRadius: 14)
                    .strokeBorder(emailError.isEmpty ? (emailFocused ? Color.sgPrimary : Color.sgBorder) : Color.sgDanger, lineWidth: 1.5)
            )

            if !emailError.isEmpty {
                HStack(spacing: 6) {
                    Text("\u{f06a}")
                        .font(.custom("FontAwesome6Free-Solid", size: 11))
                        .foregroundColor(.sgDanger)
                    Text(emailError)
                        .font(.custom("PlusJakartaSans-SemiBold", size: 11))
                        .foregroundColor(.sgDanger)
                }
                .padding(.leading, 4)
            } else {
                Text("Dipakai untuk recovery data kalau kamu ganti HP atau hapus app.")
                    .font(.custom("PlusJakartaSans-Regular", size: 11))
                    .foregroundColor(.sgTextSubtle)
                    .padding(.leading, 4)
            }
        }
    }

    private func validateEmail(_ value: String) {
        if value.isEmpty {
            emailError = ""
            return
        }
        let pattern = #"^[^\s@]+@[^\s@]+\.[^\s@]+$"#
        let valid = value.range(of: pattern, options: .regularExpression) != nil
        emailError = valid ? "" : "Format email tidak valid"
    }

    private var statsCard: some View {
        VStack(alignment: .leading, spacing: 12) {
            sectionLabel("Akun kamu")
            HStack(alignment: .top, spacing: 8) {
                stat(value: "4", label: "Kendaraan")
                stat(value: "12", label: "Servis")
                stat(value: "Jan '26", label: "Sejak")
            }
        }
        .padding(16)
        .frame(maxWidth: .infinity, alignment: .leading)
        .background(Color.sgSurfaceAlt)
        .clipShape(RoundedRectangle(cornerRadius: 18))
    }

    private func stat(value: String, label: String) -> some View {
        VStack(alignment: .leading, spacing: 2) {
            Text(value)
                .font(.system(size: 20, weight: .heavy, design: .monospaced))
                .foregroundColor(.sgTextPrimary)
                .kerning(-0.4)
            Text(label)
                .font(.custom("PlusJakartaSans-Regular", size: 11))
                .foregroundColor(.sgTextMuted)
        }
        .frame(maxWidth: .infinity, alignment: .leading)
    }

    private var resetButton: some View {
        Button {
            draftName = ""
            draftColorId = "primary"
        } label: {
            Text("Reset ke pengaturan awal")
                .font(.custom("PlusJakartaSans-Bold", size: 13))
                .foregroundColor(.sgDanger)
                .frame(maxWidth: .infinity)
                .frame(height: 48)
                .background(Color.clear)
                .clipShape(RoundedRectangle(cornerRadius: 14))
                .overlay(
                    RoundedRectangle(cornerRadius: 14)
                        .strokeBorder(Color.sgDanger.opacity(0.4), lineWidth: 1)
                )
        }
        .buttonStyle(.plain)
    }

    private func commit() {
        let trimmed = draftName.trimmingCharacters(in: .whitespacesAndNewlines)
        onSave(trimmed, draftColorId)
    }
}

#Preview {
    NavigationStack {
        EditProfileView(
            initialName: "Budi",
            initialColorId: "primary",
            onSave: { _, _ in },
            onCancel: {}
        )
    }
}
