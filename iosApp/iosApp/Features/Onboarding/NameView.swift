import SwiftUI
import Shared

private let maxNameLen = 20

struct NameView: View {
    @ObservedObject var flow: OnboardingFlowModel
    let onBack: () -> Void
    let onNext: () -> Void
    let onSkip: () -> Void

    @FocusState private var isFocused: Bool

    private var name: String { flow.state.nameInput }
    private var trimmed: String { name.trimmingCharacters(in: .whitespacesAndNewlines) }
    private var initial: String { String(trimmed.first ?? "B").uppercased() }
    private var isSubmitting: Bool { flow.state.isSubmitting }

    var body: some View {
        ZStack {
            Color.sgBgWarm.ignoresSafeArea()

            VStack(alignment: .leading, spacing: 0) {
                OnboardingStepHeader(
                    stepLabel: "Langkah 1 dari 4",
                    title: "Kami panggil kamu apa?",
                    subtitle: "Biar pengingatnya berasa lebih personal. Disimpan lokal, bisa diubah di Pengaturan kapan saja.",
                    onBack: onBack
                )

                VStack(alignment: .leading, spacing: 0) {
                    Spacer().frame(height: 20)

                    HStack {
                        Spacer()
                        ZStack {
                            RoundedRectangle(cornerRadius: 26, style: .continuous)
                                .fill(Color.sgPrimary)
                                .frame(width: 88, height: 88)
                                .shadow(color: Color.sgPrimary.opacity(0.27), radius: 12, x: 0, y: 12)
                            Text(initial)
                                .font(.custom("PlusJakartaSans-ExtraBold", size: 38))
                                .foregroundColor(.white)
                        }
                        Spacer()
                    }

                    Spacer().frame(height: 20)

                    VStack(alignment: .leading, spacing: 6) {
                        Text("Nama panggilan")
                            .font(.custom("PlusJakartaSans-SemiBold", size: 13))
                            .foregroundColor(.sgTextMuted)
                        ZStack(alignment: .leading) {
                            if name.isEmpty {
                                Text("Misal: Budi")
                                    .font(.custom("PlusJakartaSans-Regular", size: 15))
                                    .foregroundColor(.sgTextSubtle)
                                    .padding(.horizontal, 16)
                            }
                            TextField("", text: Binding(
                                get: { name },
                                set: { value in
                                    let clipped = value.count > maxNameLen
                                        ? String(value.prefix(maxNameLen))
                                        : value
                                    flow.setNameInput(clipped)
                                }
                            ))
                                .font(.custom("PlusJakartaSans-Regular", size: 15))
                                .foregroundColor(.sgTextPrimary)
                                .focused($isFocused)
                                .submitLabel(.done)
                                .onSubmit { submit() }
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
                    }

                    Spacer().frame(height: 8)

                    HStack {
                        Text("Cukup nama panggilan saja — tidak perlu nama lengkap.")
                            .font(.custom("PlusJakartaSans-Regular", size: 12))
                            .foregroundColor(.sgTextSubtle)
                        Spacer()
                        Text("\(name.count)/\(maxNameLen)")
                            .font(.custom("PlusJakartaSans-Regular", size: 11))
                            .foregroundColor(.sgTextSubtle)
                    }

                    Spacer()

                    AppButton(
                        title: "Lanjut",
                        action: submit,
                        isEnabled: !trimmed.isEmpty && !isSubmitting,
                        trailingIcon: "\u{f054}"
                    )

                    Spacer().frame(height: 4)

                    AppTextButton(title: "Lewati — pakai \"Kamu\" saja", action: onSkip)

                    Spacer().frame(height: 24)
                }
                .padding(.horizontal, 24)
            }
        }
        .navigationBarHidden(true)
        .onAppear {
            DispatchQueue.main.asyncAfter(deadline: .now() + 0.2) { isFocused = true }
        }
    }

    private func submit() {
        guard !trimmed.isEmpty else { return }
        onNext()
    }
}
