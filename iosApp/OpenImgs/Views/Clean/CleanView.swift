import SwiftUI

struct CleanView: View {
    @EnvironmentObject var appState: AppState
    @State private var selectedTab = 0

    var body: some View {
        NavigationStack {
            VStack(spacing: 0) {
                Picker("Category", selection: $selectedTab) {
                    Text("Duplicates").tag(0)
                    Text("Storage").tag(1)
                }
                .pickerStyle(.segmented)
                .padding(.horizontal)
                .padding(.top, 8)

                TabView(selection: $selectedTab) {
                    DuplicatesListView()
                        .tag(0)

                    StorageAnalysisView()
                        .tag(1)
                }
                .tabViewStyle(PageTabViewStyle(indexDisplayMode: .never))
            }
            .navigationTitle("Clean")
        }
    }
}

struct DuplicatesListView: View {
    @EnvironmentObject var appState: AppState
    @State private var duplicateGroups: [DuplicateGroupItem] = []
    @State private var isScanning = false

    var body: some View {
        Group {
            if duplicateGroups.isEmpty {
                VStack(spacing: 16) {
                    Spacer()
                    Image(systemName: "checkmark.circle")
                        .font(.system(size: 48))
                        .foregroundStyle(.green)
                    Text("No duplicates found")
                        .font(.headline)
                    Text("Your library is clean!")
                        .foregroundStyle(Color(red: 0.43, green: 0.43, blue: 0.45))
                    Spacer()
                }
            } else {
                List {
                    ForEach(duplicateGroups) { group in
                        DuplicateGroupRow(group: group)
                    }
                }
            }
        }
        .overlay {
            if isScanning {
                VStack(spacing: 12) {
                    ProgressView()
                    Text("Scanning for duplicates...")
                        .font(.subheadline)
                        .foregroundStyle(Color(red: 0.43, green: 0.43, blue: 0.45))
                }
            }
        }
    }
}

struct DuplicateGroupItem: Identifiable {
    let id: String
    let originalId: String
    let duplicateCount: Int
    let similarity: Float
}

struct DuplicateGroupRow: View {
    let group: DuplicateGroupItem

    var body: some View {
        HStack {
            VStack(alignment: .leading, spacing: 4) {
                Text("\(group.duplicateCount) duplicates")
                    .font(.body)
                Text("Similarity: \(Int(group.similarity * 100))%")
                    .font(.caption)
                    .foregroundStyle(Color(red: 0.43, green: 0.43, blue: 0.45))
            }
            Spacer()
            Button("Review") {}
                .buttonStyle(.bordered)
        }
        .padding(.vertical, 4)
    }
}

struct StorageAnalysisView: View {
    @EnvironmentObject var appState: AppState
    @State private var categories: [StorageCategoryItem] = []

    var body: some View {
        Group {
            if categories.isEmpty {
                ContentUnavailableView(
                    "Analyzing...",
                    systemImage: "chart.pie",
                    description: Text("Storage analysis will appear here")
                )
            } else {
                List {
                    ForEach(categories) { category in
                        HStack {
                            Image(systemName: iconForType(category.type))
                                .frame(width: 32)
                            VStack(alignment: .leading, spacing: 2) {
                                Text(category.name)
                                    .font(.body)
                                Text(formatSize(category.size))
                                    .font(.caption)
                                    .foregroundStyle(Color(red: 0.43, green: 0.43, blue: 0.45))
                            }
                            Spacer()
                            Text("\(category.count) items")
                                .font(.subheadline)
                                .foregroundStyle(.secondary)
                        }
                        .padding(.vertical, 2)
                    }

                    if appState.isPremium {
                        Section {
                            Button("Clean All", role: .destructive) {
                        }
                        }
                    } else {
                        Section {
                            NavigationLink("Upgrade to Premium for batch cleanup") {}
                        }
                    }
                }
            }
        }
    }

    private func iconForType(_ type: String) -> String {
        switch type {
        case "SCREENSHOTS": return "camera.viewfinder"
        case "DUPLICATES": return "doc.on.doc"
        case "LARGE_VIDEOS": return "video"
        case "SIMILAR_BURSTS": return "photo.stack"
        case "BLURRED": return "eye.slash"
        default: return "folder"
        }
    }

    private func formatSize(_ bytes: Int64) -> String {
        let formatter = ByteCountFormatter()
        formatter.allowedUnits = [.useKB, .useMB, .useGB]
        return formatter.string(fromByteCount: bytes)
    }
}

struct StorageCategoryItem: Identifiable {
    let id = UUID()
    let type: String
    let name: String
    let count: Int
    let size: Int64
}
