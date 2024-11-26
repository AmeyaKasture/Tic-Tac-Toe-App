package androidsamples.java.tictactoe;

import android.content.Context;
import android.text.InputType;
import android.view.View;
import android.widget.EditText;

import androidx.navigation.fragment.NavHostFragment;
import androidx.test.espresso.matcher.RootMatchers;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;

import org.hamcrest.Description;
import org.hamcrest.TypeSafeMatcher;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.withContentDescription;
import static androidx.test.espresso.matcher.ViewMatchers.withHint;
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
    public void testEmailFieldAccessibility() {
        // Check that the email field is displayed
        onView(withId(R.id.edit_email))
                .check(matches(isDisplayed()));

        // Check the content description of the email field
        onView(withId(R.id.edit_email))
                .check(matches(withContentDescription("Email field for login")));

        // Check the hint text of the email field
        onView(withId(R.id.edit_email))
                .check(matches(withHint("Email")));
    }

    @Test
    public void testPasswordFieldAccessibility() {
        // Check that the password field is displayed
        onView(withId(R.id.edit_password))
                .check(matches(isDisplayed()));

        // Check the content description of the password field
        onView(withId(R.id.edit_password))
                .check(matches(withContentDescription("Password field for login")));

        // Check the hint text of the password field
        onView(withId(R.id.edit_password))
                .check(matches(withHint("Password")));
    }

    @Test
    public void testLoginButtonAccessibility() {
        // Check that the login button is displayed
        onView(withId(R.id.btn_log_in))
                .check(matches(isDisplayed()));

        // Check the content description of the login button
        onView(withId(R.id.btn_log_in))
                .check(matches(withContentDescription("Click to log in")));
    }

    @Test
    public void testFieldsAreVisibleTogether() {
        // Check that all fields are displayed on the screen
        onView(withId(R.id.edit_email))
                .check(matches(isDisplayed()));
        onView(withId(R.id.edit_password))
                .check(matches(isDisplayed()));
        onView(withId(R.id.btn_log_in))
                .check(matches(isDisplayed()));
    }

    @Test
    public void testEmailFieldHintAndContentDescription() {
        // Verify the email field's hint and content description are correctly set
        onView(withId(R.id.edit_email))
                .check(matches(withHint("Email")))
                .check(matches(withContentDescription("Email field for login")));
    }






}
