package com.example.skipthegas;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;

import javax.annotation.Nullable;


/**
 * This a class that governs the payment screen, where a QR code is generated for payment transfer
 */
public class PaymentActivity extends AppCompatActivity {
    /**
     * onCreate method for PaymentActivity class
     * @param savedInstanceState
     */
    FirebaseFirestore firebaseFirestore;
    FirebaseAuth firebaseAuth;
    FirebaseUser currentUser;
    ImageView QR_Image;
    Button ratingButton;
    QRCodeWriter writer;
    TextView fareView;

    String requestID;
    String riderEmail;
    double fare;
    double currentBal;

    String TAG = "PaymentActivity:";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.payment_layout);

        fareView = findViewById(R.id.ride_fare);
        firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
        currentUser = firebaseAuth.getCurrentUser();

        Intent intent = getIntent();
        requestID = intent.getStringExtra("request_Id");
        if (currentUser!=null) {
            riderEmail = currentUser.getEmail();
        }

        firebaseFirestore
                .collection("all_requests")
                .document(requestID)
                .addSnapshotListener(new EventListener<DocumentSnapshot>() {
                    @Override
                    public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                        if (e!=null) {
                            Log.i(TAG,"error occurred:"+e.getMessage());
                            return;
                        }
                        if (documentSnapshot != null && documentSnapshot.exists()) {
                            String strFare = (String) documentSnapshot.get("est_fare");
                            assert strFare != null;
                            Log.d(TAG,strFare);
                            fareView.setText(strFare);
                        } else {
                            Log.i(TAG,"Document does not exist");
                        }
                    }
                });
        Toast.makeText(this, "fare: "+fare, Toast.LENGTH_SHORT).show();
        // get the current balance of the rider
        firebaseFirestore
                .collection("users")
                .document(riderEmail)
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot snapshot = task.getResult();
                            if (snapshot!=null && snapshot.exists()){
                                currentBal = (double)snapshot.get("QR_bucks");
                            } else {
                                Log.i(TAG,"document does not exist");
                            }
                        }
                    }
                });

        // charge from riders balance
        firebaseFirestore
                .collection("users")
                .document(riderEmail)
                .update("QR_bucks",currentBal - fare);

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
        feedBackIntent.putExtra("request_Id",requestID);
        feedBackIntent.putExtra("fare",fare);
        startActivity(feedBackIntent);
    }
}
