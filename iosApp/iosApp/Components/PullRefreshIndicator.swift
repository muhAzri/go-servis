import SwiftUI

enum PullRefreshState {
    case pulling     // user is dragging, not yet at threshold
    case releasing   // crossed threshold, will refresh on release
    case refreshing  // in-flight network call
}

/// Circular spinner with three visual states. 24pt size by default.
/// Use to compose custom pull-to-refresh affordances on screens where
/// SwiftUI's `.refreshable` isn't a fit.
struct PullRefreshIndicator: View {
    let state: PullRefreshState
    var label: String? = nil
    var size: CGFloat = 24

    @State private var spin: Double = 0

    private var progressRotation: Double {
        switch state {
        case .pulling:    return 0
        case .releasing:  return 180
        case .refreshing: return spin
        }
    }

    private var labelText: String {
        if let label { return label }
        switch state {
        case .pulling:    return "Tarik untuk refresh"
        case .releasing:  return "Lepas untuk refresh"
        case .refreshing: return "Memuat..."
        }
    }

    var body: some View {
        HStack(spacing: 10) {
            ZStack {
                Circle()
                    .stroke(Color.sgPrimarySoft, lineWidth: 3)
                    .frame(width: size, height: size)

                Circle()
                    .trim(from: 0, to: state == .refreshing ? 0.75 : 0.25)
                    .stroke(Color.sgPrimary, style: StrokeStyle(lineWidth: 3, lineCap: .round))
                    .frame(width: size, height: size)
                    .rotationEffect(.degrees(progressRotation))
                    .animation(
                        state == .refreshing
                            ? .linear(duration: 0.9).repeatForever(autoreverses: false)
                            : .easeInOut(duration: 0.2),
                        value: progressRotation
                    )
            }

            Text(labelText)
                .font(.custom("PlusJakartaSans-SemiBold", size: 13))
                .foregroundColor(.sgTextMuted)
        }
        .onAppear {
            if state == .refreshing {
                spin = 360
            }
        }
        .onChange(of: state) { newValue in
            if newValue == .refreshing {
                spin = 360
            } else {
                spin = 0
            }
        }
    }
}

#Preview {
    VStack(spacing: 16) {
        PullRefreshIndicator(state: .pulling)
        PullRefreshIndicator(state: .releasing)
        PullRefreshIndicator(state: .refreshing)
    }
    .padding()
    .background(Color.sgBgWarm)
}
