package ru.mobilengineer.ui.fragment.profile

import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Bundle
import android.view.View
import android.widget.CompoundButton
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.ViewModelProvider
import kotlinx.android.synthetic.main.fragment_profile_settings.*
import ru.mobilengineer.App
import ru.mobilengineer.R
import ru.mobilengineer.common.FileUploadResponse
import ru.mobilengineer.common.MediaListener
import ru.mobilengineer.common.PreferencesManager
import ru.mobilengineer.common.TakeOrChoosePhotoActivity
import ru.mobilengineer.ui.activity.AuthorizationActivity
import ru.mobilengineer.ui.activity.MyProfileActivity
import ru.mobilengineer.ui.activity.MyProfileSettingsActivity
import ru.mobilengineer.ui.activity.ScannerActivity
import ru.mobilengineer.ui.fragment.BaseFragment
import java.io.File
import javax.inject.Inject


class ProfileSettingsFragment : BaseFragment(), MediaListener {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    @Inject
    lateinit var preferencesManager: PreferencesManager


    private var photoUploadInfo: FileUploadResponse? = null
    lateinit var photoHelper: TakeOrChoosePhotoActivity

    override fun getLayoutId(): Int {
        return R.layout.fragment_profile_settings
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        App.getComponent().inject(this)
//        authorizationViewModel = injectViewModel(viewModelFactory)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        photoHelper = TakeOrChoosePhotoActivity(this)

        preferencesManager.apply {
            name_text.text = fullName
        }

        log_out.setOnClickListener {
            preferencesManager.apply {
                isAuthCompleted = false
                isTouchIdAdded = false
                isPasscodeChanging = false
                passcode = null
            }

            AuthorizationActivity.open(requireContext())
            activity?.finishAffinity()
        }

        back_button.setOnClickListener {
            activity?.finish()
        }

        ll_change_passcode.setOnClickListener {
            preferencesManager.isPasscodeChanging = true
//            AuthorizationActivity.open(requireContext())
            (activity as MyProfileSettingsActivity).openAuthorizationCodeFragment()
        }

        cl_change_name.setOnClickListener {
            (activity as MyProfileSettingsActivity).openEditProfileNameFragment()
        }
        add_image.setOnClickListener {
            context?.let {
                selectImage(it)
            }
        }

        if(preferencesManager.profileImagePath != null){
            setProfileImage(preferencesManager.profileImagePath!!)
        }

        ll_biometric.setOnClickListener {
            switch_touch_id.isChecked = !switch_touch_id.isChecked
        }

        switch_touch_id.isChecked = preferencesManager.isTouchIdAdded

        switch_touch_id.setOnCheckedChangeListener(CompoundButton.OnCheckedChangeListener { buttonView, isChecked ->
            preferencesManager.isTouchIdAdded = isChecked
        })

    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        photoHelper.onRequestPermissionsResult(this, requestCode, permissions, grantResults)
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        photoHelper.onActivityResult(this, requestCode, resultCode, data)
        profile_image.visibility = View.VISIBLE
        profile_image_empty.visibility = View.GONE
    }

    private fun selectImage(context: Context) {
        val options = arrayOf<CharSequence>("Выбрать снимок", "Сделать снимок", "Удалить")
        val builder = AlertDialog.Builder(context)
        builder.setItems(options) { dialog, item ->
            when {
                options[item] == "Выбрать снимок" -> {
                    photoHelper.onChoosePhotoBtnClick(this)
                }
                options[item] == "Сделать снимок" -> {
                    photoHelper.onTakePhotoBtnClick(this)
                }
                options[item] == "Удалить" -> {
                    photoUploadInfo = null
                    preferencesManager.profileImagePath = null
                    dialog.dismiss()
                    profile_image.visibility = View.GONE
                    profile_image_empty.visibility = View.VISIBLE
                }
            }
        }
        builder.show()
    }

    private fun getFile(photoPath: String): File? {
        return File(photoPath)
    }

    private fun setProfileImage(photoPath: String){
        val photo = getFile(photoPath)

        photo?.let {
            val myBitmap = BitmapFactory.decodeFile(it.absolutePath)
            profile_image.setImageBitmap(myBitmap)
        } ?: kotlin.run {
            showErrorToast(getString(R.string.error_photo_processing))
        }
    }

    override fun showLoading() {
        TODO("Not yet implemented")
    }

    override fun hideLoading() {
        TODO("Not yet implemented")
    }

    override fun success(photoPath: String) {
        photoUploadInfo = null
        preferencesManager.profileImagePath = photoPath
        setProfileImage(photoPath)
    }

}
