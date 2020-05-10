package ru.mobileinjeneer.ui.fragment.authorization

import android.os.Bundle
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import kotlinx.android.synthetic.main.fragment_add_launch_cod.*
import ru.mobileinjeneer.App
import ru.mobileinjeneer.R
import ru.mobileinjeneer.common.PreferencesManager
import ru.mobileinjeneer.ui.CodeView
import ru.mobileinjeneer.ui.activity.AuthorizationActivity
import ru.mobileinjeneer.ui.fragment.BaseFragment
import java.util.concurrent.Executor
import javax.inject.Inject


class AuthorizationCodeFragment : BaseFragment() {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    @Inject
    lateinit var preferencesManager: PreferencesManager
    private var code = ""
    private var mCode = ""

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
    }

    private fun showScreen(isAuthCompleted: Boolean) {
        val biometricManager = BiometricManager.from(requireContext())

        log_out.setOnClickListener {
            preferencesManager.isAuthCompleted = false
            preferencesManager.isTouchIdAdded = false
            (activity as AuthorizationActivity).openAuthFragment()
        }
        if (isAuthCompleted) {
            text_enter_cod.text = getString(R.string.enter_cod)
            if (biometricManager.canAuthenticate() == BiometricManager.BIOMETRIC_SUCCESS)
                touch_id.visibility = View.VISIBLE
            if (preferencesManager.isTouchIdAdded) {
                touch_id.setOnClickListener {
                    biometricPrompt.authenticate(promptInfo)
                }
            } else {
                touch_id.setOnClickListener {
                    (activity as AuthorizationActivity).openTouchFragment()
                }
            }
        } else {
            touch_id.visibility = View.GONE
            text_enter_cod.text = getString(R.string.pick_launch_cod)

        }
    }

    private val mOnKeyClickListener =
        View.OnClickListener { v ->
            if (v is TextView) {
                val string = v.text.toString()
                code += v.text.toString()
                if (string.length != 1) {
                    return@OnClickListener
                }
                code_view.input(string)
                if (code.length == 4) {
                    if (!preferencesManager.isAuthCompleted) {
                        if (isNotEqualOrNull(code)) {
                            openConfirmViews()
                        } else {
                            preferencesManager.isAuthCompleted = true
                            val biometricManager = BiometricManager.from(requireContext())
                            if (biometricManager.canAuthenticate() == BiometricManager.BIOMETRIC_SUCCESS) {
                                (activity as AuthorizationActivity).openTouchFragment()
                            } else (activity as AuthorizationActivity).openCodeFragment()
                        }
                    } else if (isNotEqualOrNull(code)) {
                        code = ""
                        text_enter_cod.text = getString(R.string.invalid_entered_cod)
                        code_view.invalidCode()
//                        code_view.clearCode()
                    } else {
                        code = ""
                        text_enter_cod.text = getString(R.string.enter_cod)
                        code_view.clearCode()
                        Toast.makeText(context, "Done!", Toast.LENGTH_SHORT).show()

                    }
                }
            }
        }

    private fun isNotEqualOrNull(passcode: String): Boolean {
        return if (preferencesManager.passcode == null) true
        else preferencesManager.passcode != passcode
    }

    private fun openConfirmViews() {
        if (preferencesManager.passcode == null)
            preferencesManager.passcode = code
        code = ""
        text_enter_cod.text = getString(R.string.replay_pick_launch_cod)
        code_view.clearCode()

    }

    val mCodeListener = object : CodeView.OnPFCodeListener {
        override fun onCodeCompleted(code: String) {
            if (!preferencesManager.isAuthCompleted) {
                mCode = code
                return
            }
            mCode = code
        }

        override fun onCodeNotCompleted(code: String) {
            if (!preferencesManager.isAuthCompleted) {
                return
            }
        }

    }

    override fun onDestroy() {
        super.onDestroy()
    }

    companion object {
        const val PHONE = "PHONE"
    }
}