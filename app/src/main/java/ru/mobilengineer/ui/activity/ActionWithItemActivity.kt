package ru.mobilengineer.ui.activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import kotlinx.android.synthetic.main.progress_bar.*
import ru.mobilengineer.App
import ru.mobilengineer.R
import ru.mobilengineer.common.PreferencesManager
import ru.mobilengineer.ui.fragment.engineer.IncommingSkuFromEngineerFragment
import ru.mobilengineer.ui.fragment.engineer.PickEngineerFragment
import ru.mobilengineer.ui.fragment.filters.FiltersFragment
import ru.mobilengineer.ui.fragment.warehouse.IncommingSkuFromWarehouseFragment
import ru.mobilengineer.ui.fragment.installindevice.InstallInDeviceConfirmFragment
import ru.mobilengineer.ui.fragment.installindevice.InstallInDeviceFragment
import ru.mobilengineer.ui.fragment.returntowarehouse.PickWarehouseFragment
import ru.mobilengineer.ui.fragment.returntowarehouse.ReturnFromWarehouseFragment
import ru.mobilengineer.ui.fragment.returntowarehouse.ReturnToWarehouseConfirmFragment
import ru.mobilengineer.ui.fragment.returntowarehouse.ReturnToWarehouseFragment
import ru.mobilengineer.ui.fragment.transfertoengineer.TransferFromEngineerFragment
import ru.mobilengineer.ui.fragment.transfertoengineer.TransferToEngineerConfirmFragment
import ru.mobilengineer.ui.fragment.transfertoengineer.TransferToEngineerFragment
import javax.inject.Inject

class ActionWithItemActivity : BaseActivity() {
    @Inject
    lateinit var preferencesManager: PreferencesManager

    override fun getLayoutId(): Int = R.layout.activity_main

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        App.getComponent().inject(this)

        val isScanned = intent.getBooleanExtra(IS_SCANNED, true)
        val items = intent.getStringExtra(ITEMS)
        val title = intent.getStringExtra(TITLE)
        val quantity = -1


