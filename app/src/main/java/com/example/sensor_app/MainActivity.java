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

    // 충돌 및 의식 상실 감지 변수
    private boolean crashDetected = false;
    private boolean unconsciousDetected = false;
    private boolean checkingUnconscious = false;
    private long unconsciousStartTime = 0L; // 10.5 이하 상태가 시작된 시간 기록용
    private static final float CRASH_THRESHOLD = 83.9f;
    private static final float UNCONSCIOUS_THRESHOLD = 10.5f;
    private static final long UNCONSCIOUS_DURATION = 10_000L; // 10초(10000ms)

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
            sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_UI);
        }
        if (gyroscope != null) {
            sensorManager.registerListener(this, gyroscope, SensorManager.SENSOR_DELAY_UI);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        sensorManager.unregisterListener(this);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            float x = event.values[0];
            float y = event.values[1];
            float z = event.values[2];

            // Root값 계산
            double rootValue = Math.sqrt(x*x + y*y + z*z);

            // 충돌 감지 로직
            if (!crashDetected && rootValue > CRASH_THRESHOLD) {
                crashDetected = true;
                // 여기서 crashDetected를 1로 표현하려면 별도의 int 변수 사용 가능
                // int crashStatus = 1;
            }

            // 충돌 감지 이후 의식 상실 감지 로직
            if (crashDetected && !unconsciousDetected) {
                if (rootValue <= UNCONSCIOUS_THRESHOLD) {
                    // 만약 아직 체크 시작 전이면 시작 시간 기록
                    if (!checkingUnconscious) {
                        checkingUnconscious = true;
                        unconsciousStartTime = System.currentTimeMillis();
                    } else {
                        // 이미 체크 중이라면 현재 시간이 시작 시간으로부터 10초 이상 지났는지 확인
                        long elapsed = System.currentTimeMillis() - unconsciousStartTime;
                        if (elapsed >= UNCONSCIOUS_DURATION) {
                            unconsciousDetected = true;
                            // 여기서 unconsciousDetected를 1로 표현하려면 별도의 int 변수 사용 가능
                            // int unconsciousStatus = 1;
                        }
                    }
                } else {
                    // 10.5 이하 유지 못하면 다시 초기화
                    checkingUnconscious = false;
                }
            }

            String accText = String.format("가속도 센서: X = %.2f, Y = %.2f, Z = %.2f\nroot=%.2f\ncrashDetected=%b\nunconsciousDetected=%b",
                    x, y, z, rootValue, crashDetected, unconsciousDetected);
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
        // 정확도 변경 시 필요시 구현
    }
}
