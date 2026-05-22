import SwiftUI

struct WipeDataFlow: View {
    @Binding var isPresented: Bool
    var onConfirmed: () -> Void = {}

    @State private var phase: Int = 1
    @State private var typedConfirm: String = ""

    var body: some View {
        if phase == 1 {
            Color.clear
                .alert("Hapus SEMUA data?", isPresented: $isPresented) {
                    Button("Lanjutkan", role: .destructive) { phase = 2 }
                    Button("Batal", role: .cancel) { reset() }
                } message: {
                    Text("Kendaraan, riwayat servis, pengingat, dan komponen akan hilang permanen. Tindakan ini tidak bisa dibatalkan.")
                }
        } else {
            ZStack {
                Color.black.opacity(0.45).ignoresSafeArea()
                    .onTapGesture { /* block dismiss */ }
                VStack(spacing: 16) {
                    ZStack {
                        Circle()
                            .fill(Color.sgDangerSoft)
                            .frame(width: 56, height: 56)
                        Text("\u{f1f8}")
                            .font(.custom("FontAwesome6Free-Solid", size: 26))
                            .foregroundColor(.sgDanger)
                    }
                    Text("Konfirmasi terakhir")
                        .font(.custom("PlusJakartaSans-ExtraBold", size: 17))
                        .foregroundColor(.sgTextPrimary)
                    Text("Untuk melanjutkan, ketik teks di bawah persis seperti yang tertulis.")
                        .font(.custom("PlusJakartaSans-Medium", size: 13))
                        .foregroundColor(.sgTextMuted)
                        .multilineTextAlignment(.center)
                    VStack(spacing: 6) {
                        Text("Ketik")
                            .font(.custom("PlusJakartaSans-Bold", size: 12))
                            .foregroundColor(.sgTextMuted)
                        + Text(" HAPUS SEMUA ")
                            .font(.custom("PlusJakartaSans-Bold", size: 12))
                            .foregroundColor(.sgDanger)
                        + Text("untuk lanjut")
                            .font(.custom("PlusJakartaSans-Bold", size: 12))
                            .foregroundColor(.sgTextMuted)
                        TextField("HAPUS SEMUA", text: $typedConfirm)
                            .multilineTextAlignment(.center)
                            .autocorrectionDisabled()
                            .textInputAutocapitalization(.characters)
                            .font(.system(size: 14, weight: .bold, design: .monospaced))
                            .padding(.horizontal, 14)
                            .padding(.vertical, 11)
                            .background(Color.sgBgWarm)
                            .clipShape(RoundedRectangle(cornerRadius: 12))
                            .overlay(
                                RoundedRectangle(cornerRadius: 12)
                                    .strokeBorder(
                                        typedConfirm == "HAPUS SEMUA" ? Color.sgDanger : Color.sgBorder,
                                        lineWidth: 1.5
                                    )
                            )
                    }
                    HStack(spacing: 10) {
                        Button(action: { phase = 1 }) {
                            Text("Batal")
                                .font(.custom("PlusJakartaSans-Bold", size: 14))
                                .foregroundColor(.sgTextPrimary)
                                .frame(maxWidth: .infinity)
                                .frame(height: 44)
                                .background(Color.sgSurfaceAlt)
                                .clipShape(RoundedRectangle(cornerRadius: 12))
                        }
                        .buttonStyle(.plain)
                        Button(action: {
                            if typedConfirm == "HAPUS SEMUA" {
                                onConfirmed()
                                reset()
                                isPresented = false
                            }
                        }) {
                            Text("HAPUS SEMUA")
                                .font(.custom("PlusJakartaSans-Bold", size: 14))
                                .foregroundColor(typedConfirm == "HAPUS SEMUA" ? .white : .sgTextSubtle)
                                .frame(maxWidth: .infinity)
                                .frame(height: 44)
                                .background(typedConfirm == "HAPUS SEMUA" ? Color.sgDanger : Color.sgSurfaceAlt)
                                .clipShape(RoundedRectangle(cornerRadius: 12))
                        }
                        .buttonStyle(.plain)
                        .disabled(typedConfirm != "HAPUS SEMUA")
                    }
                }
                .padding(22)
                .frame(maxWidth: 320)
                .background(Color.sgSurface)
                .clipShape(RoundedRectangle(cornerRadius: 22))
                .shadow(color: .black.opacity(0.3), radius: 20, y: 10)
            }
        }
    }

    private func reset() {
        phase = 1
        typedConfirm = ""
    }
}
