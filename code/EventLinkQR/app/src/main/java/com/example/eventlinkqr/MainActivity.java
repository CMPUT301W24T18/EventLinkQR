package com.example.eventlinkqr;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class MainActivity extends AppCompatActivity {


    private Button uploadImgButton;
    private ImageView dummyImg;
    private Uri imageUri;

    private FirebaseFirestore db;
    private CollectionReference citiesRef;

    private FirebaseStorage storage;
    private Context context;


    private final ActivityResultLauncher<String> getImage = registerForActivityResult(
            new ActivityResultContracts.GetContent(),
            uri -> {
                imageUri = uri;
                dummyImg.setImageURI(uri);
            });


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        storage = FirebaseStorage.getInstance();

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        dummyImg = findViewById(R.id.image_preview);
        Bitmap deterministicBitmap = ImageManager.generateDeterministicImage("China");
        dummyImg.setImageBitmap(deterministicBitmap);

        uploadImgButton = findViewById(R.id.upload_cityImg_button);

        db = FirebaseFirestore.getInstance();
        citiesRef = db.collection("cities");


        uploadImgButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, UploadImageActivity.class);
                startActivity(intent);
            }
        });

//                setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (imageUri != null) {
//                    String userId = "Basia";
//                    String fileName = "uploaded_image_" + System.currentTimeMillis() + ".jpg"; // Example file name
//                    String imagePath = "users/" + userId + "/" + fileName;
//
//                    uploadImage(context, imageUri, userId, imagePath);
//                }
//            }
//        });
    }
}