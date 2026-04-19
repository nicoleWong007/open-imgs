package com.openimgs.shared.platform

import android.content.ContentResolver
import android.content.ContentUris
import android.content.Context
import android.database.ContentObserver
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import com.openimgs.shared.domain.model.Photo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.withContext
import kotlinx.datetime.Instant

actual class PhotoProvider actual constructor() {

    private lateinit var context: Context
    private lateinit var contentResolver: ContentResolver

    fun initialize(context: Context) {
        this.context = context.applicationContext
        this.contentResolver = context.contentResolver
    }

    private fun ensureInitialized() {
        check(::contentResolver.isInitialized) {
            "PhotoProvider not initialized. Call initialize(context) first."
        }
    }

    actual suspend fun requestPermission(): Boolean {
        return hasPermission()
    }

    actual fun hasPermission(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            context.checkCallingOrSelfPermission(android.Manifest.permission.READ_MEDIA_IMAGES) ==
                android.content.pm.PackageManager.PERMISSION_GRANTED
        } else {
            context.checkCallingOrSelfPermission(android.Manifest.permission.READ_EXTERNAL_STORAGE) ==
                android.content.pm.PackageManager.PERMISSION_GRANTED
        }
    }

    actual fun observeChanges(): Flow<Unit> = callbackFlow {
        ensureInitialized()
        val observer = object : ContentObserver(null) {
            override fun onChange(selfChange: Boolean) {
                trySend(Unit)
            }
        }
        contentResolver.registerContentObserver(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
            true,
            observer
        )
        awaitClose { contentResolver.unregisterContentObserver(observer) }
    }

    actual suspend fun loadPhotos(limit: Int, offset: Int): List<Photo> =
        withContext(Dispatchers.IO) {
            ensureInitialized()
            val photos = mutableListOf<Photo>()
            val uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
            val projection = arrayOf(
                MediaStore.Images.Media._ID,
                MediaStore.Images.Media.DISPLAY_NAME,
                MediaStore.Images.Media.DATE_TAKEN,
                MediaStore.Images.Media.SIZE,
                MediaStore.Images.Media.MIME_TYPE,
                MediaStore.Images.Media.WIDTH,
                MediaStore.Images.Media.HEIGHT,
                MediaStore.Images.Media.ORIENTATION,
                MediaStore.Images.Media.DATA
            )

            val sortOrder = "${MediaStore.Images.Media.DATE_TAKEN} DESC"

            val queryBundle = Bundle().apply {
                putString(ContentResolver.QUERY_ARG_SQL_SORT_ORDER, sortOrder)
                putInt(ContentResolver.QUERY_ARG_LIMIT, limit)
                putInt(ContentResolver.QUERY_ARG_OFFSET, offset)
            }

            @Suppress("DEPRECATION")
            contentResolver.query(
                uri,
                projection,
                queryBundle,
                null
            )?.use { cursor ->
                val idCol = cursor.getColumnIndexOrThrow(MediaStore.Images.Media._ID)
                val nameCol = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DISPLAY_NAME)
                val dateCol = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATE_TAKEN)
                val sizeCol = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.SIZE)
                val mimeCol = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.MIME_TYPE)
                val widthCol = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.WIDTH)
                val heightCol = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.HEIGHT)
                val orientCol = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.ORIENTATION)
                val dataCol = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)

                while (cursor.moveToNext()) {
                    val contentUri = ContentUris.withAppendedId(uri, cursor.getLong(idCol))
                    photos.add(
                        Photo(
                            id = contentUri.toString(),
                            path = cursor.getString(dataCol) ?: "",
                            dateTaken = Instant.fromEpochMilliseconds(cursor.getLong(dateCol)),
                            size = cursor.getLong(sizeCol),
                            mimeType = cursor.getString(mimeCol) ?: "image/jpeg",
                            width = cursor.getInt(widthCol),
                            height = cursor.getInt(heightCol),
                            orientation = cursor.getInt(orientCol)
                        )
                    )
                }
            }
            photos
        }

    actual suspend fun getPhotoById(id: String): Photo? = withContext(Dispatchers.IO) {
        ensureInitialized()
        val uri = Uri.parse(id)
        val projection = arrayOf(
            MediaStore.Images.Media._ID,
            MediaStore.Images.Media.DISPLAY_NAME,
            MediaStore.Images.Media.DATE_TAKEN,
            MediaStore.Images.Media.SIZE,
            MediaStore.Images.Media.MIME_TYPE,
            MediaStore.Images.Media.WIDTH,
            MediaStore.Images.Media.HEIGHT,
            MediaStore.Images.Media.ORIENTATION,
            MediaStore.Images.Media.DATA
        )

        contentResolver.query(uri, projection, null, null, null)?.use { cursor ->
            if (cursor.moveToFirst()) {
                val dataCol = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
                val dateCol = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATE_TAKEN)
                val sizeCol = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.SIZE)
                val mimeCol = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.MIME_TYPE)
                val widthCol = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.WIDTH)
                val heightCol = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.HEIGHT)
                val orientCol = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.ORIENTATION)

                Photo(
                    id = id,
                    path = cursor.getString(dataCol) ?: "",
                    dateTaken = Instant.fromEpochMilliseconds(cursor.getLong(dateCol)),
                    size = cursor.getLong(sizeCol),
                    mimeType = cursor.getString(mimeCol) ?: "image/jpeg",
                    width = cursor.getInt(widthCol),
                    height = cursor.getInt(heightCol),
                    orientation = cursor.getInt(orientCol)
                )
            } else null
        }
    }

    actual suspend fun deletePhotos(ids: List<String>): List<String> =
        withContext(Dispatchers.IO) {
            ensureInitialized()
            val deleted = mutableListOf<String>()
            for (id in ids) {
                val uri = Uri.parse(id)
                val rows = contentResolver.delete(uri, null, null)
                if (rows > 0) deleted.add(id)
            }
            deleted
        }

    actual suspend fun getPhotoCount(): Int = withContext(Dispatchers.IO) {
        ensureInitialized()
        contentResolver.query(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
            arrayOf("COUNT(*)"),
            null,
            null,
            null
        )?.use { cursor ->
            if (cursor.moveToFirst()) cursor.getInt(0) else 0
        } ?: 0
    }

    actual suspend fun getThumbnail(id: String, width: Int, height: Int): ByteArray? =
        withContext(Dispatchers.IO) {
            try {
                val uri = Uri.parse(id)
                contentResolver.openInputStream(uri)?.use { it.readBytes() }
            } catch (_: Exception) {
                null
            }
        }

    actual suspend fun getFullImage(id: String): ByteArray? =
        withContext(Dispatchers.IO) {
            try {
                val uri = Uri.parse(id)
                contentResolver.openInputStream(uri)?.use { it.readBytes() }
            } catch (_: Exception) {
                null
            }
        }
}
