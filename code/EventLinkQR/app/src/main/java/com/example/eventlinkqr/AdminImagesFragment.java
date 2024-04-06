package com.example.eventlinkqr;
import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import java.util.ArrayList;
import java.util.List;

/**
 * Fragment used for managing images in an admin context within an Android application.
 * This fragment allows admins to view a list of images, and provides functionality for deleting images.
 *
 * The class interacts with Firestore to fetch and delete images, and uses a custom adapter
 * to bind image data to a ListView.
 *
 * Usage: This fragment should be used within an activity where admin users can manage images.
 * It requires an XML layout resource named 'fragment_admin_image' that contains a ListView
 * for displaying the images.
 */
public class AdminImagesFragment extends Fragment {
    private ListView listView;
    private ImageAdapter adapter;
    private List<ImageModel> imageList;
    private List<String> documentIds; // List to hold document IDs
    private AdminManager adminManager;


    /**
     * Called to have the fragment instantiate its user interface view.
     * This is optional, and non-graphical fragments can return null.
     *
     * @param inflater           The LayoutInflater object that can be used to inflate any views in the fragment.
     * @param container          If non-null, this is the parent view that the fragment's UI should be attached to.
     * @param savedInstanceState If non-null, this fragment is being re-constructed from a previous saved state.
     * @return Return the View for the fragment's UI, or null.
     */
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_admin_image, container, false);
        listView = view.findViewById(R.id.listViewImages);
        imageList = new ArrayList<>();
        documentIds = new ArrayList<>(); // Initialize the document IDs list
        adapter = new ImageAdapter(getActivity(), R.layout.item_image, imageList);
        listView.setAdapter(adapter);
        adminManager = new AdminManager(getActivity());
        fetchImages();
        setupListViewListener();
        return view;
    }

    /**
     * Fetches images from the Firestore database and populates them into the image list.
     * The method queries the 'Images' collection in Firestore, extracts image data,
     * and updates the ListView adapter.
     */
    private void fetchImages() {
        adminManager.fetchImages(new AdminManager.FetchImagesCallback() {
            @Override
            public void onSuccess(List<ImageModel> images, List<String> documentIds) {
                imageList.clear();
                imageList.addAll(images);
                AdminImagesFragment.this.documentIds.clear();
                AdminImagesFragment.this.documentIds.addAll(documentIds);
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onFailure(String errorMessage) {
                Toast.makeText(getActivity(), errorMessage, Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * Sets up a listener for the ListView. This listener handles item click events,
     * triggering a confirmation dialog for deleting the selected image.
     */
    private void setupListViewListener() {
        listView.setOnItemClickListener((parent, view, position, id) -> {
            // Get the document ID for the selected image
            String documentId = documentIds.get(position);
            showDeleteConfirmationDialog(documentId, position);
        });
    }

    /**
     * Displays a confirmation dialog for deleting an image. If the user confirms,
     * the method {@link #deleteImage(String, int)} is called to perform the deletion.
     *
     * @param documentId The Firestore document ID of the image to be deleted.
     * @param position   The position of the image in the ListView.
     */
    private void showDeleteConfirmationDialog(String documentId, int position) {
        new AlertDialog.Builder(getActivity())
                .setTitle("Delete Image")
                .setMessage("Are you sure you want to delete this image?")
                .setPositiveButton(android.R.string.yes, (dialog, which) -> deleteImage(documentId, position))
                .setNegativeButton(android.R.string.no, null)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }

    /**
     * Deletes an image from Firestore and updates the ListView.
     * This method removes the image from both the Firestore database and the local image list,
     * then notifies the adapter to refresh the ListView.
     *
     * @param documentId The Firestore document ID of the image to be deleted.
     * @param position   The position of the image in the ListView.
     */
    private void deleteImage(String documentId, int position) {
        adminManager.replaceImageWithDefault(documentId, new AdminManager.AdminEventOperationCallback() {
            @Override
            public void onSuccess() {
                imageList.remove(position);
                documentIds.remove(position);
                adapter.notifyDataSetChanged();
                Toast.makeText(getActivity(), "Image deleted successfully", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(String errorMessage) {
                Toast.makeText(getActivity(), "Error deleting image: " + errorMessage, Toast.LENGTH_SHORT).show();
            }
        });
    }
}


