package com.pettrack.app.core.common

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.media.ExifInterface
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream

/**
 * Downscales + re-encodes a picked image before upload. Camera photos are often several MB and
 * many megapixels; uploading them raw wastes bandwidth, storage, and holds a huge ByteArray in
 * memory. Caps the longest side to [maxDim] and re-encodes as JPEG, honoring the source EXIF
 * orientation so portrait phone photos aren't uploaded sideways.
 *
 * Returns null (so the caller falls back to the original bytes) when: decoding fails, the source
 * has an alpha channel (JPEG would flatten transparency to black), or JPEG encoding fails.
 */
object ImageDownscaler {

    fun downscaleToJpeg(source: ByteArray, maxDim: Int = 1600, quality: Int = 85): Pair<ByteArray, String>? {
        return try {
            val bounds = BitmapFactory.Options().apply { inJustDecodeBounds = true }
            BitmapFactory.decodeByteArray(source, 0, source.size, bounds)
            val width = bounds.outWidth
            val height = bounds.outHeight
            if (width <= 0 || height <= 0) return null

            var sample = 1
            while (width / sample > maxDim || height / sample > maxDim) sample *= 2

            val opts = BitmapFactory.Options().apply { inSampleSize = sample }
            val decoded = BitmapFactory.decodeByteArray(source, 0, source.size, opts) ?: return null

            // Re-encoding to JPEG flattens transparency to black — leave alpha images untouched.
            if (decoded.hasAlpha()) {
                decoded.recycle()
                return null
            }

            val oriented = applyExifOrientation(decoded, source)
            val out = ByteArrayOutputStream()
            val ok = oriented.compress(Bitmap.CompressFormat.JPEG, quality, out)
            oriented.recycle()
            if (!ok || out.size() == 0) return null
            out.toByteArray() to "image/jpeg"
        } catch (t: Throwable) {
            AppLog.w("Image downscale failed; falling back to original bytes", t)
            null
        }
    }

    private fun applyExifOrientation(bitmap: Bitmap, source: ByteArray): Bitmap {
        val orientation = try {
            ExifInterface(ByteArrayInputStream(source))
                .getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL)
        } catch (_: Exception) {
            ExifInterface.ORIENTATION_NORMAL
        }
        val matrix = Matrix()
        when (orientation) {
            ExifInterface.ORIENTATION_ROTATE_90 -> matrix.postRotate(90f)
            ExifInterface.ORIENTATION_ROTATE_180 -> matrix.postRotate(180f)
            ExifInterface.ORIENTATION_ROTATE_270 -> matrix.postRotate(270f)
            ExifInterface.ORIENTATION_FLIP_HORIZONTAL -> matrix.postScale(-1f, 1f)
            ExifInterface.ORIENTATION_FLIP_VERTICAL -> matrix.postScale(1f, -1f)
            else -> return bitmap
        }
        val rotated = Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
        if (rotated != bitmap) bitmap.recycle()
        return rotated
    }
}
