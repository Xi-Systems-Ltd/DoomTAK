package com.atakmap.android.doomtak.input;

import static java.lang.Math.abs;
import static java.lang.Math.acos;
import static java.lang.Math.cos;
import static java.lang.Math.sin;
import static java.lang.Math.sqrt;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

public class GyroMouseListener implements SensorEventListener {
    private SensorManager sensorManager;
    private Sensor accelerometer;
    private Sensor gyroscope;

    private double[] gravity = new double[3];

    // Sensitivity for controlling mouse movement.
    private final double sensitivity;

    // Interface for sending mouse movements to the game.
    private MouseMovementListener listener;

    public GyroMouseListener(Context context, double sensitivity) {
        sensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        gyroscope = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
        this.sensitivity = sensitivity;
    }

    public void setMouseMovementListener(MouseMovementListener listener) {
        this.listener = listener;
    }

    public void start() {
        // Register listeners for both sensors.
        sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_GAME);
        sensorManager.registerListener(this, gyroscope, SensorManager.SENSOR_DELAY_GAME);
    }

    public void stop() {
        // Unregister listeners when not needed.
        sensorManager.unregisterListener(this);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            final float alpha = 0.8f;
            gravity[0] = alpha * gravity[0] + (1 - alpha) * event.values[0];
            gravity[1] = alpha * gravity[1] + (1 - alpha) * event.values[1];
            gravity[2] = alpha * gravity[2] + (1 - alpha) * event.values[2];
        } else if (event.sensor.getType() == Sensor.TYPE_GYROSCOPE) {
            double elevationAngle = calculatePhoneElevationAngle(gravity);
            double deltaAz = abs(cos(elevationAngle)) * (-event.values[2]) + abs(sin(elevationAngle)) * (-event.values[0]);
            double deltaEl = event.values[1];
            int mouseX = (int) (deltaAz * sensitivity);
//            int mouseY = (int) (deltaEl * sensitivity); Ignored to not interfere with menu-ing.
            listener.onMouseMove(mouseX, 0);
        }
    }

    public static double calculatePhoneElevationAngle(double[] gravityVector) {
        if (gravityVector.length != 3) {
            throw new IllegalArgumentException("Gravity vector must have three elements.");
        }

        // Normalize gravity vector.
        double magnitude = sqrt(gravityVector[0] * gravityVector[0] +
                gravityVector[1] * gravityVector[1] +
                gravityVector[2] * gravityVector[2]);

        // The angle in radians.
        return acos(gravityVector[2] / magnitude);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // Unused.
    }

    // Interface to send mouse movement events.
    @FunctionalInterface
    public interface MouseMovementListener {
        void onMouseMove(int deltaX, int deltaY);
    }
}
