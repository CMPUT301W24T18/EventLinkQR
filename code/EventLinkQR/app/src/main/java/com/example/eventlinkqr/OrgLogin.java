package com.example.eventlinkqr;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.Objects;

/**
 * this class handles the signin page for the organizer, including the password and email validation
 */
public class OrgLogin extends Fragment {

    private String password, email;

    private EditText inputPassword, inputEmail;
    private Button signInButton, signUpButton;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.org_login_page, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        inputEmail = view.findViewById(R.id.org_email_address);
        inputPassword = view.findViewById(R.id.org_password);
        signInButton = view.findViewById(R.id.org_signin_button);
        signUpButton = view.findViewById(R.id.org_signup_button);

        CollectionReference organizersRef = ((OrgMainActivity) requireActivity()).getDb().collection("Organizers");

        signInButton.setOnClickListener(v -> {

            // get the email and password the user entered
            password = inputPassword.getText().toString().trim();
            email = inputEmail.getText().toString().trim();

            if(email.equals("") || password.equals("")){
                // send message if either field is empty
                Toast.makeText(getContext(), "Please fill all fields", Toast.LENGTH_SHORT).show();
            }else{
                // look for organizers that have the email the user entered
                organizersRef.whereEqualTo("email", email)
                        .get()
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                // Retrieve the query snapshot
                                QuerySnapshot querySnapshot = task.getResult();
                                if (querySnapshot != null && !querySnapshot.isEmpty()) {
                                    // organizer with the matching email
                                    DocumentSnapshot doc = querySnapshot.getDocuments().get(0);

                                    // set the name of the organizer for future queries and go to the home page
                                    if(Objects.equals(password, doc.getString("password"))){

                                        // make the bottom buttons visible
                                        ((OrgMainActivity) requireActivity()).setButtonsVisible();

                                        ((OrgMainActivity) requireActivity()).setOrganizerName(doc.getString("name"));
                                        Navigation.findNavController(view).navigate(R.id.action_orgLogin_to_org_home_page);
                                    }else{
                                        // send wrong password message
                                        Toast.makeText(getContext(), "Wrong password", Toast.LENGTH_SHORT).show();
                                    }
                                } else {
                                    // send invalid email message
                                    Toast.makeText(getContext(), "Invalid email", Toast.LENGTH_SHORT).show();
                                }
                            } else {
                                // Handle errors
                                Exception exception = task.getException();
                                if (exception != null) {
                                    exception.printStackTrace();
                                }
                            }
                        });
            }
        });

        // redirects to the organizer sign up page
        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getContext(), "This function is not ready yet", Toast.LENGTH_SHORT).show();
            }
        });


    }
}
