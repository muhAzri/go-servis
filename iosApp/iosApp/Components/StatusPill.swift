import SwiftUI

enum ReminderUrgency {
    case overdue
    case soon
    case ok

    var label: String {
        switch self {
        case .overdue: return "Telat"
        case .soon:    return "Segera"
        case .ok:      return "Aman"
        }
    }

    var color: Color {
        switch self {
        case .overdue: return .sgDanger
        case .soon:    return .sgWarning
        case .ok:      return .sgPrimary
        }
    }

    var softColor: Color {
        switch self {
        case .overdue: return .sgDangerSoft
        case .soon:    return .sgWarningSoft
        case .ok:      return .sgPrimarySoft
        }
    }
}

struct StatusPill: View {
    let urgency: ReminderUrgency

    var body: some View {
        HStack(spacing: 6) {
            Circle()
                .fill(urgency.color)
                .frame(width: 8, height: 8)
            Text(urgency.label.uppercased())
                .font(.custom("PlusJakartaSans-Bold", size: 11))
                .kerning(0.4)
                .foregroundColor(urgency.color)
        }
        .padding(.horizontal, 10)
        .padding(.vertical, 4)
        .background(urgency.softColor)
        .clipShape(Capsule())
    }
}

struct StatusDot: View {
    let urgency: ReminderUrgency

    var body: some View {
        Circle()
            .fill(urgency.color)
            .frame(width: 8, height: 8)
    }
}

#Preview {
    VStack(spacing: 12) {
        StatusPill(urgency: .overdue)
        StatusPill(urgency: .soon)
        StatusPill(urgency: .ok)
        HStack { StatusDot(urgency: .overdue); StatusDot(urgency: .soon); StatusDot(urgency: .ok) }
    }
    .padding()
}
