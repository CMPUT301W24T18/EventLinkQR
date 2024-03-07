package com.example.eventlinkqr;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SwitchCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;

import java.util.HashMap;

/**
 * this class takes care of taking in the input for a new event and adding it to the data
 */
public class OrgCreateEventFragment extends Fragment {

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.org_create_event_page, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //String [] genresList = {"Networking","Entertainment"};

        //Map all the input fields
        EditText nameInput = view.findViewById(R.id.event_name_input);
        EditText descriptionInput = view.findViewById(R.id.event_description_input);
        Spinner categoryInput = view.findViewById(R.id.category_selector);
        EditText locationInput = view.findViewById(R.id.event_location_input);
        SwitchCompat geoTracking = view.findViewById(R.id.new_event_geo_switch);

        Button publishButton = view.findViewById(R.id.publish_button);
        Button chooseQrButton = view.findViewById(R.id.choose_qr_button);

        CollectionReference eventRef = ((OrgMainActivity) requireActivity()).getDb().collection("Events");

        publishButton.setOnClickListener(v -> {

            String name = nameInput.getText().toString().trim();
            String description = descriptionInput.getText().toString().trim();
            //String category = categoryInput.getSelectedItem().toString();
            String location = locationInput.getText().toString().trim();
            Boolean tracking = geoTracking.isChecked();

            if(name.equals("") || description.equals("") || location.equals("") ){
                // send wrong password message
                Toast.makeText(getContext(), "Please fill all fields", Toast.LENGTH_SHORT).show();
            }else{
                HashMap<String, Object> eventDocument = new HashMap<>();
                eventDocument.put("name", name);
                eventDocument.put("description", description);
                eventDocument.put("category", "networking");
                eventDocument.put("location", location);
                eventDocument.put("dateAndTime", Timestamp.now());
                eventDocument.put("geoTracking", tracking);
                eventDocument.put("organizer", ((OrgMainActivity) requireActivity()).getOrganizerName());
                eventDocument.put("checkin", ((OrgMainActivity) requireActivity()).getOrganizerName());


                //adds the event's information
                eventRef.add(eventDocument).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                        @Override
                        public void onSuccess(DocumentReference documentReference) {
                        }
                });

                // create empty collection for attendees
//                eventRef.document(name).collection("attendees")
//
//                        .document()
//                        .set(new Object())  // Set a dummy object or an empty map as the document content
//                        .addOnSuccessListener(new OnSuccessListener<Void>() {
//                            @Override
//                            public void onSuccess(Void aVoid) {
//                                // Successfully added an empty document to the subcollection
//                                // Handle success if needed
//                            }
//                        })
//                        .addOnFailureListener(new OnFailureListener() {
//                            @Override
//                            public void onFailure(@NonNull Exception e) {
//                                // Handle failures
//                            }
//                        });

                // return to the home page
                Navigation.findNavController(view).navigate(R.id.action_createEventFragment_to_org_home_page);

            }

        });

    }

}