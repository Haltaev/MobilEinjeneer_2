package ru.mobilengineer.ui.fragment.authorization

import android.content.Context
import android.os.Bundle
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.TextView.OnEditorActionListener
import androidx.lifecycle.ViewModelProvider
import kotlinx.android.synthetic.main.fragment_auth.*
import ru.mobilengineer.App
import ru.mobilengineer.R
import ru.mobilengineer.common.PreferencesManager
import ru.mobilengineer.ui.activity.AuthorizationActivity
import ru.mobilengineer.ui.fragment.BaseFragment
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
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        auth_ok.setOnClickListener {
            checkUserInfo()
        }

        et_password.setOnEditorActionListener(OnEditorActionListener { v, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                val imm = activity?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                imm.hideSoftInputFromWindow(requireView().windowToken, 0)

                checkUserInfo()

                return@OnEditorActionListener true
            }
            false
        })
    }

    private fun checkUserInfo(){
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

    companion object {
        const val REQUEST_TAKE_PHOTO = 1
        const val DATA = "data"
    }
}