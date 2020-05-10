package ru.mobileinjeneer.ui.fragment.authorization

import android.os.Bundle
import android.view.View
import androidx.lifecycle.ViewModelProvider
import kotlinx.android.synthetic.main.fragment_add_touch.*
import ru.mobileinjeneer.App
import ru.mobileinjeneer.R
import ru.mobileinjeneer.common.PreferencesManager
import ru.mobileinjeneer.ui.activity.AuthorizationActivity
import ru.mobileinjeneer.ui.fragment.BaseFragment
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
            (activity as AuthorizationActivity).openCodeFragment()

        }
        button_no.setOnClickListener {
            preferencesManager.isTouchIdAdded = false
            (activity as AuthorizationActivity).openCodeFragment()
        }

        log_out.setOnClickListener {
            preferencesManager.isAuthCompleted = false
            preferencesManager.isTouchIdAdded = false
            (activity as AuthorizationActivity).openAuthFragment()
        }

    }

}