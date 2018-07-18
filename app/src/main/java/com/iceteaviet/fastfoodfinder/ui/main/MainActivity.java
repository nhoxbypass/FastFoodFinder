package com.iceteaviet.fastfoodfinder.ui.main;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.iceteaviet.fastfoodfinder.R;
import com.iceteaviet.fastfoodfinder.data.remote.user.model.User;
import com.iceteaviet.fastfoodfinder.data.transport.model.SearchEventResult;
import com.iceteaviet.fastfoodfinder.ui.ar.ArCameraActivity;
import com.iceteaviet.fastfoodfinder.ui.login.LoginActivity;
import com.iceteaviet.fastfoodfinder.ui.profile.ProfileFragment;
import com.iceteaviet.fastfoodfinder.ui.settings.SettingActivity;
import com.iceteaviet.fastfoodfinder.utils.PermissionUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;

public class MainActivity extends AppCompatActivity {

    public static String PACKAGE_NAME;

    @BindView(R.id.nav_view)
    NavigationView mNavigationView;
    @BindView(R.id.drawer_layout)
    DrawerLayout mDrawerLayout;
    @BindView(R.id.toolbar)
    Toolbar mToolbar;
    View mHeaderLayout;
    SearchView mSearchView;
    LinearLayout mNavHeaderContainer;
    CircleImageView mNavHeaderAvatar;
    TextView mNavHeaderName;
    TextView mNavHeaderScreenName;
    EditText mSearchInput;
    Button mNavHeaderSignIn;
    private ActionBarDrawerToggle mDrawerToggle;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        setSupportActionBar(mToolbar);

        PACKAGE_NAME = getApplicationContext().getPackageName();

        setupAllViews();
        // Initialize Auth
        initAuth();

