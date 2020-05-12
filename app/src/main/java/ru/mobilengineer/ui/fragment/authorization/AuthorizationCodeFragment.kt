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
import ru.mobilengineer.ui.fragment.BaseFragment
import java.util.concurrent.Executor
import javax.inject.Inject


class AuthorizationCodeFragment : BaseFragment() {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    @Inject
    lateinit var preferencesManager: PreferencesManager
    private var code = ""
    private var isCodeCrated = false

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
            if (!preferencesManager.isAuthCompleted)
                if (preferencesManager.passcode != null) {
                    preferencesManager.passcode = null
                    text_enter_cod.text = getString(R.string.pick_launch_cod)
                    code_view.clearCode()
                    code = ""
                } else activity?.supportFragmentManager?.popBackStack()
            else (activity as AuthorizationActivity).finish()
        }
    }

    private fun showScreen(isAuthCompleted: Boolean) {
        if (isAuthCompleted) {
            text_enter_cod.text = getString(R.string.enter_cod)

            if (isHasBiometricManager())
                touch_id.visibility = View.VISIBLE

            touch_id.setOnClickListener {
                if (preferencesManager.isTouchIdAdded) biometricPrompt.authenticate(promptInfo)
                else (activity as AuthorizationActivity).openTouchFragment()
            }
        } else {
            touch_id.visibility = View.GONE
            text_enter_cod.text = getString(R.string.pick_launch_cod)
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
            if (text_enter_cod.text.toString() == getString(R.string.invalid_entered_cod))
                text_enter_cod.text = getString(R.string.enter_cod)
            if (v is TextView) {
                val string = v.text.toString()
                code += string
                if (string.length != 1) {
                    return@OnClickListener
                }
                code_view.input(string)
                if (code.length == PASS_CODE_LENGTH) {
                    compareOrSaveCode()
                }
            }
        }

    private fun isNotEqualOrNull(passcode: String): Boolean {
        return if (preferencesManager.passcode == null) true
        else preferencesManager.passcode != passcode
    }

    private fun openConfirmViews() {
        if (preferencesManager.passcode == null) {
            preferencesManager.passcode = code
        } else Toast.makeText(context, "Неверно введен код", Toast.LENGTH_SHORT).show()
        code = ""
        code_view.clearCode()
        text_enter_cod.text = getString(R.string.replay_pick_launch_cod)
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

    private fun compareOrSaveCode() {
        if (!preferencesManager.isAuthCompleted) {
            if (isNotEqualOrNull(code)) {
//            if (isNotEqualOrNull(code_view.getCode())) {
                openConfirmViews()
            } else {
                preferencesManager.passcode = code
                if (isHasBiometricManager()) {
                    code = ""
                    code_view.clearCode()
                    (activity as AuthorizationActivity).openTouchFragment()
                } else (activity as AuthorizationActivity).openCodeFragment()
            }
        } else if (isNotEqualOrNull(code)) {
            code = ""
            text_enter_cod.text = getString(R.string.invalid_entered_cod)
            code_view.clearCode()
            code_view.invalidCode()
        } else {
            code = ""
            code_view.clearCode()
            text_enter_cod.text = getString(R.string.enter_cod)
            Toast.makeText(context, "Done!", Toast.LENGTH_SHORT).show()
        }

    }

    private fun isHasBiometricManager(): Boolean {
        val biometricManager = BiometricManager.from(requireContext())
        return (biometricManager.canAuthenticate() == BiometricManager.BIOMETRIC_SUCCESS)
    }

    override fun onDestroy() {
        super.onDestroy()
        if (!preferencesManager.isAuthCompleted)
            if (!isCodeCrated) {
                preferencesManager.passcode = null
            }
    }

    companion object {
        const val PHONE = "PHONE"
        const val PASS_CODE_LENGTH = 4
    }
}