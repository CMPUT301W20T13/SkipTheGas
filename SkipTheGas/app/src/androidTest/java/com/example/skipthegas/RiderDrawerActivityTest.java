package com.example.skipthegas;

import android.app.Activity;
import android.view.KeyEvent;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.rule.ActivityTestRule;

import com.robotium.solo.Solo;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class RiderDrawerActivityTest {
    private Solo solo;

    @Rule
    public ActivityTestRule<RiderDrawerActivity> rule =
            new ActivityTestRule<>(RiderDrawerActivity.class, true, true);

    /**
     * Test checks the setUp of the activity
     * Creates solo instance and runs before all other tests
     * @throws Exception
     */
    @Before
    public void setUp() throws Exception {
        solo = new Solo(InstrumentationRegistry.getInstrumentation(), rule.getActivity());
    }

    /**
     * This test gets and starts the activity
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
    public void openEditProfile() {
        solo.clickOnImageButton(0);
        solo.sendKey(KeyEvent.KEYCODE_DPAD_DOWN);
        solo.clickOnMenuItem("Profile");
        solo.clickOnButton("Edit Profile");
        solo.assertCurrentActivity("Wrong Activity", RiderProfileEditable.class);
    }

    /**
     * Test checks the Ride Requests Menu Item in hamburger menu
     */
    @Test
    public void openRequests() {
        solo.clickOnImageButton(0);
        solo.sendKey(KeyEvent.KEYCODE_DPAD_DOWN);
        solo.clickOnMenuItem("Ride Requests");
        solo.waitForText("View Ride Requests");
    }

    /**
     * Test checks the navigation to the Completed Requests page
     */
    @Test
    public void openCompletedRequests() {
        solo.clickOnImageButton(0);
        solo.sendKey(KeyEvent.KEYCODE_DPAD_DOWN);
        solo.clickOnMenuItem("Ride Requests");
        solo.clickOnButton("Completed Requests");
        solo.waitForText("Completed Requests");
    }

    /**
     * Test checks the navigation to the Cancelled Requests page
     */
    @Test
    public void openCancelledRequests() {
        solo.clickOnImageButton(0);
        solo.sendKey(KeyEvent.KEYCODE_DPAD_DOWN);
        solo.clickOnMenuItem("Ride Requests");
        solo.clickOnButton("Cancelled Requests");
        solo.waitForText("Cancelled Requests");
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
     * This test checks the click on Map twice
     * Should create two markers and draw polyline between them
     */
    @Test
    public void clickOnScreen() {
        solo.assertCurrentActivity("Wrong Activity", RiderDrawerActivity.class);
        solo.clickOnScreen(400, 405);
        solo.clickOnScreen(402, 403);
        solo.drag(400, 410, 405, 415, 12);
        solo.sleep(20);
        solo.clickOnScreen(401, 406);
        solo.clickOnScreen(402, 403);
        solo.assertCurrentActivity("Wrong Activity", RiderDrawerActivity.class);
    }

    /**
     * This test checks the functionality behind the post request button
     * Post a request after selecting start & end locations on the map
     */
    @Test
    public void postRequestSuccess() {
        solo.assertCurrentActivity("Wrong Activity", RiderDrawerActivity.class);
        solo.clickOnScreen(400, 405);
        solo.clickOnScreen(402, 403);
        solo.drag(400, 410, 405, 415, 12);
        solo.sleep(20);
        solo.clickOnScreen(401, 406);
        //solo.clickOnScreen(402, 403);
        solo.sleep(500);
        solo.clickOnButton("POST Request");
        solo.clickOnButton("Confirm");
        solo.waitForText("Can post request");
        solo.assertCurrentActivity("Wrong Activity", RiderTripProcessActivity.class);
    }

    /**
     * This test checks the functionality behind the post request button
     * Post a request after selecting start & end locations on the map
     */
    @Test
    public void postRequestFail() {
        solo.assertCurrentActivity("Wrong Activity", RiderDrawerActivity.class);
        solo.clickOnScreen(400, 405);
        solo.clickOnScreen(402, 403);
        solo.sleep(500);
        solo.clickOnButton("POST Request");
        solo.waitForText("At least 2 points needed.");
        solo.assertCurrentActivity("Wrong Activity", RiderDrawerActivity.class);
    }

    /**
     * This test checks the clear map button
     */
    @Test
    public void clearMap() {
        solo.assertCurrentActivity("Wrong Activity", RiderDrawerActivity.class);
        solo.clickOnScreen(400, 405);
        solo.clickOnScreen(402, 403);
        solo.drag(400, 410, 405, 415, 12);
        solo.sleep(20);
        solo.clickOnScreen(401, 406);
        solo.clickOnScreen(402, 403);
        solo.clickOnButton("Clear Map");
        solo.assertCurrentActivity("Wrong Activity", RiderDrawerActivity.class);
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
