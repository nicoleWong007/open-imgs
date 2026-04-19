import SwiftUI
import Photos

struct OnboardingView: View {
    @EnvironmentObject var appState: AppState
    @State private var currentPage = 0

    var body: some View {
        TabView(selection: $currentPage) {
            onboardingPage(
                title: "Your Photos, Your Privacy",
                subtitle: "All processing happens on your device. Nothing leaves your phone.",
                icon: "lock.shield.fill",
                tag: 0
            )
            onboardingPage(
                title: "Smart Search",
                subtitle: "Find any photo using natural language. Powered by on-device AI.",
                icon: "magnifyingglass",
                tag: 1
            )
            onboardingPage(
                title: "Clean & Organize",
                subtitle: "Find duplicates, manage storage, and keep your library tidy.",
                icon: "sparkles",
                tag: 2
            )
        }
        .tabViewStyle(PageTabViewStyle())
        Button(action: { requestPhotoPermission() }) {
            Text("Get Started")
                .font(.headline)
                .frame(maxWidth: .infinity)
                .padding(.vertical, 16)
        }
        .buttonStyle(.borderedProminent)
        .padding(.horizontal, 32)
        .padding(.bottom, 32)
    }

    private func onboardingPage(title: String, subtitle: String, icon: String, tag: Int) -> some View {
        VStack(spacing: 24) {
            Spacer()
            Image(systemName: icon)
                .font(.system(size: 64))
                .foregroundStyle(Color.accentColor)
            VStack(spacing: 12) {
                Text(title)
                    .font(.title.bold())
                    .multilineTextAlignment(.center)
                Text(subtitle)
                    .font(.body)
                    .foregroundStyle(Color(red: 0.43, green: 0.43, blue: 0.45))
                    .multilineTextAlignment(.center)
            }
            .padding(.horizontal, 32)
            Spacer()
        }
        .tag(tag)
    }

    private func requestPhotoPermission() {
        PHPhotoLibrary.requestAuthorization(for: .readWrite) { status in
            DispatchQueue.main.async {
                if status == .authorized || status == .limited {
                    appState.grantPermission()
                }
            }
        }
    }
}
