package com.example.eventlinkqr;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.eventlinkqr.AdmMainActivity;
import com.google.firebase.firestore.FirebaseFirestore;

public class EnterPinActivity extends Activity {

    private EditText pinEditText;
    private Button submitPinButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_enter_pin);

        pinEditText = findViewById(R.id.pinEditText);
        submitPinButton = findViewById(R.id.submitPinButton);

        submitPinButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String pin = pinEditText.getText().toString();
                if ("1234".equals(pin)) { // Assuming "1234" is the correct PIN
                    // Correct PIN, set isAdmin to true and navigate to AdmMainActivity
                    FirebaseFirestore db = FirebaseFirestore.getInstance();
                    SharedPreferences prefs = getSharedPreferences("MyAppPrefs", MODE_PRIVATE);
                    String uuid = prefs.getString("UUID", null);

                    if (uuid != null) {
                        db.collection("Users").document(uuid)
                                .update("isAdmin", true)
                                .addOnSuccessListener(aVoid -> {
                                    startActivity(new Intent(EnterPinActivity.this, AdmMainActivity.class));
                                });
                    }
                } else {
                    // Incorrect PIN
                    Toast.makeText(EnterPinActivity.this, "Incorrect PIN", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
