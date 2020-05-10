package ru.mobileinjeneer.ui.activity

import android.os.Bundle
import android.widget.Toast
import androidx.annotation.LayoutRes
import androidx.appcompat.app.AppCompatActivity
import ru.mobileinjeneer.App

abstract class BaseActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(getLayoutId())

        App.getComponent().inject(this)
    }

    @LayoutRes
    abstract fun getLayoutId(): Int

    fun showErrorToast(error: String) {
        Toast.makeText(this, error, Toast.LENGTH_SHORT).show()
    }
}