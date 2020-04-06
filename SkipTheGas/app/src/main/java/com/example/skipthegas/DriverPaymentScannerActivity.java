package com.example.skipthegas;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.zxing.Result;

import java.util.Objects;

import me.dm7.barcodescanner.zxing.ZXingScannerView;

import static android.Manifest.permission.CAMERA;

/**
 * The class allows the driver to receive payment after ride completion
 * Driver can receive payment by scanning the QR code on the rider's phone
 */
public class DriverPaymentScannerActivity extends AppCompatActivity implements ZXingScannerView.ResultHandler {
    private static final int REQUEST_CAMERA = 1;
    private static ZXingScannerView scannerView;
    private static final String TAG = "Message";
    private FirebaseFirestore firebaseFirestore;
    double currentBal;
    FirebaseAuth firebaseAuth;
    String userEmail;

    /**
     * onCreate method for the DriverPaymentScannerActivity
     * Fetches driver's current balance from the database
     * @param savedInstanceState saved Instance
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
        userEmail = Objects.requireNonNull(firebaseAuth.getCurrentUser()).getEmail();

        Intent intent = getIntent();
        currentBal = intent.getDoubleExtra("current_balance", 300.0);

        scannerView = new ZXingScannerView(this);
        setContentView(scannerView);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkPermission()){

                Toast.makeText(this, "Permission Granted", Toast.LENGTH_SHORT).show();

            } else {

                requestPermission();

            }
        }
    }

    /**
     * Method checks is permission is granted for the app to use the device camera
     * @return permission_status
     */
    public boolean checkPermission() {

        return (ContextCompat.checkSelfPermission(DriverPaymentScannerActivity.this, CAMERA)== PackageManager.PERMISSION_GRANTED);

    }

    /**
     * Method invoked if permission hasn't already been granted for the app to use the device camera
     * This method requests permission from the device for camera use
     */
    public void requestPermission() {

        ActivityCompat.requestPermissions(this, new String[]{CAMERA},REQUEST_CAMERA);

    }

    /**
     * Displays a dialog box
     * @param message message
     * @param listener listener
     */
    public void displayAlertDialog(String message, DialogInterface.OnClickListener listener) {
        new AlertDialog.Builder(DriverPaymentScannerActivity.this)
                .setMessage(message)
                .setPositiveButton("OK",listener)
                .setNegativeButton("Cancel",null)
                .create().show();
    }

    /**
     * Method determines what to do once the access permissions for the camera is either granted or denied
     * @param requestCode request code
     * @param permissions permission
     * @param grantResults result
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CAMERA) {

            if (grantResults.length > 0) {

                boolean cameraAccepted = (grantResults[0] == PackageManager.PERMISSION_GRANTED);

                if (cameraAccepted) {

                    Toast.makeText(this, "Permission Granted", Toast.LENGTH_SHORT).show();

                } else {

                    Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show();

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

                        if (shouldShowRequestPermissionRationale(CAMERA)) {

                            displayAlertDialog("You need to allow access for both permission",
                                    (dialogInterface, i) -> requestPermissions(new String[]{CAMERA}, REQUEST_CAMERA));

                        }
                    }
                }
            }
        }
    }

    /**
     * Method that dictates how the scanner behave on resume
     */
    @Override
    protected void onResume() {
        super.onResume();
        if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.M){
            if (checkPermission()){
                if (scannerView == null){
                    scannerView = new ZXingScannerView(this);
                    setContentView(scannerView);
                }
                scannerView.setResultHandler(this);
                scannerView.startCamera();
            }else{
                requestPermission();
            }
        }
    }

    /**
     * Camera scanner is stopped on destroy
     */
    @Override
    protected void onDestroy() {
        Log.d(TAG,"Scanner interrupted");
        super.onDestroy();
        scannerView.stopCamera();
    }

    /**
     * Method scans the rider's QR code
     * Once the scan is successful, a dialog box is shown with the payment received
     * Allows the driver to turn down/cancel offered payment if desired
     * interface :--> ZXingScannerView.ResultHandler
     * @param result result
     */
    @Override
    public void handleResult(final Result result) {
        String scanResult = result.getText();

        double paymentReceived = Double.parseDouble(scanResult); // QR bucks you received for this ride
        Toast.makeText(this, "You earned:"+paymentReceived, Toast.LENGTH_SHORT).show();

        new AlertDialog.Builder(this).setTitle("Receiving Payment")
                .setMessage("QR Bucks: "+scanResult)
                .setNeutralButton("Continue", (dialogInterface, i) -> {
                    // Continue scanning
                    scannerView.resumeCameraPreview(DriverPaymentScannerActivity.this);
                })
                .setPositiveButton("Accept", (dialogInterface, i) -> {
                    firebaseFirestore
                            .collection("users")
                            .document(userEmail)
                            .update("QR_bucks",currentBal + paymentReceived);

                    // jump back to driver activity
                    Intent driverDrawerIntent = new Intent(getApplicationContext(),DriverDrawerActivity.class);
                    startActivity(driverDrawerIntent);
                    finish();
                })
                .create().show();
    }
}
