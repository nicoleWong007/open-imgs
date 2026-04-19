import SwiftUI

struct MainTabView: View {
    @State private var selectedTab = 0

    var body: some View {
        TabView(selection: $selectedTab) {
            GalleryView()
                .tabItem {
                    Label("Photos", systemImage: "photo.on.rectangle")
                }
                .tag(0)

            AlbumListView()
                .tabItem {
                    Label("Albums", systemImage: "rectangle.stack")
                }
                .tag(1)

            SearchView()
                .tabItem {
                    Label("Search", systemImage: "magnifyingglass")
                }
                .tag(2)

            CleanView()
                .tabItem {
                    Label("Clean", systemImage: "trash.circle")
                }
                .tag(3)

            SettingsView()
                .tabItem {
                    Label("Settings", systemImage: "gearshape")
                }
                .tag(4)
        }
        .tint(.accentColor)
    }
}
