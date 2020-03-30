package com.example.skipthegas;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;


/**
 * This a class that governs the payment screen, where a QR code is generated for payment transfer
 */
public class PaymentActivity extends AppCompatActivity {
    /**
     * onCreate method for PaymentActivity class
     * @param savedInstanceState
     */

    ImageView QR_Image;
    Button ratingButton;
    QRCodeWriter writer;
    double fare;
    Ride currentRide;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.payment_layout);

        // TODO: ********** ********** GET FARE FROM THE CURRENT RIDE ********** **********
        fare = 25.0;// Default fare
        // TODO: ********** ********** GET FARE FROM THE CURRENT RIDE ********** **********

        QR_Image = findViewById(R.id.imageView5);
        ratingButton = findViewById(R.id.rating_button);
        writer = new QRCodeWriter();
        try {
            BitMatrix bitMatrix = writer.encode(Double.toString(fare), BarcodeFormat.QR_CODE,500,500,null);
            int height = bitMatrix.getHeight();
            int width = bitMatrix.getWidth();

            // Create a Bitmap image with the same size as bitMatrix
            Bitmap bmp = Bitmap.createBitmap(width,height,Bitmap.Config.RGB_565);

            // Fill up the Bitmap bmp with the value of BitMatrix bitMatrix
            for (int x=0; x<width; x++) {
                for (int y=0; y<height; y++) {
                    bmp.setPixel(x, y, bitMatrix.get(x, y) ? Color.BLACK : Color.WHITE);
                }
            }

            // show the bitmap on the screen
            QR_Image.setImageBitmap(bmp);

        } catch (WriterException we) {
            Toast.makeText(this, "Unable to generate QR-code", Toast.LENGTH_SHORT).show();
            we.printStackTrace();
        }
    }

    public void goToRating(View view) {
        Intent feedBackIntent = new Intent(getApplicationContext(),TripFeedBackActivity.class);
        startActivity(feedBackIntent);
    }
}
