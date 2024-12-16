package com.example.sensor_app;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity implements SensorEventListener {

    private SensorManager sensorManager;
    private Sensor accelerometer;
    private Sensor gyroscope;

    private TextView tvAccelerometer, tvGyroscope;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        tvAccelerometer = findViewById(R.id.tv_accelerometer);
        tvGyroscope = findViewById(R.id.tv_gyroscope);

        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);


        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        gyroscope = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);

        if (accelerometer == null) {
            tvAccelerometer.setText("가속도 센서를 지원하지 않습니다.");
        }
        if (gyroscope == null) {
            tvGyroscope.setText("자이로 센서를 지원하지 않습니다.");
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (accelerometer != null) {
            // SensorManager.SENSOR_DELAY_UI는 적당한 업데이트 속도(약 60ms)로 센서값을 받아오는 딜레이를 의미
            sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_UI);
        }
        if (gyroscope != null) {
            sensorManager.registerListener(this, gyroscope, SensorManager.SENSOR_DELAY_UI);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        // 액티비티가 비활성화될 때 센서 리스너 해제
        sensorManager.unregisterListener(this);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        // 센서 별 값 처리
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            float x = event.values[0];
            float y = event.values[1];
            float z = event.values[2];
            String accText = String.format("가속도 센서: X = %.2f, Y = %.2f, Z = %.2f", x, y, z);
            tvAccelerometer.setText(accText);
        }

        if (event.sensor.getType() == Sensor.TYPE_GYROSCOPE) {
            float x = event.values[0];
            float y = event.values[1];
            float z = event.values[2];
            String gyroText = String.format("자이로 센서: X = %.2f, Y = %.2f, Z = %.2f", x, y, z);
            tvGyroscope.setText(gyroText);
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // 센서 정확도 변경 시 처리할 일이 있으면 구현
    }
}
