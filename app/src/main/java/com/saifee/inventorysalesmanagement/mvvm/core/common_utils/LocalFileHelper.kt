package com.saifee.inventorysalesmanagement.mvvm.core.common_utils

import android.content.Context
import android.net.Uri
import dagger.hilt.android.qualifiers.ApplicationContext
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LocalFileHelper @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private fun getParentDir(parent: String) = File(context.filesDir, parent).apply { if (!exists()) mkdirs() }
    private fun getTempDir(parent: String) = File(getParentDir(parent), "temp").apply { if (!exists()) mkdirs() }
    private fun getNonSyncDir(parent: String) = File(getParentDir(parent), "non-sync").apply { if (!exists()) mkdirs() }
    private fun getSyncDir(parent: String) = File(getParentDir(parent), "sync").apply { if (!exists()) mkdirs() }

    fun hasEnoughStorageSpace(minBytes: Long = 10 * 1024 * 1024L): Boolean {
        return context.filesDir.usableSpace >= minBytes
    }

    fun saveToTemp(parent: String, uri: Uri): String? {
        if (!hasEnoughStorageSpace()) return null
        return try {
            val fileName = "IMG_${UUID.randomUUID()}.jpg"
            val file = File(getTempDir(parent), fileName)
            context.contentResolver.openInputStream(uri)?.use { input ->
                FileOutputStream(file).use { output ->
                    input.copyTo(output)
                }
            }
            file.absolutePath
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    /**
     * Compresses and downsamples high-resolution images to prevent OOM errors.
     */
    fun compressAndSaveImage(parent: String, uri: Uri, maxWidth: Int = 1024, maxHeight: Int = 1024, quality: Int = 80): String? {
        if (!hasEnoughStorageSpace()) return null
        return try {
            val fileName = "IMG_${UUID.randomUUID()}.jpg"
            val file = File(getNonSyncDir(parent), fileName)
            
            val options = android.graphics.BitmapFactory.Options().apply {
                inJustDecodeBounds = true
            }
            context.contentResolver.openInputStream(uri)?.use { input ->
                android.graphics.BitmapFactory.decodeStream(input, null, options)
            }

            var sampleSize = 1
            if (options.outHeight > maxHeight || options.outWidth > maxWidth) {
                val halfHeight = options.outHeight / 2
                val halfWidth = options.outWidth / 2
                while ((halfHeight / sampleSize) >= maxHeight && (halfWidth / sampleSize) >= maxWidth) {
                    sampleSize *= 2
                }
            }

            val decodeOptions = android.graphics.BitmapFactory.Options().apply {
                inSampleSize = sampleSize
            }
            
            val bitmap = context.contentResolver.openInputStream(uri)?.use { input ->
                android.graphics.BitmapFactory.decodeStream(input, null, decodeOptions)
            } ?: return null

            FileOutputStream(file).use { output ->
                bitmap.compress(android.graphics.Bitmap.CompressFormat.JPEG, quality, output)
            }
            bitmap.recycle()
            file.absolutePath
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    fun moveToNonSync(parent: String, tempPath: String): String? {
        return moveFile(tempPath, getNonSyncDir(parent))
    }

    fun moveToSync(parent: String, path: String): String? {
        return moveFile(path, getSyncDir(parent))
    }

    private fun moveFile(sourcePath: String, targetDir: File): String? {
        val sourceFile = File(sourcePath)
        if (!sourceFile.exists()) return null
        val targetFile = File(targetDir, sourceFile.name)
        return if (sourceFile.renameTo(targetFile)) {
            targetFile.absolutePath
        } else {
            try {
                sourceFile.copyTo(targetFile, overwrite = true)
                sourceFile.delete()
                targetFile.absolutePath
            } catch (e: Exception) {
                e.printStackTrace()
                null
            }
        }
    }

    fun saveToSync(parent: String, inputStream: InputStream, fileName: String): String? {
        return try {
            val file = File(getSyncDir(parent), fileName)
            FileOutputStream(file).use { output ->
                inputStream.copyTo(output)
            }
            file.absolutePath
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    fun existsInSync(parent: String, fileName: String): Boolean {
        return File(getSyncDir(parent), fileName).exists()
    }

    fun getSyncFile(parent: String, fileName: String): File {
        return File(getSyncDir(parent), fileName)
    }

    fun clearTemp(parent: String) {
        getTempDir(parent).listFiles()?.forEach { it.delete() }
    }

    /**
     * Clears all temp folders across all parents.
     * This is useful on app startup.
     */
    fun clearAllTemps() {
        context.filesDir.listFiles { file -> file.isDirectory }?.forEach { parentDir ->
            val tempDir = File(parentDir, "temp")
            if (tempDir.exists() && tempDir.isDirectory) {
                tempDir.listFiles()?.forEach { it.delete() }
            }
        }
    }
}
