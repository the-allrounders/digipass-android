package com.digipass.android;

import android.Manifest;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;

import com.digipass.android.singletons.Data;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, HomeFragment.OnFragmentInteractionListener, PreferencesFragment.OnFragmentInteractionListener {

    public static final String LOG_TAG = "Digipass";
    BackgroundService backgroundService;
    boolean boundWithService = false;

    DrawerLayout mDrawer;
    NavigationView navigationView;

    Fragment fragment;

    ActionBar actionBar;
    ActionBarDrawerToggle toggle;

    public Boolean showHomeAsUp = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        actionBar = getSupportActionBar();

        mDrawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        toggle = new ActionBarDrawerToggle(this, mDrawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        mDrawer.setDrawerListener(toggle);

        if (toolbar != null) {
            toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (showHomeAsUp) {
                        onBackPressed();
                    } else {
                        mDrawer.openDrawer(GravityCompat.START);
                    }
                }
            });
        }

        toggle.syncState();

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        if (navigationView != null) {
            navigationView.setNavigationItemSelectedListener(this);
            initMainFragment();
        }

        // Start the background service
        Intent intent = new Intent(this, BackgroundService.class);
        startService(intent);

        // Check if we have permission to access location
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED){
            // If not, ask
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, 1);
        }

        getSupportFragmentManager().addOnBackStackChangedListener(
            new FragmentManager.OnBackStackChangedListener() {
                public void onBackStackChanged() {
                    actionBar.setDisplayHomeAsUpEnabled(showHomeAsUp);
                    if (!showHomeAsUp) {
                        toggle.syncState();
                    }
                }
            });
    }

    public void initMainFragment() {
        onNavigationItemSelected(navigationView.getMenu().getItem(1));
    }

    private ServiceConnection backgroundServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName className,
                                       IBinder service) {
            // We've bound to LocalService, cast the IBinder and get LocalService instance
            BackgroundService.LocalBinder binder = (BackgroundService.LocalBinder) service;
            backgroundService = binder.getService();
            boundWithService = true;
            backgroundService.getBTScanner().addListner(new Runnable(){
                public void run() {
                    Log.d("MainActivity", "Scanner callback recieved.");
                }
            });
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            boundWithService = false;
        }
    };

    @Override
    public void onBackPressed() {
        if (mDrawer.isDrawerOpen(GravityCompat.START)) {
            mDrawer.closeDrawer(GravityCompat.START);
        } else {
            showHomeAsUp = false;
            toggle.setDrawerIndicatorEnabled(true);
            super.onBackPressed();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case android.R.id.home:
                if (showHomeAsUp) {
                    super.onBackPressed();
                } else {
                    mDrawer.openDrawer(GravityCompat.START);
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        Class fragmentClass;

        Bundle bundle = new Bundle();

        switch(id) {
            case R.id.nav_home:
                fragmentClass = HomeFragment.class;
                break;
            case R.id.nav_preferences:
                fragmentClass = PreferencesFragment.class;
                bundle.putParcelableArrayList("data", Data.GetInstance(this).GetPreferences("0"));
                break;
            case R.id.nav_permissions:
                fragmentClass = HomeFragment.class;
                break;
            case R.id.nav_activity_log:
                fragmentClass = HomeFragment.class;
                break;
            case R.id.nav_contacts:
                fragmentClass = HomeFragment.class;
                break;
            case R.id.nav_settings:
                fragmentClass = HomeFragment.class;
                break;
            case R.id.nav_help_feedback:
                fragmentClass = HomeFragment.class;
                break;
            default:
                fragmentClass = HomeFragment.class;
        }

        item.setChecked(true);
        showHomeAsUp = false;

        StartFragment(fragmentClass, bundle);
        return true;
    }

    public void StartFragment(Class fragmentClass, Bundle data) {
        try {
            fragment = (Fragment) fragmentClass.newInstance();
            fragment.setArguments(data);
            getSupportFragmentManager().beginTransaction().replace(R.id.main_content, fragment).addToBackStack(null).commit();

            mDrawer.closeDrawers();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        actionBar.setDisplayHomeAsUpEnabled(false);
        toggle.setDrawerIndicatorEnabled(true);
        toggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        toggle.onConfigurationChanged(newConfig);
    }
}
