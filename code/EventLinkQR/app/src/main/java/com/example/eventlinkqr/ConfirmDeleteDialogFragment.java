package com.example.eventlinkqr;

import android.app.AlertDialog;
import android.app.Dialog;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class ConfirmDeleteDialogFragment extends DialogFragment {

    private final ImageView imageView;
    private final Bitmap bitmapToRestore;
    private final String uuid;

    public ConfirmDeleteDialogFragment(ImageView imageView, Bitmap bitmapToRestore, String uuid) {
        this.imageView = imageView;
        this.bitmapToRestore = bitmapToRestore;
        this.uuid = uuid;
    }


    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage("Are you sure you want to delete this image?")
                .setPositiveButton("Delete", (dialog, id) -> {
                    imageView.setImageBitmap(bitmapToRestore); // Reset to the original Bitmap
                    deleteImageFromFirebase(uuid);
                })
                .setNegativeButton("Cancel", (dialog, id) -> {
                });
        return builder.create();
    }

    private void deleteImageFromFirebase(String uuid) {
        // Construct the reference to image file in Firebase Storage
        String imagePath = "images/" + uuid + ".jpg";
        DocumentReference imageRef = Manager.getFirebase().collection("images_testing")
                .document(uuid);

        imageRef.delete().addOnSuccessListener(aVoid -> {
            // Image deleted successfully

            // remove the toast from this class
            //            Toast.makeText(getContext(), "Image deleted successfully.", Toast.LENGTH_SHORT).show();
//            imageView.setImageBitmap(null); // Clear the image from the ImageView

        }).addOnFailureListener(exception -> {
            // Handle unsuccessful deletions
            // remove the toast from this class
//            Toast.makeText(getContext(), "Deletion failed.", Toast.LENGTH_SHORT).show();
        });
    }
}
