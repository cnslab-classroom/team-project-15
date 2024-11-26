package com.team15.oppteamproject;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

public class ContactActivity extends AppCompatActivity {

    private ImageView preBtn, plusBtn;
    private TextView editBtn;
    private List<Contact> contacts;
    private ContactAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact);

        // View 초기화
        preBtn = findViewById(R.id.previousBtn);
        editBtn = findViewById(R.id.editBtn);
        plusBtn = findViewById(R.id.plusBtn);

        // ListView 및 Adapter 초기화
        ListView contactListView = findViewById(R.id.contactList);
        contacts = new ArrayList<>();
        contacts.add(new Contact("홍길동", "010-1234-5678"));
        contacts.add(new Contact("이몽룡", "010-8765-4321"));

        adapter = new ContactAdapter(this, contacts);
        contactListView.setAdapter(adapter);

        // 플러스 버튼 클릭 이벤트
        plusBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showAddContactDialog();
            }
        });

        // 이전 버튼 클릭 이벤트
        preBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(ContactActivity.this, MainActivity.class));
                finish();
            }
        });
    }

    // 다이얼로그 생성 및 표시
    private void showAddContactDialog() {
        // 다이얼로그 레이아웃 인플레이트
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_add_contact, null);

        EditText editName = dialogView.findViewById(R.id.editName);
        EditText editPhone = dialogView.findViewById(R.id.editPhone);

        // 다이얼로그 생성
        AlertDialog dialog = new AlertDialog.Builder(this)
                .setView(dialogView)
                .setCancelable(false) // 다이얼로그 외부 클릭 시 닫히지 않도록 설정
                .create();

        // 버튼 동작 설정
        Button btnCancel = dialogView.findViewById(R.id.btnCancel);
        Button btnAdd = dialogView.findViewById(R.id.btnAdd);

        // 취소 버튼
        btnCancel.setOnClickListener(v -> dialog.dismiss());

        // 추가 버튼
        btnAdd.setOnClickListener(v -> {
            String name = editName.getText().toString().trim();
            String phone = editPhone.getText().toString().trim();

            if (name.isEmpty() || phone.isEmpty()) {
                editName.setError(name.isEmpty() ? "이름을 입력하세요." : null);
                editPhone.setError(phone.isEmpty() ? "전화번호를 입력하세요." : null);
                return;
            }

            // 리스트에 추가
            contacts.add(new Contact(name, phone));
            adapter.notifyDataSetChanged(); // 리스트 갱신

            dialog.dismiss(); // 다이얼로그 닫기
        });

        dialog.show(); // 다이얼로그 표시
    }
}
