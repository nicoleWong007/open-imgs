import SwiftUI

@main
struct OpenImgsApp: App {
    @StateObject private var appState = AppState()

    var body: some Scene {
        WindowGroup {
            if appState.hasPermission {
                MainTabView()
                    .environmentObject(appState)
            } else {
                OnboardingView()
                    .environmentObject(appState)
            }
        }
    }
}

class AppState: ObservableObject {
    @Published var hasPermission = false
    @Published var isPremium = false
    @Published var searchesUsed = 0
    @Published var duplicateDeletesUsed = 0

    let searchesLimit = 10
    let duplicateDeletesLimit = 5

    func grantPermission() {
        hasPermission = true
    }

    func canSearch() -> Bool {
        isPremium || searchesUsed < searchesLimit
    }

    func canDeleteDuplicate() -> Bool {
        isPremium || duplicateDeletesUsed < duplicateDeletesLimit
    }

    func recordSearch() {
        if !isPremium {
            searchesUsed += 1
        }
    }

    func recordDuplicateDelete() {
        if !isPremium {
            duplicateDeletesUsed += 1
        }
    }
}
