package androidsamples.java.tictactoe;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.rule.ActivityTestRule;

import org.junit.FixMethodOrder;
import org.junit.runners.MethodSorters;

import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static androidx.test.espresso.Espresso.*;
import static androidx.test.espresso.action.ViewActions.*;
import static androidx.test.espresso.assertion.ViewAssertions.*;
import static androidx.test.espresso.matcher.RootMatchers.isDialog;
import static androidx.test.espresso.matcher.ViewMatchers.*;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
@RunWith(AndroidJUnit4.class)
public class GameInstrumentedTest {

    @Rule
    public ActivityTestRule<MainActivity> mActivityRule =
            new ActivityTestRule<>(MainActivity.class);

    @Test
    public void test1UserLogin() {
        // Enter email
        onView(withId(R.id.edit_email))
                .perform(typeText("IAmTesting@sdpd.com"), closeSoftKeyboard());

        // Enter password
        onView(withId(R.id.edit_password))
                .perform(typeText("TestingPasswordSDPD"), closeSoftKeyboard());

        // Click login button
        onView(withId(R.id.btn_log_in))
                .perform(click());

        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

    }

    @Test
    public void test2StartNewSinglePlayerGame() {
        // Assume user is already logged in and on the dashboard

        try {
            Thread.sleep(200);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        // Click on new game FAB
        onView(withId(R.id.fab_new_game))
                .perform(click());

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        // Select "Single Player" option from dialog
        onView(withText(R.string.one_player)) // Use resource ID instead of hardcoded string
                .inRoot(isDialog()) // Specify we're looking in a dialog
                .perform(click());

    }

    @Test
    public void test4StartNewTwoPlayerGame() {
        // Enter email


        // Wait for login to complete
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        // Click on new game FAB
        onView(withId(R.id.fab_new_game))
                .perform(click());

        // Wait for dialog to appear
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        // Select "Two Player" from dialog using resource ID
        onView(withText(R.string.two_player))
                .inRoot(isDialog())
                .perform(click());

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        // Player taps on the first cell
        onView(withId(R.id.button0))
                .perform(click());

    }


    @Test
    public void test3ForfeitSinglePlayerGame() {
        // Enter email and login
//        onView(withId(R.id.edit_email))
//                .perform(typeText("IAmTesting@sdpd.com"), closeSoftKeyboard());
//
//        onView(withId(R.id.edit_password))
//                .perform(typeText("TestingPasswordSDPD"), closeSoftKeyboard());
//
//        onView(withId(R.id.btn_log_in))
//                .perform(click());

//         Wait for login
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        // Start a new game
        onView(withId(R.id.fab_new_game))
                .perform(click());

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        // Choose single player game
        onView(withText(R.string.one_player))
                .inRoot(isDialog())
                .perform(click());

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        onView(withText("X"))
                .inRoot(isDialog())
                .perform(click());
        // Make one move to ensure game is in progress
        onView(withId(R.id.button0))
                .perform(click());

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        // Click on the forfeit button
        onView(withId(R.id.back_btn))
                .perform(click());

        // If a confirmation dialog appears, confirm the forfeit
        onView(withText("Are you sure you want to forfeit this game?"))
                .inRoot(isDialog())
                .check(matches(isDisplayed()))
                .perform(click());



    }

    @Test
    public void test5ForfeitMultiPlayerGame() {
        // Enter email and login
//        onView(withId(R.id.edit_email))
//                .perform(typeText("IAmTesting@sdpd.com"), closeSoftKeyboard());
//
//        onView(withId(R.id.edit_password))
//                .perform(typeText("TestingPasswordSDPD"), closeSoftKeyboard());
//
//        onView(withId(R.id.btn_log_in))
//                .perform(click());

        // Wait for login
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        // Start a new game
        onView(withId(R.id.fab_new_game))
                .perform(click());

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        // Choose single player game
        onView(withText(R.string.two_player))
                .inRoot(isDialog())
                .perform(click());

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        // Make one move to ensure game is in progress
        onView(withId(R.id.button0))
                .perform(click());

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        // Click on the forfeit button
        onView(withId(R.id.back_btn))
                .perform(click());

        // If a confirmation dialog appears, confirm the forfeit
        onView(withText("Are you sure you want to forfeit this game?"))
                .inRoot(isDialog())
                .check(matches(isDisplayed()))
                .perform(click());



    }

    @Test
    public void test7Logout() {
//        onView(withId(R.id.edit_email))
//                .perform(typeText("IAmTesting@sdpd.com"), closeSoftKeyboard());
//
//        onView(withId(R.id.edit_password))
//                .perform(typeText("TestingPasswordSDPD"), closeSoftKeyboard());
//
//        onView(withId(R.id.btn_log_in))
//                .perform(click());

        // Wait for login
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        openActionBarOverflowOrOptionsMenu(mActivityRule.getActivity());
        onView(withText("Log Out"))
                .perform(click());
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        // Check if login screen is displayed
        onView(withId(R.id.edit_email))
                .check(matches(isDisplayed()));
    }

}