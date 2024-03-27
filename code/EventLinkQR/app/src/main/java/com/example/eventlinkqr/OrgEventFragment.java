package com.example.eventlinkqr;

import static androidx.core.content.FileProvider.getUriForFile;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;


/**
 * takes care of the event page on the organizer activity
 */
public class OrgEventFragment extends Fragment {

    private ImageView qrCodeImage;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.org_event_page, container, false);

        /** All buttons and the toolbar that will be used on this page*/
        Button detailsButton = view.findViewById(R.id.details_button);
        Button attendeesButton = view.findViewById(R.id.attendees_button);
        Button promotionalButton = view.findViewById(R.id.promotional_qr_button);
        Button sharebutton = view.findViewById(R.id.share_qr_button);
        ImageView notificationSendIcon = view.findViewById(R.id.notification_send_icon);
        TextView eventTitle = view.findViewById(R.id.org_event_name);
        TextView eventLocation = view.findViewById(R.id.org_event_location);
        TextView eventDescription = view.findViewById(R.id.org_event_description);
        TextView eventDate = view.findViewById(R.id.org_event_datetime);
        qrCodeImage = view.findViewById(R.id.imageView);

        Toolbar orgEventToolBar = view.findViewById(R.id.org_event_toolbar);

        ((AppCompatActivity) requireActivity()).setSupportActionBar(orgEventToolBar);
        orgEventToolBar.setTitle(null);

        // make the back button return to the home page
        orgEventToolBar.setNavigationOnClickListener(v ->
                Navigation.findNavController(view).navigate(R.id.attendeeHomePage));

        // make the attendees button go to the attendees page
        attendeesButton.setOnClickListener(v ->
                Navigation.findNavController(view).navigate(R.id.action_orgEventFragment_to_orgAttendeesPage));

        // Set the onClickListener for the send notification icon
        notificationSendIcon.setOnClickListener(v ->
                Navigation.findNavController(view).navigate(R.id.action_orgEventFragment_to_viewNotification));


        Event event = ((AttendeeMainActivity) requireActivity()).getCurrentEvent();

        // temporary message since it is not yet completely implemented
        detailsButton.setOnClickListener(v -> {
            if (event != null) {
                Intent intent = new Intent(getActivity(), OrganizerEventStats.class);
                intent.putExtra("eventId", event.getId()); // Replace "eventId" with the key you are using in OrganizerEventStats to retrieve the extra

                startActivity(intent);
            } else {
                Toast.makeText(getContext(), "Event details are not available", Toast.LENGTH_SHORT).show();
            }
        });

        // Set the values to be displayed
        eventTitle.setText(event.getName());
        eventLocation.setText(event.getLocation());
        eventDescription.setText(event.getDescription());
        eventDate.setText(event.getDate().toDate().toString());


        QRCodeManager.fetchQRCode(event, QRCode.CHECK_IN_TYPE).addOnSuccessListener(q -> {
            try {
                qrCodeImage.setImageBitmap(q.toBitmap(512, 512));
                sharebutton.setOnClickListener(v -> {
                    shareImage(q);
                });
            } catch (QRCodeGeneratorException e) {
                throw new RuntimeException(e);
            }
        });

        // When clicked, bring up the promotional QR code
        promotionalButton.setOnClickListener(v -> {
            QRCodeManager.fetchQRCode(event, QRCode.PROMOTIONAL_TYPE).addOnSuccessListener(this::shareImage);
        });

        // Inflate the layout for this fragment
        return view;
    }

    private void shareImage(QRCode q) {
        try {
            File temp = new File(requireContext().getCacheDir(), "qr.png");
            FileOutputStream outputStream = new FileOutputStream(temp);
            q.toBitmap(512, 512).compress(Bitmap.CompressFormat.PNG,100, outputStream);

            Uri contentUri = getUriForFile(requireContext(), "com.example.eventlinkqr.fileprovider", temp);

            // See https://developer.android.com/training/sharing/send#java
            Intent sendIntent = new Intent();
            sendIntent.setAction(Intent.ACTION_SEND);
            sendIntent.putExtra(Intent.EXTRA_STREAM, contentUri);
            sendIntent.setType("image/png");
            startActivity(sendIntent);
        } catch (IOException | QRCodeGeneratorException e) {
            throw new RuntimeException(e);
        }
    }
}