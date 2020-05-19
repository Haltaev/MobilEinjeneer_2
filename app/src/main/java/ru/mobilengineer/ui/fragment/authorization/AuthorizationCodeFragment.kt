package ru.mobilengineer.ui.fragment.authorization

import android.os.Bundle
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.activity.addCallback
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import kotlinx.android.synthetic.main.fragment_add_launch_cod.*
import ru.mobilengineer.App
import ru.mobilengineer.R
import ru.mobilengineer.common.PreferencesManager
import ru.mobilengineer.ui.CodeView
import ru.mobilengineer.ui.activity.AuthorizationActivity
import ru.mobilengineer.ui.activity.HomeActivity
import ru.mobilengineer.ui.activity.MyProfileActivity
import ru.mobilengineer.ui.fragment.BaseFragment
import java.util.concurrent.Executor
import javax.inject.Inject


class AuthorizationCodeFragment : BaseFragment() {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    @Inject
    lateinit var preferencesManager: PreferencesManager
    private var mCode = ""
    private var localPMPasscode: String? = null
    private var savedCode: String? = null

    private lateinit var executor: Executor
    private lateinit var biometricPrompt: BiometricPrompt
    private lateinit var promptInfo: BiometricPrompt.PromptInfo

    override fun getLayoutId(): Int {
        return R.layout.fragment_add_launch_cod
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        App.getComponent().inject(this)
//        authorizationViewModel = injectViewModel(viewModelFactory)
        localPMPasscode = preferencesManager.passcode
        includeBiometricPromt()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        code_view.setListener(mCodeListener)

        num_button_0.setOnClickListener(mOnKeyClickListener)
        num_button_1.setOnClickListener(mOnKeyClickListener)
        num_button_2.setOnClickListener(mOnKeyClickListener)
        num_button_3.setOnClickListener(mOnKeyClickListener)
        num_button_4.setOnClickListener(mOnKeyClickListener)
        num_button_5.setOnClickListener(mOnKeyClickListener)
        num_button_6.setOnClickListener(mOnKeyClickListener)
        num_button_7.setOnClickListener(mOnKeyClickListener)
        num_button_8.setOnClickListener(mOnKeyClickListener)
        num_button_9.setOnClickListener(mOnKeyClickListener)

        showScreen(preferencesManager.isAuthCompleted)

        requireActivity().onBackPressedDispatcher.addCallback(this) {
            if (preferencesManager.isAuthCompleted && preferencesManager.isPasscodeChanging) {
                activity?.supportFragmentManager?.popBackStack()
            }
            if (preferencesManager.isAuthCompleted && !preferencesManager.isPasscodeChanging) {
                (activity as AuthorizationActivity).finish()
            }
            if (!preferencesManager.isAuthCompleted) {
                if (savedCode.isNullOrEmpty()) {
                    activity?.supportFragmentManager?.popBackStack()
                } else {
                    showCreatePasscode()
                    savedCode = null
                }
            }
        }
    }

    private fun showScreen(isAuthCompleted: Boolean) {
        if (isAuthCompleted) {
            showEnterPasscode()
        } else {
            showCreatePasscode()
            savedCode = null
        }
        touch_id.setOnClickListener {
            if (preferencesManager.isTouchIdAdded) biometricPrompt.authenticate(promptInfo)
            else (activity as AuthorizationActivity).openTouchFragment()
        }
        log_out.setOnClickListener {
            preferencesManager.isAuthCompleted = false
            preferencesManager.isTouchIdAdded = false
            preferencesManager.passcode = null
            AuthorizationActivity.open(requireContext())
            (activity as AuthorizationActivity).finish()
        }
    }

    private val mOnKeyClickListener =
        View.OnClickListener { v ->
            if (text_head_cod.text.toString() == getString(R.string.invalid_entered_cod))
                text_head_cod.text = getString(R.string.enter_cod)
            if (v is TextView) {
                val string = v.text.toString()
                mCode += string
                if (string.length != 1) {
                    return@OnClickListener
                }
                code_view.input(string)
                if (mCode.length == PASS_CODE_LENGTH) {
                    compareCode(mCode)
                }
            }
        }

