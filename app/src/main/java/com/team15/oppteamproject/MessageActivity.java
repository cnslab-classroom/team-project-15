package com.team15.oppteamproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class MessageActivity extends AppCompatActivity {
    ImageButton prebtn;
    private ListView contactList;
    private ContactAdapter adapter;
    private List<Contact> contacts;
    private DatabaseReference contactsRef;
    private String lastKey = null;
    private boolean isLoading = false;
    private static final int PAGE_SIZE = 10;

    private Button sendMessageButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);

        contactList = findViewById(R.id.contactList);
        sendMessageButton = findViewById(R.id.sendMessageButton);

        contacts = new ArrayList<>();
        adapter = new ContactAdapter(this, contacts);
        contactList.setAdapter(adapter);

        contactsRef = FirebaseDatabase.getInstance().getReference("contacts");

        loadContacts();
        prebtn = findViewById(R.id.previousBtn);
        prebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // MainActivity로 돌아가기 위한 Intent 생성
                Intent intent = new Intent(MessageActivity.this, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intent);
                finish(); // 현재 액티비티 종료
            }
        });

        sendMessageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendMessages();
            }
        });
    }



    private void loadContacts() {
        isLoading = true;

        // Firebase에서 현재 사용자의 UID를 기준으로 연락처 가져오기
        FirebaseAuth auth = FirebaseAuth.getInstance();
        String userId = auth.getCurrentUser().getUid();
        DatabaseReference userContactsRef = FirebaseDatabase.getInstance().getReference("contacts").child(userId);

        Query query = userContactsRef.orderByKey().limitToFirst(PAGE_SIZE);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                contacts.clear(); // 기존 리스트 초기화
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Contact contact = snapshot.getValue(Contact.class);
                    if (contact != null) {
                        contacts.add(contact);
                        lastKey = snapshot.getKey();
                    }
                }
                adapter.notifyDataSetChanged(); // 어댑터 갱신
                isLoading = false;
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("MessageActivity", "loadContacts:onCancelled", databaseError.toException());
                isLoading = false;
            }
        });
    }


    private void loadMoreContacts() {
        if (lastKey == null) return;
        isLoading = true;
        Query query = contactsRef.orderByKey().startAfter(lastKey).limitToFirst(PAGE_SIZE);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Contact contact = snapshot.getValue(Contact.class);
                    if (contact != null) {
                        contacts.add(contact);
                        lastKey = snapshot.getKey();
                    }
                }
                adapter.notifyDataSetChanged();
                isLoading = false;
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("MessageActivity", "loadMoreContacts:onCancelled", databaseError.toException());
                isLoading = false;
            }
        });
    }

    private void sendMessages() {
        for (Contact contact : contacts) {
            sendSMS(contact.getPhone(), contact.getName());
        }
        Toast.makeText(this, "모든 연락처에 메시지를 보냈습니다.", Toast.LENGTH_SHORT).show();
    }

    private void sendSMS(String phoneNumber, String name) {
        String message = "안녕하세요, " + name + "님. 긴급 상황입니다.";
        try {
            SmsManager smsManager = SmsManager.getDefault();
            smsManager.sendTextMessage(phoneNumber, null, message, null, null);
        } catch (Exception e) {
            Toast.makeText(this, "SMS 전송 실패: " + phoneNumber, Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }
}
