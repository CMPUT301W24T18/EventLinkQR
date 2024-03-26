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

public class AdminImagesFragment extends Fragment {
    private ListView listView;
    private ImageAdapter adapter;
    private List<ImageModel> imageList;
    private List<String> documentIds; // List to hold document IDs

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_admin_image, container, false);
        listView = view.findViewById(R.id.listViewImages);
        imageList = new ArrayList<>();
        documentIds = new ArrayList<>(); // Initialize the document IDs list
        adapter = new ImageAdapter(getActivity(), R.layout.item_image, imageList);
        listView.setAdapter(adapter);
        fetchImages();
        setupListViewListener();
        return view;
    }

    private void fetchImages() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("images_testing").get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (!queryDocumentSnapshots.isEmpty()) {
                        for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                            ImageModel image = documentSnapshot.toObject(ImageModel.class);
                            imageList.add(image);
                            documentIds.add(documentSnapshot.getId()); // Store document ID
                        }
                        adapter.notifyDataSetChanged();
                    }
                })
                .addOnFailureListener(e -> {/* Handle error */});
    }


    private void setupListViewListener() {
        listView.setOnItemClickListener((parent, view, position, id) -> {
            // Get the document ID for the selected image
            String documentId = documentIds.get(position);
            showDeleteConfirmationDialog(documentId, position);
        });
    }

    private void showDeleteConfirmationDialog(String documentId, int position) {
        new AlertDialog.Builder(getActivity())
                .setTitle("Delete Image")
                .setMessage("Are you sure you want to delete this image?")
                .setPositiveButton(android.R.string.yes, (dialog, which) -> deleteImage(documentId, position))
                .setNegativeButton(android.R.string.no, null)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }

    private void deleteImage(String documentId, int position) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("images_testing").document(documentId)
                .delete()
                .addOnSuccessListener(aVoid -> {
                    imageList.remove(position); // Remove from the image list
                    documentIds.remove(position); // Also remove the document ID from its list
                    adapter.notifyDataSetChanged();
                    Toast.makeText(getActivity(), "Image deleted successfully", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> Toast.makeText(getActivity(), "Error deleting image", Toast.LENGTH_SHORT).show());
    }
}


