import SwiftUI

struct NotifPermissionSheet: View {
    var onDismiss: () -> Void = {}
    var onOpenSettings: () -> Void = {}

    var body: some View {
        VStack(spacing: 16) {
            ZStack {
                Circle()
                    .fill(Color.sgPrimarySoft)
                    .frame(width: 72, height: 72)
                Text("\u{f0f3}")
                    .font(.custom("FontAwesome6Free-Solid", size: 32))
                    .foregroundColor(.sgPrimary)
            }

            Text("Notifikasi dimatikan")
                .font(.custom("PlusJakartaSans-ExtraBold", size: 20))
                .foregroundColor(.sgTextPrimary)

            Text("ServisGo butuh izin notifikasi sistem supaya bisa kirim pengingat servis tepat waktu.")
                .font(.custom("PlusJakartaSans-Medium", size: 14))
                .foregroundColor(.sgTextMuted)
                .multilineTextAlignment(.center)
                .padding(.horizontal, 8)

            VStack(spacing: 10) {
                step(1, "Tap \"Buka Pengaturan\" di bawah")
                step(2, "Pilih \"Notifikasi\" lalu cari ServisGo")
                step(3, "Aktifkan toggle notifikasi utama")
            }
            .padding(16)
            .frame(maxWidth: .infinity, alignment: .leading)
            .background(Color.sgSurfaceAlt)
            .clipShape(RoundedRectangle(cornerRadius: 14))

            Button(action: {
                onOpenSettings()
                onDismiss()
            }) {
                Text("Buka Pengaturan iOS")
                    .font(.custom("PlusJakartaSans-Bold", size: 15))
                    .foregroundColor(.white)
                    .frame(maxWidth: .infinity)
                    .frame(height: 52)
                    .background(Color.sgPrimary)
                    .clipShape(RoundedRectangle(cornerRadius: 14))
            }
            .buttonStyle(.plain)
            .padding(.top, 4)

            Button(action: onDismiss) {
                Text("Nanti saja")
                    .font(.custom("PlusJakartaSans-SemiBold", size: 13))
                    .foregroundColor(.sgTextMuted)
                    .frame(maxWidth: .infinity)
                    .frame(height: 40)
            }
            .buttonStyle(.plain)
        }
        .padding(24)
        .background(Color.sgSurface)
    }

    private func step(_ number: Int, _ text: String) -> some View {
        HStack(alignment: .center, spacing: 12) {
            ZStack {
                Circle()
                    .fill(Color.sgPrimary)
                    .frame(width: 24, height: 24)
                Text("\(number)")
                    .font(.custom("PlusJakartaSans-ExtraBold", size: 12))
                    .foregroundColor(.white)
            }
            Text(text)
                .font(.custom("PlusJakartaSans-Medium", size: 13))
                .foregroundColor(.sgTextPrimary)
            Spacer()
        }
    }
}
