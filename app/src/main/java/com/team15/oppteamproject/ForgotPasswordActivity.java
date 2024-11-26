package com.team15.oppteamproject;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import com.google.firebase.auth.FirebaseAuth;

public class ForgotPasswordActivity extends AppCompatActivity {

    private EditText emailEditText;
    private Button resetPasswordButton, goToLoginButton;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        // FirebaseAuth 초기화
        mAuth = FirebaseAuth.getInstance();

        emailEditText = findViewById(R.id.emailEditText);
        resetPasswordButton = findViewById(R.id.resetPasswordButton);
        goToLoginButton = findViewById(R.id.goToLoginButton);

        // 비밀번호 재설정 버튼 클릭 이벤트
        resetPasswordButton.setOnClickListener(v -> resetPassword());

        // 로그인 화면으로 이동 버튼 클릭 이벤트
        goToLoginButton.setOnClickListener(v -> {
            Intent intent = new Intent(ForgotPasswordActivity.this, LoginActivity.class);
            startActivity(intent);
            finish(); // 현재 화면 종료
        });
    }

    private void resetPassword() {
        String email = emailEditText.getText().toString();

        if (email.isEmpty()) {
            Toast.makeText(ForgotPasswordActivity.this, "이메일을 입력하세요.", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Toast.makeText(ForgotPasswordActivity.this, "유효한 이메일 주소를 입력하세요.", Toast.LENGTH_SHORT).show();
            return;
        }

        mAuth.sendPasswordResetEmail(email)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(ForgotPasswordActivity.this, "비밀번호 재설정 이메일을 보냈습니다.", Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(ForgotPasswordActivity.this, "실패: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
