package com.iceteaviet.fastfoodfinder.ui.main

import android.app.SearchManager
import android.content.Context
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
import com.iceteaviet.fastfoodfinder.ui.base.BaseActivity
import com.iceteaviet.fastfoodfinder.ui.main.search.SearchFragment
import com.iceteaviet.fastfoodfinder.ui.profile.ProfileFragment
import com.iceteaviet.fastfoodfinder.utils.e
import com.iceteaviet.fastfoodfinder.utils.openARLiveSightActivity
import com.iceteaviet.fastfoodfinder.utils.openLoginActivity
import com.iceteaviet.fastfoodfinder.utils.openSettingsActivity
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : BaseActivity(), MainContract.View, View.OnClickListener {
    override lateinit var presenter: MainContract.Presenter

    lateinit var mNavigationView: NavigationView
    lateinit var drawerLayout: DrawerLayout
    lateinit var mToolbar: Toolbar
    private var mSearchView: SearchView? = null
    private var navHeaderAvatar: CircleImageView? = null
    private var navHeaderName: TextView? = null
    private var navHeaderEmail: TextView? = null
    private var mSearchInput: EditText? = null
    private var mNavHeaderSignIn: Button? = null
    private var mDrawerToggle: ActionBarDrawerToggle? = null
    private var searchFragment: SearchFragment? = null

    private var searchItem: MenuItem? = null

    override val layoutId: Int
        get() = R.layout.activity_main

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        presenter = MainPresenter(App.getDataManager(), App.getSchedulerProvider(), App.getBus(), this)

        setupUI()
        setupEventHandlers()

        //Inflate Map fragment
        mNavigationView.menu.getItem(0).isChecked = true
        mNavigationView.setCheckedItem(R.id.menu_action_map)
        val fragmentManager = supportFragmentManager
        fragmentManager.beginTransaction().replace(R.id.fl_fragment_placeholder, MainFragment.newInstance()).commit()
    }

    override fun onPostCreate(@Nullable savedInstanceState: Bundle?) {
        super.onPostCreate(savedInstanceState)
        mDrawerToggle?.syncState()
    }

    override fun onResume() {
        super.onResume()
        presenter.subscribe()
    }

    override fun onPause() {
        super.onPause()
        presenter.unsubscribe()
    }


    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        //Inflate SearchView
        mSearchView = initSearchView(menu)
        return super.onCreateOptionsMenu(menu)
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                drawerLayout.openDrawer(GravityCompat.START)
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
        mDrawerToggle?.onConfigurationChanged(newConfig)
    }

    override fun showProfileView() {
        replaceFragment(ProfileFragment.newInstance(), getString(R.string.profile))
    }

    override fun showLoginView() {
        openLoginActivity(this)
        finish()
    }

    override fun showARLiveSightView() {
        openARLiveSightActivity(this)
    }

    override fun showSettingsView() {
        openSettingsActivity(this)
    }

    override fun setSearchQueryText(searchString: String) {
        mSearchView?.setQuery(searchString, false)
    }

    override fun hideKeyboard() {
        // Check if no view has focus:
        val view = this.currentFocus
        if (view != null) {
            val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(view.windowToken, 0)
        }
    }

    override fun clearFocus() {
        val view = this.currentFocus
        if (view != null) {
            view.clearFocus()
        }
        mSearchInput?.clearFocus()
        mSearchView?.clearFocus()
    }

    override fun showSearchWarningMessage() {
        Toast.makeText(this, R.string.search_error, Toast.LENGTH_SHORT).show()
    }

    override fun showSearchView() {
        val ft = supportFragmentManager.beginTransaction()
        ft.setCustomAnimations(android.R.anim.slide_in_left, android.R.anim.slide_out_right)

        if (searchFragment == null)
            searchFragment = SearchFragment.newInstance()

        val fragmentPlaceHolder = findViewById<View>(R.id.fragment_search_placeholder)
        fragmentPlaceHolder.visibility = View.VISIBLE
        ft.replace(R.id.fragment_search_placeholder, searchFragment!!, "search-fragment")

        // Start the animated transition.
        ft.commit()
    }

    override fun hideSearchView() {
        val ft = supportFragmentManager.beginTransaction()
        ft.setCustomAnimations(android.R.anim.slide_in_left, android.R.anim.slide_out_right)

        val fragment = supportFragmentManager.findFragmentById(R.id.fragment_search_placeholder)

        if (fragment != null) {
            ft.remove(fragment)
        }

        ft.commit()
    }

    override fun updateProfileHeader(showSignIn: Boolean) {
        if (showSignIn) {
            navHeaderName?.visibility = View.GONE
            navHeaderEmail?.visibility = View.GONE
            mNavHeaderSignIn?.visibility = View.VISIBLE
        } else {
            navHeaderName?.visibility = View.VISIBLE
            navHeaderEmail?.visibility = View.VISIBLE
            mNavHeaderSignIn?.visibility = View.GONE
        }
    }

    override fun loadProfileHeaderAvatar(photoUrl: String) {
        navHeaderAvatar?.let {
            Glide.with(this)
                    .load(photoUrl)
                    .into(it)
        }
    }

    override fun setProfileHeaderNameText(name: String) {
        navHeaderName?.text = name
    }

    override fun setProfileHeaderEmailText(email: String) {
        navHeaderEmail?.text = email
    }

    override fun onClick(view: View) {
        when (view.id) {
            R.id.search_close_btn -> {
                if (!searchFragment!!.isVisible) {
                    MenuItemCompat.collapseActionView(searchItem)
                }
            }

            R.id.btn_nav_header_signin -> {
                presenter.onSignInMenuItemClick()
            }

            R.id.iv_nav_header_avatar, R.id.tv_nav_header_name, R.id.tv_nav_header_screenname -> {
                // Close the navigation drawer
                drawerLayout.closeDrawers()
                presenter.onProfileMenuItemClick()
            }
        }
    }

    private fun setupUI() {
        mNavigationView = nav_view
        drawerLayout = drawer_layout
        mToolbar = toolbar

        setSupportActionBar(mToolbar)

        mDrawerToggle = ActionBarDrawerToggle(this, drawerLayout, mToolbar, R.string.drawer_open, R.string.drawer_close)

        val headerLayout = mNavigationView.getHeaderView(0)
        navHeaderAvatar = headerLayout.findViewById(R.id.iv_nav_header_avatar)
        navHeaderName = headerLayout.findViewById(R.id.tv_nav_header_name)
        navHeaderEmail = headerLayout.findViewById(R.id.tv_nav_header_screenname)
        mNavHeaderSignIn = headerLayout.findViewById(R.id.btn_nav_header_signin)

        mDrawerToggle?.let { it.drawerArrowDrawable.color = Color.WHITE }
    }

    private fun setupEventHandlers() {
        navHeaderAvatar?.setOnClickListener(this)
        navHeaderName?.setOnClickListener(this)
        navHeaderEmail?.setOnClickListener(this)

        mNavHeaderSignIn?.setOnClickListener(this)

        mNavigationView.setNavigationItemSelectedListener { item ->
            drawerLayout.closeDrawers()
            selectDrawerItem(item)
            true
        }

        // Tie DrawerLayout events to the ActionBarToggle
        mDrawerToggle?.let { drawerLayout.addDrawerListener(it) }
    }


    private fun initSearchView(menu: Menu): SearchView? {
        val searchView: SearchView?

        menuInflater.inflate(R.menu.menu_main, menu)
        searchItem = menu.findItem(R.id.action_search) ?: return null

        val searchManager = getSystemService(Context.SEARCH_SERVICE) as SearchManager

        searchView = MenuItemCompat.getActionView(searchItem) as SearchView

        searchView.setSearchableInfo(searchManager.getSearchableInfo(componentName))

        searchView.queryHint = getString(R.string.type_name_store)
        searchView.setBackgroundColor(ContextCompat.getColor(this, R.color.material_red_700))
        mSearchInput = searchView.findViewById(androidx.appcompat.R.id.search_src_text)
        mSearchInput?.setHintTextColor(ContextCompat.getColor(this, R.color.colorHintText))
        mSearchInput?.setTextColor(Color.WHITE)

        // Set on search query submit
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                presenter.onSearchQuerySubmit(query)
                return false
            }

            override fun onQueryTextChange(newText: String): Boolean {
                searchFragment?.let {
                    if (!it.isVisible)
                        return false

                    if (newText.isNotBlank()) {
                        it.hideOptionsContainer()
                        it.showSearchContainer()
                        it.updateSearchList(newText)
                    } else {
                        it.showOptionsContainer()
                        it.hideSearchContainer()
                    }
                }
                return false
            }
        })

        searchView.findViewById<View>(R.id.search_close_btn).setOnClickListener(this)

        //Set event expand search view
        MenuItemCompat.setOnActionExpandListener(searchItem, object : MenuItemCompat.OnActionExpandListener {
            override fun onMenuItemActionExpand(menuItem: MenuItem): Boolean {
                presenter.onSearchMenuItemExpand()
                return true
            }

            override fun onMenuItemActionCollapse(menuItem: MenuItem): Boolean {
                presenter.onSearchMenuItemCollapse()
                return true
            }
        })

        return searchView
    }


    private fun selectDrawerItem(menuItem: MenuItem) {
        // set item as selected to persist highlight
        mNavigationView.setCheckedItem(menuItem)
        menuItem.isChecked = true

        when (menuItem.itemId) {
            R.id.menu_action_profile -> {
                presenter.onProfileMenuItemClick()
                return
            }
            R.id.menu_action_map -> {
                if (supportFragmentManager.backStackEntryCount > 0) {
                    supportFragmentManager.popBackStack()
                }
                return
            }
            R.id.menu_action_ar -> {
                presenter.onARLiveSightMenuItemClick()
                return
            }
            R.id.menu_action_setting -> {
                presenter.onSettingsMenuItemClick()
                return
            }
            else -> {
                e(TAG, "Wrong menu item id")
            }
        }
    }

    private fun replaceFragment(fragment: Fragment, actionBarTitle: String) {
        // Insert the fragment by replacing any existing fragment
        val fragmentManager = supportFragmentManager
        fragmentManager
                .beginTransaction()
                .add(R.id.fl_fragment_placeholder, fragment)
                .addToBackStack(null) // Add this transaction to the back stack
                .commit()
        fragmentManager.executePendingTransactions()

        // Set action bar title
        title = actionBarTitle
    }

    companion object {
        private val TAG = MainActivity::class.java.simpleName
    }
}
