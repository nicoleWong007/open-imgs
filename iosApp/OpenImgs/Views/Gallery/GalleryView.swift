import SwiftUI
import Photos

struct GalleryView: View {
    @State private var photos: [PHAsset] = []
    @State private var gridSize: GridItem.Size = .adaptive(minimum: 100)
    private let columns = [
        GridItem(.adaptive(minimum: 100), spacing: 2)
    ]

    var body: some View {
        NavigationStack {
            ScrollView {
                LazyVGrid(columns: columns, spacing: 2) {
                    ForEach(photos, id: \.localIdentifier) { asset in
                        PhotoThumbnail(asset: asset)
                    }
                }
            }
            .navigationTitle("Photos")
        }
        .onAppear { loadPhotos() }
    }

    private func loadPhotos() {
        let options = PHFetchOptions()
        options.sortDescriptors = [NSSortDescriptor(key: "creationDate", ascending: false)]
        let result = PHAsset.fetchAssets(with: .image, options: options)
        var loaded: [PHAsset] = []
        result.enumerateObjects { asset, _, _ in
            loaded.append(asset)
        }
        photos = loaded
    }
}

struct PhotoThumbnail: View {
    let asset: PHAsset
    @State private var image: UIImage?

    var body: some View {
        Group {
            if let image {
                Image(uiImage: image)
                    .resizable()
                    .aspectRatio(contentMode: .fill)
            } else {
                Rectangle()
                    .fill(Color.gray.opacity(0.2))
            }
        }
        .frame(minWidth: 0, maxWidth: .infinity, minHeight: 100, maxHeight: 100)
        .clipped()
        .onAppear { loadImage() }
    }

    private func loadImage() {
        let options = PHImageRequestOptions()
        options.deliveryMode = .opportunistic
        options.isSynchronous = false
        let targetSize = CGSize(width: 200, height: 200)
        PHImageManager.default().requestImage(
            for: asset,
            targetSize: targetSize,
            contentMode: .aspectFill,
            options: options
        ) { result, _ in
            if let result {
                image = result
            }
        }
    }
}
