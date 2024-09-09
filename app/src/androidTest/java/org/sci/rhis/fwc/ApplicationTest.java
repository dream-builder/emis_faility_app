package org.sci.rhis.fwc;

import android.app.Application;
import android.test.ApplicationTestCase;
import android.app.Application;
import android.support.test.espresso.UiController;
import android.support.test.espresso.ViewAction;
import android.support.test.espresso.ViewInteraction;
import android.test.ApplicationTestCase;
import android.view.View;

import org.hamcrest.Matcher;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.closeSoftKeyboard;
import static android.support.test.espresso.action.ViewActions.replaceText;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.isRoot;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withParent;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;

/**
 * <a href="http://d.android.com/tools/testing/testing_android.html">Testing Fundamentals</a>
 */
public class ApplicationTest extends ApplicationTestCase<Application> {

    public static String strUserId = "424199", strPassword = "123",strHID = "123456789", strNID = "1234567891234", strBirthReg = "12345678912345", strNRCID = "23943065008506";
    public static String strDistrict = "হবিগঞ্জ", strUpazilla = "আজমিরিগঞ্জ", strUnion = "কাকাইলছেও", strVillage = "কামালপুর";
    public static String strMobile = "01811111111", strClientName="name", strFather = "Father Name", strMother = "mother Name";
    public static String strStartDate = "১৭/০১/২০১৮", strEndDate = "১৭/০৭/২০১৮";
    public static String marriageDate = "১৭/০৭/২০১0", lastDeliveryDate = "১৭/০৬/২০১৭", lmpDate = "১৭/০৬/২০১৮";

    public ApplicationTest() {super(Application.class);}

    public static void login(){
        ViewInteraction appCompatEditText = onView(
                allOf(withId(R.id.providerId),
                        withParent(withId(R.id.providerIdLayout)),
                        isDisplayed()));
        appCompatEditText.perform(replaceText(strUserId), closeSoftKeyboard());

        ViewInteraction appCompatEditText2 = onView(
                allOf(withId(R.id.providerPassword),
                        withParent(withId(R.id.passwordLayout)),
                        isDisplayed()));
        appCompatEditText2.perform(replaceText(strPassword), closeSoftKeyboard());

        ViewInteraction appCompatButton = onView(
                allOf(withId(R.id.loginbtn1), withText("লগ ইন"), isDisplayed()));
        appCompatButton.perform(click());

        ViewInteraction appCompatButton2 = onView(
                allOf(withId(R.id.buttonPlacePopUpOK), withText("Select"), isDisplayed()));
        appCompatButton2.perform(click());
    }

    /**
     * Perform action of waiting for a specific time.
     */
    public static ViewAction waitFor(final long millis) {
        return new ViewAction() {
            @Override
            public Matcher<View> getConstraints() {
                return isRoot();
            }

            @Override
            public String getDescription() {
                return "Wait for " + millis + " milliseconds.";
            }

            @Override
            public void perform(UiController uiController, final View view) {
                uiController.loopMainThreadForAtLeast(millis);
            }
        };
    }
}