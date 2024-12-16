package com.team15.oppteamproject;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

public class ContactActivity extends AppCompatActivity {

    private ImageView preBtn, plusBtn;
    private TextView editBtn;
    private List<Contact> contacts;
    private ContactAdapter adapter;
    private DatabaseReference database;  // Firebase Database 참조

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact);

        // Firebase Database 참조 초기화
        database = FirebaseDatabase.getInstance().getReference();

        // View 초기화
        preBtn = findViewById(R.id.previousBtn);
        editBtn = findViewById(R.id.editBtn);
        plusBtn = findViewById(R.id.plusBtn);

        // ListView 및 Adapter 초기화
        ListView contactListView = findViewById(R.id.contactList);
        contacts = new ArrayList<>();
        adapter = new ContactAdapter(this, contacts);
        contactListView.setAdapter(adapter);

        // Firebase에서 데이터 읽기
        loadContactsFromFirebase();

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

    // Firebase에서 연락처 목록 읽어오기
/*private void loadContactsFromFirebase() {
        FirebaseAuth auth = FirebaseAuth.getInstance();
        String userId = auth.getCurrentUser().getUid(); // 현재 로그인한 사용자 ID
        DatabaseReference database = FirebaseDatabase.getInstance().getReference("contacts");

        database.child(userId).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                if (task.getResult().exists()) {
                    for (DataSnapshot contactSnapshot : task.getResult().getChildren()) {
                        String name = contactSnapshot.child("name").getValue(String.class);
                        String phone = contactSnapshot.child("phone").getValue(String.class);

                        // 로그로 출력 (테스트용)
                        Log.d("Contact", "Name: " + name + ", Phone: " + phone);
                    }
                } else {
                    Toast.makeText(ContactActivity.this, "연락처가 없습니다.", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(ContactActivity.this, "연락처 불러오기 실패: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }*/
    private void loadContactsFromFirebase() {
        FirebaseAuth auth = FirebaseAuth.getInstance();
        String userId = auth.getCurrentUser().getUid(); // 로그인한 사용자의 UID 가져오기
        DatabaseReference database = FirebaseDatabase.getInstance().getReference("contacts");

        // UID를 기준으로 해당 사용자의 연락처 데이터만 가져오기
        database.child(userId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                contacts.clear(); // 기존 데이터를 초기화

                // Firebase에서 데이터를 읽어서 contacts 리스트에 추가
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Contact contact = snapshot.getValue(Contact.class);
                    if (contact != null) {
                        contacts.add(contact); // 리스트에 추가
                    }
                }

                adapter.notifyDataSetChanged(); // 리스트 갱신
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(ContactActivity.this, "데이터 읽기 실패: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
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

            // Firebase에 연락처 저장
            saveContactToFirebase(name, phone);

            // 리스트에 추가
            contacts.add(new Contact(name, phone));
            adapter.notifyDataSetChanged(); // 리스트 갱신

            dialog.dismiss(); // 다이얼로그 닫기
        });

        dialog.show(); // 다이얼로그 표시
    }

    // Firebase에 연락처 저장
    private void saveContactToFirebase(String name, String phone) {
        FirebaseAuth auth = FirebaseAuth.getInstance();
        String userId = auth.getCurrentUser().getUid(); // 현재 로그인한 사용자 ID 가져오기
        DatabaseReference database = FirebaseDatabase.getInstance().getReference("contacts");

        // 고유 ID 생성
        String contactId = database.child(userId).push().getKey(); // 사용자 ID 하위에 고유 ID 생성

        if (contactId != null) {
            // Firebase에 데이터 저장
            Contact contact = new Contact(name, phone);
            database.child(userId).child(contactId).setValue(contact)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Toast.makeText(ContactActivity.this, "연락처가 Firebase에 저장되었습니다.", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(ContactActivity.this, "저장 실패: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }

}
