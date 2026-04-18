package com.iceteaviet.fastfoodfinder.ui.main

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.iceteaviet.fastfoodfinder.R
import com.iceteaviet.fastfoodfinder.ui.main.map.MainMapFragment

/**
 * Created by Genius Doan on 11/8/2016.
 */
class MainPagerAdapter internal constructor(fm: FragmentManager) : FragmentPagerAdapter(fm) {
    private val pageTitles = arrayOf(R.string.map.toString())
    private val imageResId = intArrayOf(R.drawable.ic_main_map)

    override fun getItem(position: Int): Fragment {
        return MainMapFragment.newInstance()
    }

    override fun getPageTitle(position: Int): CharSequence {
        return pageTitles[position]
    }

    override fun getCount(): Int {
        return PAGE_COUNT
    }

    fun getIcon(position: Int): Int {
        return imageResId[position]
    }

    companion object {
        private const val PAGE_COUNT = 1
    }
}
