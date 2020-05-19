package ru.mobilengineer.ui.fragment.profile

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ObjectAnimator
import android.graphics.BitmapFactory
import android.os.Bundle
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.animation.DecelerateInterpolator
import androidx.lifecycle.ViewModelProvider
import kotlinx.android.synthetic.main.fragment_my_profile.*
import ru.mobilengineer.App
import ru.mobilengineer.R
import ru.mobilengineer.common.MediaListener
import ru.mobilengineer.common.PreferencesManager
import ru.mobilengineer.ui.activity.HomeActivity
import ru.mobilengineer.ui.activity.MyProfileActivity
import ru.mobilengineer.ui.fragment.BaseFragment
import java.io.File
import javax.inject.Inject


class MyProfileFragment : BaseFragment() {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    @Inject
    lateinit var preferencesManager: PreferencesManager

    override fun getLayoutId(): Int {
        return R.layout.fragment_my_profile
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        App.getComponent().inject(this)
//        authorizationViewModel = injectViewModel(viewModelFactory)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        barcode_button.setOnClickListener {
            turnOverCardView(card_view_profile, card_view_barcode)
        }

        card_view_barcode.setOnClickListener {
            turnOverCardView(card_view_barcode, card_view_profile)
        }

        settings.setOnClickListener {
            (activity as MyProfileActivity).openProfileSettingsFragment()
        }

        my_frame.setOnClickListener {
            HomeActivity.open(requireContext())
            (activity as MyProfileActivity).finish()
        }

        if(preferencesManager.profileImagePath != null){
            setProfileImage(preferencesManager.profileImagePath!!)
        }
    }

    private fun turnOverCardView(frontSide: View, backside: View){
        val oa1 = ObjectAnimator.ofFloat(frontSide, "scaleX", 1f, 0f)
        val oa2 = ObjectAnimator.ofFloat(backside, "scaleX", 0f, 1f)
        oa1.interpolator = DecelerateInterpolator()
        oa2.interpolator = AccelerateDecelerateInterpolator()
        oa1.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator) {
                super.onAnimationEnd(animation)
                frontSide.visibility = View.GONE
                backside.visibility = View.VISIBLE
                oa2.start()
            }
        })
        oa1.start()
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
}
