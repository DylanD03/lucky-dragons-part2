package com.example.luckydragon.Activities;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.luckydragon.Controllers.EventController;
import com.example.luckydragon.GlobalApp;
import com.example.luckydragon.Models.Event;
import com.example.luckydragon.R;
import com.example.luckydragon.Views.EventView;

/**
 * EventActivity is the activity for viewing an event. Has a View and
 * Controller, and allows a Entrant to join/leave/accept an invitation to an
 * Event. It also displays a button to allow Admins to remove the QR code
 * or delete the event entirely.
 */
public class EventActivity extends AppBarActivity {
    private Event event;
    private EventView eventView;
    private EventController eventController;
    private String role;

    Button signUp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event);
        getSupportActionBar().setTitle("Event");

        String deviceId = ((GlobalApp) getApplication()).getUser().getDeviceId();
        String eventId = getIntent().getStringExtra("eventID");
        role = getIntent().getStringExtra("role");

        if (eventId == null) {
            throw new RuntimeException("Event Id is Null!");
        }

        event = ((GlobalApp) getApplication()).getEvent(eventId);  // TODO: Might have to move this onResume in case viewing diff event does not work
        eventController = new EventController(event);

        eventView = new EventView(event, deviceId, this);

        Button signUp = findViewById(R.id.signUpButton);
        Button cancel = findViewById(R.id.eventCancel);
        Button decline = findViewById(R.id.eventDecline);
        Button accept = findViewById(R.id.eventAccept);
        Button viewPoster = findViewById(R.id.viewPosterButton);
        Button deleteEvent = findViewById(R.id.deleteEventAdminView);
        Button removeQR = findViewById(R.id.removeQRAdminView);

        // Signup button will put their deviceID on the waitlist.
        signUp.setOnClickListener(v -> {
            eventController.waitList(deviceId);
        });

        cancel.setOnClickListener(v -> {
            eventController.cancel(deviceId);
        });

        decline.setOnClickListener(v -> {
            eventController.declineInvitation(deviceId);
        });

        accept.setOnClickListener(v -> {
            eventController.acceptInvitation(deviceId);
        });

        deleteEvent.setOnClickListener(v -> {
            eventController.deleteEvent(event.getId());
            Toast.makeText(EventActivity.this, "Deleted Successfully", Toast.LENGTH_SHORT).show();
            finish();
        });

        removeQR.setOnClickListener(v -> {
            eventController.removeQR(event.getId());
            Toast.makeText(EventActivity.this, "QR code removed successfully", Toast.LENGTH_SHORT).show();
            finish();
        });

        // ViewPoster button will show a dialog with an ImageView.
        viewPoster.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // ADD FUNCTIONALITY HERE.
            }
        });
    }

    public String getRole() {

        return role;

    }

}