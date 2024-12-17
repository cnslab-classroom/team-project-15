package com.team15.oppteamproject;

import android.Manifest;
import android.app.Activity;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.MediaPlayer;
import android.os.Build;
import android.telephony.SmsManager;
import android.util.Log;
import android.widget.TextView;

import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class IncidentHandler implements SensorEventListener {

    private MediaPlayer mediaPlayer; // MP3 파일 재생용

    // 가속도 값 비교용 변수
    private static final long CHECK_INTERVAL = 1000; // 1초 간격 (밀리초)
    private static final double CRASH_THRESHOLD_DIFF = 80.0; // 충돌 감지 가속도 차이 임계값
    private float previousAcceleration = 0.0f; // 이전 가속도 값
    private long lastUpdateTime = 0L; // 마지막 업데이트 시간
    private static final String CHANNEL_ID = "incident_channel";
    private static final float CRASH_THRESHOLD = 15f;
    private static final float UNCONSCIOUS_THRESHOLD = 10.5f;
    private static final long UNCONSCIOUS_DURATION = 10_000L;
    private NotificationReceiver notificationReceiver; // BroadcastReceiver 선언
    private Context context;
    private SensorManager sensorManager;
    private Sensor accelerometer;
    private FusedLocationProviderClient fusedLocationClient;
    private DatabaseReference incidentsRef;
    private DatabaseReference contactsRef;

    private ScheduledExecutorService gpsScheduler;
    private boolean crashDetected = false;
    private boolean unconsciousDetected = false;
    private boolean checkingUnconscious = false;

    private long unconsciousStartTime = 0L;
    private String incidentId;

    private TextView tvAccelerometer;
    private Sensor gyroscope;

    private ScheduledExecutorService scheduler;


    public IncidentHandler(Context context, TextView tvAccelerometer) {
        this.context = context;
        this.sensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
        this.accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        this.fusedLocationClient = LocationServices.getFusedLocationProviderClient(context);
        this.incidentsRef = FirebaseDatabase.getInstance().getReference("incidents");
        this.contactsRef = FirebaseDatabase.getInstance().getReference("contacts");

        this.tvAccelerometer = tvAccelerometer;

        // BroadcastReceiver 등록
        notificationReceiver = new NotificationReceiver();


        createNotificationChannel();

    }

    private void playSound() {
        if (mediaPlayer == null) {
            mediaPlayer = MediaPlayer.create(context, R.raw.beep); // res/raw/beep.mp3 사용
            mediaPlayer.setLooping(true); // 반복 재생
            mediaPlayer.start();
        }
    }


    public void stopSound() {
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }



    public void cleanup() {
        if (notificationReceiver != null) {
            context.unregisterReceiver(notificationReceiver);
            Log.d("IncidentHandler", "NotificationReceiver unregistered.");
        }
    }

    // 센서 리스너 등록
    public void startMonitoring() {
        if (accelerometer != null) {
            sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_UI);
        }
        if (gyroscope != null) {
            sensorManager.registerListener(this, gyroscope, SensorManager.SENSOR_DELAY_UI);
        }
    }
    public void setCrashDetected() {
        if (!crashDetected) { // 충돌 감지가 아직 발생하지 않았을 때만 실행
            crashDetected = true;
            startIncidentHandling();
            startGPSTracking(incidentId);
            showNotification("충돌 감지됨", "사용자의 상태를 확인하세요.");
            Log.d("IncidentHandler", "Crash detected manually via button click.");
        } else {
            Log.d("IncidentHandler", "Crash already detected.");
        }
    }

    // 센서 리스너 해제
    public void stopMonitoring() {
        sensorManager.unregisterListener(this);
        stopGPSTracking();
        Log.d("IncidentHandler", "Sensor monitoring stopped.");
    }

    // 충돌 발생 시 Firebase에 데이터 저장
    private void startIncidentHandling() {
        incidentId = incidentsRef.push().getKey();
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        Map<String, Object> incidentData = new HashMap<>();
        incidentData.put("user_id", userId);
        incidentData.put("status", "crash_detected");
        incidentData.put("timestamp", System.currentTimeMillis());

        incidentsRef.child(incidentId).setValue(incidentData);
        Log.d("IncidentHandler", "Incident logged: " + incidentId);
    }

    private List<double[]> gpsLocations = new ArrayList<>(); // GPS 데이터를 저장할 리스트

    private boolean gpsTrackingStarted = false;

    public IncidentHandler(Context context) {
        this.context = context;
        this.incidentsRef = FirebaseDatabase.getInstance().getReference("incidents");
        this.fusedLocationClient = LocationServices.getFusedLocationProviderClient(context);
    }

    public void startGPSTracking(String incidentId) {
        if (gpsTrackingStarted) {
            Log.d("IncidentHandler", "GPS tracking already started.");
            return;
        }

        if (incidentId == null) {
            Log.e("IncidentHandler", "Incident ID is null. Cannot start GPS tracking.");
            return;
        }

        gpsTrackingStarted = true;
        this.incidentId = incidentId;

        // 스케줄러가 이미 실행 중이라면 중지
        stopGPSTracking();

        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Log.e("IncidentHandler", "Location permission not granted.");
            return;
        }

        scheduler = Executors.newScheduledThreadPool(1);
        scheduler.scheduleAtFixedRate(() -> {
            fusedLocationClient.getLastLocation().addOnSuccessListener(location -> {
                if (location != null) {
                    double latitude = location.getLatitude();
                    double longitude = location.getLongitude();

                    saveLocationToFirebase(latitude, longitude);
                    gpsLocations.add(new double[]{latitude, longitude});

                    if (gpsLocations.size() >= 4) { // 20초 동안의 데이터 수집
                        analyzeMovement();
                        gpsLocations.clear();
                    }
                }
            });
        }, 0, 5, TimeUnit.SECONDS);
    }
    // Firebase에 위치 데이터 저장
    private void saveLocationToFirebase(double latitude, double longitude) {
        long timestamp = System.currentTimeMillis();

        Map<String, Object> locationData = new HashMap<>();
        locationData.put("latitude", latitude);
        locationData.put("longitude", longitude);
        locationData.put("timestamp", timestamp);

        incidentsRef.child(incidentId).child("locations").push().setValue(locationData);
        Log.d("IncidentHandler", "Location saved: " + latitude + ", " + longitude);
    }

    // 움직임 분석
    private void analyzeMovement() {
        double totalDistance = 0.0;
        for (int i = 1; i < gpsLocations.size(); i++) {
            double[] prev = gpsLocations.get(i - 1);
            double[] curr = gpsLocations.get(i);
            totalDistance += calculateDistance(prev[0], prev[1], curr[0], curr[1]);
        }

        if (totalDistance < 5.0 ) {
            Log.d("IncidentHandler", "User is stationary. Total distance: " + totalDistance + " meters.");
        } else if (totalDistance > 50.0) {
            Log.d("IncidentHandler", "Rapid movement detected! Total distance: " + totalDistance + " meters.");
        } else {
            Log.d("IncidentHandler", "Normal movement. Total distance: " + totalDistance + " meters.");
        }
    }

    // 거리 계산 메서드 (Haversine 공식 사용)
    private double calculateDistance(double lat1, double lon1, double lat2, double lon2) {
        double radius = 6371e3; // 지구 반지름 (미터)
        double latDiff = Math.toRadians(lat2 - lat1);
        double lonDiff = Math.toRadians(lon2 - lon1);

        double a = Math.sin(latDiff / 2) * Math.sin(latDiff / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(lonDiff / 2) * Math.sin(lonDiff / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        return radius * c;
    }

    // GPS 데이터 수집 중단
    public void stopGPSTracking() {
        if (scheduler != null && !scheduler.isShutdown()) {
            scheduler.shutdown();
            gpsTrackingStarted = false;
            Log.d("IncidentHandler", "GPS tracking stopped.");
        }
    }
    private boolean checkSMSPermission() {
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.SEND_SMS)
                != PackageManager.PERMISSION_GRANTED) {
            if (context instanceof Activity) {
                ActivityCompat.requestPermissions((Activity) context,
                        new String[]{Manifest.permission.SEND_SMS}, 1);
            } else {
                Log.e("IncidentHandler", "Context is not an instance of Activity. Cannot request permission.");
            }
            return false;
        }
        return true;
    }
    private boolean checkAndRequestSMSPermission(Activity activity) {
        if (ContextCompat.checkSelfPermission(activity, Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.SEND_SMS}, 1);
            return false;
        }
        return true;
    }


    // Firebase에서 최초 GPS 정보 가져오기 및 SMS 전송
    private void getFirstGPSAndSendSMS() {
        DatabaseReference gpsRef = incidentsRef.child(incidentId).child("locations");
        gpsRef.limitToFirst(1).get().addOnSuccessListener(dataSnapshot -> {
            if (dataSnapshot.exists()) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Double latitude = snapshot.child("latitude").getValue(Double.class);
                    Double longitude = snapshot.child("longitude").getValue(Double.class);

                    if (latitude != null && longitude != null) {
                        String gpsInfo = "위치: https://maps.google.com/?q=" + latitude + "," + longitude;
                        sendEmergencyMessagesWithGPS(gpsInfo);
                    }
                }
            } else {
                Log.e("IncidentHandler", "No GPS data found.");
            }
        }).addOnFailureListener(e -> Log.e("IncidentHandler", "Failed to fetch GPS data: ", e));
    }

    // GPS 정보를 포함한 SMS 전송 메서드
    private void sendEmergencyMessagesWithGPS(String gpsInfo) {
        if (!checkSMSPermission()) {
            Log.e("IncidentHandler", "SMS permission not granted.");
            return;
        }

        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        contactsRef.child(userId).get().addOnSuccessListener(dataSnapshot -> {
            List<String> failedContacts = new ArrayList<>();
            for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                String phoneNumber = snapshot.child("phone").getValue(String.class);
                String name = snapshot.child("name").getValue(String.class);

                if (phoneNumber != null && name != null) {
                    boolean success = sendSMS(phoneNumber, name, gpsInfo);
                    if (!success) failedContacts.add(phoneNumber);
                }
            }

            if (failedContacts.isEmpty()) {
                Log.d("IncidentHandler", "All emergency messages sent successfully with GPS.");
            } else {
                Log.e("IncidentHandler", "Failed to send SMS to: " + failedContacts);
            }
        });
    }

    // GPS 정보를 포함하여 SMS 전송
    private boolean sendSMS(String phoneNumber, String name, String gpsInfo) {
        String message = "안녕하세요, " + name + "님. 긴급 상황 발생!\n" +
                "사용자의 상태를 확인해 주세요.\n" + gpsInfo;

        try {
            SmsManager smsManager = SmsManager.getDefault();
            smsManager.sendTextMessage(phoneNumber, null, message, null, null);
            Log.d("IncidentHandler", "SMS sent to: " + phoneNumber);
            return true;
        } catch (Exception e) {
            Log.e("IncidentHandler", "SMS failed to: " + phoneNumber, e);
            return false;
        }
    }

    // 기존 sendEmergencyMessages에서 GPS 데이터 사용
    private void sendEmergencyMessages() {
        if (incidentId != null) {
            getFirstGPSAndSendSMS();
        } else {
            Log.e("IncidentHandler", "Incident ID is null. Cannot fetch GPS data.");
        }
    }


    // SMS 전송
    private boolean sendSMS(String phoneNumber, String name) {
        String message = "안녕하세요, " + name + "님. 긴급 상황 발생! 사용자의 상태를 확인해 주세요.";
        try {
            SmsManager smsManager = SmsManager.getDefault();
            smsManager.sendTextMessage(phoneNumber, null, message, null, null);
            Log.d("IncidentHandler", "SMS sent to: " + phoneNumber);
            return true;
        } catch (Exception e) {
            Log.e("IncidentHandler", "SMS failed to: " + phoneNumber, e);
            return false;
        }
    }

    // 알림 생성
    private void showNotification(String title, String content) {

        playSound();

        Intent broadcastIntent = new Intent(context, NotificationReceiver.class);
        broadcastIntent.setAction("ACTION_CONSCIOUS_CONFIRMED");

        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                context, 0, broadcastIntent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setContentTitle(title)
                .setContentText(content)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setDefaults(NotificationCompat.DEFAULT_ALL)
                .setContentIntent(pendingIntent) // 알림 클릭 시 BroadcastReceiver 호출
                .setAutoCancel(true);

        NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        manager.notify(1, builder.build());
    }


    public void resetCrashState() {
        if (crashDetected) {
            crashDetected = false;
            unconsciousDetected = false;
            checkingUnconscious = false;
            unconsciousStartTime = 0L; // 의식 상실 타이머 초기화
            messagesSent = false; // 메시지 전송 상태 리셋
            stopGPSTracking(); // GPS 추적 중단
            stopSound(); // 소리 중지
            Log.d("IncidentHandler", "Crash state reset. All pending actions stopped.");
        }
    }



    private static final int REQUEST_CODE_POST_NOTIFICATIONS = 101; // 알림 권한 요청 코드

    private void checkNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) { // Android 13 이상
            if (ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS)
                    != PackageManager.PERMISSION_GRANTED) {
                // 권한 요청
                ActivityCompat.requestPermissions((MainActivity) context,
                        new String[]{Manifest.permission.POST_NOTIFICATIONS}, REQUEST_CODE_POST_NOTIFICATIONS);
            } else {
                // 권한이 이미 허용된 경우
                showNotification("충돌 감지됨", "사용자의 상태를 확인하세요.");
            }
        } else {
            // Android 12 이하에서는 바로 알림 생성
            showNotification("충돌 감지됨", "사용자의 상태를 확인하세요.");
        }
    }


    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID, "Incident Alerts", NotificationManager.IMPORTANCE_HIGH
            );
            NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            manager.createNotificationChannel(channel);
        }
    }

    private boolean messagesSent = false; // 메시지 전송 상태 확인 플래그

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            float x = event.values[0];
            float y = event.values[1];
            float z = event.values[2];

            // 현재 시간 및 가속도 값 계산
            long currentTime = System.currentTimeMillis();
            double currentAcceleration = Math.sqrt(x * x + y * y + z * z);

            // 1초 단위로 가속도 비교
            if ((currentTime - lastUpdateTime) > CHECK_INTERVAL) {
                double accelerationDifference = Math.abs(currentAcceleration - previousAcceleration);

                // 충돌 감지 조건: 가속도 차이가 CRASH_THRESHOLD_DIFF 이상일 때
                if (!crashDetected && accelerationDifference >= CRASH_THRESHOLD_DIFF) {
                    crashDetected = true;
                    startIncidentHandling();
                    startGPSTracking(incidentId);
                    checkNotificationPermission();
                    showNotification("충돌 감지됨", "사용자의 상태를 확인하세요.");
                    Log.d("IncidentHandler", "Crash detected! Acceleration difference: " + accelerationDifference);
                }

                // 의식 상실 감지 로직
                if (crashDetected && currentAcceleration <= UNCONSCIOUS_THRESHOLD) {
                    if (!checkingUnconscious) {
                        checkingUnconscious = true;
                        unconsciousStartTime = currentTime;
                    } else if (currentTime - unconsciousStartTime >= UNCONSCIOUS_DURATION) {
                        if (!unconsciousDetected) {
                            unconsciousDetected = true;

                            if (!messagesSent) {
                                sendEmergencyMessages();
                                showNotification("의식 상실 감지됨", "비상 연락망에 메시지를 전송했습니다.");
                                messagesSent = true;
                            }
                        }
                    }
                } else {
                    checkingUnconscious = false; // 의식이 확인되면 초기화
                }

                // 이전 가속도 및 시간 업데이트
                previousAcceleration = (float) currentAcceleration;
                lastUpdateTime = currentTime;

                // 로그 출력 및 UI 업데이트
                String accText = String.format("Current=%.2f, Prev=%.2f, Diff=%.2f, Crash=%b, Unconscious=%b",
                        currentAcceleration, previousAcceleration, accelerationDifference, crashDetected, unconsciousDetected);
                tvAccelerometer.setText(accText);
            }
        }
    }



    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }




}

