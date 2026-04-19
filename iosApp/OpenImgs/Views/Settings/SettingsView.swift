import SwiftUI

struct SettingsView: View {
    @EnvironmentObject var appState: AppState

    var body: some View {
        NavigationStack {
            List {
                Section("Premium") {
                    if appState.isPremium {
                        Label("Premium Active", systemImage: "crown.fill")
                            .foregroundStyle(.yellow)
                    } else {
                        NavigationLink {
                            PremiumUpgradeView()
                        } label: {
                            Label("Upgrade to Premium", systemImage: "crown")
                        }
                    }
                }

                Section("Usage") {
                    HStack {
                        Text("Smart Searches")
                        Spacer()
                        Text("\(appState.searchesUsed)/\(appState.searchesLimit)")
                            .foregroundStyle(appState.canSearch() ? Color.secondary : Color.red)
                    }
                    HStack {
                        Text("Duplicate Deletes")
                        Spacer()
                        Text("\(appState.duplicateDeletesUsed)/\(appState.duplicateDeletesLimit)")
                            .foregroundStyle(appState.canDeleteDuplicate() ? Color.secondary : Color.red)
                    }
                }

                Section("About") {
                    LabeledContent("Version", value: "1.0.0")
                    Link("Privacy Policy", destination: URL(string: "https://openimgs.com/privacy")!)
                    Link("Terms of Service", destination: URL(string: "https://openimgs.com/terms")!)
                }

                Section {
                    Button("Reset Daily Usage", role: .destructive) {
                        appState.duplicateDeletesUsed = 0
                    }
                }
            }
            .navigationTitle("Settings")
        }
    }
}

struct PremiumUpgradeView: View {
    var body: some View {
        List {
            Section {
                VStack(spacing: 16) {
                    Image(systemName: "crown.fill")
                        .font(.system(size: 48))
                        .foregroundStyle(.yellow)
                    Text("OpenImgs Premium")
                        .font(.title2.bold())
                    Text("Unlock the full power of your photo library")
                        .font(.subheadline)
                        .foregroundStyle(Color(red: 0.43, green: 0.43, blue: 0.45))
                        .multilineTextAlignment(.center)
                }
                .frame(maxWidth: .infinity)
                .padding(.vertical, 24)
            }

            Section("Features") {
                Label("Unlimited Albums", systemImage: "rectangle.stack.fill")
                Label("Unlimited Smart Search", systemImage: "magnifyingglass")
                Label("Unlimited Duplicate Delete", systemImage: "trash")
                Label("Batch Storage Cleanup", systemImage: "sparkles")
            }

            Section("Pricing") {
                Button("$2.99/month") {}
                    .buttonStyle(.bordered)
                Button("$19.99/year (Save 44%)") {}
                    .buttonStyle(.borderedProminent)
            }
        }
    }
}
