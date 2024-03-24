package com.example.eventlinkqr;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
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

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_admin_image, container, false);
        listView = view.findViewById(R.id.listViewImages);
        imageList = new ArrayList<>();
        adapter = new ImageAdapter(getActivity(), R.layout.item_image, imageList);
        listView.setAdapter(adapter);
        fetchImages();
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
                        }
                        adapter.notifyDataSetChanged();
                    }
                })
                .addOnFailureListener(e -> {/* Handle error */});
    }
}
