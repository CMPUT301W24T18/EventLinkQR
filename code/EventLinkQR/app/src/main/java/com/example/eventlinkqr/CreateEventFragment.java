package com.example.eventlinkqr;

import android.app.TimePickerDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.google.android.material.button.MaterialButton;
import com.google.firebase.Timestamp;

import java.util.Calendar;
import java.util.Date;

/**
 * this class takes care of taking in the input for a new event and adding it to the data
 */
public class CreateEventFragment extends Fragment implements DateTimePickerFragment.DateTimePickerListener{

    private String customQRString;
    private MaterialButton dateButton;
    Timestamp timestamp;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.create_event_page, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        String [] eventCategories = {"Networking","Entertainment", "Conference", "Trade show", "Workshop", "Product Launch", "Charity", "Category"};

        //Map all the input fields
        EditText nameInput = view.findViewById(R.id.event_name_input);
        EditText descriptionInput = view.findViewById(R.id.event_description_input);
        Spinner categoryInput = view.findViewById(R.id.category_selector);
        EditText locationInput = view.findViewById(R.id.event_location_input);
        SwitchCompat geoTracking = view.findViewById(R.id.new_event_geo_switch);
        Toolbar toolbar = view.findViewById(R.id.create_event_toolbar);
        dateButton = view.findViewById(R.id.date_picker);

        Button publishButton = view.findViewById(R.id.publish_button);
        Button chooseQrButton = view.findViewById(R.id.choose_qr_button);

        ((AppCompatActivity) requireActivity()).setSupportActionBar(toolbar);
        toolbar.setTitle(null);

        // make the back button return to the home page
        toolbar.setNavigationOnClickListener(v ->
                Navigation.findNavController(view).navigate(R.id.action_orgCreateEventFragment_to_attendeeHomePage));


        //Add the categories options to the spinner objects and hiding the descriptor "Categories" from selection
        //https://stackoverflow.com/questions/9863378/how-to-hide-one-item-in-an-android-spinner Romich, uploaded Feb 2, 2014
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(requireActivity(),
                android.R.layout.simple_spinner_item, eventCategories){
            @Override
            public int getCount() {
                //Truncate so "Categories" isn't one of the options
                return(eventCategories.length-1);
            }
        };
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        categoryInput.setAdapter(adapter);
        categoryInput.setSelection(eventCategories.length-1);

        publishButton.setOnClickListener(v -> {

            // store all the inputted values
            String name = nameInput.getText().toString().trim();
            String description = descriptionInput.getText().toString().trim();
            String category = categoryInput.getSelectedItem().toString();
            String location = locationInput.getText().toString().trim();
            Boolean tracking = geoTracking.isChecked();

            if(name.equals("") || description.equals("") || location.equals("") || category.equals("Category") || timestamp == null){
                // send wrong password message
                Toast.makeText(getContext(), "Please fill all fields", Toast.LENGTH_SHORT).show();
            } else {
                // create new event form data and add it toi the database using the event manager
                Event newEvent = new Event(name, description, category, timestamp, location, tracking, 0, 0);
                String organizer = ((AttendeeMainActivity) requireActivity()).getAttUUID();

                EventManager.createEvent(newEvent, organizer, customQRString);

                // return to the home page
                Navigation.findNavController(view).navigate(R.id.attendeeHomePage);
            }

        });

        // send message since the function is not yet implemented
        chooseQrButton.setOnClickListener(v -> {
            publishButton.setEnabled(false);
            ((AttendeeMainActivity) requireActivity()).getScanner().codeFromScan(codeText -> {
                this.customQRString = codeText;
                publishButton.setEnabled(true);
            }, e -> {
                Toast.makeText(requireActivity(), "Code scan failed", Toast.LENGTH_SHORT).show();
                publishButton.setEnabled(true);
            });
        });

        // launches the date picker dialog fragment
        dateButton.setOnClickListener(v -> {
            new DateTimePickerFragment().show(getChildFragmentManager(), "Select Date and Time");
        });

    }

    /**
     * listener for the date picker, receives the date and time selected by the organizer
     * @param dateAndtime the date and time of the new event
     */
    @Override
    public void addDateTime(Calendar dateAndtime) {
        Date date = dateAndtime.getTime();
        String timeChosen = date.toString();
        // display the time chosen on the screen so the user can confirm
        dateButton.setHint(timeChosen);
        this.timestamp = new Timestamp(date);
    }

}