import SwiftUI

enum ContextBannerTone {
    case info, warning, danger

    var background: Color {
        switch self {
        case .info: return .sgPrimarySofter
        case .warning: return .sgWarningSoft
        case .danger: return .sgDangerSoft
        }
    }

    var border: Color {
        switch self {
        case .info: return Color.sgPrimary.opacity(0.24)
        case .warning: return Color.sgWarning.opacity(0.32)
        case .danger: return Color.sgDanger.opacity(0.24)
        }
    }

    var foreground: Color {
        switch self {
        case .info: return .sgPrimary
        case .warning: return .sgWarning
        case .danger: return .sgDanger
        }
    }
}

struct ContextBanner: View {
    let title: String
    let message: String
    var iconUnicode: String = "\u{f05a}"
    var tone: ContextBannerTone = .info
    var ctaLabel: String? = nil
    var onCta: (() -> Void)? = nil
    var onDismiss: (() -> Void)? = nil

    init(
        title: String,
        body: String,
        iconUnicode: String = "\u{f05a}",
        tone: ContextBannerTone = .info,
        ctaLabel: String? = nil,
        onCta: (() -> Void)? = nil,
        onDismiss: (() -> Void)? = nil
    ) {
        self.title = title
        self.message = body
        self.iconUnicode = iconUnicode
        self.tone = tone
        self.ctaLabel = ctaLabel
        self.onCta = onCta
        self.onDismiss = onDismiss
    }

    var body: some View {
        HStack(alignment: .top, spacing: 10) {
            Text(iconUnicode)
                .font(.custom("FontAwesome6Free-Solid", size: 16))
                .foregroundColor(tone.foreground)
                .frame(width: 20, alignment: .center)
                .padding(.top, 1)

            VStack(alignment: .leading, spacing: 4) {
                Text(title)
                    .font(.custom("PlusJakartaSans-Bold", size: 13))
                    .foregroundColor(tone.foreground)
                Text(message)
                    .font(.custom("PlusJakartaSans-Medium", size: 12))
                    .foregroundColor(.sgTextMuted)
                    .lineSpacing(3)
                    .fixedSize(horizontal: false, vertical: true)

                if let ctaLabel, let onCta {
                    Button(action: onCta) {
                        Text(ctaLabel)
                            .font(.custom("PlusJakartaSans-Bold", size: 12))
                            .foregroundColor(tone.foreground)
                    }
                    .buttonStyle(.plain)
                    .padding(.top, 4)
                }
            }
            .frame(maxWidth: .infinity, alignment: .leading)

            if let onDismiss {
                Button(action: onDismiss) {
                    Text("\u{f00d}")
                        .font(.custom("FontAwesome6Free-Solid", size: 12))
                        .foregroundColor(.sgTextMuted)
                        .frame(width: 24, height: 24)
                }
                .buttonStyle(.plain)
            }
        }
        .padding(14)
        .background(tone.background)
        .clipShape(RoundedRectangle(cornerRadius: 14))
        .overlay(
            RoundedRectangle(cornerRadius: 14)
                .strokeBorder(tone.border, lineWidth: 1)
        )
    }
}

#Preview {
    VStack(spacing: 12) {
        ContextBanner(
            title: "Dari reminder",
            body: "Ganti Oli Mesin · Beat Hitam — field di bawah sudah diisi otomatis.",
            iconUnicode: "\u{f0f3}",
            tone: .info,
            ctaLabel: "Lihat reminder →",
            onCta: {}
        )
        ContextBanner(
            title: "Notif belum aktif",
            body: "Pengingat servis tidak akan muncul di lock screen. Aktifkan supaya tidak kelewat.",
            iconUnicode: "\u{f1f6}",
            tone: .warning,
            ctaLabel: "Aktifkan",
            onCta: {},
            onDismiss: {}
        )
    }
    .padding()
    .background(Color.sgBgWarm)
}
