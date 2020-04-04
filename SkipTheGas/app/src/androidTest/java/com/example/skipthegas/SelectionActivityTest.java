package com.example.skipthegas;

import android.app.Activity;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.rule.ActivityTestRule;

import com.robotium.solo.Solo;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.security.PublicKey;

@RunWith(AndroidJUnit4.class)
public class SelectionActivityTest {
    private Solo solo;

    @Rule
    public ActivityTestRule<SelectionActivity> rule =
            new ActivityTestRule<>(SelectionActivity.class, true, true);

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
     * Should open rider account when Rider button is clicked
     */
    @Test
    public void selectRider() {
        solo.clickOnButton("Rider");
        solo.assertCurrentActivity("Wrong Activity", RiderDrawerActivity.class);
    }

    /**
     * Should open driver account when Driver button is clicked
     */
    @Test
    public void selectDriver() {
        solo.clickOnButton("Driver");
        solo.assertCurrentActivity("Wrong Activity", DriverDrawerActivity.class);
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
