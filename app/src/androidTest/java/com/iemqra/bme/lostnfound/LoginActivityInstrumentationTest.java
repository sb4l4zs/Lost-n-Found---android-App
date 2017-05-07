package com.iemqra.bme.lostnfound;

import android.app.Application;
import android.support.test.rule.ActivityTestRule;
import android.test.ApplicationTestCase;

import com.iemqra.bme.lostnfound.ui.activity.LoginActivity;

import org.junit.Rule;
import org.junit.Test;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;

/**
 * <a href="http://d.android.com/tools/testing/testing_android.html">Testing Fundamentals</a>
 */
public class LoginActivityInstrumentationTest {
    @Rule
    public ActivityTestRule<LoginActivity> activityTestRule =
            new ActivityTestRule<>(LoginActivity.class);

    @Test
    public void validateEditText() {
        onView(withId(R.id.activity_login_title)).check(matches(withText("Lost n Found")));
    }
}