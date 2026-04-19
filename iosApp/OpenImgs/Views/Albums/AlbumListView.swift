import SwiftUI
import Photos

struct AlbumListView: View {
    @EnvironmentObject var appState: AppState
    @State private var albums: [AlbumItem] = []
    @State private var showingCreate = false

    var body: some View {
        NavigationStack {
            List {
                ForEach(albums) { album in
                    NavigationLink(destination: AlbumDetailView(album: album)) {
                        HStack(spacing: 12) {
                            RoundedRectangle(cornerRadius: 8)
                                .fill(Color.gray.opacity(0.2))
                                .frame(width: 56, height: 56)
                                .overlay {
                                    if let cover = album.coverAsset {
                                        PhotoThumbnail(asset: cover)
                                            .clipShape(RoundedRectangle(cornerRadius: 8))
                                    } else {
                                        Image(systemName: "photo")
                                            .foregroundStyle(.secondary)
                                    }
                                }
                            VStack(alignment: .leading, spacing: 4) {
                                Text(album.name)
                                    .font(.body)
                                Text("\(album.photoCount) photos")
                                    .font(.subheadline)
                                    .foregroundStyle(Color(red: 0.43, green: 0.43, blue: 0.45))
                            }
                        }
                        .padding(.vertical, 4)
                    }
                }
            }
            .navigationTitle("Albums")
            .toolbar {
                ToolbarItem(placement: .topBarTrailing) {
                    Button(action: { showingCreate = true }) {
                        Image(systemName: "plus")
                    }
                    .disabled(!appState.isPremium && albums.count >= 5)
                }
            }
            .sheet(isPresented: $showingCreate) {
                CreateAlbumView { name in
                    createAlbum(name: name)
                }
            }
            .onAppear { loadAlbums() }
        }
    }

    private func loadAlbums() {
        let options = PHFetchOptions()
        let userAlbums = PHAssetCollection.fetchAssetCollections(
            with: .album,
            subtype: .albumRegular,
            options: options
        )
        var loaded: [AlbumItem] = []
        userAlbums.enumerateObjects { collection, _, _ in
            let fetch = PHAsset.fetchAssets(in: collection, options: nil)
            let coverAsset = fetch.firstObject
            loaded.append(AlbumItem(
                id: collection.localIdentifier,
                name: collection.localizedTitle ?? "Untitled",
                photoCount: fetch.count,
                coverAsset: coverAsset,
                collection: collection
            ))
        }
        albums = loaded
    }

    private func createAlbum(name: String) {
        PHPhotoLibrary.shared().performChanges {
            PHAssetCollectionChangeRequest.creationRequestForAssetCollection(withTitle: name)
        } completionHandler: { success, _ in
            if success {
                DispatchQueue.main.async { loadAlbums() }
            }
        }
    }
}

struct AlbumItem: Identifiable {
    let id: String
    let name: String
    let photoCount: Int
    let coverAsset: PHAsset?
    let collection: PHAssetCollection
}

struct AlbumDetailView: View {
    let album: AlbumItem
    @State private var photos: [PHAsset] = []
    private let columns = [GridItem(.adaptive(minimum: 100), spacing: 2)]

    var body: some View {
        ScrollView {
            LazyVGrid(columns: columns, spacing: 2) {
                ForEach(photos, id: \.localIdentifier) { asset in
                    PhotoThumbnail(asset: asset)
                }
            }
        }
        .navigationTitle(album.name)
        .onAppear { loadPhotos() }
    }

    private func loadPhotos() {
        let options = PHFetchOptions()
        options.sortDescriptors = [NSSortDescriptor(key: "creationDate", ascending: false)]
        let result = PHAsset.fetchAssets(in: album.collection, options: options)
        var loaded: [PHAsset] = []
        result.enumerateObjects { asset, _, _ in loaded.append(asset) }
        photos = loaded
    }
}

struct CreateAlbumView: View {
    @Environment(\.dismiss) private var dismiss
    @State private var name = ""
    let onCreate: (String) -> Void

    var body: some View {
        NavigationStack {
            Form {
                TextField("Album Name", text: $name)
            }
            .navigationTitle("New Album")
            .navigationBarTitleDisplayMode(.inline)
            .toolbar {
                ToolbarItem(placement: .cancellationAction) {
                    Button("Cancel") { dismiss() }
                }
                ToolbarItem(placement: .confirmationAction) {
                    Button("Create") {
                        if !name.trimmingCharacters(in: .whitespaces).isEmpty {
                            onCreate(name)
                            dismiss()
                        }
                    }
                    .disabled(name.trimmingCharacters(in: .whitespaces).isEmpty)
                }
            }
        }
    }
}
