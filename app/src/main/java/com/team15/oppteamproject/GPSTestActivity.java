/*package com.team15.oppteamproject;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class GPSTestActivity extends AppCompatActivity {

    private FusedLocationProviderClient fusedLocationClient;
    private DatabaseReference incidentsRef;
    private ScheduledExecutorService scheduler;
    private String incidentId; // Firebase에 저장할 고유 incident ID

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gps_test);

        // Firebase 및 위치 서비스 초기화
        incidentsRef = FirebaseDatabase.getInstance().getReference("incidents");
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        Button btnSendGPS = findViewById(R.id.btnSendGPS);

        btnSendGPS.setOnClickListener(v -> {
            // 위치 권한 확인
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                    ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 100);
                return;
            }

            // 충돌 발생 시 GPS 데이터 추적 시작
            startLocationTracking();
        });
    }

    // 충돌 발생 후 GPS 데이터를 주기적으로 수집 및 저장
    private void startLocationTracking() {
        // incidentId 생성 및 Firebase에 초기 데이터 저장
        incidentId = incidentsRef.push().getKey();
        FirebaseAuth auth = FirebaseAuth.getInstance();
        String userId = auth.getCurrentUser().getUid();

        Map<String, Object> incidentData = new HashMap<>();
        incidentData.put("user_id", userId);
        incidentData.put("status", "pending");
        incidentData.put("timestamp", System.currentTimeMillis());
        incidentsRef.child(incidentId).setValue(incidentData);

        // GPS 데이터 주기적 수집
        scheduler = Executors.newScheduledThreadPool(1);
        scheduler.scheduleAtFixedRate(() -> {
             if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                    ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }

            fusedLocationClient.getLastLocation().addOnSuccessListener(location -> {
                if (location != null) {
                    double latitude = location.getLatitude();
                    double longitude = location.getLongitude();
                    long timestamp = System.currentTimeMillis();

                    // 위치 데이터를 Firebase에 저장
                    Map<String, Object> locationData = new HashMap<>();
                    locationData.put("latitude", latitude);
                    locationData.put("longitude", longitude);
                    locationData.put("timestamp", timestamp);
                    incidentsRef.child(incidentId).child("locations").push().setValue(locationData);

                    // 로그 출력
                    analyzeMovement(latitude, longitude); // 움직임 분석 호출
                }
            });
        }, 0, 5, TimeUnit.SECONDS); // 5초 간격으로 위치 업데이트
    }

    // 움직임 분석 로직 참고해서 구현하면 될거같습니당
    private void analyzeMovement(double latitude, double longitude) {
        // Firebase에서 위치 데이터를 가져와 분석
        incidentsRef.child(incidentId).child("locations").get().addOnSuccessListener(dataSnapshot -> {
            double totalDistance = 0;
            double lastLatitude = 0;
            double lastLongitude = 0;

            for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                double lat = snapshot.child("latitude").getValue(Double.class);
                double lon = snapshot.child("longitude").getValue(Double.class);

                if (lastLatitude != 0 && lastLongitude != 0) {
                    totalDistance += calculateDistance(lastLatitude, lastLongitude, lat, lon);
                }

                lastLatitude = lat;
                lastLongitude = lon;
            }

            // 로그 출력
            if (totalDistance < 5) {
                Log.d("AnalyzeMovement", "User is stationary. Total distance: " + totalDistance);
                // 사용자 움직임 없음 처리 로직 추가 가능
            } else if (totalDistance > 50) {
                Log.d("AnalyzeMovement", "Rapid movement detected. Total distance: " + totalDistance);
                // 급격한 움직임 처리 로직 추가 가능
            }
        });
    }

    // 거리 계산 메서드
    private double calculateDistance(double lat1, double lon1, double lat2, double lon2) {
        double radius = 6371e3; // 지구 반지름 (미터)
        double latDiff = Math.toRadians(lat2 - lat1);
        double lonDiff = Math.toRadians(lon2 - lon1);

        double a = Math.sin(latDiff / 2) * Math.sin(latDiff / 2) +
                Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
                        Math.sin(lonDiff / 2) * Math.sin(lonDiff / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        return radius * c;
    }

    // GPS 추적 중단
    private void stopLocationTracking() {
        if (scheduler != null && !scheduler.isShutdown()) {
            scheduler.shutdown();
        }
    }

    // 위치 권한 요청 결과 처리
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 100 && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            startLocationTracking();
        } else {
            Toast.makeText(this, "위치 권한이 필요합니다.", Toast.LENGTH_SHORT).show();
        }
    }
}*/
