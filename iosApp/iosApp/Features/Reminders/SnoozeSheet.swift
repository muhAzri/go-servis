import SwiftUI
import Shared

struct SnoozeSheet: View {
    var onPick: (SnoozeReminder.SnoozeDuration) -> Void

    private let options: [(SnoozeReminder.SnoozeDuration, String)] = [
        (SnoozeReminder.SnoozeDuration.oneday, "Besok"),
        (SnoozeReminder.SnoozeDuration.threedays, "3 hari lagi"),
        (SnoozeReminder.SnoozeDuration.oneweek, "1 minggu lagi"),
        (SnoozeReminder.SnoozeDuration.twoweeks, "2 minggu lagi"),
        (SnoozeReminder.SnoozeDuration.onemonth, "1 bulan lagi"),
    ]

    var body: some View {
        VStack(alignment: .leading, spacing: 0) {
            Text("Tunda pengingat")
                .font(.custom("PlusJakartaSans-ExtraBold", size: 20))
                .foregroundColor(.sgTextPrimary)
                .kerning(-0.3)
                .padding(.horizontal, 20)
                .padding(.top, 18)
            Text("Kapan kamu mau diingatkan lagi?")
                .font(.custom("PlusJakartaSans-Medium", size: 13))
                .foregroundColor(.sgTextMuted)
                .padding(.horizontal, 20)
                .padding(.top, 2)
                .padding(.bottom, 12)

            VStack(spacing: 8) {
                ForEach(options, id: \.1) { entry in
                    Button(action: { onPick(entry.0) }) {
                        HStack {
                            Text(entry.1)
                                .font(.custom("PlusJakartaSans-SemiBold", size: 14))
                                .foregroundColor(.sgTextPrimary)
                            Spacer()
                            Text("\u{f078}")
                                .font(.custom("FontAwesome6Free-Solid", size: 12))
                                .foregroundColor(.sgTextSubtle)
                        }
                        .padding(14)
                        .background(Color.sgSurfaceAlt)
                        .clipShape(RoundedRectangle(cornerRadius: 14))
                    }
                    .buttonStyle(.plain)
                }
            }
            .padding(.horizontal, 20)
            .padding(.bottom, 24)

            Spacer()
        }
        .background(Color.sgSurface)
    }
}
