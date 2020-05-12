package ru.mobilengineer.ui.fragment.authorization

import android.os.Bundle
import android.view.View
import androidx.activity.addCallback
import androidx.lifecycle.ViewModelProvider
import kotlinx.android.synthetic.main.fragment_add_touch.*
import ru.mobilengineer.App
import ru.mobilengineer.R
import ru.mobilengineer.common.PreferencesManager
import ru.mobilengineer.ui.activity.AuthorizationActivity
import ru.mobilengineer.ui.fragment.BaseFragment
import javax.inject.Inject

class AddTouchIdFragment : BaseFragment() {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    @Inject
    lateinit var preferencesManager: PreferencesManager

    override fun getLayoutId(): Int {
        return R.layout.fragment_add_touch
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        App.getComponent().inject(this)
//        authorizationViewModel = injectViewModel(viewModelFactory)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        button_yes.setOnClickListener {
            preferencesManager.isTouchIdAdded = true
            preferencesManager.isAuthCompleted = true
            (activity as AuthorizationActivity).openCodeFragmentWithoutBackStack()

        }
        button_no.setOnClickListener {
            preferencesManager.isTouchIdAdded = false
            preferencesManager.isAuthCompleted = true
            (activity as AuthorizationActivity).openCodeFragmentWithoutBackStack()
        }

        log_out.setOnClickListener {
            preferencesManager.isAuthCompleted = false
            preferencesManager.isTouchIdAdded = false
            preferencesManager.passcode = null
            AuthorizationActivity.open(requireContext())
            (activity as AuthorizationActivity).finish()
        }

        requireActivity().onBackPressedDispatcher.addCallback(this) {
            if (!preferencesManager.isAuthCompleted)
                preferencesManager.passcode = null
            activity?.supportFragmentManager?.popBackStack()

        }

    }

}