        when (intent.getStringExtra(KEY)) {
            RETURN_TO_WAREHOUSE -> {
                if (isScanned) {
                    openReturnToWarehouseConfirmFragment(
                        key = RETURN_TO_WAREHOUSE,
                        isScanned = isScanned,
                        items = items,
                        title = title ?: "",
                        quantity = quantity
                    )
                } else {
                    openReturnToWarehouseFragment(
                        key = RETURN_TO_WAREHOUSE,
                        items = items
                    )
                }
            }
            WAREHOUSE -> {
                if (isScanned) {
                    openWarehouseFragment(
                        key = WAREHOUSE,
                        isScanned = isScanned,
                        title = title ?: ""
                    )
                } else {
                    openPickWarehouseFragment(
                        key = WAREHOUSE,
                        isScanned = isScanned,
                        title = title ?: ""
                    )
                }
            }
            ENGINEER -> {
                if (isScanned) {
                    openIncommingSkuFromEngineerFragment(
                        key = ENGINEER,
                        isScanned = isScanned,
                        title = title ?: ""
                    )
                } else {
                    openPickEngineerFragment(
                        key = ENGINEER,
                        isScanned = isScanned,
                        title = title ?: ""
                    )
                }
            }
            INSTALL_IN_DEVICE -> {
                if (isScanned) {
                    openInstallInDeviceConfirmFragment(
                        key = INSTALL_IN_DEVICE,
                        isScanned = isScanned,
                        items = items,
                        title = title ?: ""
                    )
                } else {
                    openInstallInDeviceFragment(
                        key = INSTALL_IN_DEVICE,
                        items = items
                    )
                }
            }
            TRANSFER_TO_ENGINEER -> {
                if (isScanned) {
                    openTransferToEngineerConfirmFragment(
                        key = TRANSFER_TO_ENGINEER,
                        isScanned = isScanned,
                        items = items,
                        anotherEngineers = title ?: "",
                        quantity = quantity
                    )
                } else {
                    openTransferToEngineerFragment(
                        key = TRANSFER_TO_ENGINEER,
                        items = items
                    )
                }
            }
        }

    }

    fun openReturnToWarehouseFragment(
        key: String?,
        items: String?
    ) {
        val arguments = Bundle()
        arguments.putString(KEY, key)
        arguments.putString(ITEMS, items)
        val fragment =
            ReturnToWarehouseFragment()
        fragment.arguments = arguments
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .commit()
    }

    fun openReturnToWarehouseConfirmFragment(
        key: String?,
        isScanned: Boolean,
        title: String,
        items: String?,
        quantity: Int
    ) {
        val arguments = Bundle()
        arguments.putString(KEY, key)
        arguments.putBoolean(IS_SCANNED, isScanned)
        arguments.putString(TITLE, title)
        arguments.putString(ITEMS, items)
        arguments.putInt(QUANTITY, quantity)
        val fragment =
            ReturnToWarehouseConfirmFragment()
        fragment.arguments = arguments
        supportFragmentManager
            .beginTransaction()
            .addToBackStack(null)
            .replace(R.id.fragment_container, fragment)
            .commit()
    }

    fun openPickWarehouseFragment(
        key: String?,
        isScanned: Boolean,
        title: String
    ) {
        val arguments = Bundle()
        arguments.putString(KEY, key)
        arguments.putBoolean(IS_SCANNED, isScanned)
        arguments.putString(TITLE, title)
        val fragment =
            PickWarehouseFragment()
        fragment.arguments = arguments
        supportFragmentManager
            .beginTransaction()
            .addToBackStack(null)
            .replace(R.id.fragment_container, fragment)
            .commit()
    }

    fun openWarehouseFragment(
        key: String?,
        isScanned: Boolean,
        title: String
    ) {
        val arguments = Bundle()
        arguments.putString(KEY, key)
        arguments.putBoolean(IS_SCANNED, isScanned)
        arguments.putString(TITLE, title)
        val fragment =
            IncommingSkuFromWarehouseFragment()
        fragment.arguments = arguments
        supportFragmentManager
            .beginTransaction()
            .addToBackStack(null)
            .replace(R.id.fragment_container, fragment)
            .commit()
    }

    fun openPickEngineerFragment(
        key: String?,
        isScanned: Boolean,
        title: String
    ) {
        val arguments = Bundle()
        arguments.putString(KEY, key)
        arguments.putBoolean(IS_SCANNED, isScanned)
        arguments.putString(TITLE, title)
        val fragment =
            PickEngineerFragment()
        fragment.arguments = arguments
        supportFragmentManager
            .beginTransaction()
            .addToBackStack(null)
            .replace(R.id.fragment_container, fragment)
            .commit()
    }

    fun openIncommingSkuFromEngineerFragment(
        key: String?,
        isScanned: Boolean,
        title: String
    ) {
        val arguments = Bundle()
        arguments.putString(KEY, key)
        arguments.putBoolean(IS_SCANNED, isScanned)
        arguments.putString(TITLE, title)
        val fragment =
            IncommingSkuFromEngineerFragment()
        fragment.arguments = arguments
        supportFragmentManager
            .beginTransaction()
            .addToBackStack(null)
            .replace(R.id.fragment_container, fragment)
            .commit()
    }


    fun openFiltersFragment() {
        val fragment =
            FiltersFragment()
        supportFragmentManager
            .beginTransaction()
            .addToBackStack(null)
            .replace(R.id.fragment_container, fragment)
            .commit()
    }


    fun openInstallInDeviceFragment(
        key: String?,
        items: String?
    ) {
        val arguments = Bundle()
        arguments.putString(KEY, key)
        arguments.putString(ITEMS, items)
        val fragment =
            InstallInDeviceFragment()
        fragment.arguments = arguments
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .commit()
    }

    fun openInstallInDeviceConfirmFragment(
        key: String?,
        isScanned: Boolean,
        title: String,
        items: String?
    ) {
        val arguments = Bundle()
        arguments.putString(KEY, key)
        arguments.putBoolean(IS_SCANNED, isScanned)
        arguments.putString(TITLE, title)
        arguments.putString(ITEMS, items)
        val fragment =
            InstallInDeviceConfirmFragment()
        fragment.arguments = arguments
        supportFragmentManager
            .beginTransaction()
            .addToBackStack(null)
            .replace(R.id.fragment_container, fragment)
            .commit()
    }

    fun openTransferToEngineerFragment(
        key: String?,
        items: String?
    ) {
        val arguments = Bundle()
        arguments.putString(KEY, key)
        arguments.putString(ITEMS, items)
        val fragment =
            TransferToEngineerFragment()
        fragment.arguments = arguments
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .commit()
    }

    fun openTransferToEngineerConfirmFragment(
        key: String?,
        isScanned: Boolean,
        anotherEngineers: String = "",
        items: String?,
        quantity: Int
    ) {
        val arguments = Bundle()
        arguments.putString(KEY, key)
        arguments.putBoolean(IS_SCANNED, isScanned)
        arguments.putString(TITLE, anotherEngineers)
        arguments.putString(ITEMS, items)
        arguments.putInt(QUANTITY, quantity)
        val fragment =
            TransferToEngineerConfirmFragment()
        fragment.arguments = arguments
        supportFragmentManager
            .beginTransaction()
            .addToBackStack(null)
            .replace(R.id.fragment_container, fragment)
            .commit()
    }

    fun openTransferFromEngineerFragment(
        key: String?,
        isScanned: Boolean,
        anotherEngineers: String = "",
        items: String?
    ) {
        val arguments = Bundle()
        arguments.putString(KEY, key)
        arguments.putBoolean(IS_SCANNED, isScanned)
        arguments.putString(TITLE, anotherEngineers)
        arguments.putString(ITEMS, items)
        val fragment =
            TransferFromEngineerFragment()
        fragment.arguments = arguments
        supportFragmentManager
            .beginTransaction()
            .addToBackStack(null)
            .replace(R.id.fragment_container, fragment)
            .commit()
    }

    fun openReturnFromWarehouseFragment(
        key: String?,
        isScanned: Boolean,
        anotherWarehouses: String = "",
        items: String?
    ) {
        val arguments = Bundle()
        arguments.putString(KEY, key)
        arguments.putBoolean(IS_SCANNED, isScanned)
        arguments.putString(TITLE, anotherWarehouses)
        arguments.putString(ITEMS, items)
        val fragment =
            ReturnFromWarehouseFragment()
        fragment.arguments = arguments
        supportFragmentManager
            .beginTransaction()
            .addToBackStack(null)
            .replace(R.id.fragment_container, fragment)
            .commit()
    }


    companion object {
        const val RETURN_TO_WAREHOUSE = "RETURN_TO_WAREHOUSE"
        const val WAREHOUSE = "WAREHOUSE"
        const val ENGINEER = "ENGINEER"
        const val TRANSFER_TO_ENGINEER = "TRANSFER_TO_ENGINEER"
        const val INSTALL_IN_DEVICE = "INSTALL_IN_DEVICE"

        const val KEY = "KEY"
        const val QUANTITY = "QUANTITY"
        const val VENDOR_CODE = "VENDOR_CODE"
        const val IS_SCANNED = "IS_SCANNED"
        const val TITLE = "TITLE"
        const val ITEMS = "ITEMS"

        const val CAMERA_BACK = "0"

        fun open(
            context: Context,
            key: String,
            isScanned: Boolean,
            title: String,
            items: String = ""
        ) {
            val intent = Intent(context, ActionWithItemActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            intent.putExtra(KEY, key)
            intent.putExtra(IS_SCANNED, isScanned)
            intent.putExtra(ITEMS, items)
            intent.putExtra(TITLE, title)
            context.startActivity(intent)
        }
    }

    fun showProgressBar() {
        progress_bar.visibility = View.VISIBLE
    }
    fun hideProgressBar() {
        progress_bar.visibility = View.GONE
    }

}