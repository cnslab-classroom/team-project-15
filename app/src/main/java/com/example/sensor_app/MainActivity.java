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

    private TextView tvAccelerometer, tvGyroscope, tvCollision, tvFall;

    // 감지 변수
    private int crash_detected = 0; // 충돌 감지 변수
    private int fall_detected = 0;  // 낙하 감지 변수

    // 충돌 및 낙하 임계값
    private static final float COLLISION_THRESHOLD = 83.9f; // 충돌 기준
    private static final float FALL_THRESHOLD = 0.2f;       // 낙하 기준

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // TextView 초기화
        tvAccelerometer = findViewById(R.id.tv_accelerometer);
        tvGyroscope = findViewById(R.id.tv_gyroscope);
        tvCollision = findViewById(R.id.tv_collision);
        tvFall = findViewById(R.id.tv_fall);

        // SensorManager 초기화
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
            sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_UI);
        }
        if (gyroscope != null) {
            sensorManager.registerListener(this, gyroscope, SensorManager.SENSOR_DELAY_UI);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        // 센서 리스너 해제
        sensorManager.unregisterListener(this);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            // 가속도 데이터 처리
            float ax = event.values[0];
            float ay = event.values[1];
            float az = event.values[2];

            // 가속도의 크기 계산
            double acc_total = Math.sqrt(ax * ax + ay * ay + az * az);

            // 가속도 데이터 표시
            tvAccelerometer.setText(String.format("가속도 센서: X=%.2f, Y=%.2f, Z=%.2f, Total=%.2f", ax, ay, az, acc_total));

            // 충돌 감지
            if (acc_total > COLLISION_THRESHOLD) {
                crash_detected = 1;
                tvCollision.setText("충돌 감지됨!");
            } else {
                crash_detected = 0;
                tvCollision.setText("충돌 감지되지 않음");
            }

            // 낙하 감지
            if (Math.abs(ax) <= FALL_THRESHOLD && Math.abs(ay) <= FALL_THRESHOLD && Math.abs(az) <= FALL_THRESHOLD) {
                fall_detected = 1;
                tvFall.setText("낙하 감지됨!");
            } else {
                fall_detected = 0;
                tvFall.setText("낙하 감지되지 않음");
            }
        }

        if (event.sensor.getType() == Sensor.TYPE_GYROSCOPE) {

            float gx = event.values[0];
            float gy = event.values[1];
            float gz = event.values[2];

            // 각속도의 크기 계산
            double gcc_total = Math.sqrt(gx * gx + gy * gy + gz * gz);

            // 자이로 데이터 표시
            tvGyroscope.setText(String.format("자이로 센서: X=%.2f, Y=%.2f, Z=%.2f, Total=%.2f", gx, gy, gz, gcc_total));
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // 정확도 변경 처리 (필요시 구현)
    }
}
