package com.team15.oppteamproject;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;

public class SettingActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        ImageView preBtn = findViewById(R.id.previousBtn);
        ImageView accountBtn = findViewById(R.id.accountBtn);
        ImageView alarmBtn = findViewById(R.id.alarmBtn);
        ImageView sensorBtn = findViewById(R.id.sensorBtn);

        accountBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                View dialogPW = View.inflate(SettingActivity.this, R.layout.dialog_reset_pw, null);
                AlertDialog.Builder dlg = new AlertDialog.Builder(SettingActivity.this);
                dlg.setView(dialogPW);
                dlg.setPositiveButton("전송", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        EditText dlgEditEmail = dialogPW.findViewById(R.id.editEmail);
                        String dlgEmail = dlgEditEmail.getText().toString();
                        FirebaseAuth mAuth = FirebaseAuth.getInstance();

                        mAuth.sendPasswordResetEmail(dlgEmail)
                                .addOnCompleteListener(task -> {
                                    if (task.isSuccessful()) {
                                        Toast.makeText(SettingActivity.this, "비밀번호 재설정 이메일을 보냈습니다.", Toast.LENGTH_LONG).show();
                                    } else {
                                        Toast.makeText(SettingActivity.this, "실패: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                });
                    }
                });
                dlg.setNegativeButton("취소", null);
                dlg.show();
            }
        });

        alarmBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String[] modeArray = new String[] {"소리", "진동", "무음"};
                AlertDialog.Builder dlg = new AlertDialog.Builder(SettingActivity.this);
                dlg.setTitle("알람 설정");
                dlg.setSingleChoiceItems(modeArray, 0, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        // 푸시알람 모드 바꾸기
                    }
                });
                dlg.setPositiveButton("닫기", null);
                dlg.show();

            }
        });

        sensorBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                View dialogSensor = View.inflate(SettingActivity.this, R.layout.dialog_sensor, null);
                AlertDialog.Builder dlg = new AlertDialog.Builder(SettingActivity.this);
                dlg.setView(dialogSensor);

                SeekBar seekBar = dialogSensor.findViewById(R.id.sensorBar);

                seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                    // 인터페이스의 추상 메서드 다 구현해야 함, 비워두는 것은 가능
                    @Override
                    public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                        // progress 인자로 센서 조정
                        // 0 1 2 3 4 (5단계)
                    }

                    @Override
                    public void onStartTrackingTouch(SeekBar seekBar) {
                        // 사용자가 SeekBar를 터치했을 때
                    }

                    @Override
                    public void onStopTrackingTouch(SeekBar seekBar) {
                        // 사용자가 SeekBar를 터치에서 뗐을 때
                    }
                });

                dlg.setPositiveButton("닫기", null);
                dlg.show();
            }
        });

        preBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(SettingActivity.this, MainActivity.class));
                finish();
            }
        });


    }
}