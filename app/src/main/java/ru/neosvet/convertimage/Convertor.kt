package ru.neosvet.convertimage

import android.graphics.Bitmap
import io.reactivex.rxjava3.android.MainThreadDisposable
import io.reactivex.rxjava3.core.CompletableEmitter
import java.io.File
import java.io.FileOutputStream
import java.util.concurrent.Executors

class Convertor(
    private val image: Bitmap,
    private val path: String,
    private val emitter: CompletableEmitter
) : MainThreadDisposable(), Runnable {
    private var file: File? = null

    private val converterTask by lazy {
        Executors
            .newSingleThreadExecutor()
            .submit(this)
    }

    fun convert() {
        converterTask
    }

    override fun run() {
        try {
            file = File(path)
            val fos = FileOutputStream(file)
            image.compress(Bitmap.CompressFormat.PNG, 100, fos)
            fos.close()

            file = null
            converterTask
                ?.takeIf { !isDisposed }
                ?.takeIf { task -> !task.isDone }
                ?.takeIf { task -> !task.isCancelled }
                ?.let { emitter.onComplete() }
        } catch (e: Exception) {
            emitter.onError(e)
        }
    }

    override fun onDispose() {
        converterTask
            ?.takeIf { task -> !task.isDone }
            ?.takeIf { task -> !task.isCancelled }
            ?.cancel(true)
            ?.also { deleteFile() }
    }

    private fun deleteFile() {
        file?.let {
            try {
                if (it.exists())
                    it.delete()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}