        //Inflate Map fragment
        mNavigationView.getMenu().getItem(0).setChecked(true);
        mNavigationView.setCheckedItem(R.id.menu_action_map);
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.fl_fragment_placeholder, MainFragment.newInstance()).commit();
    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        mDrawerToggle.syncState();
    }

    @Override
    protected void onPause() {
        super.onPause();
        EventBus.getDefault().unregister(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        EventBus.getDefault().register(this);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //Inflate SearchView
        mSearchView = initSearchView(menu);
        return super.onCreateOptionsMenu(menu);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                mDrawerLayout.openDrawer(GravityCompat.START);
                return true;
        }
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode) {
            case PermissionUtils.REQUEST_LOCATION:
                // TODO: Enable my location
                break;

            default:
                break;
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        // Pass any configuration change to the drawer toggles
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onSearchResult(SearchEventResult searchEventResult) {
        int resultCode = searchEventResult.getResultCode();
        switch (resultCode) {
            case SearchEventResult.SEARCH_QUICK_OK:
                mSearchView.setQuery(searchEventResult.getSearchString(), false);
                mSearchView.setIconified(false);
                // Check if no view has focus:
                View view = this.getCurrentFocus();
                if (view != null) {
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                    view.clearFocus();
                    mSearchInput.clearFocus();
                }
                break;
            case SearchEventResult.SEARCH_STORE_OK:
                removeSearchFragment();
                break;

            case SearchEventResult.SEARCH_COLLAPSE:
                break;

            default:
                Toast.makeText(MainActivity.this, R.string.search_error, Toast.LENGTH_SHORT).show();
        }
    }


    private void initAuth() {
        if (User.currentUser == null) {
            mNavHeaderName.setVisibility(View.GONE);
            mNavHeaderScreenName.setVisibility(View.GONE);
            mNavHeaderSignIn.setVisibility(View.VISIBLE);
        } else {
            Glide.with(MainActivity.this)
                    .load(User.currentUser.getPhotoUrl())
                    .into(mNavHeaderAvatar);
            mNavHeaderName.setText(User.currentUser.getName());
            mNavHeaderScreenName.setText(User.currentUser.getEmail());
        }
    }


    public SearchView initSearchView(Menu menu) {
        SearchView searchView = null;

        getMenuInflater().inflate(R.menu.menu_main, menu);
        MenuItem searchItem = menu.findItem(R.id.action_search);

        SearchManager searchManager =
                (SearchManager) MainActivity.this.getSystemService(Context.SEARCH_SERVICE);

        if (searchItem != null) {
            searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
        }
        if (searchView != null) {
            searchView.setSearchableInfo(searchManager.getSearchableInfo(MainActivity.this.getComponentName()));

            searchView.setQueryHint(getString(R.string.type_name_store));
            searchView.setBackgroundColor(Color.parseColor("#E53935"));
            mSearchInput = (EditText) searchView.findViewById(android.support.v7.appcompat.R.id.search_src_text);
            mSearchInput.setHintTextColor(ContextCompat.getColor(MainActivity.this, R.color.colorHintText));
            mSearchInput.setTextColor(Color.WHITE);
        }


        //Set on search query submit
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                EventBus.getDefault().post(new SearchEventResult(SearchEventResult.SEARCH_STORE_OK, query));
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });

        //Set event expand search view
        MenuItemCompat.setOnActionExpandListener(searchItem, new MenuItemCompat.OnActionExpandListener() {
            @Override
            public boolean onMenuItemActionExpand(MenuItem menuItem) {
                showSearchFragment();
                return true;
            }

            @Override
            public boolean onMenuItemActionCollapse(MenuItem menuItem) {
                EventBus.getDefault().post(new SearchEventResult(SearchEventResult.SEARCH_COLLAPSE));
                removeSearchFragment();
                return true;
            }
        });

        return searchView;
    }


    private void showSearchFragment() {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.setCustomAnimations(android.R.anim.slide_in_left, android.R.anim.slide_out_right);

        SearchFragment searchFragment = SearchFragment.newInstance();

        View fragment_placeholder = findViewById(R.id.fragment_search_placeholder);
        fragment_placeholder.setVisibility(View.VISIBLE);
        ft.replace(R.id.fragment_search_placeholder, searchFragment, "blankFragment");

        // Start the animated transition.
        ft.commit();
    }


    private void removeSearchFragment() {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.setCustomAnimations(android.R.anim.slide_in_left, android.R.anim.slide_out_right);

        Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.fragment_search_placeholder);

        if (fragment != null) {
            ft.remove(fragment);
        }

        ft.commit();
    }


    private void setupAllViews() {
        mDrawerToggle = setupDrawerToggle();

        mHeaderLayout = mNavigationView.getHeaderView(0);
        mNavHeaderContainer = (LinearLayout) mHeaderLayout.findViewById(R.id.nav_header_container);
        mNavHeaderAvatar = (CircleImageView) mHeaderLayout.findViewById(R.id.iv_nav_header_avatar);
        mNavHeaderName = (TextView) mHeaderLayout.findViewById(R.id.tv_nav_header_name);
        mNavHeaderScreenName = (TextView) mHeaderLayout.findViewById(R.id.tv_nav_header_screenname);
        mNavHeaderSignIn = (Button) mHeaderLayout.findViewById(R.id.btn_nav_header_signin);

        // Tie DrawerLayout events to the ActionBarToggle
        mDrawerLayout.addDrawerListener(mDrawerToggle);

        mNavHeaderSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });

        mNavigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                selectDrawerItem(item);
                return true;
            }
        });
    }


    private void selectDrawerItem(MenuItem menuItem) {
        // Create a new fragment and specify the fragment to show based on nav item clicked
        Fragment fragment = null;
        Class fragmentClass;
        switch (menuItem.getItemId()) {
            case R.id.menu_action_profile:
                fragmentClass = ProfileFragment.class;
                fragment = ProfileFragment.newInstance();
                break;
            case R.id.menu_action_map:
                fragmentClass = MainFragment.class;
                break;
            case R.id.menu_action_ar:
                //TODO: Implement live sight
                Intent arIntent = new Intent(MainActivity.this, ArCameraActivity.class);
                startActivity(arIntent);
                return;
            case R.id.menu_action_setting:
                Intent settingIntent = new Intent(MainActivity.this, SettingActivity.class);
                startActivity(settingIntent);
                return;
            default:
                fragmentClass = MainFragment.class;
        }

        try {
            fragment = (Fragment) fragmentClass.newInstance();
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Insert the fragment by replacing any existing fragment
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.fl_fragment_placeholder, fragment).commit();
        fragmentManager.executePendingTransactions();
        // Highlight the selected item has been done by NavigationView
        menuItem.setChecked(true);
        // Set action bar title
        setTitle(menuItem.getTitle());
        // Close the ic_search_navigation drawer
        mDrawerLayout.closeDrawers();
    }


    private ActionBarDrawerToggle setupDrawerToggle() {
        return new ActionBarDrawerToggle(this, mDrawerLayout, mToolbar, R.string.drawer_open, R.string.drawer_close);
    }
}
