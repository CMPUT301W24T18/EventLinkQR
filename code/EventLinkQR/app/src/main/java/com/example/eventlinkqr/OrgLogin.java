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

        signInButton.setOnClickListener(v -> {

            // get the email and password the user entered
            password = inputPassword.getText().toString().trim();
            email = inputEmail.getText().toString().trim();

            if(email.equals("") || password.equals("")){
                // send message if either field is empty
                Toast.makeText(getContext(), "Please fill all fields", Toast.LENGTH_SHORT).show();
            }else{
                OrganizerManager.signIn(email, password).addOnSuccessListener(name -> {
                    // make the bottom buttons visible
                    ((OrgMainActivity) requireActivity()).setButtonsVisible();

                    ((OrgMainActivity) requireActivity()).setOrganizerName(name);
                    Navigation.findNavController(view).navigate(R.id.action_orgLogin_to_org_home_page);
                }).addOnFailureListener(name -> {
                    // send invalid email message
                    Toast.makeText(getContext(), "Invalid credentials", Toast.LENGTH_SHORT).show();
                });
            }
        });

        // redirects to the organizer sign up page
        signUpButton.setOnClickListener(v -> {

        });


    }
}
