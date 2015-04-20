package edu.mobile.ravelryknit;

import android.content.Intent;
import android.test.ActivityInstrumentationTestCase2;
import android.util.Log;

import com.robotium.solo.By;
import com.robotium.solo.Solo;



public class TestRavelry extends ActivityInstrumentationTestCase2<Login> {

    private Solo solo;

    public TestRavelry() {
        super(Login.class);
    }

    public void setUp() throws Exception {
        super.setUp();
        solo = new Solo(getInstrumentation(), getActivity());

    }


    public void testSubmit(){
        solo.assertCurrentActivity("Login", Login.class);
        solo.clickOnButton(0);
        solo.clickOnWebElement(By.tagName("button"));
        solo.assertCurrentActivity("Main", Main.class);
        solo.clickOnActionBarItem(R.menu.menu_main);
        solo.clickOnMenuItem("Submit");
        solo.assertCurrentActivity("Submit", Submit.class);
        //Clicking on submit before filling out project and pattern name should raise a Toast
        solo.clickOnButton("Submit");
        solo.waitForText("The fields 'Project Name' and 'Name of pattern' cannot be blank");
        //Fill in fields
        solo.enterText(0, "projectName");
        solo.enterText(1, "patternName");
        solo.enterText(2, "notes");
        solo.enterText(3, "yarnName");
        solo.pressSpinnerItem(0, 1);
        solo.pressSpinnerItem(1, 1);
        solo.pressSpinnerItem(2, 1);
        solo.clickOnButton("Submit");
        solo.waitForText("Success!");
        solo.assertCurrentActivity("Main", Main.class);
    }



    @Override
    public void tearDown() throws Exception {
        solo.finishOpenedActivities();
    }
}