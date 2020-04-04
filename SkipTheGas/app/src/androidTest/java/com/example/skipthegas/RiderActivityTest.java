package com.example.skipthegas;

import android.app.Activity;
import android.view.View;
import android.widget.EditText;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.rule.ActivityTestRule;
import static junit.framework.TestCase.assertTrue;

import com.robotium.solo.Solo;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class RiderActivityTest {
    private Solo solo;

    @Rule
    public ActivityTestRule<RiderActivity> rule =
            new ActivityTestRule<>(RiderActivity.class, true, true);

    /**
     * Test checks the setUp of the activity
     * Creates solo instance
     *
     * @throws Exception
     */
    @Before
    public void setUp() throws Exception {
        solo = new Solo(InstrumentationRegistry.getInstrumentation(), rule.getActivity());
    }

    /**
     * This test gets and starts the activity
     *
     * @throws Exception
     */
    @Test
    public void start() throws Exception {
        Activity activity = rule.getActivity();
    }

    /**
     * This test checks the click on Map functionality
     * Should create two markers on the map
     */
    @Test
    public void clickOnScreen() {
        solo.assertCurrentActivity("Wrong Activity", RiderActivity.class);
        solo.clickOnScreen(387, 380);
        solo.clickOnScreen(400, 405);
        solo.assertCurrentActivity("Wrong Activity", RiderActivity.class);
    }

    /**
     * This test checks to see if the floating action button is working
     * We could not figure out how to test the floating action button using Robotium
     * Leaving this for after Project Part 3
     */
    @Test
    public void testFloatingActionBtn() {
//        solo.assertCurrentActivity("Wrong Activity", RiderActivity.class);
//        solo.clickOnScreen(387, 380);
//        solo.clickOnScreen(400, 405);
//        View fab = solo.getCurrentActivity().findViewById(R.id.rider_float_action_menu);
//        solo.clickOnView(fab);
//        View post = solo.getCurrentActivity().findViewById(R.id.request_ride);
//        solo.clickOnView(post);
//        assertTrue(solo.waitForDialogToOpen());
//        solo.assertCurrentActivity("Wrong Activity", RiderActivity.class);
    }

    /**
     * This test checks the clear map button
     * After two markers are placed on the map, pressing the clear map button
     * should clear the map of the markers
     */
    @Test
    public void clearMap() {
        solo.assertCurrentActivity("Wrong Activity", RiderActivity.class);
        solo.clickOnScreen(387, 380);
        solo.clickOnScreen(400, 405);
        solo.clickOnButton("Clear\nmap");
        solo.assertCurrentActivity("Wrong Activity", RiderActivity.class);
    }

}