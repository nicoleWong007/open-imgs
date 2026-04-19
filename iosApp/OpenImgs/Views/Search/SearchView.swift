import SwiftUI

struct SearchView: View {
    @EnvironmentObject var appState: AppState
    @State private var searchText = ""
    @State private var results: [String] = []
    @State private var isSearching = false

    var body: some View {
        NavigationStack {
            VStack {
                if results.isEmpty && !searchText.isEmpty {
                    ContentUnavailableView(
                        "No Results",
                        systemImage: "photo.on.rectangle.magnifyingglass",
                        description: Text("Try a different search term")
                    )
                } else if results.isEmpty {
                    ContentUnavailableView(
                        "Smart Search",
                        systemImage: "magnifyingglass",
                        description: Text("Search your photos by content")
                    )
                } else {
                    List(results, id: \.self) { resultId in
                        HStack {
                            RoundedRectangle(cornerRadius: 8)
                                .fill(Color.gray.opacity(0.2))
                                .frame(width: 56, height: 56)
                            Text(resultId)
                        }
                    }
                }
            }
            .navigationTitle("Search")
            .searchable(text: $searchText, prompt: "Search photos...")
            .onChange(of: searchText) { _, newValue in
                performSearch(query: newValue)
            }
            .overlay {
                if isSearching {
                    ProgressView()
                }
            }
        }
    }

    private func performSearch(query: String) {
        guard !query.isEmpty, appState.canSearch() else { return }
        isSearching = true
        // Will integrate with shared SearchEngine
        DispatchQueue.main.asyncAfter(deadline: .now() + 0.5) {
            results = []
            isSearching = false
            appState.recordSearch()
        }
    }
}
