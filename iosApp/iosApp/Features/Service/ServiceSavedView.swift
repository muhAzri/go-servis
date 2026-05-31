import SwiftUI
import Shared

struct ServiceSavedView: View {
    let onBackToHome: () -> Void
    var onOpenHistory: () -> Void = {}
    var onOpenServiceDetail: () -> Void = {}
    var onAddReminderFromContext: () -> Void = {}
    var recordId: String? = nil

    @StateObject private var model = ServiceDetailModel()

    var body: some View {
        VStack(spacing: 0) {
            Spacer()

            SuccessIcon()
                .padding(.bottom, 20)

            Text("Servis tercatat 🎉")
                .font(.custom("PlusJakartaSans-ExtraBold", size: 26))
                .foregroundColor(.sgTextPrimary)
                .kerning(-0.5)
                .multilineTextAlignment(.center)
                .padding(.bottom, 8)

            Text("Tersimpan di riwayat servis kamu.")
                .font(.custom("PlusJakartaSans-Medium", size: 14))
                .foregroundColor(.sgTextMuted)
                .multilineTextAlignment(.center)
                .padding(.horizontal, 24)

            if let record = model.state.record {
                let meta = ServiceTypeMeta.for(key: record.serviceType.key)
                let subtitle: String = {
                    var parts: [String] = []
                    if let v = model.state.vehicle, !v.displayTitle.isEmpty {
                        parts.append(v.displayTitle)
                    }
                    if record.cost > 0 {
                        let f = NumberFormatter()
                        f.numberStyle = .decimal
                        f.groupingSeparator = "."
                        let s = f.string(from: NSNumber(value: record.cost)) ?? "\(record.cost)"
                        parts.append("Rp \(s)")
                    }
                    return parts.joined(separator: " · ")
                }()

                Button(action: onOpenServiceDetail) {
                    HStack(spacing: 10) {
                        IconBadge(
                            iconUnicode: meta.icon,
                            foreground: meta.color,
                            background: meta.color.opacity(0.15),
                            size: 36, iconSize: 18, corner: 10
                        )
                        VStack(alignment: .leading, spacing: 2) {
                            Text(meta.label.replacingOccurrences(of: "\n", with: " "))
                                .font(.custom("PlusJakartaSans-Bold", size: 13))
                                .foregroundColor(.sgTextPrimary)
                            Text(subtitle.isEmpty ? "Catatan baru" : subtitle)
                                .font(.custom("PlusJakartaSans-Medium", size: 12))
                                .foregroundColor(.sgTextMuted)
                        }
                        Spacer()
                    }
                    .padding(18)
                    .background(Color.sgSurface)
                    .clipShape(RoundedRectangle(cornerRadius: 18))
                    .overlay(
                        RoundedRectangle(cornerRadius: 18)
                            .strokeBorder(Color.sgBorder, lineWidth: 1)
                    )
                }
                .buttonStyle(.plain)
                .padding(.horizontal, 24)
                .padding(.top, 24)
            }

            Spacer()

            VStack(spacing: 8) {
                AppButton(title: "Kembali ke Beranda", action: onBackToHome)
                HStack(spacing: 16) {
                    Button(action: onOpenServiceDetail) {
                        Text("Lihat Detail")
                            .font(.custom("PlusJakartaSans-SemiBold", size: 14))
                            .foregroundColor(.sgTextMuted)
                            .frame(maxWidth: .infinity)
                            .frame(height: 50)
                    }
                    .buttonStyle(.plain)
                    Button(action: onOpenHistory) {
                        Text("Lihat Riwayat")
                            .font(.custom("PlusJakartaSans-SemiBold", size: 14))
                            .foregroundColor(.sgTextMuted)
                            .frame(maxWidth: .infinity)
                            .frame(height: 50)
                    }
                    .buttonStyle(.plain)
                }
            }
            .padding(.horizontal, 24)
            .padding(.bottom, 24)
        }
        .frame(maxWidth: .infinity, maxHeight: .infinity)
        .background(Color.sgBgWarm)
        .navigationBarBackButtonHidden(true)
        .toolbar(.hidden, for: .navigationBar)
        .onAppear {
            if let id = recordId { model.load(recordId: id) }
        }
        // Reserved hook for next-reminder customization (Reminder phase).
        .task { _ = onAddReminderFromContext }
    }
}

private struct SuccessIcon: View {
    var body: some View {
        ZStack {
            Circle()
                .fill(Color.sgPrimary)
                .frame(width: 96, height: 96)
                .shadow(color: Color.sgPrimary.opacity(0.33), radius: 20, x: 0, y: 16)
            Text("\u{f00c}")
                .font(.custom("FontAwesome6Free-Solid", size: 44))
                .foregroundColor(.white)
        }
    }
}
