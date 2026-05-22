import SwiftUI

struct ShareCardMeta: Identifiable {
    let id: String
    let label: String
    let value: String

    init(id: String = UUID().uuidString, label: String, value: String) {
        self.id = id
        self.label = label
        self.value = value
    }
}

/// 4:5 portrait gradient poster for sharing a service record / proof.
/// Fixed gradient (green) with white content and subtle stripe overlay.
struct ShareCardPoster: View {
    let serviceLabel: String
    let serviceIconUnicode: String
    let vehicleName: String
    let vehiclePlate: String
    let vehicleIconUnicode: String
    /// 2×2 grid → provide 2-4 metas (will lay out in pairs).
    let meta: [ShareCardMeta]
    var brandLabel: String = "ServisGo"
    var kickerLabel: String = "BUKTI SERVIS"
    var footerText: String = "servisgo.app"

    private let gradient = LinearGradient(
        colors: [
            Color(red: 0.18, green: 0.55, blue: 0.34),
            Color(red: 0.13, green: 0.41, blue: 0.25),
            Color(red: 0.10, green: 0.29, blue: 0.16),
        ],
        startPoint: .topLeading,
        endPoint: .bottomTrailing
    )

    var body: some View {
        GeometryReader { geo in
            let w = geo.size.width
            let h = w * 1.25 // 4:5
            ZStack {
                gradient
                StripeOverlay()
                    .opacity(0.06)

                VStack(alignment: .leading, spacing: 0) {
                    header
                    Spacer(minLength: 0)
                    centerBlock
                    Spacer(minLength: 0)
                    vehicleRow
                    metaGrid
                        .padding(.top, 12)
                    Spacer(minLength: 0)
                    footer
                }
                .padding(20)
            }
            .frame(width: w, height: h)
            .clipShape(RoundedRectangle(cornerRadius: 24))
        }
        .aspectRatio(4.0/5.0, contentMode: .fit)
    }

    private var header: some View {
        HStack(spacing: 10) {
            ZStack {
                RoundedRectangle(cornerRadius: 10)
                    .fill(Color.white.opacity(0.18))
                    .frame(width: 38, height: 38)
                Text("\u{f021}") // wrench
                    .font(.custom("FontAwesome6Free-Solid", size: 16))
                    .foregroundColor(.white)
            }
            VStack(alignment: .leading, spacing: 2) {
                Text(brandLabel)
                    .font(.custom("PlusJakartaSans-ExtraBold", size: 15))
                    .foregroundColor(.white)
                Text(kickerLabel.uppercased())
                    .font(.custom("PlusJakartaSans-Bold", size: 10))
                    .kerning(1.2)
                    .foregroundColor(.white.opacity(0.72))
            }
            Spacer()
        }
    }

    private var centerBlock: some View {
        VStack(alignment: .leading, spacing: 10) {
            ZStack {
                RoundedRectangle(cornerRadius: 18)
                    .fill(Color.white.opacity(0.16))
                    .frame(width: 64, height: 64)
                Text(serviceIconUnicode)
                    .font(.custom("FontAwesome6Free-Solid", size: 28))
                    .foregroundColor(.white)
            }
            Text("SERVIS")
                .font(.custom("PlusJakartaSans-Bold", size: 11))
                .kerning(1.0)
                .foregroundColor(.white.opacity(0.7))
            Text(serviceLabel)
                .font(.custom("PlusJakartaSans-ExtraBold", size: 24))
                .foregroundColor(.white)
                .lineLimit(2)
                .fixedSize(horizontal: false, vertical: true)
        }
        .frame(maxWidth: .infinity, alignment: .leading)
    }

    private var vehicleRow: some View {
        HStack(spacing: 12) {
            ZStack {
                RoundedRectangle(cornerRadius: 10)
                    .fill(Color.white.opacity(0.18))
                    .frame(width: 38, height: 38)
                Text(vehicleIconUnicode)
                    .font(.custom("FontAwesome6Free-Solid", size: 16))
                    .foregroundColor(.white)
            }
            VStack(alignment: .leading, spacing: 2) {
                Text(vehicleName)
                    .font(.custom("PlusJakartaSans-Bold", size: 14))
                    .foregroundColor(.white)
                Text(vehiclePlate)
                    .font(.system(size: 12, weight: .medium, design: .monospaced))
                    .foregroundColor(.white.opacity(0.75))
            }
            Spacer()
        }
        .padding(12)
        .background(Color.white.opacity(0.12))
        .clipShape(RoundedRectangle(cornerRadius: 14))
    }

    private var metaGrid: some View {
        let rows = stride(from: 0, to: meta.count, by: 2).map { idx -> [ShareCardMeta] in
            Array(meta[idx..<min(idx + 2, meta.count)])
        }
        return VStack(spacing: 8) {
            ForEach(Array(rows.enumerated()), id: \.offset) { _, pair in
                HStack(spacing: 8) {
                    ForEach(pair) { item in
                        metaCell(item)
                    }
                    if pair.count == 1 {
                        Color.clear.frame(maxWidth: .infinity)
                    }
                }
            }
        }
    }

    private func metaCell(_ item: ShareCardMeta) -> some View {
        VStack(alignment: .leading, spacing: 4) {
            Text(item.label.uppercased())
                .font(.custom("PlusJakartaSans-Bold", size: 9))
                .kerning(0.8)
                .foregroundColor(.white.opacity(0.7))
            Text(item.value)
                .font(.custom("PlusJakartaSans-ExtraBold", size: 15))
                .foregroundColor(.white)
                .lineLimit(1)
        }
        .padding(.horizontal, 12)
        .padding(.vertical, 10)
        .frame(maxWidth: .infinity, alignment: .leading)
        .background(Color.white.opacity(0.10))
        .clipShape(RoundedRectangle(cornerRadius: 12))
    }

    private var footer: some View {
        HStack {
            Text(footerText)
                .font(.custom("PlusJakartaSans-SemiBold", size: 11))
                .foregroundColor(.white.opacity(0.65))
            Spacer()
            Text("\u{f021}")
                .font(.custom("FontAwesome6Free-Solid", size: 10))
                .foregroundColor(.white.opacity(0.55))
        }
        .padding(.top, 6)
    }
}

private struct StripeOverlay: View {
    var body: some View {
        GeometryReader { geo in
            Canvas { ctx, size in
                let spacing: CGFloat = 12
                let lineWidth: CGFloat = 1
                let count = Int((size.width + size.height) / spacing) + 2
                for i in 0..<count {
                    var path = Path()
                    let offset = CGFloat(i) * spacing
                    path.move(to: CGPoint(x: offset, y: 0))
                    path.addLine(to: CGPoint(x: offset - size.height, y: size.height))
                    ctx.stroke(path, with: .color(.white), lineWidth: lineWidth)
                }
            }
            .frame(width: geo.size.width, height: geo.size.height)
        }
    }
}

#Preview {
    ShareCardPoster(
        serviceLabel: "Ganti Oli Mesin",
        serviceIconUnicode: "\u{f613}",
        vehicleName: "Beat Hitam",
        vehiclePlate: "B 4521 KZA",
        vehicleIconUnicode: "\u{f21c}",
        meta: [
            .init(label: "Odometer", value: "12.345 km"),
            .init(label: "Tanggal", value: "22 Mei 2026"),
            .init(label: "Biaya", value: "Rp 75.000"),
            .init(label: "Bengkel", value: "AHASS Cibubur"),
        ]
    )
    .padding()
    .background(Color.sgBgWarm)
}
