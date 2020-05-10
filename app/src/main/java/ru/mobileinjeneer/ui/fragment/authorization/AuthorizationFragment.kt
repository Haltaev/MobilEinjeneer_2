package ru.mobileinjeneer.ui.fragment.authorization

import android.os.Bundle
import android.view.View
import androidx.lifecycle.ViewModelProvider
import kotlinx.android.synthetic.main.fragment_auth.*
import ru.mobileinjeneer.App
import ru.mobileinjeneer.R
import ru.mobileinjeneer.common.PreferencesManager
import ru.mobileinjeneer.ui.activity.AuthorizationActivity
import ru.mobileinjeneer.ui.fragment.BaseFragment
import javax.inject.Inject

class AuthorizationFragment : BaseFragment() {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    @Inject
    lateinit var preferencesManager: PreferencesManager

    override fun getLayoutId(): Int {
        return R.layout.fragment_auth
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        App.getComponent().inject(this)
//        authorizationViewModel = injectViewModel(viewModelFactory)

        if (preferencesManager.isAuthCompleted) {
            (activity as AuthorizationActivity).openCodeFragment()
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        auth_ok.setOnClickListener {
            if (et_login.text.toString() == "test" && et_password.text.toString() == "123456") {
                context.let { ctx ->
                    if (ctx != null) {
                        (activity as AuthorizationActivity).openCodeFragment()
                    }
                }
            } else {
                auth_error_massage.visibility = View.VISIBLE
            }
        }
    }

    companion object {
        const val REQUEST_TAKE_PHOTO = 1
        const val DATA = "data"
    }
}