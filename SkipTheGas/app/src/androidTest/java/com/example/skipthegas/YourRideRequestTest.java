package com.example.skipthegas;

import android.app.Activity;

import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.rule.ActivityTestRule;

import com.robotium.solo.Solo;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

public class YourRideRequestTest {
    private Solo solo;

    @Rule
    public ActivityTestRule<YourRideRequestActivity> rule =
            new ActivityTestRule<>(YourRideRequestActivity.class, true, true);

    /**
     * Test checks the setUp of the activity
     * Creates solo instance
     * @throws Exception
     */
    @Before
    public void setUp() throws Exception{
        solo = new Solo(InstrumentationRegistry.getInstrumentation(), rule.getActivity());
    }

    /**
     * This test gets and starts the activity
     * @throws Exception
     */
    @Test
    public void start()throws Exception{
        Activity activity = rule.getActivity();
    }


    /**
     * This test confirms that when you click the cancel button, a fragment pops up asking the
     * user to confirm the cancellation.
     */
    @Test
    public void cancelButton(){
        solo.assertCurrentActivity("Wrong Activity", YourRideRequestActivity.class);
        solo.clickOnButton("Cancel Ride");
        solo.assertCurrentActivity("Wrong Activity", CancelFragment.class);
    }

    @Test
    public void viewDriverProfile(){
        solo.assertCurrentActivity("Wrong Activity", YourRideRequestActivity.class);
        solo.clickOnText(findViewById(R.id.Driver));
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
