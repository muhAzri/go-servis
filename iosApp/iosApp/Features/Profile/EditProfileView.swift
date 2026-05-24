import SwiftUI
import Shared

struct EditProfileView: View {
    let onBack: () -> Void
    let onSaved: () -> Void

    @StateObject private var model = EditProfileModel()
    @ObservedObject private var vehicles = VehicleListModel.shared
    @ObservedObject private var services = ServiceHistoryModel.shared

    @FocusState private var nameFocused: Bool
    @FocusState private var emailFocused: Bool

    private var state: EditProfileViewModel.UiState { model.state }
    private var selectedColor: Color { hexColor(state.avatarColorHex) ?? .sgPrimary }

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
                Button(action: { model.save() }) {
                    Text("Simpan")
                        .font(.custom("PlusJakartaSans-Bold", size: 14))
                        .foregroundColor(state.canSave ? .sgPrimary : .sgPrimary.opacity(0.45))
                }
                .disabled(!state.canSave)
            }
        }
        .onAppear { model.onSaved = onSaved }
        .onDisappear { model.onSaved = nil }
    }

    private var avatarSection: some View {
        VStack(spacing: 10) {
            ZStack {
                RoundedRectangle(cornerRadius: 28, style: .continuous)
                    .fill(selectedColor)
                    .frame(width: 96, height: 96)
                    .shadow(color: selectedColor.opacity(0.27), radius: 14, x: 0, y: 14)
                Text(state.initial)
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
            ForEach(state.palette, id: \.self) { hex in
                let isSelected = hex == state.avatarColorHex
                Button {
                    model.setAvatarColor(hex)
                } label: {
                    RoundedRectangle(cornerRadius: 14)
                        .fill(hexColor(hex) ?? .sgPrimary)
                        .frame(width: 40, height: 40)
                        .overlay(
                            RoundedRectangle(cornerRadius: 14)
                                .strokeBorder(
                                    isSelected ? Color.sgTextPrimary : Color.sgBorder,
                                    lineWidth: isSelected ? 3 : 1
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
                if state.name.isEmpty {
                    Text("Misal: Budi")
                        .font(.custom("PlusJakartaSans-Regular", size: 15))
                        .foregroundColor(.sgTextSubtle)
                        .padding(.horizontal, 16)
                }
                TextField("", text: Binding(
                    get: { state.name },
                    set: { model.setName($0) }
                ))
                    .font(.custom("PlusJakartaSans-Regular", size: 15))
                    .foregroundColor(.sgTextPrimary)
                    .focused($nameFocused)
                    .padding(.horizontal, 16)
                    .padding(.vertical, 14)
            }
            .background(Color.white)
            .clipShape(RoundedRectangle(cornerRadius: 14))
            .overlay(
                RoundedRectangle(cornerRadius: 14)
                    .strokeBorder(
                        nameError != nil ? Color.sgDanger :
                            (nameFocused ? Color.sgPrimary : Color.black.opacity(0.08)),
                        lineWidth: 1.5
                    )
            )

            HStack {
                Text(nameHintOrError)
                    .font(.custom("PlusJakartaSans-Regular", size: 12))
                    .foregroundColor(nameError != nil ? .sgDanger : .sgTextSubtle)
                Spacer()
                Text("\(state.nameLen)/\(state.nameMaxLen)")
                    .font(.custom("PlusJakartaSans-Regular", size: 11))
                    .foregroundColor(.sgTextSubtle)
            }
        }
    }

    private var nameError: EditProfileViewModel.NameError? { state.nameError }
    private var nameHintOrError: String {
        if let err = nameError {
            switch err {
            case .required: return "Nama wajib diisi"
            case .toolong: return "Nama terlalu panjang"
            default: return ""
            }
        }
        return "Dipakai untuk sapaan di Beranda. Tidak dikirim ke server."
    }

    private var emailField: some View {
        VStack(alignment: .leading, spacing: 6) {
            sectionLabel("Email (opsional · untuk backup)")
            HStack(spacing: 10) {
                Text("\u{f0e0}")
                    .font(.custom("FontAwesome6Free-Solid", size: 16))
                    .foregroundColor(.sgTextMuted)
                TextField("alamat@email.com", text: Binding(
                    get: { state.email },
                    set: { model.setEmail($0) }
                ))
                    .focused($emailFocused)
                    .keyboardType(.emailAddress)
                    .textInputAutocapitalization(.never)
                    .autocorrectionDisabled()
                    .font(.custom("PlusJakartaSans-SemiBold", size: 15))
                    .foregroundColor(.sgTextPrimary)
                if !state.email.isEmpty && state.emailError == nil {
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
                    .strokeBorder(
                        state.emailError != nil ? Color.sgDanger :
                            (emailFocused ? Color.sgPrimary : Color.sgBorder),
                        lineWidth: 1.5
                    )
            )

            if let err = state.emailError {
                HStack(spacing: 6) {
                    Text("\u{f06a}")
                        .font(.custom("FontAwesome6Free-Solid", size: 11))
                        .foregroundColor(.sgDanger)
                    Text(emailErrorLabel(err))
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

    private func emailErrorLabel(_ err: EditProfileViewModel.EmailError) -> String {
        switch err {
        case .invalidformat: return "Format email tidak valid"
        default: return ""
        }
    }

    private var statsCard: some View {
        let vehicleCount = vehicles.state.vehicles.count
        let serviceCount = Int(services.state.totalCount)
        let sinceLabel = PresentationFactory.shared.formatSinceLabel(epochMillis: state.sinceEpochMillis)
        return VStack(alignment: .leading, spacing: 12) {
            sectionLabel("Akun kamu")
            HStack(alignment: .top, spacing: 8) {
                stat(value: "\(vehicleCount)", label: "Kendaraan")
                stat(value: "\(serviceCount)", label: "Servis")
                stat(value: sinceLabel, label: "Sejak")
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
            model.resetToDefaults()
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
}

#Preview {
    NavigationStack {
        EditProfileView(onBack: {}, onSaved: {})
    }
}
