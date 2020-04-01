package com.example.skipthegas;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;

/**
 * This class implements the drawer style menu for the rider
 */
public class RiderDrawerActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private DrawerLayout riderDrawer;

    /**
     * onCreate method for RiderDrawerActivity class
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rider_drawer);

        Toolbar toolbar = findViewById(R.id.rider_tool_bar);
        setSupportActionBar(toolbar);

        riderDrawer = findViewById(R.id.rider_drawer_layout);
        NavigationView rider_navigationView = findViewById(R.id.rider_nav_view);
        rider_navigationView.setNavigationItemSelectedListener(this);
//        rider_navigationView.getMenu().findItem(R.id.rider_logout).setOnMenuItemClickListener(MenuItem -> {
//            logout();
//            return true;
//        });

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, riderDrawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        riderDrawer.addDrawerListener(toggle);
        toggle.syncState();

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction().replace(R.id.rider_fragment_container, new RiderMapFragment()).commit();
            rider_navigationView.setCheckedItem(R.id.rider_nav_map);
        }
    }

    /**
     * Method is called when the navigation items inside the drawer menu are selected
     * For RiderDrawerActivity class
     * @param menuItem
     * @return
     */
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case R.id.rider_nav_map:
                getSupportFragmentManager().beginTransaction().replace(R.id.rider_fragment_container, new RiderMapFragment()).commit();
                break;
            case R.id.rider_nav_request:
                getSupportFragmentManager().beginTransaction().replace(R.id.rider_fragment_container, new RiderRequestFragment()).commit();
                break;
            case R.id.rider_nav_profile:
                getSupportFragmentManager().beginTransaction().replace(R.id.rider_fragment_container, new RiderDrawerProfileFragment()).commit();
                break;
            case R.id.rider_logout:
                Toast.makeText(this, "Logging out Rider", Toast.LENGTH_SHORT).show();
                logout();
                break;
        }

        riderDrawer.closeDrawer(GravityCompat.START);
        return true;
    }

    /**
     * Method is called when the back button is pressed
     */
    @Override
    public void onBackPressed() {
        if (riderDrawer.isDrawerOpen(GravityCompat.START)) {
            riderDrawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }

    }

    /**
     * This logs out a user upon a button click
     * Changes screens from the rider profile view to the login screen
     */
    public void logout() {
        FirebaseAuth.getInstance().signOut();
        startActivity(new Intent(getApplicationContext(), MainActivity.class));
        finish();
    }
}
