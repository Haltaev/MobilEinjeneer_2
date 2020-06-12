package ru.mobilengineer.ui.activity

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.google.gson.Gson
import com.google.zxing.Result
import kotlinx.android.synthetic.main.activity_scanner.*
import kotlinx.android.synthetic.main.progress_bar.*
import me.dm7.barcodescanner.zxing.ZXingScannerView
import ru.mobilengineer.App
import ru.mobilengineer.R
import ru.mobilengineer.common.PreferencesManager
import ru.mobilengineer.common.injectViewModel
import ru.mobilengineer.viewmodel.ReturnFromWarehouseViewModel
import ru.mobilengineer.viewmodel.ScannerViewModel
import javax.inject.Inject

class ScannerActivity : BaseActivity(), ZXingScannerView.ResultHandler {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    @Inject
    lateinit var preferencesManager: PreferencesManager

    var key = ""
    var items = ""

    lateinit var scannerViewModel: ScannerViewModel

    @RequiresApi(api = Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        App.getComponent().inject(this)
        scannerViewModel = injectViewModel(viewModelFactory)


        if(intent != null) {
            key = intent.getStringExtra(KEY)
            items = intent.getStringExtra(ITEMS)
        }

        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN)

        Log.d(LOG_TAG, "Запрашиваем разрешение")
        if ((checkSelfPermission(Manifest.permission.CAMERA)
                    != PackageManager.PERMISSION_GRANTED
                    || (ContextCompat.checkSelfPermission(
                this@ScannerActivity,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            )
                    != PackageManager.PERMISSION_GRANTED))
        ) {
            requestPermissions(
                arrayOf<String>(
                    Manifest.permission.CAMERA,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                ), 1
            )
        }
//        setContentView(scanner_view)

        back_button.setOnClickListener {
            finish()
        }
        button_choose_manually.setOnClickListener {
            startActionWithItemActivity(
                isScanned = false,
                title = ""
            )
        }
        button_take_picture.setOnClickListener {
        }
        when (key) {
            RETURN_TO_WAREHOUSE -> text.text = "Отсканируйте штрих-код склада"
            INSTALL_IN_DEVICE -> text.text = "Отсканируйте штрих-код устройства"
            TRANSFER_TO_ENGINEER -> text.text = "Отсканируйте штрих-код инженера"
        }

    }

    override fun onResume() {
        super.onResume()
        observeViewModel(scannerViewModel)
        scanner_view.setResultHandler(this)
        if (checkSelfPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            scanner_view.startCamera()
        } else {
            requestPermissions(
                arrayOf<String>(
                    Manifest.permission.CAMERA,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                ), 1
            )
        }
    }

    override fun onPause() {
        super.onPause()
        scanner_view.stopCamera()
    }

    override fun getLayoutId(): Int {
        return R.layout.activity_scanner
    }

    companion object {
        const val RETURN_TO_WAREHOUSE = "RETURN_TO_WAREHOUSE"
        const val INSTALL_IN_DEVICE = "INSTALL_IN_DEVICE"
        const val TRANSFER_TO_ENGINEER = "TRANSFER_TO_ENGINEER"

        const val LOG_TAG = "myLogs"
        const val KEY = "KEY"
        const val ITEMS = "ITEMS"

        fun open(
            context: Context,
            key: String,
            items: String
        ) {
            val intent = Intent(context, ScannerActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            intent.putExtra(KEY, key)
            intent.putExtra(ITEMS, items)
            context.startActivity(intent)
        }
    }

    private fun startActionWithItemActivity(
       context : Context = this@ScannerActivity,
       key: String  = this.key,
       isScanned : Boolean = true,
       items : String  = this.items,
       title: String
    ){
        ActionWithItemActivity.open(
            context = context,
            key = key,
            isScanned = isScanned,
            items = items,
            title = title
        )
        this@ScannerActivity.finish()
    }

    private fun observeViewModel(viewModel: ScannerViewModel) {
        viewModel.apply {
            dataLiveData.observe(this@ScannerActivity, Observer { resp ->
                hideProgressBar()
                startActionWithItemActivity(title = Gson().toJson(resp))
            })
            errorLiveData.observe(this@ScannerActivity, Observer { errorResp ->
                hideProgressBar()
                if (errorResp == 401) {
                    preferencesManager.apply {
                        isAuthCompleted = false
                        isTouchIdAdded = false
                        isPasscodeChanging = false
                        passcode = null
                    }

                    AuthorizationActivity.open(this@ScannerActivity)
                    this@ScannerActivity.finishAffinity()
                } else if (errorResp == 404) {
                    Toast.makeText(context, "Устройство не найдено", Toast.LENGTH_LONG).show()
                    scanner_view.setResultHandler(this@ScannerActivity)
                    scanner_view.startCamera()
                }
            })
        }
    }

    override fun handleResult(rawResult: Result?) {
        if(key == INSTALL_IN_DEVICE){
            scanner_view.stopCamera()
            showProgressBar()
            scannerViewModel.checkSerialNumber(rawResult.toString().substring(3))
        } else {
            startActionWithItemActivity(title = rawResult.toString())
        }
    }

    fun showProgressBar() {
        progress_bar.visibility = View.VISIBLE
    }
    fun hideProgressBar() {
        progress_bar.visibility = View.GONE
    }
}