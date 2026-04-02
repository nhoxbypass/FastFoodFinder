package com.iceteaviet.fastfoodfinder.ui.main

import android.content.res.Configuration
import android.graphics.Color
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.widget.SearchView
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.bumptech.glide.Glide
import com.google.android.material.navigation.NavigationView
import dagger.hilt.android.AndroidEntryPoint
import com.iceteaviet.fastfoodfinder.R
import com.iceteaviet.fastfoodfinder.core.common.ext.getInputMethodManager
import com.iceteaviet.fastfoodfinder.core.common.ext.getSearchManager
import com.iceteaviet.fastfoodfinder.databinding.ActivityMainBinding
import com.iceteaviet.fastfoodfinder.ui.base.BaseActivity
import com.iceteaviet.fastfoodfinder.ui.main.search.SearchFragment
import com.iceteaviet.fastfoodfinder.ui.profile.ProfileFragment
import com.iceteaviet.fastfoodfinder.utils.e
import com.iceteaviet.fastfoodfinder.utils.openARLiveSightActivity
import com.iceteaviet.fastfoodfinder.utils.openLoginActivity
import com.iceteaviet.fastfoodfinder.utils.openSettingsActivity
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MainActivity : BaseActivity(), View.OnClickListener {

    private val viewModel: MainViewModel by viewModels()

    private lateinit var binding: ActivityMainBinding

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

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupUI()
        setupEventHandlers()
        setupObservers()

        //Inflate Map fragment
        mNavigationView.menu.getItem(0).isChecked = true
        mNavigationView.setCheckedItem(R.id.menu_action_map)
        val fragmentManager = supportFragmentManager
        fragmentManager.beginTransaction().replace(R.id.fl_fragment_placeholder, MainFragment.newInstance()).commit()
    }

    override fun onPostCreate(savedInstanceState: Bundle?) {
        super.onPostCreate(savedInstanceState)
        mDrawerToggle?.syncState()
    }

    override fun onResume() {
        super.onResume()
        viewModel.start()
    }

    private fun setupObservers() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collectLatest { state ->
                    updateProfileHeader(state.showSignInButton)
                    
                    if (!state.showSignInButton) {
                        state.userName?.let { setProfileHeaderNameText(it) }
                        state.userEmail?.let { setProfileHeaderEmailText(it) }
                        state.userAvatarUrl?.let { loadProfileHeaderAvatar(it) }
                    }

                    when (val event = state.event) {
                        is MainEvent.Idle -> {}
                        is MainEvent.NavigateToProfile -> {
                            showProfileView()
                            viewModel.markEventConsumed()
                        }
                        is MainEvent.NavigateToLogin -> {
                            showLoginView()
                            viewModel.markEventConsumed()
                        }
                        is MainEvent.NavigateToAR -> {
                            showARLiveSightView()
                            viewModel.markEventConsumed()
                        }
                        is MainEvent.NavigateToSettings -> {
                            showSettingsView()
                            viewModel.markEventConsumed()
                        }
                        is MainEvent.ShowSearchView -> {
                            showSearchView()
                            viewModel.markEventConsumed()
                        }
                        is MainEvent.HideSearchView -> {
                            hideSearchView()
                            viewModel.markEventConsumed()
                        }
                        is MainEvent.HideKeyboard -> {
                            hideKeyboard()
                            viewModel.markEventConsumed()
                        }
                        is MainEvent.ClearFocus -> {
                            clearFocus()
                            viewModel.markEventConsumed()
                        }
                        is MainEvent.ShowSearchWarning -> {
                            showSearchWarningMessage()
                            viewModel.markEventConsumed()
                        }
                        is MainEvent.UpdateSearchQueryText -> {
                            setSearchQueryText(event.query)
                            viewModel.markEventConsumed()
                        }
                    }
                }
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        mSearchView = initSearchView(menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                drawerLayout.openDrawer(GravityCompat.START)
                return true
            }
        }
        return if (mDrawerToggle!!.onOptionsItemSelected(item)) {
            true
        } else super.onOptionsItemSelected(item)
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        mDrawerToggle?.onConfigurationChanged(newConfig)
    }

    private fun showProfileView() {
        replaceFragment(ProfileFragment.newInstance(), getString(R.string.profile))
    }

    private fun showLoginView() {
        openLoginActivity(this)
        finish()
    }

    private fun showARLiveSightView() {
        openARLiveSightActivity(this)
    }

    private fun showSettingsView() {
        openSettingsActivity(this)
    }

    private fun setSearchQueryText(searchString: String) {
        mSearchView?.setQuery(searchString, false)
    }

    private fun hideKeyboard() {
        val view = this.currentFocus
        if (view != null) {
            val imm = getInputMethodManager()
            imm?.hideSoftInputFromWindow(view.windowToken, 0)
        }
    }

    private fun clearFocus() {
        val view = this.currentFocus
        if (view != null) {
            view.clearFocus()
        }
        mSearchInput?.clearFocus()
        mSearchView?.clearFocus()
    }

    private fun showSearchWarningMessage() {
        Toast.makeText(this, R.string.search_error, Toast.LENGTH_SHORT).show()
    }

    private fun showSearchView() {
        val ft = supportFragmentManager.beginTransaction()
        ft.setCustomAnimations(android.R.anim.slide_in_left, android.R.anim.slide_out_right)

        if (searchFragment == null)
            searchFragment = SearchFragment.newInstance()

        val fragmentPlaceHolder = findViewById<View>(R.id.fragment_search_placeholder)
        fragmentPlaceHolder.visibility = View.VISIBLE
        ft.replace(R.id.fragment_search_placeholder, searchFragment!!, "search-fragment")
        ft.commit()
    }

    private fun hideSearchView() {
        val ft = supportFragmentManager.beginTransaction()
        ft.setCustomAnimations(android.R.anim.slide_in_left, android.R.anim.slide_out_right)

        val fragment = supportFragmentManager.findFragmentById(R.id.fragment_search_placeholder)

        if (fragment != null) {
            ft.remove(fragment)
        }
        ft.commit()
    }

    private fun updateProfileHeader(showSignIn: Boolean) {
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

    private fun loadProfileHeaderAvatar(photoUrl: String) {
        navHeaderAvatar?.let {
            Glide.with(this)
                .load(photoUrl)
                .into(it)
        }
    }

    private fun setProfileHeaderNameText(name: String) {
        navHeaderName?.text = name
    }

    private fun setProfileHeaderEmailText(email: String) {
        navHeaderEmail?.text = email
    }

    override fun onClick(view: View) {
        when (view.id) {
            R.id.btn_nav_header_signin -> {
                viewModel.onSignInMenuItemClick()
            }
            R.id.iv_nav_header_avatar, R.id.tv_nav_header_name, R.id.tv_nav_header_screenname -> {
                drawerLayout.closeDrawers()
                viewModel.onProfileMenuItemClick()
            }
        }
    }

    private fun setupUI() {
        mNavigationView = binding.navView
        drawerLayout = binding.drawerLayout
        mToolbar = binding.toolbar

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

        mDrawerToggle?.let { drawerLayout.addDrawerListener(it) }
    }

    private fun initSearchView(menu: Menu): SearchView? {
        val searchView: SearchView?

        menuInflater.inflate(R.menu.menu_main, menu)
        searchItem = menu.findItem(R.id.action_search) ?: return null

        val searchManager = getSearchManager()

        searchView = searchItem!!.actionView as SearchView
        searchView.setSearchableInfo(searchManager?.getSearchableInfo(componentName))

        searchView.queryHint = getString(R.string.type_name_store)
        searchView.setBackgroundColor(ContextCompat.getColor(this, R.color.material_red_700))
        mSearchInput = searchView.findViewById(androidx.appcompat.R.id.search_src_text)
        mSearchInput?.setHintTextColor(ContextCompat.getColor(this, R.color.colorHintText))
        mSearchInput?.setTextColor(Color.WHITE)

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                viewModel.onSearchQuerySubmit(query)
                return false
            }

            override fun onQueryTextChange(newText: String): Boolean {
                searchFragment?.let {
                    if (!it.isVisible) return false

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

        searchItem!!.setOnActionExpandListener(object : MenuItem.OnActionExpandListener {
            override fun onMenuItemActionExpand(menuItem: MenuItem): Boolean {
                viewModel.onSearchMenuItemExpand()
                return true
            }

            override fun onMenuItemActionCollapse(menuItem: MenuItem): Boolean {
                viewModel.onSearchMenuItemCollapse()
                return true
            }
        })

        return searchView
    }

    private fun selectDrawerItem(menuItem: MenuItem) {
        mNavigationView.setCheckedItem(menuItem)
        menuItem.isChecked = true

        when (menuItem.itemId) {
            R.id.menu_action_profile -> {
                viewModel.onProfileMenuItemClick()
            }
            R.id.menu_action_map -> {
                if (supportFragmentManager.backStackEntryCount > 0) {
                    supportFragmentManager.popBackStack()
                }
            }
            R.id.menu_action_ar -> {
                viewModel.onARLiveSightMenuItemClick()
            }
            R.id.menu_action_setting -> {
                viewModel.onSettingsMenuItemClick()
            }
            else -> {
                e(TAG, "Wrong menu item id")
            }
        }
    }

    private fun replaceFragment(fragment: Fragment, actionBarTitle: String) {
        val fragmentManager = supportFragmentManager
        val oldFragment = supportFragmentManager.findFragmentByTag("profile_screen")

        if (oldFragment == null || !oldFragment.isAdded || oldFragment.isRemoving) {
            fragmentManager
                .beginTransaction()
                .replace(R.id.fl_fragment_placeholder, fragment, "profile_screen")
                .addToBackStack(null)
                .commit()
            fragmentManager.executePendingTransactions()
        }

        title = actionBarTitle
    }

    companion object {
        private val TAG = MainActivity::class.java.simpleName
    }
}
