package com.example.mypc.fastfoodfinder.activity;

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
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.mypc.fastfoodfinder.ui.main.MainMapFragment;
import com.example.mypc.fastfoodfinder.ui.profile.ProfileFragment;
import com.example.mypc.fastfoodfinder.R;
import com.example.mypc.fastfoodfinder.ui.main.SearchFragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;
import io.realm.Realm;

public class MainActivity extends AppCompatActivity {

    public static String PACKAGE_NAME;

    @BindView(R.id.nav_view) NavigationView mNavigationView;
    @BindView(R.id.drawer_layout) DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mDrawerToggle;
    @BindView(R.id.toolbar) Toolbar mToolbar;
    View mHeaderLayout;
    SearchView mSearchView;
    LinearLayout mNavHeaderContainer;
    CircleImageView mNavHeaderAvatar;
    TextView mNavHeaderName;
    TextView mNavHeaderScreenName;
    Button mNavHeaderSignIn;

    // Firebase instance variables
    private FirebaseAuth mFirebaseAuth;
    private FirebaseUser mFirebaseUser;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Realm.init(MainActivity.this);
        ButterKnife.bind(this);

        setSupportActionBar(mToolbar);

        PACKAGE_NAME = getApplicationContext().getPackageName();


        mDrawerToggle = setupDrawerToggle();
        mHeaderLayout = mNavigationView.getHeaderView(0);
        mNavHeaderContainer = (LinearLayout) mHeaderLayout.findViewById(R.id.nav_header_container);
        mNavHeaderAvatar = (CircleImageView) mHeaderLayout.findViewById(R.id.iv_nav_header_avatar);
        mNavHeaderName = (TextView) mHeaderLayout.findViewById(R.id.tv_nav_header_name);
        mNavHeaderScreenName = (TextView)mHeaderLayout.findViewById(R.id.tv_nav_header_screenname);
        mNavHeaderSignIn = (Button) mHeaderLayout.findViewById(R.id.btn_nav_header_signin);


        mNavigationView.getMenu().getItem(0).setChecked(true);
        mNavigationView.setCheckedItem(R.id.menu_action_map);
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.fl_fragment_placeholder, MainMapFragment.newInstance()).commit();

        // Initialize Firebase Auth
        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseUser = mFirebaseAuth.getCurrentUser();
        if (mFirebaseUser == null) {
            mNavHeaderName.setVisibility(View.GONE);
            mNavHeaderScreenName.setVisibility(View.GONE);
            mNavHeaderSignIn.setVisibility(View.VISIBLE);
        }
        else
        {
            Glide.with(MainActivity.this)
                    .load(mFirebaseUser.getPhotoUrl())
                    .into(mNavHeaderAvatar);
        }

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





        // Tie DrawerLayout events to the ActionBarToggle
        mDrawerLayout.addDrawerListener(mDrawerToggle);
    }


    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        mDrawerToggle.syncState();
    }


    public void selectDrawerItem(MenuItem menuItem) {
        // Create a new fragment and specify the fragment to show based on nav item clicked
        Fragment fragment = null;
        Class fragmentClass;
        switch(menuItem.getItemId()) {
            case R.id.menu_action_profile:
                fragmentClass = ProfileFragment.class;
                break;
            case R.id.menu_action_map:
                fragmentClass = MainMapFragment.class;
                break;
            case R.id.menu_action_setting:
                Intent intent = new Intent(MainActivity.this, SettingActivity.class);
                startActivity(intent);
                return;
            default:
                fragmentClass = MainMapFragment.class;
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
        // Close the navigation drawer
        mDrawerLayout.closeDrawers();
    }


    private ActionBarDrawerToggle setupDrawerToggle() {
        return new ActionBarDrawerToggle(this, mDrawerLayout, mToolbar ,R.string.drawer_open, R.string.drawer_close);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        MenuItem searchItem = menu.findItem(R.id.action_search);

        SearchManager searchManager =
                (SearchManager) MainActivity.this.getSystemService(Context.SEARCH_SERVICE);

        if (searchItem != null) {
            mSearchView = (SearchView) MenuItemCompat.getActionView(searchItem);
        }
        if (mSearchView != null) {
            mSearchView.setSearchableInfo(searchManager.getSearchableInfo(MainActivity.this.getComponentName()));

            mSearchView.setQueryHint("Nhập tên cửa hàng...");
            mSearchView.setBackgroundColor(Color.parseColor("#E53935"));
            EditText searchFeild = (EditText) mSearchView.findViewById(android.support.v7.appcompat.R.id.search_src_text);
            searchFeild.setHintTextColor(ContextCompat.getColor(MainActivity.this, R.color.colorHintText));
            searchFeild.setTextColor(Color.WHITE);
        }


        MenuItemCompat.OnActionExpandListener expandListener = new MenuItemCompat.OnActionExpandListener() {
            @Override
            public boolean onMenuItemActionExpand(MenuItem menuItem) {
                Toast.makeText(MainActivity.this, "On click", Toast.LENGTH_SHORT).show();
                FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                ft.setCustomAnimations(android.R.anim.slide_in_left, android.R.anim.slide_out_right);

                SearchFragment newFragment = SearchFragment.newInstance();

                View view = findViewById(R.id.fragment_search_placeholder);
                view.setVisibility(View.VISIBLE);
                ft.replace(R.id.fragment_search_placeholder, newFragment, "blankFragment");

// Start the animated transition.
                ft.commit();

                return true;
            }

            @Override
            public boolean onMenuItemActionCollapse(MenuItem menuItem) {

                Toast.makeText(MainActivity.this, "On close", Toast.LENGTH_SHORT).show();
                getSupportFragmentManager().beginTransaction().
                        remove(getSupportFragmentManager().findFragmentById(R.id.fragment_search_placeholder))
                        .commit();
                return true;
            }
        };


        MenuItemCompat.setOnActionExpandListener(searchItem, expandListener);

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
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        // Pass any configuration change to the drawer toggles
        mDrawerToggle.onConfigurationChanged(newConfig);
    }


}
