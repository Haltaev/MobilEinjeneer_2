package ru.mobileinjeneer.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.LayoutRes
import androidx.fragment.app.Fragment
import kotlinx.android.synthetic.main.fragment_auth.view.*
import ru.mobileinjeneer.ui.activity.BaseActivity

abstract class BaseFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(getLayoutId(), container, false).apply {
        this.progress?.setOnTouchListener { _, _ ->  true}
    }

    @LayoutRes
    abstract fun getLayoutId(): Int

    private fun getBaseActivity(): BaseActivity? =
        if (activity is BaseActivity) activity as BaseActivity else null

    fun showErrorToast(error: String?) {
        error?.let {
            Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
        }

    }
}