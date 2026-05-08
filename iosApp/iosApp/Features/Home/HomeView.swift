import SwiftUI
import Shared

struct HomeView: View {
    @Environment(AppRouter.self) private var router
    @State private var showContent = false

    var body: some View {
        VStack {
            Button("Click me!") {
                withAnimation {
                    showContent.toggle()
                }
            }
            Button("Tambah Kendaraan") {
                router.navigate(to: .addVehicle)
            }

            if showContent {
                VStack(spacing: 16) {
                    Image(systemName: "swift")
                        .font(.system(size: 200))
                        .foregroundColor(.accentColor)
                    Text("SwiftUI: \(Greeting().greet())")
                }
                .transition(.move(edge: .top).combined(with: .opacity))
            }
        }
        .frame(maxWidth: .infinity, maxHeight: .infinity, alignment: .top)
        .padding()
    }
}

#Preview {
    HomeView()
}
