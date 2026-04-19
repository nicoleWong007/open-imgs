package com.openimgs.shared.platform

import android.content.ContentUris
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.provider.MediaStore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

actual class ThumbnailLoader actual constructor() {

    private lateinit var context: Context

    fun initialize(context: Context) {
        this.context = context.applicationContext
    }

    actual suspend fun loadThumbnail(photoId: String, width: Int, height: Int): ByteArray? =
        withContext(Dispatchers.IO) {
            try {
                val uri = Uri.parse(photoId)
                val bitmap = MediaStore.Images.Thumbnails.getThumbnail(
                    context.contentResolver,
                    ContentUris.parseId(uri),
                    MediaStore.Images.Thumbnails.MINI_KIND,
                    null
                ) ?: return@withContext null

                val scaled = Bitmap.createScaledBitmap(bitmap, width, height, true)
                val stream = java.io.ByteArrayOutputStream()
                scaled.compress(Bitmap.CompressFormat.JPEG, 85, stream)
                stream.toByteArray()
            } catch (_: Exception) {
                loadThumbnailFallback(photoId, width, height)
            }
        }

    private suspend fun loadThumbnailFallback(
        photoId: String,
        width: Int,
        height: Int
    ): ByteArray? = withContext(Dispatchers.IO) {
        try {
            val uri = Uri.parse(photoId)
            context.contentResolver.openInputStream(uri)?.use { input ->
                val options = BitmapFactory.Options().apply {
                    inJustDecodeBounds = true
                }
                BitmapFactory.decodeStream(input, null, options)

                val sampleSize = calculateSampleSize(options.outWidth, options.outHeight, width, height)
                val decodeOptions = BitmapFactory.Options().apply {
                    inSampleSize = sampleSize
                }
                context.contentResolver.openInputStream(uri)?.use { actualInput ->
                    val bitmap = BitmapFactory.decodeStream(actualInput, null, decodeOptions)
                    val scaled = Bitmap.createScaledBitmap(bitmap!!, width, height, true)
                    val stream = java.io.ByteArrayOutputStream()
                    scaled.compress(Bitmap.CompressFormat.JPEG, 85, stream)
                    stream.toByteArray()
                }
            }
        } catch (_: Exception) {
            null
        }
    }

    private fun calculateSampleSize(srcWidth: Int, srcHeight: Int, reqWidth: Int, reqHeight: Int): Int {
        var sampleSize = 1
        if (srcWidth > reqWidth || srcHeight > reqHeight) {
            val halfWidth = srcWidth / 2
            val halfHeight = srcHeight / 2
            while (halfWidth / sampleSize >= reqWidth && halfHeight / sampleSize >= reqHeight) {
                sampleSize *= 2
            }
        }
        return sampleSize
    }
}
