package com.example.skipthegas;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

public class RiderProfileEditable extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.rider_profile_edit_layout);
    }

    public void cancel(View view) {
        Intent intent = new Intent(this, RiderProfileActivity.class);
        startActivity(intent);
    }

    public void submitEdit(View view) {
        Intent intent = new Intent(this, RiderProfileActivity.class);
        startActivity(intent);
        //passes new info back to view-only profile
    }
}
