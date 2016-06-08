package com.digipass.android;

import android.Manifest;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
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
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;

import com.digipass.android.singletons.Data;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, HomeFragment.OnFragmentInteractionListener, PreferencesFragment.OnFragmentInteractionListener {

    public static final String LOG_TAG = "Digipass";
    BackgroundService backgroundService;
    boolean boundWithService = false;

    DrawerLayout mDrawer;
    NavigationView navigationView;

    Fragment fragment;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mDrawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, mDrawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        mDrawer.setDrawerListener(toggle);
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
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED){
            // If not, ask
            ActivityCompat.requestPermissions(
                    this,
                    new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                    1);
        }

//        Intent service_intent = new Intent(this, BackgroundService.class);
//        bindService(service_intent, backgroundServiceConnection, Context.BIND_AUTO_CREATE);
    }

    public void initMainFragment() {
        onNavigationItemSelected(navigationView.getMenu().getItem(1));
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    private ServiceConnection backgroundServiceConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName className,
                                       IBinder service) {
            // We've bound to LocalService, cast the IBinder and get LocalService instance
            BackgroundService.LocalBinder binder = (BackgroundService.LocalBinder) service;
            backgroundService = binder.getService();
            boundWithService = true;
            backgroundService.getBTScanner().addListner(onScannerChange);
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            boundWithService = false;
        }
    };

    private Runnable onScannerChange = new Runnable(){
        public void run() {
            Log.d("MainActivity", "Scanner callback recieved.");
        }
    };

    @Override
    public void onBackPressed() {
        if (mDrawer.isDrawerOpen(GravityCompat.START)) {
            mDrawer.closeDrawer(GravityCompat.START);
        } else {
            int count = getSupportFragmentManager().getBackStackEntryCount();

            if (count == 0) {
                super.onBackPressed();
            } else {
                getSupportFragmentManager().popBackStack();
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
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

        mDrawer.closeDrawer(GravityCompat.START);

        try {
            fragment = (Fragment) fragmentClass.newInstance();
            fragment.setArguments(bundle);
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Insert the fragment by replacing any existing fragment
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.main_content, fragment).addToBackStack(null).commit();

        // Highlight the selected item has been done by NavigationView
        item.setChecked(true);
        // Set action bar title
        setTitle(item.getTitle());
        // Close the navigation drawer
        mDrawer.closeDrawers();
        return true;
    }

    public void StartFragment(Class fragmentClass, ArrayList data) {
        try {
            Bundle bundle = new Bundle();
            bundle.putParcelableArrayList("data", data);
            fragment = (Fragment) fragmentClass.newInstance();
            fragment.setArguments(bundle);
            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction().replace(R.id.main_content, fragment).addToBackStack(null).commit();

            mDrawer.closeDrawers();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }
}
