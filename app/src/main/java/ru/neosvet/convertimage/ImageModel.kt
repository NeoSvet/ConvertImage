package ru.neosvet.convertimage

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.schedulers.Schedulers
import java.io.File
import java.io.FileInputStream

class ImageModel : ImageMaker {
    private var isOpened = false
    private lateinit var path: String
    private lateinit var image: Bitmap
    private var convertor: Convertor? = null

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
        if (!isOpened)
            emitter.onError(Exception("Image is not open"))

        val new_path = path.substring(0, path.lastIndexOf(".")) + ".png"
        convertor = Convertor(image, new_path, emitter)
        convertor?.convert()
    }.subscribeOn(Schedulers.io())
        .doOnDispose {
            convertor?.dispose()
        }
}