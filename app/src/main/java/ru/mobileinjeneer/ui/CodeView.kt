package ru.mobileinjeneer.ui

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.annotation.Nullable
import androidx.core.content.ContextCompat
import ru.mobileinjeneer.R

class CodeView : LinearLayout {
    private var mCodeViews: ArrayList<ImageView> = arrayListOf()
    var code = ""
    private var mCodeLength = DEFAULT_CODE_LENGTH
    private var mListener: OnPFCodeListener? = null

    constructor(context: Context) : super(context) {
        init()
    }

    constructor(context: Context, @Nullable attrs: AttributeSet) : super(context, attrs) {
        init()
    }

    constructor(context: Context, @Nullable attrs: AttributeSet, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        init()
    }

    private fun init() {
        inflate(context, R.layout.view_code_pf_lockscreen, this)
        setUpCodeViews()
    }

    fun setCodeLength(codeLength: Int) {
        mCodeLength = codeLength
        setUpCodeViews()
    }

    private fun setUpCodeViews() {
        removeAllViews()
        mCodeViews = arrayListOf()
        code = ""
        for (i in 0 until mCodeLength) {
            val inflater = context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            val view = inflater.inflate(R.layout.view_code_checkbox, null) as ImageView
            val layoutParams = LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT
            )
            val margin = resources.getDimensionPixelSize(R.dimen.code_margin)
            layoutParams.setMargins(margin, 0, margin, 0)
            view.layoutParams = layoutParams
            view.isEnabled = false
            addView(view)
            mCodeViews.add(view)
        }
        mListener?.onCodeNotCompleted("")
    }

    fun input(number: String): Int {
        if (code.isEmpty()) clearCode()
        mCodeViews[code.length].background =
            ContextCompat.getDrawable(context, R.drawable.ic_circle_white)
        if (code.length == mCodeLength) {
            return code.length
        }
        code += number
        if (code.length == mCodeLength && mListener != null) {
            mListener!!.onCodeCompleted(code)
        }
        return code.length
    }

    fun delete(): Int {
        mListener?.onCodeNotCompleted(code)
        if (code.isEmpty()) {
            return code.length
        }
        code = code.substring(0, code.length - 1)
        mCodeViews[code.length].background =
            ContextCompat.getDrawable(context, R.drawable.ic_circle_blue)
        return code.length
    }

    fun clearCode() {
        mListener?.onCodeNotCompleted(code)
        code = ""
        for (codeView in mCodeViews) {
            codeView.background = ContextCompat.getDrawable(context, R.drawable.ic_circle_blue)
        }
    }

    fun invalidCode() {
        mListener?.onCodeNotCompleted(code)
        code = ""
        for (codeView in mCodeViews) {
            codeView.background =
                ContextCompat.getDrawable(context, R.drawable.ic_circle_error_gray)
        }
    }

    fun setListener(listener: OnPFCodeListener) {
        mListener = listener
    }

    interface OnPFCodeListener {
        fun onCodeCompleted(code: String)
        fun onCodeNotCompleted(code: String)
    }

    companion object {
        private const val DEFAULT_CODE_LENGTH = 4
    }
}
