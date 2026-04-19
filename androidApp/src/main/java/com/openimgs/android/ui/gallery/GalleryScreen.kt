package com.openimgs.android.ui.gallery

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.openimgs.shared.domain.model.Photo
import com.openimgs.shared.platform.PhotoProvider

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GalleryScreen() {
    var photos by remember { mutableStateOf<List<Photo>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    suspend fun loadPhotos() {
        isLoading = true
        errorMessage = null
        try {
            val provider = PhotoProvider()
            photos = provider.loadPhotos(100, 0)
        } catch (e: Exception) {
            errorMessage = e.message ?: "Failed to load photos"
        } finally {
            isLoading = false
        }
    }

    LaunchedEffect(Unit) { loadPhotos() }

    androidx.compose.material3.Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Photos") },
                actions = {
                    if (errorMessage != null) {
                        IconButton(onClick = { }) {
                            Icon(Icons.Default.Refresh, contentDescription = "Retry")
                        }
                    }
                }
            )
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            when {
                isLoading -> {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        CircularProgressIndicator()
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            "Loading photos...",
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
                errorMessage != null -> {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            Icons.Default.PhotoLibrary,
                            contentDescription = null,
                            modifier = Modifier.size(48.dp),
                            tint = MaterialTheme.colorScheme.error
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            errorMessage!!,
                            style = MaterialTheme.typography.bodyLarge,
                            textAlign = TextAlign.Center,
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                }
                photos.isEmpty() -> {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            Icons.Default.PhotoLibrary,
                            contentDescription = null,
                            modifier = Modifier.size(48.dp),
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            "No photos yet",
                            style = MaterialTheme.typography.titleMedium
                        )
                        Text(
                            "Photos from your library will appear here",
                            color = Color(0xFF6E6E73),
                            textAlign = TextAlign.Center
                        )
                    }
                }
                else -> {
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(3),
                        modifier = Modifier.fillMaxSize(),
                        horizontalArrangement = Arrangement.spacedBy(2.dp),
                        verticalArrangement = Arrangement.spacedBy(2.dp)
                    ) {
                        items(photos, key = { it.id }) { photo ->
                            AsyncImage(
                                model = photo.path,
                                contentDescription = null,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .aspectRatio(1f)
                                    .background(Color.LightGray),
                                contentScale = ContentScale.Crop
                            )
                        }
                    }
                }
            }
        }
    }
}
