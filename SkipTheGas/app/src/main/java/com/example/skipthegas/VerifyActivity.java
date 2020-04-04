package com.example.skipthegas;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
/**
 * This is a class that that verifies an email after registering
 */
public class VerifyActivity extends AppCompatActivity {
    Button continueButton;

    /**
     * onCreate method for VerifyActivity
     * Retrieves and displays the associated layout file
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verify);

        continueButton = findViewById(R.id.verify_continue_button);
        continueButton.setOnClickListener(new View.OnClickListener() {
            /**
             * Method invoked when the continue button is clicked
             * Redirects to the Selection Activity page
             * @param v
             */
            @Override
            public void onClick(View v) {
                Intent selectionPageIntent = new Intent(getApplicationContext(), SelectionActivity.class);
                startActivity(selectionPageIntent);
            }
        });
    }
}
