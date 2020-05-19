package ru.mobilengineer.ui.fragment.profile

import android.os.Bundle
import android.view.View
import androidx.lifecycle.ViewModelProvider
import kotlinx.android.synthetic.main.fragment_edit_profile_name.*
import ru.mobilengineer.App
import ru.mobilengineer.R
import ru.mobilengineer.common.PreferencesManager
import ru.mobilengineer.ui.fragment.BaseFragment
import javax.inject.Inject

class EditProfileNameFragment : BaseFragment() {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    @Inject
    lateinit var preferencesManager: PreferencesManager

    override fun getLayoutId(): Int {
        return R.layout.fragment_edit_profile_name
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        App.getComponent().inject(this)
//        authorizationViewModel = injectViewModel(viewModelFactory)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        first_name.hint = preferencesManager.firstName.toString()
        last_name.hint = preferencesManager.lastName.toString()
        patronymic.hint = preferencesManager.patronymic.toString()

        button_back.setOnClickListener {
            activity?.supportFragmentManager?.popBackStack()
        }

        button_save.setOnClickListener {
            if(first_name.text.toString() != "") {
                preferencesManager.firstName = first_name.text.toString()
            }

            if(patronymic.text.toString() != "") {
                preferencesManager.patronymic = patronymic.text.toString()
            }

            if(last_name.text.toString() != "") {
                preferencesManager.lastName= last_name.text.toString()
            }

            activity?.supportFragmentManager?.popBackStack()
        }
    }

}
