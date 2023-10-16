package com.deefrent.rnd.common.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import timber.log.Timber
import java.io.File
import java.io.FileOutputStream

/**
 * 29.06.2023
 */
fun saveBitmapToFile(bitmap: Bitmap?, context: Context, fileName: String): String? {
    var filePath: String? = null
    try {
        val file = File(context.cacheDir, "${fileName}.jpg")
        val outputStream = FileOutputStream(file)
        bitmap?.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
        outputStream.flush()
        outputStream.close()
        filePath = file.absolutePath
    } catch (e: Exception) {
        e.printStackTrace()
    }
    return filePath
}

fun getBitmapFromFile(filePath: String): Bitmap? {
    return try {
        BitmapFactory.decodeFile(filePath)
    } catch (e: Exception) {
        e.printStackTrace()
        Timber.e(e.toString())
        null
    }
}

fun deleteImageFile(filePath: String): Boolean {
    val file = File(filePath)
    return file.delete()
}

fun deleteImageFileWithFileName(context: Context, stringToContain: String) {
    val directory =
        File(context.cacheDir, "")// Specify the directory path where the files are located

    val filesToDelete = directory.listFiles { file ->
        file.isFile && file.name.contains(stringToContain)
    }

    filesToDelete?.forEach { file ->
        file.delete()
    }
}

