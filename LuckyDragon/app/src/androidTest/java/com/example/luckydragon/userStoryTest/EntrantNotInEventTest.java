package com.example.luckydragon.userStoryTest;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.junit.Assert.assertTrue;

import android.content.Context;
import android.content.Intent;

import androidx.test.core.app.ActivityScenario;
import androidx.test.platform.app.InstrumentationRegistry;

import com.example.luckydragon.Activities.ViewEventActivity;
import com.example.luckydragon.GlobalApp;
import com.example.luckydragon.MockedDb;
import com.example.luckydragon.Models.Event;
import com.example.luckydragon.R;

import org.junit.Test;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class EntrantNotInEventTest extends MockedDb {
    private String deviceId = "fakeDeviceId";
    @Override
    protected HashMap<String, Object> getMockUserData() {
        // Define test user
        HashMap<String, Object> testUserData = new HashMap<>();
        // Personal info
        testUserData.put("name", "John Doe");
        testUserData.put("email", "jdoe@ualberta.ca");
        testUserData.put("phoneNumber", "780-831-3291");
        // Roles
        testUserData.put("isEntrant", true);
        testUserData.put("isOrganizer", true);
        testUserData.put("isAdmin", false);
        // Facility
        testUserData.put("facility", "The Sports Centre");

        return testUserData;
    }

    @Override
    protected void loadMockEventData(Map<String, Map<String, Object>> events) {
        HashMap<String, Object> eventData = getMockEventData();

//        String id = String.valueOf(new Random().nextInt());
        events.put(eventId, eventData);
    }

    private HashMap<String, Object> getMockEventData() {
        HashMap<String, Object> eventData = new HashMap<>();
        eventData.put("name", "C301 Standup");
        eventData.put("organizerDeviceId", "mockOrgId");
        eventData.put("facility", "UofA");
        eventData.put("waitListLimit", 10L);
        eventData.put("attendeeLimit", 10L);
        eventData.put("hasGeolocation", false);
        eventData.put("date", LocalDate.now().toString());
        eventData.put("hours", 10L);
        eventData.put("minutes", 30L);
        eventData.put("hashedQR", "Fake QR");
        eventData.put("waitList", new ArrayList<>());
        eventData.put("inviteeList", new ArrayList<>());
        eventData.put("attendeeList", new ArrayList<>());
        eventData.put("cancelledList", new ArrayList<>());

        return eventData;
    }



    /**
     * USER STORY TEST
     * > US 01.01.01 Entrant - join the waiting list for a specific event
     * Launch activity directly on event activity
     * User clicks sign up
     * User is now part of the waitlist
     * Button changes from "Join Waitlist" to "Cancel"
     * User can see that they are part of the waitlist
     * User can see on their profile they are on the waitlist
     */
    @Test
    public void testJoinWaitlist() {
        // Launch event activity directly
        final Context targetContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
        GlobalApp globalApp = (GlobalApp) targetContext.getApplicationContext();
        globalApp.setDb(mockFirestore);
        globalApp.setRole(GlobalApp.ROLE.ENTRANT);
        // Set event to view
        Event testEvent = new Event(eventId, mockFirestore);
        testEvent.parseEventDocument(getMockEventData());
        testEvent.setIsLoaded(true);
        globalApp.setEventToView(testEvent);
        // Launch event activity
        final Intent intent = new Intent(targetContext, ViewEventActivity.class);
        try(final ActivityScenario<ViewEventActivity> scenario = ActivityScenario.launch(intent)) {
            // Ensure that all event lists are empty
            assertTrue(testEvent.getInviteeList().isEmpty());
            assertTrue(testEvent.getAttendeeList().isEmpty());
            assertTrue(testEvent.getCancelledList().isEmpty());
            // Check that waitlist action button has correct text
            onView(withId(R.id.waitlistActionButton)).check(matches(withText("Join Waitlist")));
            // Click "join waitlist"
            onView(withId(R.id.waitlistActionButton)).perform(click());
            // Check that we are on waitlist
            assertTrue(testEvent.getWaitList().contains(deviceId));
            // Check that waitlist action button text changes to "Cancel"
            onView(withId(R.id.waitlistActionButton)).check(matches(withText("Cancel")));
        }
    }
}
