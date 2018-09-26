package com.iceteaviet.fastfoodfinder.ui.main

import android.app.SearchManager
import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.graphics.Color
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.Nullable
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import androidx.core.view.MenuItemCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.google.android.material.navigation.NavigationView
import com.iceteaviet.fastfoodfinder.App
import com.iceteaviet.fastfoodfinder.R
import com.iceteaviet.fastfoodfinder.data.DataManager
import com.iceteaviet.fastfoodfinder.data.transport.model.SearchEventResult
import com.iceteaviet.fastfoodfinder.ui.ar.ArCameraActivity
import com.iceteaviet.fastfoodfinder.ui.login.LoginActivity
import com.iceteaviet.fastfoodfinder.ui.profile.ProfileFragment
import com.iceteaviet.fastfoodfinder.ui.settings.SettingActivity
import com.iceteaviet.fastfoodfinder.utils.e
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.android.synthetic.main.activity_main.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

class MainActivity : AppCompatActivity() {
    lateinit var mNavigationView: NavigationView
    lateinit var mDrawerLayout: DrawerLayout
    lateinit var mToolbar: Toolbar
    private var mSearchView: SearchView? = null
    private var mNavHeaderAvatar: CircleImageView? = null
    private var mNavHeaderName: TextView? = null
    private var mNavHeaderScreenName: TextView? = null
    private var mSearchInput: EditText? = null
    private var mNavHeaderSignIn: Button? = null
    private var mDrawerToggle: ActionBarDrawerToggle? = null

    private var dataManager: DataManager? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        mNavigationView = nav_view
        mDrawerLayout = drawer_layout
        mToolbar = toolbar

        dataManager = App.getDataManager()

        setSupportActionBar(mToolbar)
        setupAllViews()
        setupEventListeners()

        // Initialize Auth
        initAuth()

