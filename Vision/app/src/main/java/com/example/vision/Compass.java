package com.example.vision;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

public class Compass implements SensorEventListener {
    // 必须实现这个SensorEventListener
    private static final String TAG = "Compass";

    public interface CompassListener {
        // 定义了一个接口，说一下到底要实现哪些函数
        // 主要作用是当值发生变化后的通知
        void onNewAzimuth(float azimuth, float light);
    }

    private CompassListener listener;

    private SensorManager sensorManager;
    private Sensor gsensor; // 加速度传感器
    private Sensor msensor; // 霍尔传感器/磁场传感器
    private Sensor lsensor; // 光线传感器

    private float[] mGravity = new float[3];
    private float[] mGeomagnetic = new float[3];
    private float[] R = new float[9];
    private float[] I = new float[9];

    private float azimuth;
    private float azimuthFix;
    private float light;    // 光强

    public Compass(Context context) {
        // 构造函数
        sensorManager = (SensorManager) context
                .getSystemService(Context.SENSOR_SERVICE);  // 获取管理器
        gsensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);    // 加速度传感器
        msensor = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);   // 磁场传感器
        lsensor = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);            // 光线传感器
    }

    public void start() {
        // 需要注册，其实注册了就表示可以获取数据了
        // 传感器类型，采样频率
        sensorManager.registerListener(this, gsensor,
                SensorManager.SENSOR_DELAY_GAME);
        sensorManager.registerListener(this, msensor,
                SensorManager.SENSOR_DELAY_GAME);
        sensorManager.registerListener(this, lsensor, SensorManager.SENSOR_DELAY_GAME);
    }

    // 注销，也就是不接受数据
    public void stop() {
        sensorManager.unregisterListener(this);
    }

    public void setAzimuthFix(float fix) {
        azimuthFix = fix;
    }

    public void resetAzimuthFix() {
        setAzimuthFix(0);
    }

    public void setListener(CompassListener l) {
        listener = l;
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        // 传感器数值发生变化
        final float alpha = 0.97f;

        synchronized (this) {
            if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {// 当前是哪个传感器

                mGravity[0] = alpha * mGravity[0] + (1 - alpha)
                        * event.values[0];
                mGravity[1] = alpha * mGravity[1] + (1 - alpha)
                        * event.values[1];
                mGravity[2] = alpha * mGravity[2] + (1 - alpha)
                        * event.values[2];

                // mGravity = event.values;

                // Log.e(TAG, Float.toString(mGravity[0]));
            }

            if (event.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD) {
                // mGeomagnetic = event.values;

                mGeomagnetic[0] = alpha * mGeomagnetic[0] + (1 - alpha)
                        * event.values[0];
                mGeomagnetic[1] = alpha * mGeomagnetic[1] + (1 - alpha)
                        * event.values[1];
                mGeomagnetic[2] = alpha * mGeomagnetic[2] + (1 - alpha)
                        * event.values[2];
                // Log.e(TAG, Float.toString(event.values[0]));

            }

            if (event.sensor.getType() == Sensor.TYPE_LIGHT){
                // 如果是光线传感器
                light = event.values[0];
            }

            boolean success = SensorManager.getRotationMatrix(R, I, mGravity,
                    mGeomagnetic);
            if (success) {
                float orientation[] = new float[3];
                SensorManager.getOrientation(R, orientation);
                // Log.d(TAG, "azimuth (rad): " + azimuth);
                azimuth = (float) Math.toDegrees(orientation[0]); // orientation
                azimuth = (azimuth + azimuthFix + 360) % 360;
                // Log.d(TAG, "azimuth (deg): " + azimuth);
                if (listener != null) {
                    listener.onNewAzimuth(azimuth, light);
                }
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // 传感器精度发生变化
    }
}
