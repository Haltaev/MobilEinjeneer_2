package ru.mobilengineer.ui.fragment.profile

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ObjectAnimator
import android.graphics.BitmapFactory
import android.os.Bundle
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.animation.DecelerateInterpolator
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import kotlinx.android.synthetic.main.fragment_my_profile.*
import ru.mobilengineer.App
import ru.mobilengineer.R
import ru.mobilengineer.common.PreferencesManager
import ru.mobilengineer.common.injectViewModel
import ru.mobilengineer.ui.activity.*
import ru.mobilengineer.ui.fragment.BaseFragment
import ru.mobilengineer.viewmodel.MyProfileViewModel
import java.io.File
import javax.inject.Inject


class MyProfileFragment : BaseFragment() {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    @Inject
    lateinit var preferencesManager: PreferencesManager

    lateinit var warehousesViewModel: MyProfileViewModel

    override fun getLayoutId(): Int {
        return R.layout.fragment_my_profile
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        App.getComponent().inject(this)
        warehousesViewModel = injectViewModel(viewModelFactory)
    }


    override fun onResume() {
        super.onResume()
        warehousesViewModel.apply {
            getSelfInfo()
            getMyStock()
            getAvailableFromUsers()
            getAvailableFromWarehouses()
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        (activity as MyProfileActivity).showProgressBar()
        observeViewModel(warehousesViewModel)

        if (!preferencesManager.fullName.isNullOrEmpty()) profile_name.text =
            preferencesManager.fullName

        barcode_button.setOnClickListener {
            turnOverCardView(card_view_profile, card_view_barcode)
        }

        card_view_barcode.setOnClickListener {
            turnOverCardView(card_view_barcode, card_view_profile)
        }

        settings.setOnClickListener {
            MyProfileSettingsActivity.open(requireContext())
        }

        my_warehouse.setOnClickListener {
            MyStockActivity.open(requireContext())
//            (activity as MyProfileActivity).finish()
        }

        if (preferencesManager.profileImagePath != null) {
            setProfileImage(preferencesManager.profileImagePath!!)
        }
        card_view_mini_warehouse.setOnClickListener {
            ScannerActivity.open(
                requireContext(),
                key = WAREHOUSE,
                items = ""
            )
        }

        card_view_another_engineer.setOnClickListener {
            AvailableWarehouseActivity.open(requireContext(), FROM_ENGINEER)
        }

        card_view_main_warehouse.setOnClickListener {
            AvailableWarehouseActivity.open(requireContext(), FROM_WAREHOUSE)
        }
    }

    private fun observeViewModel(viewModel: MyProfileViewModel) {
        viewModel.apply {
            selfInfoLiveData.observe(viewLifecycleOwner, Observer { resp ->
                (activity as MyProfileActivity).hideProgressBar()
                preferencesManager.apply {
                    fullName = resp.name
                    warehouseId = resp.warehouseId.toString()
                    profile_name.text = fullName
                }
            })
            myWarehouseLiveData.observe(viewLifecycleOwner, Observer { resp ->
                (activity as MyProfileActivity).hideProgressBar()
                if (resp == 401) {
                    openAuthorizationActivity()
                } else {
                    count_warehouse.text = resp.toString()
                }
            })
            availableFromWarehousesLiveData.observe(viewLifecycleOwner, Observer { resp ->
                (activity as MyProfileActivity).hideProgressBar()
                if (resp == 401) {
                    openAuthorizationActivity()
                } else {
                    count_main_warehouse.text = resp.toString()
                }
            })
            availableFromUsersLiveData.observe(viewLifecycleOwner, Observer { resp ->
                (activity as MyProfileActivity).hideProgressBar()
                if (resp == 401) {
                    openAuthorizationActivity()
                } else {
                    count_from_engineers.text = resp.toString()
                }
            })
        }
    }

    private fun turnOverCardView(frontSide: View, backside: View) {
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

    private fun openAuthorizationActivity(){
        preferencesManager.apply {
            isAuthCompleted = false
            isTouchIdAdded = false
            isPasscodeChanging = false
            passcode = null
        }
        AuthorizationActivity.open(requireContext())
        activity?.finishAffinity()
    }

    private fun getFile(photoPath: String): File? {
        return File(photoPath)
    }

    private fun setProfileImage(photoPath: String) {
        val photo = getFile(photoPath)

        photo?.let {
            val myBitmap = BitmapFactory.decodeFile(it.absolutePath)
            profile_image.setImageBitmap(myBitmap)
        } ?: kotlin.run {
            showErrorToast(getString(R.string.error_photo_processing))
        }
    }

    companion object{
        const val WAREHOUSE = "WAREHOUSE"
        const val ENGINEER = "ENGINEER"
        const val FROM_ENGINEER = "users"
        const val FROM_WAREHOUSE = "warehouses"
    }
}
