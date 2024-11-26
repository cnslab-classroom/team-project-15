package com.team15.oppteamproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private EditText emailEditText, passwordEditText;
    private Button loginButton, goToSignUpButton;
    TextView forgotPasswordText;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // FirebaseAuth 인스턴스 초기화
        mAuth = FirebaseAuth.getInstance();

        emailEditText = findViewById(R.id.emailEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        loginButton = findViewById(R.id.loginButton);
        goToSignUpButton = findViewById(R.id.signUpButton);
        forgotPasswordText = findViewById(R.id.forgotPasswordText);

        forgotPasswordText.setOnClickListener(v -> {
            startActivity(new Intent(LoginActivity.this, ForgotPasswordActivity.class));
        });

        // 로그인 버튼 클릭 시
        loginButton.setOnClickListener(v -> loginUser());

        // 회원가입 화면으로 이동 버튼 클릭 시
        goToSignUpButton.setOnClickListener(v -> {
            startActivity(new Intent(LoginActivity.this, SignUpActivity.class));
        });
    }

    private void loginUser() {
        String email = emailEditText.getText().toString();
        String password = passwordEditText.getText().toString();

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(LoginActivity.this, "이메일과 비밀번호를 입력하세요.", Toast.LENGTH_SHORT).show();
            return;
        }

        // Firebase Authentication을 사용하여 로그인
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        // 로그인 성공 시
                        FirebaseUser user = mAuth.getCurrentUser();
                        Toast.makeText(LoginActivity.this, "로그인 성공!", Toast.LENGTH_SHORT).show();
                        // 메인 액티비티로 이동 (필요에 따라 수정)
                        startActivity(new Intent(LoginActivity.this, MainActivity.class));
                        finish();
                    } else {
                        // 로그인 실패 시
                        Toast.makeText(LoginActivity.this, "로그인 실패: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
