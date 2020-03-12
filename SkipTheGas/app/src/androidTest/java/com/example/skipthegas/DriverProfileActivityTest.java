package com.example.skipthegas;

import android.app.Activity;
import android.widget.EditText;

import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.rule.ActivityTestRule;

import com.robotium.solo.Solo;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import java.sql.Driver;

public class DriverProfileActivityTest {
    private Solo solo;

    @Rule
    public ActivityTestRule<DriverProfileActivity> rule =
            new ActivityTestRule<>(DriverProfileActivity.class, true, true);

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
     * This test checks to see that clicking on the Edit Profile
     * sends the user to the EditProfileActivity class
     */

    @Test
    public void EditProfileButtonTest(){
        solo.assertCurrentActivity("Wrong Activity", DriverProfileActivity.class);
        solo.clickOnButton("Edit Profile");
        //solo.assertCurrentActivity("Wrong Activity", );
    }


    /**
     * This test checks to see if clicking on the Request Ride button successfully take the
     * user to the request ride activity
     */
    @Test
    public void BrowseRequestsButton(){
        solo.assertCurrentActivity("Wrong Activity", DriverProfileActivity.class);
        solo.clickOnButton("Browse Active Requests");
        solo.assertCurrentActivity("Wrong activity", ActiveRequestsActivity.class);
    }

    @Test
    public void logOutButton(){
        solo.assertCurrentActivity("Wrong Activity", DriverProfileActivity.class);
        solo.clickOnButton("Log Out");
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
