package com.team15.oppteamproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class SignUpActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private EditText emailEditText, passwordEditText, confirmPasswordEditText;
    private Button sendEmailVerificationButton, checkEmailVerificationButton, signUpButton;
    private FirebaseUser currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        // FirebaseAuth 인스턴스 초기화
        mAuth = FirebaseAuth.getInstance();

        emailEditText = findViewById(R.id.emailEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        confirmPasswordEditText = findViewById(R.id.confirmPasswordEditText);
        sendEmailVerificationButton = findViewById(R.id.sendEmailVerificationButton);
        checkEmailVerificationButton = findViewById(R.id.checkEmailVerificationButton);
        signUpButton = findViewById(R.id.signUpButton);

        // 이메일 인증 버튼 클릭 시
        sendEmailVerificationButton.setOnClickListener(v -> sendEmailVerification());

        // 이메일 인증 확인 버튼 클릭 시
        checkEmailVerificationButton.setOnClickListener(v -> checkEmailVerification());

        // 회원가입 버튼 클릭 시
        signUpButton.setOnClickListener(v -> signUpUser());
    }

    private void sendEmailVerification() {
        String email = emailEditText.getText().toString();
        if (email.isEmpty()) {
            Toast.makeText(SignUpActivity.this, "이메일을 입력하세요.", Toast.LENGTH_SHORT).show();
            return;
        }

        // Firebase 사용자 생성 (임시 계정)
        mAuth.createUserWithEmailAndPassword(email, "temporaryPassword123")
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        currentUser = mAuth.getCurrentUser();
                        if (currentUser != null) {
                            currentUser.sendEmailVerification()
                                    .addOnCompleteListener(verificationTask -> {
                                        if (verificationTask.isSuccessful()) {
                                            Toast.makeText(SignUpActivity.this, "인증 이메일이 전송되었습니다. 이메일을 확인하세요.", Toast.LENGTH_LONG).show();
                                            checkEmailVerificationButton.setVisibility(android.view.View.VISIBLE);
                                        } else {
                                            Toast.makeText(SignUpActivity.this, "인증 이메일 전송 실패: " + verificationTask.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                        }
                                    });
                        }
                    } else {
                        Toast.makeText(SignUpActivity.this, "이메일 인증 요청 실패: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void checkEmailVerification() {
        if (currentUser != null) {
            currentUser.reload()
                    .addOnCompleteListener(task -> {
                        if (currentUser.isEmailVerified()) {
                            Toast.makeText(SignUpActivity.this, "이메일 인증이 완료되었습니다. 비밀번호를 설정하세요.", Toast.LENGTH_LONG).show();
                            passwordEditText.setVisibility(android.view.View.VISIBLE);
                            confirmPasswordEditText.setVisibility(android.view.View.VISIBLE);
                            signUpButton.setVisibility(android.view.View.VISIBLE);
                        } else {
                            Toast.makeText(SignUpActivity.this, "이메일 인증이 아직 완료되지 않았습니다. 다시 확인하세요.", Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }

    private void signUpUser() {
        String password = passwordEditText.getText().toString();
        String confirmPassword = confirmPasswordEditText.getText().toString();

        if (password.isEmpty() || confirmPassword.isEmpty()) {
            Toast.makeText(SignUpActivity.this, "비밀번호와 비밀번호 확인을 입력하세요.", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!password.equals(confirmPassword)) {
            Toast.makeText(SignUpActivity.this, "비밀번호가 일치하지 않습니다.", Toast.LENGTH_SHORT).show();
            return;
        }

        // Firebase 계정 업데이트
        currentUser.updatePassword(password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(SignUpActivity.this, "회원가입이 완료되었습니다!", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(SignUpActivity.this, LoginActivity.class));
                        finish();
                    } else {
                        Toast.makeText(SignUpActivity.this, "회원가입 실패: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
