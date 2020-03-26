package com.example.skipthegas;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.navigation.NavigationView;
/**
 * This is a class that implements the drawer style menu for the driver
 */
public class DriverDrawerActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private DrawerLayout driverDrawer;

    /**
     * onCreate method for DriverDrawerActivity class
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driver_drawer);

        Toolbar toolbar = findViewById(R.id.driver_tool_bar);
        setSupportActionBar(toolbar);

        driverDrawer = findViewById(R.id.driver_drawer_layout);
        NavigationView navigationView = findViewById(R.id.driver_nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, driverDrawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        driverDrawer.addDrawerListener(toggle);
        toggle.syncState();

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction().replace(R.id.driver_fragment_container, new DriverMapFragment()).commit();
            navigationView.setCheckedItem(R.id.nav_map);
        }
    }

    /**
     * Method is called when navigation item is selected in the DriverDrawerActivity class
     * @param menuItem
     * @return
     */
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case R.id.nav_map:
                getSupportFragmentManager().beginTransaction().replace(R.id.driver_fragment_container, new DriverMapFragment()).commit();
                break;
            case R.id.nav_request:
                getSupportFragmentManager().beginTransaction().replace(R.id.driver_fragment_container, new DriverRequestFragment()).commit();
//                Intent activeRequestIntent = new Intent(this, ActiveRequestsActivity.class);
//                startActivity(activeRequestIntent);
                break;
            case R.id.nav_profile:
                getSupportFragmentManager().beginTransaction().replace(R.id.driver_fragment_container, new DriverDrawerProfileFragment()).commit();

                break;
            case R.id.nav_active_request:
                getSupportFragmentManager().beginTransaction().replace(R.id.driver_fragment_container, new DriverActiveRequestFragment()).commit();

                break;
        }

        driverDrawer.closeDrawer(GravityCompat.START);

        return true;
    }

    /**
     * Method is called when the back button is pressed
     */
    @Override
    public void onBackPressed() {
        if (driverDrawer.isDrawerOpen(GravityCompat.START)) {
            driverDrawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }

    }
}
