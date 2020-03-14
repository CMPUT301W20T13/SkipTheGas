package com.example.skipthegas;

import android.app.Activity;
import android.view.View;
import android.widget.EditText;

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
public class RiderActivityTest {
    private Solo solo;

    @Rule
    public ActivityTestRule<RiderActivity> rule =
            new ActivityTestRule<>(RiderActivity.class, true, true);

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
     * This test checks to see if the user sign up was successful
     * Once user hits the "Register" button in this activity,
     * the program should redirect them to the VerifyActivity
     */
    @Test
    public void requestRide(){
        solo.assertCurrentActivity("Wrong Activity", RiderActivity.class);
        solo.clickOnScreen(387, 380);
        View fab = solo.getCurrentActivity().findViewById(R.id.rider_float_action_menu);
        solo.clickOnView(fab);
//        solo.clickOnScreen(395, 391);
//        View fab = solo.getCurrentActivity().findViewById(R.id.rider_float_action_menu);
//        solo.clickOnView(fab);
        //solo.clickOnImageButton(100);
        //solo.clickOnButton("Post Ride");
//        solo.enterText((EditText)solo.getView(R.id.username_register_field), "Julie");
//        solo.enterText((EditText)solo.getView(R.id.email_register_field), "nandu201098@gmail.com");
//        solo.enterText((EditText)solo.getView(R.id.password_register_field), "helloworld");
//        solo.enterText((EditText)solo.getView(R.id.phone_register_field), "5875850075");
//        solo.clickOnButton("Register");
//        solo.assertCurrentActivity("Wrong Activity", VerifyActivity.class);
    }

    /**
     * This test checks the password field during Sign up
     * If the value entered is too short, a toast should be posted
     */
//    @Test
//    public void pwTooShort(){
//        solo.assertCurrentActivity("Wrong Activity", SignUpActivity.class);
//        solo.enterText((EditText)solo.getView(R.id.username_register_field), "Julie");
//        solo.enterText((EditText)solo.getView(R.id.email_register_field), "nandu201098@gmail.com");
//        solo.enterText((EditText)solo.getView(R.id.password_register_field), "123");
//        solo.enterText((EditText)solo.getView(R.id.phone_register_field), "5875850075");
//        solo.clickOnButton("Register");
//        solo.waitForText("Password is too short");
//        solo.assertCurrentActivity("Wrong Activity", SignUpActivity.class);
//    }

    /**
     * This test checks the username field during Sign up
     * If the value entered is non-existent, a toast should be posted
     */
//    @Test
//    public void signUpFail1(){
//        solo.assertCurrentActivity("Wrong Activity", SignUpActivity.class);
//        solo.enterText((EditText)solo.getView(R.id.username_register_field), "");
//        solo.enterText((EditText)solo.getView(R.id.email_register_field), "nandu201098@gmail.com");
//        solo.enterText((EditText)solo.getView(R.id.password_register_field), "helloworld");
//        solo.enterText((EditText)solo.getView(R.id.phone_register_field), "5875850075");
//        solo.clickOnButton("Register");
//        solo.waitForText("Username is required");
//        solo.assertCurrentActivity("Wrong Activity", SignUpActivity.class);
//    }

    /**
     * This test checks the email field during Sign up
     * If the value entered is non-existent, a toast should be posted
     */
//    @Test
//    public void signUpFail2(){
//        solo.assertCurrentActivity("Wrong Activity", SignUpActivity.class);
//        solo.enterText((EditText)solo.getView(R.id.username_register_field), "Julie");
//        solo.enterText((EditText)solo.getView(R.id.email_register_field), "");
//        solo.enterText((EditText)solo.getView(R.id.password_register_field), "helloworld");
//        solo.enterText((EditText)solo.getView(R.id.phone_register_field), "5875850075");
//        solo.clickOnButton("Register");
//        solo.waitForText("Email is required");
//        solo.assertCurrentActivity("Wrong Activity", SignUpActivity.class);
//    }

    /**
     * This test checks the phone number field during Sign up
     * If the value entered is non-existent, a toast should be posted
     */
//    @Test
//    public void signUpFail3(){
//        solo.assertCurrentActivity("Wrong Activity", SignUpActivity.class);
//        solo.enterText((EditText)solo.getView(R.id.username_register_field), "Julie");
//        solo.enterText((EditText)solo.getView(R.id.email_register_field), "nandu201098@gmail.com");
//        solo.enterText((EditText)solo.getView(R.id.password_register_field), "helloworld");
//        solo.enterText((EditText)solo.getView(R.id.phone_register_field), "");
//        solo.clickOnButton("Register");
//        solo.waitForText("Phone number is required");
//        solo.assertCurrentActivity("Wrong Activity", SignUpActivity.class);
//    }
//
//    /**
//     * Close activity after each test
//     * @throws Exception
//     */
//    @After
//    public void tearDown() throws Exception{
//        solo.finishOpenedActivities();
//    }

}