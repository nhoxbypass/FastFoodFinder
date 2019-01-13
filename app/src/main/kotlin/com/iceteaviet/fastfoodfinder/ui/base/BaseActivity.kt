package com.iceteaviet.fastfoodfinder.ui.base

import android.os.Bundle

import androidx.annotation.LayoutRes
import androidx.annotation.Nullable
import androidx.appcompat.app.AppCompatActivity
import com.iceteaviet.fastfoodfinder.App
import com.iceteaviet.fastfoodfinder.data.DataManager

/**
 * Created by tom on 7/10/18.
 */
abstract class BaseActivity : AppCompatActivity() {

    /**
     * @return layout resource id
     */
    @get:LayoutRes
    abstract val layoutId: Int

    protected lateinit var dataManager: DataManager

    override fun onCreate(@Nullable savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(layoutId)
        dataManager = App.getDataManager()
    }
}
