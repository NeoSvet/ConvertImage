package ru.neosvet.convertimage

import android.Manifest
import android.app.ProgressDialog
import android.content.DialogInterface
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import moxy.MvpAppCompatActivity
import moxy.ktx.moxyPresenter
import ru.neosvet.convertimage.databinding.ActivityMainBinding

class MainActivity : MvpAppCompatActivity(), MainView {

    private val presenter: MainPresenter by moxyPresenter {
        MainPresenter(
            model = ImageModel(),
            uiScheduler = AndroidSchedulers.mainThread()
        )
    }

    private val picker =
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
            uri?.let {
                presenter.openImage(getPathFromURI(it))
            }
        }
    private val requestPermissions =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
            when {
                granted -> {
                    picker.launch("image/jpg")
                }
                !shouldShowRequestPermissionRationale(Manifest.permission.WRITE_EXTERNAL_STORAGE) -> {
                    // доступ запрещен, пользователь поставил галочку Don't ask again.
                }
                else -> {
                    // доступ запрещен, пользователь отклонил запрос
                }
            }
        }

    private val viewBinding: ActivityMainBinding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }

    private var mProgressDialog: ProgressDialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(viewBinding.root)

        with(viewBinding) {
            btnOpen.setOnClickListener {
                if (shouldShowRequestPermissionRationale(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                    picker.launch("image/jpg")
                } else {
                    requestPermissions.launch(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                }
            }

            btnConvert.setOnClickListener {
                presenter.startConvert()
            }
        }
    }

    override fun showImage(image: Bitmap) {
        with(viewBinding) {
            ivImage.setImageBitmap(image)
            btnConvert.isEnabled = true
        }
    }

    override fun showProgressDialog() {
        mProgressDialog = ProgressDialog(this).apply {
            setProgressStyle(ProgressDialog.STYLE_SPINNER)
            setMessage(getString(R.string.running_convert))
            setButton(getString(R.string.cancel), { d: DialogInterface, i: Int ->
                mProgressDialog?.dismiss()
            })
            setOnDismissListener {
                presenter.stop()
            }
        }
        mProgressDialog?.show()
    }

    override fun onComplete() {
        mProgressDialog?.dismiss()
        Toast.makeText(this, getString(R.string.completed), Toast.LENGTH_LONG).show()
    }

    override fun showError(t: Throwable) {
        mProgressDialog?.dismiss()
        Toast.makeText(
            this, getString(R.string.error) +
                    ": " + t.message, Toast.LENGTH_LONG
        ).show()
    }
}