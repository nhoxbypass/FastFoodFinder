package com.iceteaviet.fastfoodfinder.ui.base

import javax.inject.Inject
import dagger.hilt.android.AndroidEntryPoint
import android.os.Bundle
import androidx.annotation.LayoutRes
import androidx.appcompat.app.AppCompatActivity

/**
 * Created by tom on 7/10/18.
 */
abstract @AndroidEntryPoint
class BaseActivity : AppCompatActivity() {

    /**
     * @return layout resource id
     */
    @get:LayoutRes
    abstract val layoutId: Int

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(layoutId)
    }
}
