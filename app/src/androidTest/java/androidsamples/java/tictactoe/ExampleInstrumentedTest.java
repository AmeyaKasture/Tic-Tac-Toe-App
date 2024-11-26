package androidsamples.java.tictactoe;

import android.content.Context;
import android.view.View;

import androidx.test.espresso.matcher.RootMatchers;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.withContentDescription;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertEquals;


@RunWith(AndroidJUnit4.class)
public class ExampleInstrumentedTest {

    @Rule
    public ActivityScenarioRule<MainActivity> activityRule =
            new ActivityScenarioRule<>(MainActivity.class);

    @Test
    public void useAppContext() {
        // Context of the app under test.
        Context appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
        assertEquals("androidsamples.java.tictactoe", appContext.getPackageName());
    }

    @Test
    public void testLoginScreenAccessibility() {
        // Check that the email EditText has a content description
        onView(withId(R.id.edit_email))
                .check(matches(withContentDescription("Email field for login")));

        // Check that the password EditText has a content description
        onView(withId(R.id.edit_password))
                .check(matches(withContentDescription("Password field for login")));

        // Check that the login button has a content description
        onView(withId(R.id.btn_log_in))
                .check(matches(withContentDescription("Click to log in")));
    }

    @Test
    public void testDashboardAccessibility() throws InterruptedException {
        // Step 1: Perform login
        InstrumentationRegistry.getInstrumentation().getUiAutomation()
                .executeShellCommand("settings put secure autofill_service null");

        onView(withId(R.id.edit_email))
                .perform(typeText("hi@gmail.com"), closeSoftKeyboard());

        onView(withId(R.id.edit_password))
                .perform(typeText("1122334455"), closeSoftKeyboard());

        onView(withId(R.id.btn_log_in))
                .perform(click());

        // Step 2: Add delay after clicking the login button
        Thread.sleep(3000); // Wait for 3 seconds

        // Step 3: Verify the Dashboard is displayed and check accessibility
        onView(withId(R.id.won_score))
                .check(matches(withContentDescription("Games won score")))
                .check(matches(withText("0")));

        onView(withId(R.id.lost_score))
                .check(matches(withContentDescription("Games lost score")))
                .check(matches(withText("0")));

        onView(withId(R.id.open_display))
                .check(matches(withContentDescription("List of open games")))
                .check(matches(withText("Open Games")));


    }

    @Test
    public void testLoginFailedToast() {
        // Step 1: Enter invalid email
        onView(withId(R.id.edit_email))
                .perform(typeText("hi@gmail.com"), closeSoftKeyboard());

        // Step 2: Enter invalid password
        onView(withId(R.id.edit_password))
                .perform(typeText("000000000"), closeSoftKeyboard());

        // Step 3: Click the login button
        onView(withId(R.id.btn_log_in))
                .perform(click());

        // Step 5: Retrieve the DecorView
        final View[] decorView = new View[1];
        activityRule.getScenario().onActivity(activity -> {
            decorView[0] = activity.getWindow().getDecorView();
        });

        // Step 6: Verify the toast message
        onView(withText("Authentication failed"))
                .inRoot(RootMatchers.withDecorView(not(decorView[0])))
                .check(matches(isDisplayed()));
    }









}
