package ru.neosvet.convertimage

import io.reactivex.rxjava3.core.Scheduler
import io.reactivex.rxjava3.disposables.Disposable
import moxy.MvpPresenter

class MainPresenter(
    private val model: ImageMaker,
    private val uiScheduler: Scheduler
) : MvpPresenter<MainView>() {
    private var process: Disposable? = null

    fun openImage(path: String?) {
        if (path == null)
            viewState.showError(Exception("Image is not found"))

        path?.let {
            model.open(it).observeOn(uiScheduler).subscribe(
                viewState::showImage,
                viewState::showError
            )
        }
    }

    fun startConvert() {
        viewState.showProgressDialog()
        process = model.convert().observeOn(uiScheduler).subscribe(
            viewState::onComplete,
            viewState::showError
        )
    }

    fun stop() {
        process?.dispose()
        process = null
    }

    override fun onDestroy() {
        process?.dispose()
    }
}