package com.app.salati;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.hardware.GeomagneticField;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.common.util.concurrent.ListenableFuture;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class QiblaActivity extends AppCompatActivity implements SensorEventListener, LocationListener {
    private static final String TAG = "QiblaActivity";
    private static final int PERMISSION_REQUEST_CODE = 123;
    private static final double KAABA_LATITUDE = 21.4225;
    private static final double KAABA_LONGITUDE = 39.8262;

    private SensorManager sensorManager;
    private Sensor accelerometer;
    private Sensor magnetometer;
    private LocationManager locationManager;
    private PreviewView previewView;
    private ImageView qiblaArrow;
    private TextView angleText;
    private ExecutorService cameraExecutor;

    private float[] lastAccelerometer = new float[3];
    private float[] lastMagnetometer = new float[3];
    private boolean haveSensor = false;
    private boolean haveSensor2 = false;
    private float[] rotationMatrix = new float[9];
    private float[] orientation = new float[3];
    private double qiblaAngle = 0.0;
    private Location currentLocation;
    private static final float INITIAL_ARROW_ROTATION = 140f;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qibla);

        initializeViews();
        checkPermissions();
        initializeSensors();
        cameraExecutor = Executors.newSingleThreadExecutor();
    }

    private void initializeViews() {
        previewView = findViewById(R.id.previewView);
        qiblaArrow = findViewById(R.id.qiblaArrow);
        angleText = findViewById(R.id.angleText);
        qiblaArrow.setRotation(INITIAL_ARROW_ROTATION);

    }

    private void initializeSensors() {
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        magnetometer = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);

        if (accelerometer == null || magnetometer == null) {
            Toast.makeText(this, "Required sensors not available", Toast.LENGTH_LONG).show();
            finish();
            return;
        }
    }

    private void startCamera() {
        ListenableFuture<ProcessCameraProvider> cameraProviderFuture =
                ProcessCameraProvider.getInstance(this);

        cameraProviderFuture.addListener(() -> {
            try {
                ProcessCameraProvider cameraProvider = cameraProviderFuture.get();

                Preview preview = new Preview.Builder().build();
                preview.setSurfaceProvider(previewView.getSurfaceProvider());

                CameraSelector cameraSelector = new CameraSelector.Builder()
                        .requireLensFacing(CameraSelector.LENS_FACING_BACK)
                        .build();

                try {
                    cameraProvider.unbindAll();
                    cameraProvider.bindToLifecycle(this, cameraSelector, preview);
                } catch (Exception e) {
                    Log.e(TAG, "Use case binding failed", e);
                    Toast.makeText(this, "Camera error: " + e.getMessage(),
                            Toast.LENGTH_SHORT).show();
                }
            } catch (ExecutionException | InterruptedException e) {
                Log.e(TAG, "Failed to get camera provider", e);
                Toast.makeText(this, "Camera initialization failed",
                        Toast.LENGTH_SHORT).show();
            }
        }, ContextCompat.getMainExecutor(this));
    }

    private void checkPermissions() {
        String[] permissions = {
                Manifest.permission.CAMERA,
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
        };

        boolean needRequest = false;
        for (String permission : permissions) {
            if (ContextCompat.checkSelfPermission(this, permission)
                    != PackageManager.PERMISSION_GRANTED) {
                needRequest = true;
                break;
            }
        }

        if (needRequest) {
            ActivityCompat.requestPermissions(this, permissions, PERMISSION_REQUEST_CODE);
        } else {
            startLocationUpdates();
            startCamera();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_CODE) {
            boolean allGranted = true;
            for (int result : grantResults) {
                if (result != PackageManager.PERMISSION_GRANTED) {
                    allGranted = false;
                    break;
                }
            }
            if (allGranted) {
                startLocationUpdates();
                startCamera();
            } else {
                Toast.makeText(this, "Permissions required for Qibla compass",
                        Toast.LENGTH_LONG).show();
                finish();
            }
        }
    }

    private void startLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            if (locationManager != null) {
                try {
                    locationManager.requestLocationUpdates(
                            LocationManager.GPS_PROVIDER, 1000, 1, this);
                } catch (SecurityException e) {
                    Log.e(TAG, "Error requesting location updates: " + e.getMessage());
                }
            } else {
                Log.e(TAG, "LocationManager is null, cannot request location updates.");
            }
        } else {
            Log.e(TAG, "Location permission is not granted.");
        }
    }

    private float declination = 0.0f;

    private void calculateQiblaDirection() {
        if (currentLocation != null) {
            // Convert to radians
            double currentLat = Math.toRadians(currentLocation.getLatitude());
            double currentLng = Math.toRadians(currentLocation.getLongitude());
            double kaabaLat = Math.toRadians(KAABA_LATITUDE);
            double kaabaLng = Math.toRadians(KAABA_LONGITUDE);

            // Calculate Qibla direction using great circle formula
            double y = Math.sin(kaabaLng - currentLng);
            double x = Math.cos(currentLat) * Math.tan(kaabaLat) -
                    Math.sin(currentLat) * Math.cos(kaabaLng - currentLng);

            // Calculate initial bearing
            qiblaAngle = Math.toDegrees(Math.atan2(y, x));

            // Normalize to 0-360
            qiblaAngle = (qiblaAngle + 360.0) % 360.0;

            // Get magnetic declination for the current location
            GeomagneticField geoField = new GeomagneticField(
                    (float) currentLocation.getLatitude(),
                    (float) currentLocation.getLongitude(),
                    (float) currentLocation.getAltitude(),
                    System.currentTimeMillis());

            declination = geoField.getDeclination();

            Log.d(TAG, "Qibla angle: " + qiblaAngle + ", Declination: " + declination);
        }
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            System.arraycopy(event.values, 0, lastAccelerometer, 0, event.values.length);
            haveSensor = true;
        } else if (event.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD) {
            System.arraycopy(event.values, 0, lastMagnetometer, 0, event.values.length);
            haveSensor2 = true;
        }

        if (haveSensor && haveSensor2) {
            SensorManager.getRotationMatrix(rotationMatrix, null, lastAccelerometer, lastMagnetometer);
            SensorManager.getOrientation(rotationMatrix, orientation);

            // Get azimuth angle (rotation around the -z axis)
            float azimuthInRadians = orientation[0];
            // Convert to degrees
            float azimuthInDegrees = (float) Math.toDegrees(azimuthInRadians);

            // Apply magnetic declination correction
            float correctedAzimuth = azimuthInDegrees + declination;

            // Calculate the rotation needed
            float rotation = (float) (correctedAzimuth - qiblaAngle);

            // Normalize to 0-360
            rotation = (rotation + 360) % 360;

            // Apply rotation to arrow with the initial rotation offset
            qiblaArrow.setRotation(INITIAL_ARROW_ROTATION - rotation);

            // Display the bearing to Qibla
            float bearingToQibla = (360 - rotation) % 360;
            angleText.setText(String.format("%.1f°", bearingToQibla));

            Log.d(TAG, String.format("Azimuth: %.1f, Corrected: %.1f, Rotation: %.1f",
                    azimuthInDegrees, correctedAzimuth, rotation));
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        haveSensor = sensorManager.registerListener(this, accelerometer,
                SensorManager.SENSOR_DELAY_UI);
        haveSensor2 = sensorManager.registerListener(this, magnetometer,
                SensorManager.SENSOR_DELAY_UI);
    }

    @Override
    protected void onPause() {
        super.onPause();
        sensorManager.unregisterListener(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        cameraExecutor.shutdown();
    }

    // Required interface implementations
    @Override
    public void onLocationChanged(@NonNull Location location) {
        currentLocation = location;
        Log.d(TAG, "Location changed: " + location.toString());
        calculateQiblaDirection();
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {}
}
