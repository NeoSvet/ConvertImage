package ru.neosvet.convertimage

import android.graphics.Bitmap
import moxy.MvpView
import moxy.viewstate.strategy.alias.SingleState
import moxy.viewstate.strategy.alias.Skip

interface MainView : MvpView {
    @SingleState
    fun showImage(image: Bitmap)
    @Skip
    fun showProgressDialog()
    @Skip
    fun onComplete()
    @Skip
    fun showError(t: Throwable)
}