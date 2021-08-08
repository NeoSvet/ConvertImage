package ru.neosvet.convertimage

import android.content.Context
import android.database.Cursor
import android.net.Uri
import android.provider.MediaStore

fun Context.getPathFromURI(uri: Uri): String? {
    var cursor: Cursor? = null
    try {
        val proj = arrayOf(MediaStore.Images.Media.DATA)
        cursor = getContentResolver().query(uri, proj, null, null, null)
        cursor?.let {
            val column_index: Int = it.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
            it.moveToFirst()
            val path = it.getString(column_index)
            return path
        }
    } finally {
        cursor?.close()
    }
    return null
}