    private fun clearCode() {
        mCode = ""
        code_view.clearCode()
    }

    private fun showCreatePasscode() {
        clearCode()
        text_head_cod.text = getString(R.string.pick_launch_cod)
        touch_id.visibility = View.GONE
    }

    private fun showRepeatPasscode() {
        clearCode()
        text_head_cod.text = getString(R.string.repeat_launch_cod)
        touch_id.visibility = View.GONE
    }

    private fun showEnterPasscode() {
        clearCode()
        text_head_cod.text = getString(R.string.enter_cod)
        touch_id.visibility = View.VISIBLE
    }

    private fun showInvalidPasscode() {
        clearCode()
        code_view.invalidCode()
        text_head_cod.text = getString(R.string.invalid_entered_cod)
        touch_id.visibility = View.VISIBLE
    }

    private fun compareCode(code: String) {
        if (localPMPasscode == null) { //create passcode
            if (savedCode == null) { // if first input
                savedCode = code
                showRepeatPasscode()
                return
            } else { // if second input
                if (savedCode == code) {
                    saveCode(code)
                    if (preferencesManager.isPasscodeChanging) {
                        preferencesManager.isPasscodeChanging = false
                        activity?.supportFragmentManager?.popBackStack()
                    } else {
                        if (isHasBiometricManager()) {
                            clearCode()
                            (activity as AuthorizationActivity).openTouchFragment()
                        } else {
                            (activity as AuthorizationActivity).openCodeFragment()
                        }
                    }
//                    // open fragment_add_touch_id
                } else {
                    Toast.makeText(context, "invalid passcode", Toast.LENGTH_SHORT).show()
                    showRepeatPasscode()
                }
            }
        }

        if (localPMPasscode != null && !preferencesManager.isPasscodeChanging) { // check passcode
            if (preferencesManager.passcode == code) {
                clearCode()
                HomeActivity.open(requireContext())
                (activity as AuthorizationActivity).finish()
                // open fragment_my_profile
            } else {
                showInvalidPasscode()
            }
        }

        if (localPMPasscode != null && preferencesManager.isPasscodeChanging) { // change passcode
            if (preferencesManager.passcode == code) {
                showCreatePasscode()
                localPMPasscode = null
            } else {
                showInvalidPasscode()
            }
        }
    }

    private fun saveCode(code: String) {
        preferencesManager.passcode = code
    }

    private fun isHasBiometricManager(): Boolean {
        val biometricManager = BiometricManager.from(requireContext())
        return (biometricManager.canAuthenticate() == BiometricManager.BIOMETRIC_SUCCESS)
    }

    private fun includeBiometricPromt() {
        executor = ContextCompat.getMainExecutor(context)
        biometricPrompt = BiometricPrompt(this, executor,
            object : BiometricPrompt.AuthenticationCallback() {
                override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                    super.onAuthenticationError(errorCode, errString)
                }

                override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                    super.onAuthenticationSucceeded(result)
                    Toast.makeText(context, "Authentication succeeded!", Toast.LENGTH_SHORT).show()
                }

                override fun onAuthenticationFailed() {
                    super.onAuthenticationFailed()
                }
            })

        promptInfo = BiometricPrompt.PromptInfo.Builder()
            .setTitle("Biometric login for my app")
            .setSubtitle("Log in using your biometric credential")
            .setNegativeButtonText("Use account password")
            .build()
    }

    val mCodeListener = object : CodeView.OnPFCodeListener {
        override fun onCodeCompleted(code: String) {
            if (!preferencesManager.isAuthCompleted) {
                return
            }
        }

        override fun onCodeNotCompleted(code: String) {
            if (!preferencesManager.isAuthCompleted) {
                return
            }
        }
    }

    companion object {
        const val PHONE = "PHONE"
        const val PASS_CODE_LENGTH = 4
    }
}