        //Inflate Map fragment
        mNavigationView.menu.getItem(0).isChecked = true
        mNavigationView.setCheckedItem(R.id.menu_action_map)
        val fragmentManager = supportFragmentManager
        fragmentManager.beginTransaction().replace(R.id.fl_fragment_placeholder, MainFragment.newInstance()).commit()
    }

    override fun onPostCreate(@Nullable savedInstanceState: Bundle?) {
        super.onPostCreate(savedInstanceState)
        mDrawerToggle!!.syncState()
    }

    override fun onPause() {
        super.onPause()
        EventBus.getDefault().unregister(this)
    }

    override fun onResume() {
        super.onResume()
        EventBus.getDefault().register(this)
    }


    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        //Inflate SearchView
        mSearchView = initSearchView(menu)
        return super.onCreateOptionsMenu(menu)
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                mDrawerLayout!!.openDrawer(GravityCompat.START)
                return true
            }

            else -> {
            }
        }

        return if (mDrawerToggle!!.onOptionsItemSelected(item)) {
            true
        } else super.onOptionsItemSelected(item)
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)

        // Pass any configuration change to the drawer toggles
        mDrawerToggle!!.onConfigurationChanged(newConfig)
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onSearchResult(searchEventResult: SearchEventResult) {
        val resultCode = searchEventResult.resultCode
        when (resultCode) {
            SearchEventResult.SEARCH_ACTION_QUICK -> {
                mSearchView!!.setQuery(searchEventResult.searchString, false)
                mSearchView!!.isIconified = false
                // Check if no view has focus:
                val view = this.currentFocus
                if (view != null) {
                    val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                    imm.hideSoftInputFromWindow(view.windowToken, 0)
                    view.clearFocus()
                    mSearchInput!!.clearFocus()
                }
            }
            SearchEventResult.SEARCH_ACTION_QUERY_SUBMIT -> removeSearchFragment()

            SearchEventResult.SEARCH_ACTION_COLLAPSE -> {
            }

            else -> Toast.makeText(this@MainActivity, R.string.search_error, Toast.LENGTH_SHORT).show()
        }
    }


    private fun initAuth() {
        if (dataManager!!.getCurrentUser() == null) {
            mNavHeaderName!!.visibility = View.GONE
            mNavHeaderScreenName!!.visibility = View.GONE
            mNavHeaderSignIn!!.visibility = View.VISIBLE
        } else {
            Glide.with(this@MainActivity)
                    .load(dataManager!!.getCurrentUser()!!.photoUrl)
                    .into(mNavHeaderAvatar!!)
            mNavHeaderName!!.text = dataManager!!.getCurrentUser()!!.name
            mNavHeaderScreenName!!.text = dataManager!!.getCurrentUser()!!.email
        }
    }


    fun initSearchView(menu: Menu): SearchView? {
        val searchView: SearchView?

        menuInflater.inflate(R.menu.menu_main, menu)
        val searchItem = menu.findItem(R.id.action_search) ?: return null

        val searchManager = this@MainActivity.getSystemService(Context.SEARCH_SERVICE) as SearchManager

        searchView = MenuItemCompat.getActionView(searchItem) as SearchView

        if (searchView != null) {
            searchView.setSearchableInfo(searchManager.getSearchableInfo(this@MainActivity.componentName))

            searchView.queryHint = getString(R.string.type_name_store)
            searchView.setBackgroundColor(ContextCompat.getColor(this, R.color.material_red_600))
            mSearchInput = searchView.findViewById(androidx.appcompat.R.id.search_src_text)
            mSearchInput!!.setHintTextColor(ContextCompat.getColor(this@MainActivity, R.color.colorHintText))
            mSearchInput!!.setTextColor(Color.WHITE)

            // Set on search query submit
            searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
                override fun onQueryTextSubmit(query: String): Boolean {
                    EventBus.getDefault().post(SearchEventResult(SearchEventResult.SEARCH_ACTION_QUERY_SUBMIT, query))
                    return false
                }

                override fun onQueryTextChange(newText: String): Boolean {
                    return false
                }
            })

            //Set event expand search view
            MenuItemCompat.setOnActionExpandListener(searchItem, object : MenuItemCompat.OnActionExpandListener {
                override fun onMenuItemActionExpand(menuItem: MenuItem): Boolean {
                    showSearchFragment()
                    return true
                }

                override fun onMenuItemActionCollapse(menuItem: MenuItem): Boolean {
                    EventBus.getDefault().post(SearchEventResult(SearchEventResult.SEARCH_ACTION_COLLAPSE))
                    removeSearchFragment()
                    return true
                }
            })
        }

        return searchView
    }


    private fun showSearchFragment() {
        val ft = supportFragmentManager.beginTransaction()
        ft.setCustomAnimations(android.R.anim.slide_in_left, android.R.anim.slide_out_right)

        val searchFragment = SearchFragment.newInstance()

        val fragment_placeholder = findViewById<View>(R.id.fragment_search_placeholder)
        fragment_placeholder.visibility = View.VISIBLE
        ft.replace(R.id.fragment_search_placeholder, searchFragment, "blankFragment")

        // Start the animated transition.
        ft.commit()
    }


    private fun removeSearchFragment() {
        val ft = supportFragmentManager.beginTransaction()
        ft.setCustomAnimations(android.R.anim.slide_in_left, android.R.anim.slide_out_right)

        val fragment = supportFragmentManager.findFragmentById(R.id.fragment_search_placeholder)

        if (fragment != null) {
            ft.remove(fragment)
        }

        ft.commit()
    }


    private fun setupAllViews() {
        mDrawerToggle = setupDrawerToggle()

        val headerLayout = mNavigationView.getHeaderView(0)
        mNavHeaderAvatar = headerLayout.findViewById(R.id.iv_nav_header_avatar)
        mNavHeaderName = headerLayout.findViewById(R.id.tv_nav_header_name)
        mNavHeaderScreenName = headerLayout.findViewById(R.id.tv_nav_header_screenname)
        mNavHeaderSignIn = headerLayout.findViewById(R.id.btn_nav_header_signin)

        // Tie DrawerLayout events to the ActionBarToggle
        mDrawerToggle?.let { mDrawerLayout.addDrawerListener(it) }
    }

    private fun setupEventListeners() {
        mNavHeaderSignIn!!.setOnClickListener {
            val intent = Intent(this@MainActivity, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }

        mNavigationView!!.setNavigationItemSelectedListener { item ->
            selectDrawerItem(item)
            true
        }
    }


    private fun selectDrawerItem(menuItem: MenuItem) {
        // Create a new fragment and specify the fragment to show based on nav item clicked
        var fragment: Fragment? = null
        val fragmentClass: Class<*>

        when (menuItem.itemId) {
            R.id.menu_action_profile -> {
                fragmentClass = ProfileFragment::class.java
                fragment = ProfileFragment.newInstance()
            }
            R.id.menu_action_map -> fragmentClass = MainFragment::class.java
            R.id.menu_action_ar -> {
                val arIntent = Intent(this@MainActivity, ArCameraActivity::class.java)
                startActivity(arIntent)
                return
            }
            R.id.menu_action_setting -> {
                val settingIntent = Intent(this@MainActivity, SettingActivity::class.java)
                startActivity(settingIntent)
                return
            }
            else -> {
                // Default
                fragmentClass = MainFragment::class.java
                e(TAG, "Wrong menu item id")
            }
        }

        try {
            fragment = fragmentClass.newInstance() as Fragment

            // Insert the fragment by replacing any existing fragment
            val fragmentManager = supportFragmentManager
            fragmentManager.beginTransaction().replace(R.id.fl_fragment_placeholder, fragment).commit()
            fragmentManager.executePendingTransactions()
            // Highlight the selected item has been done by NavigationView
            menuItem.isChecked = true
            // Set action bar title
            title = menuItem.title
            // Close the ic_search_navigation drawer
            mDrawerLayout!!.closeDrawers()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }


    private fun setupDrawerToggle(): ActionBarDrawerToggle {
        return ActionBarDrawerToggle(this, mDrawerLayout, mToolbar, R.string.drawer_open, R.string.drawer_close)
    }

    companion object {
        private val TAG = MainActivity::class.java.simpleName
    }
}
