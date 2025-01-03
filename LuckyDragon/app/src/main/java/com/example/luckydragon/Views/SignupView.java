package com.example.luckydragon.Views;

import com.example.luckydragon.Controllers.SignupController;
import com.example.luckydragon.GlobalApp;
import com.example.luckydragon.Models.Observable;
import com.example.luckydragon.Models.User;
import com.example.luckydragon.Activities.SignupActivity;

/**
 * The SignupView class is the View managed by the SignupActivity. Handles displaying
 * the user profile information and updating it if the User model changes.
 */
public class SignupView extends Observer {
    private final SignupActivity signupActivity;
    private final SignupController signupController;

    public SignupView(User user, SignupActivity signupActivity, SignupController signupController) {
        this.signupActivity = signupActivity;
        this.signupController = signupController;
        startObserving(user);
    }

    @Override
    public User getObservable() {
        return (User) super.getObservable();
    }

    /**
     * Updates the View when the User model has changed.
     * @param whoUpdatedMe the Observable that updated this View
     */
    @Override
    public void update(Observable whoUpdatedMe) {
        // check if user has valid fields
        signupActivity.setSubmitButton(getObservable().isValid());
        signupActivity.updateProfilePictureIcon(getObservable().getUploadedProfilePicture());

        // hide signup top bar if user is signing (not editing)
        if (signupActivity.getRole() == GlobalApp.ROLE.ENTRANT && !getObservable().isEntrant()) {
            signupActivity.setNavProfileVisible(false);
        } else if (signupActivity.getRole() == GlobalApp.ROLE.ORGANIZER && !getObservable().isOrganizer()) {
            signupActivity.setNavProfileVisible(false);
        } else {
            signupActivity.setNavProfileVisible(true);
        }
    }
}
