package com.example.eventlinkqr;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;

import androidx.navigation.Navigation;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.Date;
import java.util.Objects;

public class OrgHomePage extends Fragment {

    private ListView eventList;
    private EventArrayAdapter eventsAdapter;
    private ArrayList<Event> dataList;
    private Button createEventButton;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.org_home_page, container, false);
        eventList = view.findViewById(R.id.event_list_view);
        createEventButton= view.findViewById(R.id.create_event_button);

        // get the reference to the collection of events
        CollectionReference eventsRef = ((OrgMainActivity) requireActivity()).getDb().collection("Events");
        dataList = new ArrayList<>();
        eventsAdapter = new EventArrayAdapter(view.getContext(), dataList);

        eventList.setAdapter(eventsAdapter);


        // switch to the event page
        eventList.setOnItemClickListener((parent, view1, position, id) -> {
            ((OrgMainActivity) requireActivity()).setCurrentEvent(dataList.get(position));
            Navigation.findNavController(view1).navigate(R.id.action_home_to_orgEventFragment);
        });

        // switch to the create event page
        createEventButton.setOnClickListener(v ->
                Navigation.findNavController(view).navigate(R.id.action_home_page_to_createEventFragment));

        //add the data from the database into the events listview
        eventsRef.addSnapshotListener((querySnapshots, error) -> {
            if (error != null) {
                Log.e("Firestore", error.toString());
                return;
            }
            if (querySnapshots != null) {
                dataList.clear();
                for (QueryDocumentSnapshot doc: querySnapshots) {
                    // add event to the list if the current organizer is its organizer
                    if(Objects.equals(doc.getString("organizer"), ((OrgMainActivity) requireActivity()).getOrganizerName())) {
                        String eventName = doc.getString("name");
                        //https://stackoverflow.com/questions/67846416/firestore-timestamp-returning-objects-are-not-valid-as-react-child-found-obje
                        Timestamp time = (Timestamp) (doc.get("dateAndTime"));
                        assert time != null;
                        Date date = time.toDate();
                        String eventDate = date.toString();
                        Log.d("Firestore", String.format("Event(%s, %s) fetched", eventName,
                                eventDate));
                        dataList.add(new Event(eventName, eventDate));
                    }
                }
                eventsAdapter.notifyDataSetChanged();
            }
        });
        // Inflate the layout for this fragment
        return view;
    }
}