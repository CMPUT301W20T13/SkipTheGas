package com.example.skipthegas;

import android.app.Activity;
import android.widget.EditText;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.rule.ActivityTestRule;

import com.robotium.solo.Solo;

/*import androidx.test.rule.ActivityTestRule;
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
        import static org.junit.Assert.assertFalse; */

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class SignUpActivityTest {
    private Solo solo;

    @Rule
    public ActivityTestRule<SignUpActivity> rule =
            new ActivityTestRule<>(SignUpActivity.class, true, true);

    /**
     * setUp test is one of the first tests that are run
     * Creates q solo instance
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
     */
    @Test
    public void signUpSuccess(){
        solo.assertCurrentActivity("Wrong Activity", SignUpActivity.class);
        solo.enterText((EditText)solo.getView(R.id.email_login_field), "brightonius@gmail.com");
        solo.enterText((EditText)solo.getView(R.id.password_login_field), "testpass");
        solo.clickOnButton("Log In");
        solo.assertCurrentActivity("Wrong Activity", DriverProfileActivity.class);
    }

    /**
     * Attempt to sign in to the application with incorrect information
     */
    @Test
    public void signUpFail(){
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