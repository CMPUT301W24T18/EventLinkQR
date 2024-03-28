package com.example.eventlinkqr;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Base64;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

/**
 * Activity to enter and validate an administrative PIN for access to restricted features.
 */
public class EnterPinActivity extends Activity {

    private EditText pinEditText;
    private Button submitPinButton;
    private FirebaseRemoteConfig firebaseRemoteConfig;

    // Zero initialization vector (IV)
    private final byte[] iv = new byte[16];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_enter_pin);

        pinEditText = findViewById(R.id.pinEditText);
        submitPinButton = findViewById(R.id.submitPinButton);
        firebaseRemoteConfig = FirebaseRemoteConfig.getInstance();

        // Fetch the latest configuration
        firebaseRemoteConfig.fetchAndActivate()
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        submitPinButton.setOnClickListener(v -> {
                            // Get the encrypted PIN and encryption key from Firestore and Remote Config
                            fetchEncryptedPinAndDecrypt();
                        });
                    } else {
                        Toast.makeText(EnterPinActivity.this, "Failed to fetch configuration", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    /**
     * Fetches the encrypted PIN from Firestore and attempts to decrypt it using
     * the key obtained from Firebase Remote Config. Validates the decrypted PIN
     * against the user input.
     */
    private void fetchEncryptedPinAndDecrypt() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("Config").document("AdminPin")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null) {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {
                            String encryptedPin = document.getString("encryptedPin");
                            if (encryptedPin != null) {
                                // Fetch the encryption key from Remote Config
                                String encryptionKey = firebaseRemoteConfig.getString("encryption_key");
                                String decryptedPin = decryptPin(encryptedPin, encryptionKey); // Decrypt the PIN
                                String userPin = pinEditText.getText().toString();
                                if (decryptedPin.equals(userPin)) {
                                    // Correct PIN
                                    startActivity(new Intent(EnterPinActivity.this, AdmMainActivity.class));

                                    FirebaseFirestore database = FirebaseFirestore.getInstance();
                                    SharedPreferences prefs = getSharedPreferences("MyAppPrefs", MODE_PRIVATE);
                                    String uuid = prefs.getString("UUID", null);

                                    if (uuid != null) {
                                        database.collection("Users").document(uuid)
                                                .update("isAdmin", true)
                                                .addOnSuccessListener(aVoid -> {
                                                    startActivity(new Intent(EnterPinActivity.this, AdmMainActivity.class));
                                                });
                                    }
                                } else {
                                    // Incorrect PIN
                                    Toast.makeText(EnterPinActivity.this, "Incorrect PIN", Toast.LENGTH_SHORT).show();
                                }
                            } else {
                                Toast.makeText(EnterPinActivity.this, "Encrypted PIN not found.", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(EnterPinActivity.this, "Admin PIN configuration not found.", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(EnterPinActivity.this, "Failed to fetch PIN configuration", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    /**
     * Decrypts an encrypted PIN string using AES/CBC/PKCS5Padding cipher.
     *
     * @param encryptedPin The encrypted PIN to decrypt.
     * @param key The encryption key used for decryption.
     * @return The decrypted PIN, or "Decryption Error" if decryption fails.
     */
    private String decryptPin(String encryptedPin, String key) {
        try {
            byte[] decodedKey = Base64.decode(key, Base64.DEFAULT);
            byte[] decodedData = Base64.decode(encryptedPin, Base64.DEFAULT);
            SecretKeySpec secretKeySpec = new SecretKeySpec(decodedKey, "AES");
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            IvParameterSpec ivParameterSpec = new IvParameterSpec(iv);
            cipher.init(Cipher.DECRYPT_MODE, secretKeySpec, ivParameterSpec);
            byte[] decryptedBytes = cipher.doFinal(decodedData);
            return new String(decryptedBytes);
        } catch (Exception e) {
            e.printStackTrace();
            return "Decryption Error";
        }
    }
}

