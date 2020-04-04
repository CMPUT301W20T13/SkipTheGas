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
public class LogInActivityTest {
    private Solo solo;

    @Rule
    public ActivityTestRule<LogInActivity> rule =
            new ActivityTestRule<>(LogInActivity.class, true, true);

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
     * Sign in to the application with correct information
     */
    @Test
    public void logInSuccess(){
        solo.assertCurrentActivity("Wrong Activity", LogInActivity.class);
        solo.enterText((EditText)solo.getView(R.id.email_login_field), "nan.p2198@gmail.com");
        solo.enterText((EditText)solo.getView(R.id.password_login_field), "logintest");
        solo.clickOnButton("Log In");
        solo.assertCurrentActivity("Wrong Activity", SelectionActivity.class);
    }

    /**
     * Attempt to sign in to the application with incorrect information
     */
    @Test
    public void logInFail(){
        solo.assertCurrentActivity("Wrong Activity", LogInActivity.class);
        solo.enterText((EditText)solo.getView(R.id.email_login_field), "k3v1n@chili.bureau");
        solo.enterText((EditText)solo.getView(R.id.password_login_field), "chilistain42069");
        solo.clickOnButton("Log In");
        solo.assertCurrentActivity("Wrong Activity", LogInActivity.class);
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
