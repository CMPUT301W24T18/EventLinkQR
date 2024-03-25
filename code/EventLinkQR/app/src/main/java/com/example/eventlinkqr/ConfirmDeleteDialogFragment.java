package com.example.eventlinkqr;

import android.app.AlertDialog;
import android.app.Dialog;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class ConfirmDeleteDialogFragment extends DialogFragment {

    private final ImageView imageView;
    private final Bitmap bitmapToRestore;

    public ConfirmDeleteDialogFragment(ImageView imageView, Bitmap bitmapToRestore) {
        this.imageView = imageView;
        this.bitmapToRestore = bitmapToRestore;
    }


    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage("Are you sure you want to delete this image?")
                .setPositiveButton("Delete", (dialog, id) -> {
                    imageView.setImageBitmap(bitmapToRestore); // Reset to the original Bitmap

                })
                .setNegativeButton("Cancel", (dialog, id) -> {
                });
        return builder.create();
    }

    private void deleteImageFromFirebase(String uuid) {
        // Construct the reference to image file in Firebase Storage
        String imagePath = "images/" + uuid + ".jpg";
        StorageReference imageRef = FirebaseStorage.getInstance().getReference(imagePath);

        imageRef.delete().addOnSuccessListener(aVoid -> {
            // Image deleted successfully
            Toast.makeText(getContext(), "Image deleted successfully.", Toast.LENGTH_SHORT).show();
//            imageView.setImageBitmap(null); // Clear the image from the ImageView

        }).addOnFailureListener(exception -> {
            // Handle unsuccessful deletions
            Toast.makeText(getContext(), "Deletion failed.", Toast.LENGTH_SHORT).show();
        });
    }
}
