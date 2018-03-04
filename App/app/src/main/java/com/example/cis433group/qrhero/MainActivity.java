package com.example.cis433group.qrhero;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.SparseArray;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.TextView;
import android.widget.Toolbar;

import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.gms.vision.barcode.BarcodeDetector;

import java.io.IOException;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                == PackageManager.PERMISSION_DENIED){
            ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.CAMERA}, 10);
        }
        final SurfaceView cameraView = (SurfaceView)findViewById(R.id.camera);
        final TextView barcodeInfo = (TextView)findViewById(R.id.link);
        BarcodeDetector codeDetector = new BarcodeDetector.Builder(this).setBarcodeFormats(Barcode.QR_CODE).build();

        final CameraSource cameraSource = new CameraSource.Builder(this, codeDetector).setRequestedPreviewSize(480,640)
                .setRequestedFps(30.0f).build();
        cameraView.getHolder().addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder surfaceHolder) {
                try{
                    cameraSource.start(cameraView.getHolder());
                } catch (IOException ioe) {
                    Log.e("CAMERA SOURCE", ioe.getMessage());
                }
            }

            @Override
            public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i1, int i2) {

            }

            @Override
            public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
                cameraSource.stop();
            }
        });

        codeDetector.setProcessor(new Detector.Processor<Barcode>() {
            @Override
            public void release() {

            }

            @Override
            public void receiveDetections(Detector.Detections<Barcode> detections) {
                final SparseArray barcodes = detections.getDetectedItems();
                if(barcodes.size() != 0 ){
                    barcodeInfo.post(new Runnable() {
                        @Override
                        public void run() {
                            Barcode cooode = (Barcode) barcodes.valueAt(0);
                            System.out.println(cooode.displayValue);
                            barcodeInfo.setText(cooode.displayValue);
                            Intent unshorten = new Intent(getApplicationContext(), UnshortenActivity.class);
                            unshorten.putExtra("link", cooode.displayValue);
                            startActivity(unshorten);
                        }
                    });
                }
            }
        });
    }
}