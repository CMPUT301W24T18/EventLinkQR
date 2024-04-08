package com.example.eventlinkqr;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

/**
 * dialog fragment used to confirm the user wants to delete their profile picture
 */
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
        Context context = requireContext();
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage("Are you sure you want to delete this image?")
                .setPositiveButton("Delete", (dialog, id) -> {
                    ImageManager.deleteImageFromFirebase(ConfirmDeleteDialogFragment.this, uuid, new ImageManager.UploadCallback() {
                        @Override
                        public void onSuccess() {
                            Toast.makeText(context, "Image deleted successfully!", Toast.LENGTH_SHORT).show();
                            imageView.setImageBitmap(bitmapToRestore);

                            Intent returnIntent = new Intent();
                            returnIntent.putExtra("bitmapToRestore", bitmapToRestore.toString());
                        }

                        @Override
                        public void onFailure(Exception exception) {
                            Toast.makeText(requireContext(), "Image deletion failed", Toast.LENGTH_SHORT).show();
                        }
                    });
                })
                .setNegativeButton("Cancel", (dialog, id) -> {
                });
        return builder.create();
    }
}
