import SwiftUI

struct EmptyState: View {
    let iconUnicode: String
    let title: String
    let message: String
    var ctaLabel: String? = nil
    var onCta: (() -> Void)? = nil
    var secondaryLabel: String? = nil
    var onSecondary: (() -> Void)? = nil
    var iconForeground: Color = .sgPrimary
    var iconBackground: Color = .sgPrimarySoft

    init(
        iconUnicode: String,
        title: String,
        body: String,
        ctaLabel: String? = nil,
        onCta: (() -> Void)? = nil,
        secondaryLabel: String? = nil,
        onSecondary: (() -> Void)? = nil,
        iconForeground: Color = .sgPrimary,
        iconBackground: Color = .sgPrimarySoft
    ) {
        self.iconUnicode = iconUnicode
        self.title = title
        self.message = body
        self.ctaLabel = ctaLabel
        self.onCta = onCta
        self.secondaryLabel = secondaryLabel
        self.onSecondary = onSecondary
        self.iconForeground = iconForeground
        self.iconBackground = iconBackground
    }

    private var content: some View {
        VStack(spacing: 0) {
            ZStack {
                Circle()
                    .fill(iconBackground)
                    .frame(width: 96, height: 96)
                Text(iconUnicode)
                    .font(.custom("FontAwesome6Free-Solid", size: 40))
                    .foregroundColor(iconForeground)
            }

            Text(title)
                .font(.custom("PlusJakartaSans-ExtraBold", size: 20))
                .foregroundColor(.sgTextPrimary)
                .multilineTextAlignment(.center)
                .padding(.top, 20)

            Text(message)
                .font(.custom("PlusJakartaSans-Medium", size: 14))
                .foregroundColor(.sgTextMuted)
                .multilineTextAlignment(.center)
                .lineSpacing(4)
                .frame(maxWidth: 280)
                .padding(.top, 8)

            if let ctaLabel, let onCta {
                Button(action: onCta) {
                    Text(ctaLabel)
                        .font(.custom("PlusJakartaSans-Bold", size: 15))
                        .foregroundColor(.white)
                        .padding(.horizontal, 24)
                        .padding(.vertical, 14)
                        .frame(minWidth: 220)
                        .background(Color.sgPrimary)
                        .clipShape(RoundedRectangle(cornerRadius: 18))
                }
                .buttonStyle(.plain)
                .padding(.top, 24)
            }

            if let secondaryLabel, let onSecondary {
                Button(action: onSecondary) {
                    Text(secondaryLabel)
                        .font(.custom("PlusJakartaSans-SemiBold", size: 13))
                        .foregroundColor(.sgTextMuted)
                        .underline()
                }
                .buttonStyle(.plain)
                .padding(.top, 12)
            }
        }
    }

    var body: some View {
        content
            .frame(maxWidth: .infinity, maxHeight: .infinity)
            .padding(.horizontal, 24)
            .padding(.vertical, 32)
    }
}

#Preview {
    EmptyState(
        iconUnicode: "\u{f21c}",
        title: "Belum ada kendaraan",
        body: "Tambah motor atau mobilmu untuk mulai catat servis & dapat pengingat.",
        ctaLabel: "+ Tambah Kendaraan",
        onCta: {}
    )
    .background(Color.sgBgWarm)
}
