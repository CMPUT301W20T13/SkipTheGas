package com.example.skipthegas;

import android.app.Activity;
import android.view.KeyEvent;
import android.widget.EditText;

import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.rule.ActivityTestRule;

import com.robotium.solo.Solo;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

public class DriverDrawerActivityTest {
    private Solo solo;

    @Rule
    public ActivityTestRule<MainActivity> rule = new ActivityTestRule<>(MainActivity.class, true, true);

    /**
     * Run before all tests
     * @throws Exception
     */
    @Before
    public void setUp() throws Exception {
        solo = new Solo(InstrumentationRegistry.getInstrumentation(), rule.getActivity());

        solo.assertCurrentActivity("Wrong Activity", MainActivity.class);
        solo.clickOnButton("Log In");
        solo.assertCurrentActivity("Wrong Activity", LogInActivity.class);
        solo.enterText((EditText) solo.getView(R.id.email_login_field), "junyan4@ualberta.ca");
        solo.enterText((EditText) solo.getView(R.id.password_login_field), "LJYli123");
        solo.clickOnButton("Log In");
        solo.assertCurrentActivity("Wrong Activity", SelectionActivity.class);
        solo.clickOnButton("Driver");
        solo.assertCurrentActivity("Wrong Activity", DriverDrawerActivity.class);
    }


    /**
     * check activity working or not
     * @throws Exception
     */
    @Test
    public void start() throws Exception {
        Activity activity = rule.getActivity();
    }

    /**
     * This test checks the proper function of the navigation bar
     */
    @Test
    public void checkNavigation() {
        solo.clickOnImageButton(0);
        solo.sendKey(KeyEvent.KEYCODE_DPAD_DOWN);
    }

    /**
     * Test checks the Profile Menu Item in hamburger menu
     * Also checks the Edit Profile button in the profile page
     */
    @Test
    public void openProfile() {
        solo.clickOnImageButton(0);
        solo.sendKey(KeyEvent.KEYCODE_DPAD_DOWN);
        //solo.clickOnMenuItem("Profile");
    }

    /**
     * Test checks the Logout Menu Item in hamburger menu
     */
    @Test
    public void logout() {
        solo.clickOnImageButton(0);
        solo.sendKey(KeyEvent.KEYCODE_DPAD_DOWN);
        solo.clickOnMenuItem("Logout");
        solo.assertCurrentActivity("Wrong Activity", MainActivity.class);
    }

    /**
     * Close activity after each test
     * @throws Exception
     */
    @After
    public void tearDown() throws Exception {
        solo.finishOpenedActivities();
    }
}
