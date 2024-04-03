package com.example.eventlinkqr;

import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.Timestamp;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * this class takes care of taking in the input for a new event and adding it to the data
 */
public class CreateEventFragment extends Fragment implements DateTimePickerFragment.DateTimePickerListener{

    private String customQRString;
    private MaterialButton dateButton;
    Timestamp timestamp;
    private ImageView posterImage;
    private Uri imageUri;

    // ActivityResultLauncher for handling gallery selection result
    private final ActivityResultLauncher<String> getImage = registerForActivityResult(
            new ActivityResultContracts.GetContent(),
            uri -> {
                if(uri != null){
                    setImageUri(uri);
                }
            });

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.create_event_page, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        List<String> eventCategories = Arrays.asList("Networking", "Entertainment", "Conference", "Trade show", "Workshop", "Product Launch", "Charity", "other", "Category");
        Bundle arguments = getArguments();

        //Map all the input fields
        TextView pageTitle = view.findViewById(R.id.create_event_title);
        EditText nameInput = view.findViewById(R.id.event_name_input);
        EditText descriptionInput = view.findViewById(R.id.event_description_input);
        EditText maxAttendeesInput = view.findViewById(R.id.max_attendees);
        EditText locationInput = view.findViewById(R.id.event_location_input);

        posterImage = view.findViewById(R.id.poster_image);

        Spinner categoryInput = view.findViewById(R.id.category_selector);
        SwitchCompat geoTracking = view.findViewById(R.id.new_event_geo_switch);
        Toolbar toolbar = view.findViewById(R.id.create_event_toolbar);

        dateButton = view.findViewById(R.id.date_picker);
        FloatingActionButton clearPoster = view.findViewById(R.id.poster_button);
        Button publishButton = view.findViewById(R.id.publish_button);
        Button chooseQrButton = view.findViewById(R.id.choose_qr_button);

        ((AppCompatActivity) requireActivity()).setSupportActionBar(toolbar);
        toolbar.setTitle(null);
        pageTitle.setText("Create event");

        // make the back button return to the home page
        toolbar.setNavigationOnClickListener(v ->
                Navigation.findNavController(view).navigate(R.id.action_orgCreateEventFragment_to_attendeeHomePage));

        // set the onclick listener for selecting a poster for the event
        posterImage.setOnClickListener(v ->
            getImage.launch("image/*"));

        // deselect the image poster
        clearPoster.setOnClickListener(v ->
                posterImage.setImageResource(R.drawable.ic_add_photo));

        //Add the categories options to the spinner objects and hiding the descriptor "Categories" from selection
        //https://stackoverflow.com/questions/9863378/how-to-hide-one-item-in-an-android-spinner Romich, uploaded Feb 2, 2014
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(requireActivity(),
                android.R.layout.simple_spinner_item, eventCategories){
            @Override
            public int getCount() {
                //Truncate so "Categories" isn't one of the options
                return(eventCategories.size()-1);
            }
        };
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        categoryInput.setAdapter(adapter);
        categoryInput.setSelection(eventCategories.size()-1);

        // set the values if we are editing an event instead of creating one
        if(arguments != null){
            pageTitle.setText("Edit event");
            nameInput.setText(arguments.getString("name"));
            descriptionInput.setText(arguments.getString("description"));
            maxAttendeesInput.setHint("You cannot change the attendee limit");
            maxAttendeesInput.setEnabled(false);
            locationInput.setText(arguments.getString("location"));

            // set the spinner to the position of the previous category
            String cat = arguments.getString("category");
            int descriptionPos = eventCategories.indexOf(cat);
            categoryInput.setSelection(descriptionPos);

            // set the time previously chosen
            long timeMilli = arguments.getLong("date");
            timestamp = new Timestamp(timeMilli / 1000, (int) ((timeMilli % 1000) * 1000000));
            dateButton.setHint(timestamp.toDate().toString());

            geoTracking.setChecked(arguments.getBoolean("geo"));

            // set the poster of the event
            ImageManager.getPoster(arguments.getString("id"), posterBitmap -> {
                if(posterBitmap != null) {
                    float scale;
                    if (posterBitmap.getWidth() >= posterBitmap.getHeight()) {
                        scale = (float) posterImage.getWidth() / posterBitmap.getWidth();
                    } else {
                        scale = (float) posterImage.getHeight() / posterBitmap.getHeight();
                    }
                    Bitmap scaleImage = Bitmap
                            .createScaledBitmap(posterBitmap, (int) (posterBitmap.getWidth() *scale), (int) (posterBitmap.getHeight() *scale), true);
                    posterImage.setImageBitmap(scaleImage);
                }
            });
        }


        publishButton.setOnClickListener(v -> {

            // store all the inputted values
            String name = nameInput.getText().toString().trim();
            String description = descriptionInput.getText().toString().trim();
            String category = categoryInput.getSelectedItem().toString();
            String location = locationInput.getText().toString().trim();
            String maxAttendee = maxAttendeesInput.getText().toString().trim();
            Boolean tracking = geoTracking.isChecked();

            if(name.equals("") || description.equals("") || location.equals("") || category.equals("Category") || timestamp == null){
                // send error message
                Toast.makeText(getContext(), "Please fill all fields", Toast.LENGTH_SHORT).show();
            } else {
                // create new event form data and add it to the database using the event manager
                Event event = new Event(name, description, category, timestamp, location, tracking);
                String organizer = ((AttendeeMainActivity) requireActivity()).getAttUUID();
                Bitmap image = null;

                // set the value of the image
                if (imageUri != null) {
                    // https://stackoverflow.com/questions/3879992/how-to-get-bitmap-from-an-uri
                    try {
                        image = MediaStore.Images.Media.getBitmap(requireActivity().getContentResolver(), imageUri);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }

                if(arguments != null) {
                    //edit the information of the event of the event
                    event.setId(arguments.getString("id"));
                    EventManager.editEvent(event);
                    // delete the previous poster if the user removed the poster
                    if(image == null){
                        ImageManager.deletePoster(event.getId());
                    }else{
                        ImageManager.uploadPoster(getContext(), event.getId(), image);
                    }
                }else{
                    // if the user, hasn't set a limit, set it to a default value
                    if(maxAttendee.equals("")) {
                        EventManager.createEvent(getContext(), event, organizer, customQRString, Integer.MAX_VALUE, image);
                    }else{
                        EventManager.createEvent(getContext(), event, organizer, customQRString, Integer.parseInt(maxAttendee), image);
                    }
                }
                // return to the home page
                Navigation.findNavController(view).navigate(R.id.attendeeHomePage);
            }

        });

        // Scan in a custom code.
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
        dateButton.setOnClickListener(v ->
                new DateTimePickerFragment().show(getChildFragmentManager(), "Select Date and Time"));

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

    private void setImageUri(Uri uri){
        this.imageUri = uri;
        if(imageUri != null){
            Picasso.get().load(imageUri).resize(0, posterImage.getHeight()).into(posterImage);
        }
    }

}