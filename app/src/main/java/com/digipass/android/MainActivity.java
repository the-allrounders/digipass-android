package com.digipass.android;

import android.Manifest;
import android.animation.ValueAnimator;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.net.ConnectivityManager;
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
import android.view.animation.DecelerateInterpolator;

import com.digipass.android.helpers.NetworkReceiver;
import com.digipass.android.singletons.API;
import com.digipass.android.singletons.Data;

import java.io.Serializable;

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
    public Boolean animateDrawerToggle = true;

    // The BroadcastReceiver that tracks network connectivity changes.
    private NetworkReceiver receiver;

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
                    if (!showHomeAsUp) {
                        toggle.syncState();
                    }
                }
            });

        // Register BroadcastReceiver to track connection changes.
        IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        receiver = new NetworkReceiver(this);
        this.registerReceiver(receiver, filter);
    }

    @Override
    protected void onStart() {
        super.onStart();

        Intent service_intent = new Intent(this, BackgroundService.class);
        bindService(service_intent, backgroundServiceConnection, Context.BIND_AUTO_CREATE);
    }

    public void resetDrawerToggle() {
        showHomeAsUp = false;
        toggle.syncState();
        if (animateDrawerToggle) {
            animDrawerToggle(1, 0);
        } else {
            animDrawerToggle(1, 0, 0);
        }
    }

    public void animDrawerToggle(int start, int end) {
        animDrawerToggle(start, end, 500);
    }

    public void animDrawerToggle(int start, int end, int delay) {
        if (delay > 0) {
            ValueAnimator anim = ValueAnimator.ofFloat(start, end);
            anim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator valueAnimator) {
                    float slideOffset = (Float) valueAnimator.getAnimatedValue();
                    toggle.onDrawerSlide(mDrawer, slideOffset);
                }
            });
            anim.setInterpolator(new DecelerateInterpolator());
            anim.setDuration(delay);
            anim.start();
        } else {
            toggle.onDrawerSlide(mDrawer, end);
        }
    }

    public void initMainFragment() {
        onNavigationItemSelected(navigationView.getMenu().getItem(0));
    }

    private ServiceConnection backgroundServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName className, IBinder service) {
            // We've bound to LocalService, cast the IBinder and get LocalService instance
            BackgroundService.LocalBinder binder = (BackgroundService.LocalBinder) service;
            backgroundService = binder.getService();
            boundWithService = true;
            backgroundService.getBTScanner().addListner(new Runnable(){
                public void run() {
                    Log.d("MainActivity", "Scanner callback received.");
                    API.getInstance(getApplicationContext()).GetJSONResult();
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
            animateDrawerToggle = true;
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

        switch (id) {
            default:
            case R.id.nav_activity_log:
            case R.id.nav_contacts:
            case R.id.nav_settings:
            case R.id.nav_help_feedback:
            case R.id.nav_home:
                fragmentClass = HomeFragment.class;
                bundle.putSerializable("data", (Serializable)Data.GetInstance(this).GetHomeLists());
                break;
            case R.id.nav_preferences:
                fragmentClass = PreferencesFragment.class;
                bundle.putSerializable("data", (Serializable)Data.GetInstance(this).GetPreferences("0"));
                bundle.putString("key", "0");
                bundle.putString("title", getResources().getString(R.string.title_preferences));
                break;
            case R.id.nav_permissions:
                fragmentClass = PermissionsFragment.class;
                bundle.putSerializable("data", (Serializable)Data.GetInstance(this).GetRequestsList("0"));
                bundle.putString("key", "0");
                bundle.putString("title", getResources().getString(R.string.title_permissions));
                break;
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
