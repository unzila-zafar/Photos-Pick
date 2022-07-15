package com.androidinnovations.photosview.util

import android.content.ContentResolver
import android.content.ContentValues
import android.content.Context
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.provider.OpenableColumns
import android.webkit.MimeTypeMap
import android.widget.Toast
import androidx.core.content.FileProvider
import androidx.core.net.toUri
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import java.io.BufferedInputStream
import java.io.File
import java.io.FileInputStream
import java.io.FileNotFoundException
import java.net.URL
import java.util.concurrent.TimeUnit
import java.util.concurrent.TimeoutException


fun Context.downloadFile(url:String, Callback: (File?) -> Unit) {
    val observable = Observable.fromCallable {
        return@fromCallable createFile(url)
    }.timeout(50, TimeUnit.SECONDS).retryWhen { observable ->
        observable.flatMap { error ->
            if (error is TimeoutException) {
                Toast.makeText(this, "Timed out", Toast.LENGTH_SHORT).show()
                return@flatMap Observable.just(Any())
            } else {
                return@flatMap Observable.error(error)
            }
        }
    }.subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe({
            copyFileToDownloads(it){
                Callback(it)
            }

        }, {error->
            Toast.makeText(this, error.message, Toast.LENGTH_SHORT).show()
        })

}


fun Context.createFile(url: String): File? {
    val name = url.substring(url.lastIndexOf("/")+1)
    try {
        File(externalCacheDir, name).writeBytes(URL(url).readBytes())
        return File(externalCacheDir, name)
    } catch (e: FileNotFoundException) {

    }
    return null
}

private val DOWNLOAD_DIR =
    Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)

fun Context.copyFileToDownloads(file:File?,completed:()->Unit) {
    val resolver = contentResolver
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
        val contentValues = ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, file?.name)
            put(MediaStore.MediaColumns.MIME_TYPE, getMimeType(file!!))
            put(MediaStore.MediaColumns.SIZE, fileSize(file!!))
        }
        resolver.insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, contentValues)
    } else {
        val authority = "${packageName}.provider"
        val destinyFile = File(DOWNLOAD_DIR, file?.name)
        FileProvider.getUriForFile(this, authority, destinyFile)
    }?.also { downloadedUri ->
        resolver.openOutputStream(downloadedUri).use { outputStream ->
            val brr = ByteArray(1024)
            var len: Int
            val bufferedInputStream =
                BufferedInputStream(FileInputStream(file!!.absoluteFile))
            while ((bufferedInputStream.read(brr, 0, brr.size).also { len = it }) != -1) {
                outputStream?.write(brr, 0, len)
            }
            outputStream?.flush()
            bufferedInputStream.close()
        }
        Toast.makeText(this, "File Saved to Downloads Folder", Toast.LENGTH_SHORT).show()
        completed()
    }

}
fun Context.getMimeType(file:File): String {
    var mimeType: String? = ""
    mimeType = if (ContentResolver.SCHEME_CONTENT.equals(file.toUri().getScheme())) {
        val cr = contentResolver
        cr!!.getType(file.toUri())
    } else {
        val fileExtension = MimeTypeMap.getFileExtensionFromUrl(file.toUri().toString());
        MimeTypeMap.getSingleton().getMimeTypeFromExtension(
            fileExtension.toLowerCase()
        );
    }
    return mimeType!!

}
fun Context.fileSize(file: File):String {
    var getFileSize=""
    file?.toUri()?.let { returnUri ->
        contentResolver.query(returnUri, null, null, null, null)
    }?.use { cursor ->
        /*
         * Get the column indexes of the data in the Cursor,
         * move to the first row in the Cursor, get the data,
         * and display it.
         */
        val nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
        val sizeIndex = cursor.getColumnIndex(OpenableColumns.SIZE)
        getFileSize = cursor.getLong(sizeIndex).toString()
    }
    return getFileSize
}