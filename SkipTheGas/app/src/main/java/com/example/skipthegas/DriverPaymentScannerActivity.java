package com.example.skipthegas;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.widget.Toast;

import com.google.zxing.Result;

import me.dm7.barcodescanner.zxing.ZXingScannerView;

import static android.Manifest.permission.CAMERA;

public class DriverPaymentScannerActivity extends AppCompatActivity implements ZXingScannerView.ResultHandler {
    private static final int REQUEST_CAMERA = 1;
    private static ZXingScannerView scannerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

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

    public boolean checkPermission() {

        return (ContextCompat.checkSelfPermission(DriverPaymentScannerActivity.this, CAMERA)== PackageManager.PERMISSION_GRANTED);

    }

    public void requestPermission() {

        ActivityCompat.requestPermissions(this, new String[]{CAMERA},REQUEST_CAMERA);

    }

    public void displayAlertDialog(String message, DialogInterface.OnClickListener listener) {
        new AlertDialog.Builder(DriverPaymentScannerActivity.this)
                .setMessage(message)
                .setPositiveButton("OK",listener)
                .setNegativeButton("Cancel",null)
                .create().show();
    }

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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        scannerView.stopCamera();
    }

    @Override // interface :--> ZXingScannerView.ResultHandler
    public void handleResult(final Result result) {
        String scanResult = result.getText();

        new AlertDialog.Builder(this).setTitle("Scan Result")
                .setMessage(scanResult)
                .setNeutralButton("Continue", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        scannerView.resumeCameraPreview(DriverPaymentScannerActivity.this);
                    }
                })
                .setPositiveButton("Accept", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                })
                .create().show();
    }
}
