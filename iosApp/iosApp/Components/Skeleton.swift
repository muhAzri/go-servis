import SwiftUI

private struct ShimmerModifier: ViewModifier {
    @State private var phase: CGFloat = -1.0

    func body(content: Content) -> some View {
        content
            .overlay(
                GeometryReader { geo in
                    LinearGradient(
                        stops: [
                            .init(color: Color.clear, location: 0.0),
                            .init(color: Color.white.opacity(0.45), location: 0.5),
                            .init(color: Color.clear, location: 1.0),
                        ],
                        startPoint: .leading,
                        endPoint: .trailing
                    )
                    .frame(width: geo.size.width * 1.8)
                    .offset(x: geo.size.width * phase)
                    .blendMode(.plusLighter)
                    .clipped()
                }
            )
            .clipped()
            .onAppear {
                withAnimation(.linear(duration: 1.4).repeatForever(autoreverses: false)) {
                    phase = 1.0
                }
            }
    }
}

private extension View {
    func sgShimmer() -> some View {
        modifier(ShimmerModifier())
    }
}

struct ShimmerBlock: View {
    var corner: CGFloat = 6
    var body: some View {
        RoundedRectangle(cornerRadius: corner)
            .fill(Color.sgSurfaceAlt)
            .sgShimmer()
    }
}

enum SkeletonLeading {
    case none, icon, avatar
}

enum Skeleton {

    struct Row: View {
        var leading: SkeletonLeading = .icon
        var lines: Int = 2

        var body: some View {
            HStack(alignment: .center, spacing: 12) {
                switch leading {
                case .none:
                    EmptyView()
                case .icon:
                    ShimmerBlock(corner: 12)
                        .frame(width: 44, height: 44)
                case .avatar:
                    Circle()
                        .fill(Color.sgSurfaceAlt)
                        .sgShimmer()
                        .frame(width: 44, height: 44)
                }

                VStack(alignment: .leading, spacing: 8) {
                    ShimmerBlock()
                        .frame(height: 12)
                        .frame(maxWidth: .infinity, alignment: .leading)
                        .frame(width: nil)
                    if lines >= 2 {
                        ShimmerBlock()
                            .frame(height: 10)
                    }
                }
                .frame(maxWidth: .infinity, alignment: .leading)
            }
            .padding(14)
        }
    }

    struct Card: View {
        var lines: Int = 3
        var height: CGFloat = 120

        var body: some View {
            VStack(alignment: .leading, spacing: 10) {
                ShimmerBlock()
                    .frame(height: 14)
                    .frame(maxWidth: .infinity, alignment: .leading)
                    .padding(.trailing, 100)
                ShimmerBlock(corner: 12)
                    .frame(height: max(height - 60, 24))
                if lines >= 3 {
                    ShimmerBlock()
                        .frame(height: 10)
                        .padding(.trailing, 60)
                }
            }
            .padding(16)
            .frame(maxWidth: .infinity)
            .background(Color.sgSurface)
            .clipShape(RoundedRectangle(cornerRadius: 18))
            .overlay(
                RoundedRectangle(cornerRadius: 18)
                    .strokeBorder(Color.sgBorder, lineWidth: 1)
            )
            .padding(.horizontal, 16)
        }
    }

    struct Tile: View {
        var count: Int = 6
        var columns: Int = 3

        var body: some View {
            let rows = (count + columns - 1) / columns
            VStack(spacing: 8) {
                ForEach(0..<rows, id: \.self) { rowIdx in
                    HStack(spacing: 8) {
                        ForEach(0..<columns, id: \.self) { colIdx in
                            let index = rowIdx * columns + colIdx
                            if index < count {
                                ShimmerBlock(corner: 14)
                                    .frame(height: 82)
                                    .frame(maxWidth: .infinity)
                            } else {
                                Spacer().frame(maxWidth: .infinity)
                            }
                        }
                    }
                }
            }
            .padding(.horizontal, 16)
        }
    }
}

#Preview {
    ScrollView {
        VStack(spacing: 12) {
            Skeleton.Card(lines: 3, height: 120)
            Skeleton.Row()
            Skeleton.Row(leading: .avatar)
            Skeleton.Tile(count: 6)
        }
        .padding(.vertical, 16)
    }
    .background(Color.sgBgWarm)
}
