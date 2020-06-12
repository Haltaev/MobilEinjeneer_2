package ru.mobilengineer.ui

import android.content.Context
import android.util.AttributeSet
import android.view.Gravity
import android.view.LayoutInflater
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.annotation.Nullable
import androidx.core.content.ContextCompat
import ru.mobilengineer.R

class CodeView : LinearLayout {
    private var mCodeViews: ArrayList<ImageView> = arrayListOf()
    private var mCode = ""
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
        inflate(context, R.layout.view_code_linear_layout, this)
        setUpCodeViews()
    }

    private fun setUpCodeViews() {
        removeAllViews()
        mCodeViews = arrayListOf()
        mCode = ""
        for (i in 0 until mCodeLength) {
            val inflater = context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            val view = inflater.inflate(R.layout.view_code_checkbox, null) as ImageView
            val layoutParams = LayoutParams(
                resources.getDimensionPixelSize(R.dimen.code_width),
                resources.getDimensionPixelSize(R.dimen.code_width)
            )
            val margin = resources.getDimensionPixelSize(R.dimen.code_margin)
            layoutParams.setMargins(margin, 0, margin, 0)
            layoutParams.gravity = Gravity.CENTER
            view.layoutParams = layoutParams
            val padding = resources.getDimensionPixelSize(R.dimen.code_padding)
//            view.setPadding(padding, padding, padding, padding)
            view.isEnabled = false
            addView(view)
            mCodeViews.add(view)
        }
        mListener?.onCodeNotCompleted("")
    }

    fun input(number: String): Int {
        if (mCode.isEmpty()) clearCode()
        mCodeViews[mCode.length].background =
            ContextCompat.getDrawable(context, R.drawable.ic_circle_white)
        if (mCode.length == mCodeLength) {
            return mCode.length
        }
        mCode += number
        if (mCode.length == mCodeLength && mListener != null) {
            mListener!!.onCodeCompleted(mCode)
        }
        return mCode.length
    }

    fun delete(): Int {
        mListener?.onCodeNotCompleted(mCode)
        if (mCode.isEmpty()) {
            return mCode.length
        }
        mCode = mCode.substring(0, mCode.length - 1)
        mCodeViews[mCode.length].background =
            ContextCompat.getDrawable(context, R.drawable.selector_circle)
        return mCode.length
    }

    fun clearCode() {
        mListener?.onCodeNotCompleted(mCode)
        mCode = ""
        for (codeView in mCodeViews) {
            codeView.background = ContextCompat.getDrawable(context, R.drawable.selector_circle)
        }
    }

    fun invalidCode() {
        mListener?.onCodeNotCompleted(mCode)
        mCode = ""
        for (codeView in mCodeViews) {
            codeView.background =
                ContextCompat.getDrawable(context, R.drawable.ic_circle_error_gray)
        }
    }

    fun getCode(): String {
        return mCode
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
