package com.example.skipthegas;

import androidx.test.rule.ActivityTestRule;
import com.robotium.solo.Solo;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import android.app.Activity;
import android.widget.EditText;
import android.widget.TextView;
import androidx.test.platform.app.InstrumentationRegistry;

import org.junit.Rule;
import org.junit.Before;
import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;

import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

@RunWith(AndroidJUnit4.class)
public class MainActivityTest {
    private Solo solo;

    @Rule
    public ActivityTestRule<MainActivity> rule =
            new ActivityTestRule<>(MainActivity.class, true, true);
    /**
     * Runs before all tests and creates solo instance
     * @throws Exception
     */
    @Before
    public void setUp() throws Exception{
        solo = new Solo(InstrumentationRegistry.getInstrumentation(), rule.getActivity());
    }
    /**
     * Gets the activity
     * @throws Exception
     */
    @Test
    public void start()throws Exception{
        Activity activity = rule.getActivity();
    }
    /**
     * Login to the application
     */
    @Test
    public void logIn(){
        solo.assertCurrentActivity("Wrong Activity", MainActivity.class);
        solo.clickOnButton("Log In");
        solo.assertCurrentActivity("Wrong Activity", LogInActivity.class);
    }

    /**
     * Sign in to the application
     */
    @Test
    public void signUp(){
        solo.assertCurrentActivity("Wrong Activity", MainActivity.class);
        solo.clickOnButton("Sign Up");
        solo.assertCurrentActivity("Wrong Activity", SignUpActivity.class);
    }

    /**
     * Close activity after each test
     * @throws Exception
     */
    @After
    public void tearDown() throws Exception{
        solo.finishOpenedActivities();
    }
}
