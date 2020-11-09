package com.example.bookmark;

import android.content.Intent;
import android.os.Bundle;
import android.telephony.PhoneNumberFormattingTextWatcher;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;


import com.example.bookmark.models.User;
import com.example.bookmark.server.FirebaseProvider;
import com.google.android.material.textfield.TextInputLayout;

import java.util.ArrayList;

import static com.example.bookmark.util.UserInfoFormValidator.checkIfEditTextValidEmail;
import static com.example.bookmark.util.UserInfoFormValidator.checkIfEditTextValidPhoneNumber;
import static com.example.bookmark.util.UserInfoFormValidator.validateEditTextEmpty;

public class SignupActivity extends BackButtonActivity {
    private EditText userNameEditText, firstNameEditText, lastNameEditText, emailAddressEditText, phoneNumberEditText;
    private TextInputLayout userNameLayout, firstNameLayout, lastNameLayout, emailAddressLayout, phoneNumberLayout;
    private Button signUpButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        getSupportActionBar().setTitle("Sign Up");

        FirebaseProvider firebaseProvider = FirebaseProvider.getInstance();

        // get relevant views
        userNameEditText = findViewById(R.id.signup_username_edit_text);
        firstNameEditText = findViewById(R.id.signup_firstname_edit_text);
        lastNameEditText = findViewById(R.id.signup_lastname_edit_text);
        emailAddressEditText = findViewById(R.id.signup_email_edit_text);
        phoneNumberEditText = findViewById(R.id.signup_phonenumber_edit_text);
        signUpButton = findViewById(R.id.signup_signup_button);

        userNameLayout = findViewById(R.id.signup_username_textInputLayout);
        firstNameLayout = findViewById(R.id.signup_firstname_textInputLayout);
        lastNameLayout = findViewById(R.id.signup_lastname_textInputLayout);
        emailAddressLayout = findViewById(R.id.signup_email_textInputLayout);
        phoneNumberLayout = findViewById(R.id.signup_phonenumber_textInputLayout);

        ArrayList<EditText> formTexts = new ArrayList<>();
        formTexts.add(userNameEditText);
        formTexts.add(firstNameEditText);
        formTexts.add(lastNameEditText);

        ArrayList<TextInputLayout> formLayouts = new ArrayList<>();
        formLayouts.add(userNameLayout);
        formLayouts.add(firstNameLayout);
        formLayouts.add(lastNameLayout);

        signUpButton.setOnClickListener(view -> {
            boolean invalidField = false;
            // double check all form fields filled
            for (int i = 0; i < formTexts.size(); i++) {
                if (!validateEditTextEmpty(formTexts.get(i), formLayouts.get(i))) {
                    invalidField = true;
                }
            }
            if (!checkIfEditTextValidEmail(emailAddressEditText, emailAddressLayout)) {
                invalidField = true;
            }
            if (!checkIfEditTextValidPhoneNumber(phoneNumberEditText, phoneNumberLayout)) {
                invalidField = true;
            }
            if (invalidField) {
                return;
            }

            // check username is not in db already
            String username = userNameEditText.getText().toString();
            firebaseProvider.retrieveUserByUsername(username,
                user -> {
                    if (user == null) {
                        // Get values from fields and create user object
                        String firstName = firstNameEditText.getText().toString();
                        String lastName = lastNameEditText.getText().toString();
                        String emailAddress = emailAddressEditText.getText().toString();
                        String phoneNumber = phoneNumberEditText.getText().toString();

                        // add user and go back to main activity
                        User newUser = new User(username, firstName, lastName, emailAddress, phoneNumber);
                        firebaseProvider.storeUser(newUser,
                            aVoid -> {
                            },
                            e -> Toast.makeText(SignupActivity.this, "Connection Error. Please Try again.", Toast.LENGTH_LONG).show());
                        Intent intent = new Intent(SignupActivity.this, MainActivity.class);
                        intent.putExtra("SIGNED_UP_USERNAME", username);
                        startActivity(intent);
                    } else {
                        userNameLayout.setError("Username already registered. Please choose another");
                    }
                },
                e -> Toast.makeText(SignupActivity.this, "Connection Error. Please Try again.", Toast.LENGTH_LONG).show());
        });

        // allows user to see if their email address is valid as they type
        phoneNumberEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                checkIfEditTextValidPhoneNumber(phoneNumberEditText, phoneNumberLayout);
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        phoneNumberEditText.addTextChangedListener(new PhoneNumberFormattingTextWatcher());

        // allows user to see if their phone number is valid as they type
        emailAddressEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                checkIfEditTextValidEmail(emailAddressEditText, emailAddressLayout);
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        // focus change listeners
        for (int i = 0; i < formTexts.size(); i++) {
            EditText text = formTexts.get(i);
            TextInputLayout layout = formLayouts.get(i);
            formTexts.get(i).setOnFocusChangeListener(new EmptyTextFocusListener(text, layout));
        }

        emailAddressEditText.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) {
                emailAddressLayout.setError(null);
            } else {
                if (emailAddressEditText.getText().toString().equals("")) {
                    validateEditTextEmpty(emailAddressEditText, emailAddressLayout);
                } else {
                    checkIfEditTextValidEmail(emailAddressEditText, emailAddressLayout);
                }
            }
        });

        phoneNumberEditText.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) {
                phoneNumberLayout.setError(null);
            } else {
                if (phoneNumberEditText.getText().toString().equals("")) {
                    validateEditTextEmpty(phoneNumberEditText, phoneNumberLayout);
                } else {
                    checkIfEditTextValidPhoneNumber(phoneNumberEditText, phoneNumberLayout);
                }
            }
        });
    }
}

/**
 * Custom OnFocusChangeListener to eliminate super repetitive listener
 * declarations for all "empty text" checking situations.
 */
class EmptyTextFocusListener implements View.OnFocusChangeListener {
    EditText text;
    TextInputLayout layout;

    public EmptyTextFocusListener(EditText text, TextInputLayout layout) {
        this.text = text;
        this.layout = layout;
    }

    public void onFocusChange(View v, boolean hasFocus) {
        if (hasFocus) {
            this.layout.setError(null);
        } else {
            validateEditTextEmpty(this.text, this.layout);
        }
    }
}
