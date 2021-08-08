package ru.neosvet.convertimage

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.schedulers.Schedulers
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream

class ImageModel : ImageMaker {
    private var isOpened = false
    private lateinit var path: String
    private lateinit var image: Bitmap

    override fun open(path: String) = Single.fromCallable<Bitmap> {
        this.path = path
        val f = File(path)
        val options = BitmapFactory.Options()
        options.inPreferredConfig = Bitmap.Config.ARGB_8888
        image = BitmapFactory.decodeStream(FileInputStream(f), null, options)!!
        isOpened = true
        image
    }.subscribeOn(Schedulers.io())

    override fun convert() = Completable.create { emitter ->
        try {
            if (!isOpened)
                throw Exception("Image is not open")
            val new_path = path.substring(0, path.lastIndexOf(".")) + ".png"
            val fos = FileOutputStream(File(new_path))
            image.compress(Bitmap.CompressFormat.PNG, 100, fos)
            fos.close()
            emitter.onComplete()
        } catch (e: Exception) {
            emitter.onError(e)
        }
    }.subscribeOn(Schedulers.io())

}