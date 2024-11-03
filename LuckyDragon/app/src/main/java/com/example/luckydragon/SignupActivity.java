package com.example.luckydragon;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Switch;

import com.google.android.material.switchmaterial.SwitchMaterial;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;

public class SignupActivity extends AppBarActivity {
    private User user;
    private SignupController signupController;
    private SignupView signupView;

    private EditText editName;
    private EditText editEmail;
    private EditText editPhone;
    private SwitchMaterial switchNotifications;
    private Button submitButton;
    private ImageButton uploadProfilePictureButton;
    private ActivityResultLauncher<Intent> uploadImageResultLauncher;
    private Bitmap profilePicture;
    private ImageView profilePictureView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        getSupportActionBar().setTitle("Sign-Up");

        // Unpack intent
        user = ((GlobalApp) getApplication()).getUser();
        signupController = new SignupController(user);
        signupView = new SignupView(user, this, signupController);

        user.addObserver(signupView);

        // Get input fields
        editName = findViewById(R.id.signupName);
        editEmail = findViewById(R.id.signupEmail);
        editPhone = findViewById(R.id.signupPhone);

        // Profile picture
        uploadProfilePictureButton = findViewById(R.id.uploadProfilePicture);

        uploadImageResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        if (result.getResultCode() == Activity.RESULT_OK) {
                            // There are no request codes
                            Intent data = result.getData();
                            Uri image = data.getData();
                            assert(image != null);

                            try {
                                profilePicture = MediaStore.Images.Media.getBitmap(getContentResolver(), image);
                            } catch (Exception e) {
                                Log.e("signup", "error uploading pfp");
                            }

                            // TODO: Add intent to crop image and move it around when selecting if that exists
                            // TODO: Scale properly
                            profilePicture = Bitmap.createScaledBitmap(profilePicture, 120, 120, false);
                            signupController.setProfilePicture(profilePicture);
                        }
                    }
                });
        switchNotifications = findViewById(R.id.signupNotifications);
        submitButton = findViewById(R.id.signupSubmit);

        setupListeners();
    }

    public void setSubmitButton(boolean enabled) {
        submitButton.setEnabled(enabled);
    }

    private void setupListeners() {
        setListener(editName, () -> {
            // code that will run in x seconds
            signupController.extractName(editName);
            // TODO: input validation
        });
        setListener(editEmail, () -> {
            // code that will run in x seconds
            signupController.extractEmail(editEmail);
            // TODO: input validation
        });
        setListener(editPhone, () -> {
            // code that will run in x seconds
            signupController.extractPhoneNumber(editPhone);
            // TODO: input validation
        });

        // set listener for uploading pfp button
        uploadProfilePictureButton.setOnClickListener(view -> {
            Intent intent = new Intent();
            intent.setType("image/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            uploadImageResultLauncher.launch(intent);
        });

        submitButton.setOnClickListener(view -> {
            // tell activity to launch profile
            Intent intent = new Intent(this, ProfileActivity.class);
            intent.putExtra("role", "ENTRANT");

            // Start profile activity
            startActivity(intent);
            finish();

            // become entrant
            signupController.becomeEntrant();
        });
    }

    private void setListener(EditText editText, Runnable runnable) {
        Handler handler = new Handler();
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                // clear previous requested runnable
                handler.removeCallbacks(runnable);
            }

            @Override
            public void afterTextChanged(Editable editable) {
                handler.postDelayed(runnable, 1000);
            }
        });
    }
}
