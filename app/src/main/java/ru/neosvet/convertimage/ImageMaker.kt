package ru.neosvet.convertimage

import android.graphics.Bitmap
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Single

interface ImageMaker {
    fun open(path: String): Single<Bitmap>
    fun convert(): Completable
}