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
 * This class represents a dialog fragment for confirming the deletion of an image.
 * It displays a dialog with options to delete or cancel the action.
 */
public class ConfirmDeleteDialogFragment extends DialogFragment {

    /**
     * ImageView where the image is displayed.
     */
    private final ImageView imageView;
    private final Bitmap bitmapToRestore;
    private final String uuid;

    /**
     * Constructs a new ConfirmDeleteDialogFragment with specified parameters.
     *
     * @param imageView        The ImageView displaying the image.
     * @param bitmapToRestore  The Bitmap to restore if the image deletion is canceled.
     * @param uuid             The unique identifier of the image.
     */
    public ConfirmDeleteDialogFragment(ImageView imageView, Bitmap bitmapToRestore, String uuid) {
        this.imageView = imageView;
        this.bitmapToRestore = bitmapToRestore;
        this.uuid = uuid;
    }

    /**
     * Called to create the dialog.
     * Sets up the dialog with a message, positive button for deleting the image, and negative button for canceling the action.
     *
     * @param savedInstanceState If the fragment is being re-created from a previous saved state, this is the state.
     * @return The newly created Dialog instance.
     */
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
