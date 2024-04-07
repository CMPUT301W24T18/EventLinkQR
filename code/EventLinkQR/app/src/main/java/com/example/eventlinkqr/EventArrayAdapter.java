package com.example.eventlinkqr;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;

/**
 * custom adapter to display the desired info about the event on screen
 */
public class EventArrayAdapter extends ArrayAdapter<Event> {
    private ArrayList<Event> events;
    private Context context;
    public EventArrayAdapter(@NonNull Context context, ArrayList<Event> events) {
        super(context, 0, events);
        this.events = events;
        this.context = context;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertview, @NonNull ViewGroup parent){

        View view = convertview;

        if(convertview == null){
            view = LayoutInflater.from(context).inflate(R.layout.events_content, parent, false);
        }

        Event event = events.get(position);

        //Map all the TextViews
        TextView eventName = view.findViewById(R.id.event_title);
        TextView eventCategory = view.findViewById(R.id.event_description);
        ImageView eventPoster = view.findViewById(R.id.event_image);


        assert event != null;

        //Set the value of all the TextViews
        // this is a basic implementation that will be updated later on
        eventName.setText(event.getName());
        eventCategory.setText(event.getCategory());

        // set the poster
        ImageManager.getPoster(event.getId(), posterBitmap -> {
            // chatGPT "scale bitmap to another bitmap while retaining its shape"
            // Calculate the scaling factor to retain the aspect ratio
            if(posterBitmap != null){
                float scale;
                if (posterBitmap.getWidth() >= posterBitmap.getHeight()) {
                    scale = (float) eventPoster.getWidth() / posterBitmap.getWidth();
                } else {
                    scale = (float) eventPoster.getHeight() / posterBitmap.getHeight();
                }
                Bitmap scaleImage = Bitmap
                        .createScaledBitmap(posterBitmap, (int) (posterBitmap.getWidth() *scale), (int) (posterBitmap.getHeight() *scale), true);
                eventPoster.setImageBitmap(scaleImage);
            }else{
                eventPoster.setImageResource(R.drawable.ic_event);
            }
        });
        return view;
    }
}
