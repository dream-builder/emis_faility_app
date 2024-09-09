package org.sci.rhis.fwc;

import android.support.test.espresso.UiController;
import android.support.test.espresso.ViewAction;
import android.support.test.espresso.ViewInteraction;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.test.suitebuilder.annotation.LargeTest;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.Espresso.pressBack;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.closeSoftKeyboard;
import static android.support.test.espresso.action.ViewActions.replaceText;
import static android.support.test.espresso.action.ViewActions.scrollTo;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.isRoot;
import static android.support.test.espresso.matcher.ViewMatchers.withClassName;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withParent;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.is;

/**
 * Created by hajjaz.ibrahim on 9/12/2018.
 */

@LargeTest
@RunWith(AndroidJUnit4.class)
public class SearchTest {

    @Rule
    public ActivityTestRule<LoginActivity> mActivityTestRule = new ActivityTestRule<>(LoginActivity.class);

    @Test
    public void HIDSearchTest() {

        Log.e("@Test","Performing Health ID Search Test");
        ApplicationTest.login();

        ViewInteraction appCompatEditText3 = onView(
                withId(R.id.searchableTextId));
        appCompatEditText3.perform(scrollTo(), replaceText(ApplicationTest.strHID), closeSoftKeyboard());

        ViewInteraction imageButton = onView(
                withId(R.id.searchButton));
        imageButton.perform(scrollTo(), click());

        onView(isRoot()).perform(waitFor(3000));
        //SearchTest();
    }

    @Test
    public void mobileSearchTest() {

        ApplicationTest.login();

        ViewInteraction appCompatSpinner = onView(
                withId(R.id.ClientsIdentityDropdown));
        appCompatSpinner.perform(scrollTo(), click());

        ViewInteraction appCompatCheckedTextView = onView(
                allOf(withId(android.R.id.text1), withText("Mobile No"), isDisplayed()));
        appCompatCheckedTextView.perform(click());

        ViewInteraction appCompatEditText4 = onView(
                allOf(withId(R.id.search_et_mobile_no),
                        withParent(allOf(withId(R.id.mobile_layout),
                                withParent(withId(R.id.mobileSearchMainLayout)))),
                        isDisplayed()));
        appCompatEditText4.perform(replaceText(ApplicationTest.strMobile), closeSoftKeyboard());

        ViewInteraction appCompatButton3 = onView(
                allOf(withId(R.id.advSearchBtn), withText("SEARCH"),
                        withParent(allOf(withId(R.id.search_button_layout),
                                withParent(withId(R.id.adv_search_layout)))),
                        isDisplayed()));
        appCompatButton3.perform(click());

        ViewInteraction appCompatButton4 = onView(
                allOf(withId(R.id.cancelBtn), withText("CANCEL"),
                        withParent(allOf(withId(R.id.search_button_layout),
                                withParent(withId(R.id.adv_search_layout)))),
                        isDisplayed()));
        appCompatButton4.perform(click());

        ViewInteraction appCompatButton5 = onView(
                allOf(withId(android.R.id.button3), withText("OK")));
        appCompatButton5.perform(scrollTo(), click());

        onView(isRoot()).perform(waitFor(3000));

    }

    @Test
    public void nidSearchTest(){
        ApplicationTest.login();

        ViewInteraction appCompatSpinner2 = onView(
                withId(R.id.ClientsIdentityDropdown));
        appCompatSpinner2.perform(scrollTo(), click());

        ViewInteraction appCompatCheckedTextView2 = onView(
                allOf(withId(android.R.id.text1), withText("National ID"), isDisplayed()));
        appCompatCheckedTextView2.perform(click());

        ViewInteraction appCompatEditText3 = onView(
                withId(R.id.searchableTextId));
        appCompatEditText3.perform(scrollTo(), replaceText(ApplicationTest.strNID), closeSoftKeyboard());

        ViewInteraction imageButton2 = onView(
                withId(R.id.searchButton));
        imageButton2.perform(scrollTo(), click());

        onView(isRoot()).perform(waitFor(3000));
    }

    @Test
    public void birthRegTest(){
        ApplicationTest.login();

        ViewInteraction appCompatSpinner3 = onView(
                withId(R.id.ClientsIdentityDropdown));
        appCompatSpinner3.perform(scrollTo(), click());

        ViewInteraction appCompatCheckedTextView3 = onView(
                allOf(withId(android.R.id.text1), withText("Birth Reg. No"), isDisplayed()));
        appCompatCheckedTextView3.perform(click());

        ViewInteraction appCompatEditText3 = onView(
                withId(R.id.searchableTextId));
        appCompatEditText3.perform(scrollTo(), replaceText(ApplicationTest.strBirthReg), closeSoftKeyboard());

        ViewInteraction imageButton3 = onView(
                withId(R.id.searchButton));
        imageButton3.perform(scrollTo(), click());
    }

    @Test
    public void NRCIdTest(){
        ApplicationTest.login();

        ViewInteraction appCompatSpinner4 = onView(
                withId(R.id.ClientsIdentityDropdown));
        appCompatSpinner4.perform(scrollTo(), click());

        ViewInteraction appCompatCheckedTextView4 = onView(
                allOf(withId(android.R.id.text1), withText("NRC ID"), isDisplayed()));
        appCompatCheckedTextView4.perform(click());

        ViewInteraction appCompatEditText3 = onView(
                withId(R.id.searchableTextId));
        appCompatEditText3.perform(scrollTo(), replaceText(ApplicationTest.strNRCID), closeSoftKeyboard());

        ViewInteraction imageButton4 = onView(
                withId(R.id.searchButton));
        imageButton4.perform(scrollTo(), click());
    }

    @Test
    public void serviceSearchTest() {
        ApplicationTest.login();

        ViewInteraction appCompatSpinner = onView(
                withId(R.id.ClientsIdentityDropdown));
        appCompatSpinner.perform(scrollTo(), click());

        ViewInteraction appCompatCheckedTextView = onView(
                allOf(withId(android.R.id.text1), withText("Service Search"), isDisplayed()));
        appCompatCheckedTextView.perform(click());

        ViewInteraction appCompatSpinner2 = onView(
                allOf(withId(R.id.advSearchServiceTypeSpinner),
                        withParent(allOf(withId(R.id.serviceTypeLayout),
                                withParent(withId(R.id.advSearchMainLayout)))),
                        isDisplayed()));
        appCompatSpinner2.perform(click());

        ViewInteraction appCompatCheckedTextView2 = onView(
                allOf(withId(android.R.id.text1), withText("ANC"), isDisplayed()));
        appCompatCheckedTextView2.perform(click());

        ViewInteraction appCompatEditText3 = onView(
                allOf(withId(R.id.searchStartDateEditText),
                        withParent(allOf(withId(R.id.dateRangeSearchLayout),
                                withParent(withId(R.id.advSearchMainLayout)))),
                        isDisplayed()));
        appCompatEditText3.perform(replaceText(ApplicationTest.strStartDate), closeSoftKeyboard());

        ViewInteraction appCompatEditText4 = onView(
                allOf(withId(R.id.searchEndDateEditText),
                        withParent(allOf(withId(R.id.dateRangeSearchLayout),
                                withParent(withId(R.id.advSearchMainLayout)))),
                        isDisplayed()));
        appCompatEditText4.perform(replaceText(ApplicationTest.strEndDate), closeSoftKeyboard());

        selectLocation();

        ViewInteraction appCompatButton3 = onView(
                allOf(withId(R.id.advSearchBtn), withText("SEARCH"),
                        withParent(allOf(withId(R.id.search_button_layout),
                                withParent(withId(R.id.adv_search_layout)))),
                        isDisplayed()));
        appCompatButton3.perform(click());

        ViewInteraction appCompatSpinner7 = onView(
                allOf(withId(R.id.advSearchServiceTypeSpinner),
                        withParent(allOf(withId(R.id.serviceTypeLayout),
                                withParent(withId(R.id.advSearchMainLayout)))),
                        isDisplayed()));
        appCompatSpinner7.perform(click());

        ViewInteraction appCompatCheckedTextView7 = onView(
                allOf(withId(android.R.id.text1), withText("DELIVERY"), isDisplayed()));
        appCompatCheckedTextView7.perform(click());

        ViewInteraction appCompatButton4 = onView(
                allOf(withId(R.id.advSearchBtn), withText("SEARCH"),
                        withParent(allOf(withId(R.id.search_button_layout),
                                withParent(withId(R.id.adv_search_layout)))),
                        isDisplayed()));
        appCompatButton4.perform(click());

        ViewInteraction appCompatSpinner8 = onView(
                allOf(withId(R.id.advSearchServiceTypeSpinner),
                        withParent(allOf(withId(R.id.serviceTypeLayout),
                                withParent(withId(R.id.advSearchMainLayout)))),
                        isDisplayed()));
        appCompatSpinner8.perform(click());

        ViewInteraction appCompatCheckedTextView8 = onView(
                allOf(withId(android.R.id.text1), withText("PNC"), isDisplayed()));
        appCompatCheckedTextView8.perform(click());

        ViewInteraction appCompatButton5 = onView(
                allOf(withId(R.id.advSearchBtn), withText("SEARCH"),
                        withParent(allOf(withId(R.id.search_button_layout),
                                withParent(withId(R.id.adv_search_layout)))),
                        isDisplayed()));
        appCompatButton5.perform(click());

        ViewInteraction appCompatSpinner9 = onView(
                allOf(withId(R.id.advSearchServiceTypeSpinner),
                        withParent(allOf(withId(R.id.serviceTypeLayout),
                                withParent(withId(R.id.advSearchMainLayout)))),
                        isDisplayed()));
        appCompatSpinner9.perform(click());

        ViewInteraction appCompatCheckedTextView9 = onView(
                allOf(withId(android.R.id.text1), withText("PILL_CONDOM"), isDisplayed()));
        appCompatCheckedTextView9.perform(click());

        ViewInteraction appCompatButton6 = onView(
                allOf(withId(R.id.advSearchBtn), withText("SEARCH"),
                        withParent(allOf(withId(R.id.search_button_layout),
                                withParent(withId(R.id.adv_search_layout)))),
                        isDisplayed()));
        appCompatButton6.perform(click());

        ViewInteraction appCompatSpinner10 = onView(
                allOf(withId(R.id.advSearchServiceTypeSpinner),
                        withParent(allOf(withId(R.id.serviceTypeLayout),
                                withParent(withId(R.id.advSearchMainLayout)))),
                        isDisplayed()));
        appCompatSpinner10.perform(click());

        ViewInteraction appCompatCheckedTextView10 = onView(
                allOf(withId(android.R.id.text1), withText("INJECTABLE"), isDisplayed()));
        appCompatCheckedTextView10.perform(click());

        ViewInteraction appCompatButton7 = onView(
                allOf(withId(R.id.advSearchBtn), withText("SEARCH"),
                        withParent(allOf(withId(R.id.search_button_layout),
                                withParent(withId(R.id.adv_search_layout)))),
                        isDisplayed()));
        appCompatButton7.perform(click());

        ViewInteraction appCompatSpinner11 = onView(
                allOf(withId(R.id.advSearchServiceTypeSpinner),
                        withParent(allOf(withId(R.id.serviceTypeLayout),
                                withParent(withId(R.id.advSearchMainLayout)))),
                        isDisplayed()));
        appCompatSpinner11.perform(click());

        ViewInteraction appCompatCheckedTextView11 = onView(
                allOf(withId(android.R.id.text1), withText("IUD"), isDisplayed()));
        appCompatCheckedTextView11.perform(click());

        ViewInteraction appCompatButton8 = onView(
                allOf(withId(R.id.advSearchBtn), withText("SEARCH"),
                        withParent(allOf(withId(R.id.search_button_layout),
                                withParent(withId(R.id.adv_search_layout)))),
                        isDisplayed()));
        appCompatButton8.perform(click());

        ViewInteraction appCompatSpinner12 = onView(
                allOf(withId(R.id.advSearchServiceTypeSpinner),
                        withParent(allOf(withId(R.id.serviceTypeLayout),
                                withParent(withId(R.id.advSearchMainLayout)))),
                        isDisplayed()));
        appCompatSpinner12.perform(click());

        ViewInteraction appCompatCheckedTextView12 = onView(
                allOf(withId(android.R.id.text1), withText("IUD_FOLLOWUP"), isDisplayed()));
        appCompatCheckedTextView12.perform(click());

        ViewInteraction appCompatButton9 = onView(
                allOf(withId(R.id.advSearchBtn), withText("SEARCH"),
                        withParent(allOf(withId(R.id.search_button_layout),
                                withParent(withId(R.id.adv_search_layout)))),
                        isDisplayed()));
        appCompatButton9.perform(click());

        ViewInteraction appCompatSpinner13 = onView(
                allOf(withId(R.id.advSearchServiceTypeSpinner),
                        withParent(allOf(withId(R.id.serviceTypeLayout),
                                withParent(withId(R.id.advSearchMainLayout)))),
                        isDisplayed()));
        appCompatSpinner13.perform(click());

        ViewInteraction appCompatCheckedTextView13 = onView(
                allOf(withId(android.R.id.text1), withText("GENERAL PATIENT"), isDisplayed()));
        appCompatCheckedTextView13.perform(click());

        ViewInteraction appCompatButton10 = onView(
                allOf(withId(R.id.advSearchBtn), withText("SEARCH"),
                        withParent(allOf(withId(R.id.search_button_layout),
                                withParent(withId(R.id.adv_search_layout)))),
                        isDisplayed()));
        appCompatButton10.perform(click());

        ViewInteraction appCompatSpinner14 = onView(
                allOf(withId(R.id.advSearchServiceTypeSpinner),
                        withParent(allOf(withId(R.id.serviceTypeLayout),
                                withParent(withId(R.id.advSearchMainLayout)))),
                        isDisplayed()));
        appCompatSpinner14.perform(click());

        ViewInteraction appCompatCheckedTextView14 = onView(
                allOf(withId(android.R.id.text1), withText("PAC"), isDisplayed()));
        appCompatCheckedTextView14.perform(click());

        ViewInteraction appCompatButton11 = onView(
                allOf(withId(R.id.advSearchBtn), withText("SEARCH"),
                        withParent(allOf(withId(R.id.search_button_layout),
                                withParent(withId(R.id.adv_search_layout)))),
                        isDisplayed()));
        appCompatButton11.perform(click());

        ViewInteraction appCompatButton12 = onView(
                allOf(withId(R.id.cancelBtn), withText("CANCEL"),
                        withParent(allOf(withId(R.id.search_button_layout),
                                withParent(withId(R.id.adv_search_layout)))),
                        isDisplayed()));
        appCompatButton12.perform(click());

        ViewInteraction appCompatButton13 = onView(
                allOf(withId(android.R.id.button3), withText("OK")));
        appCompatButton13.perform(scrollTo(), click());

    }

    @Test
    public void pregWomenSearch(){
        ApplicationTest.login();

        ViewInteraction appCompatSpinner = onView(
                withId(R.id.ClientsIdentityDropdown));
        appCompatSpinner.perform(scrollTo(), click());

        ViewInteraction appCompatCheckedTextView = onView(
                allOf(withId(android.R.id.text1), withText("Pregnant Woman Search"), isDisplayed()));
        appCompatCheckedTextView.perform(click());

        selectLocation();

        ViewInteraction appCompatButton3 = onView(
                allOf(withId(R.id.advSearchBtn), withText("SEARCH"),
                        withParent(allOf(withId(R.id.search_button_layout),
                                withParent(withId(R.id.adv_search_layout)))),
                        isDisplayed()));
        appCompatButton3.perform(click());

        ViewInteraction appCompatButton4 = onView(
                allOf(withId(R.id.cancelBtn), withText("CANCEL"),
                        withParent(allOf(withId(R.id.search_button_layout),
                                withParent(withId(R.id.adv_search_layout)))),
                        isDisplayed()));
        appCompatButton4.perform(click());

        ViewInteraction appCompatButton5 = onView(
                allOf(withId(android.R.id.button3), withText("OK")));
        appCompatButton5.perform(scrollTo(), click());
    }

    @Test
    public void advSearch(){
        ApplicationTest.login();

        ViewInteraction appCompatSpinner6 = onView(
                withId(R.id.ClientsIdentityDropdown));
        appCompatSpinner6.perform(scrollTo(), click());

        ViewInteraction appCompatCheckedTextView6 = onView(
                allOf(withId(android.R.id.text1), withText("Advance Search"), isDisplayed()));
        appCompatCheckedTextView6.perform(click());

        ViewInteraction appCompatEditText3 = onView(
                allOf(withId(R.id.advClient_name),
                        withParent(allOf(withId(R.id.name_age),
                                withParent(withId(R.id.advSearchMainLayout)))),
                        isDisplayed()));
        appCompatEditText3.perform(replaceText(ApplicationTest.strClientName), closeSoftKeyboard());

        ViewInteraction appCompatSpinner7 = onView(
                allOf(withId(R.id.advClientsSexSpinner),
                        withParent(allOf(withId(R.id.name_age),
                                withParent(withId(R.id.advSearchMainLayout)))),
                        isDisplayed()));
        appCompatSpinner7.perform(click());

        ViewInteraction appCompatCheckedTextView7 = onView(
                allOf(withId(android.R.id.text1), withText("মহিলা"), isDisplayed()));
        appCompatCheckedTextView7.perform(click());

        selectLocation();

        ViewInteraction appCompatButton6 = onView(
                allOf(withId(R.id.advSearchBtn), withText("SEARCH"),
                        withParent(allOf(withId(R.id.search_button_layout),
                                withParent(withId(R.id.adv_search_layout)))),
                        isDisplayed()));
        appCompatButton6.perform(click());

        ViewInteraction appCompatButton7 = onView(
                allOf(withId(R.id.cancelBtn), withText("CANCEL"),
                        withParent(allOf(withId(R.id.search_button_layout),
                                withParent(withId(R.id.adv_search_layout)))),
                        isDisplayed()));
        appCompatButton7.perform(click());

        ViewInteraction appCompatButton8 = onView(
                allOf(withId(android.R.id.button3), withText("OK")));
        appCompatButton8.perform(scrollTo(), click());
    }

    public void selectLocation(){

        ViewInteraction appCompatSpinner2 = onView(
                allOf(withId(R.id.advSearchDistrict),
                        withParent(allOf(withId(R.id.father_district),
                                withParent(withId(R.id.advSearchMainLayout)))),
                        isDisplayed()));
        appCompatSpinner2.perform(click());

        ViewInteraction appCompatCheckedTextView2 = onView(
                allOf(withId(android.R.id.text1), withText(ApplicationTest.strDistrict), isDisplayed()));
        appCompatCheckedTextView2.perform(click());

        ViewInteraction appCompatSpinner3 = onView(
                allOf(withId(R.id.advSearchUpazila),
                        withParent(allOf(withId(R.id.father_district),
                                withParent(withId(R.id.advSearchMainLayout)))),
                        isDisplayed()));
        appCompatSpinner3.perform(click());

        ViewInteraction appCompatCheckedTextView3 = onView(
                allOf(withId(android.R.id.text1), withText(ApplicationTest.strUpazilla), isDisplayed()));
        appCompatCheckedTextView3.perform(click());

        ViewInteraction appCompatSpinner4 = onView(
                allOf(withId(R.id.advSearchUnion),
                        withParent(allOf(withId(R.id.union_village),
                                withParent(withId(R.id.advSearchMainLayout)))),
                        isDisplayed()));
        appCompatSpinner4.perform(click());

        ViewInteraction appCompatCheckedTextView4 = onView(
                allOf(withId(android.R.id.text1), withText(ApplicationTest.strUnion), isDisplayed()));
        appCompatCheckedTextView4.perform(click());

        ViewInteraction appCompatSpinner5 = onView(
                allOf(withId(R.id.advSearchVillage),
                        withParent(allOf(withId(R.id.union_village),
                                withParent(withId(R.id.advSearchMainLayout)))),
                        isDisplayed()));
        appCompatSpinner5.perform(click());

        ViewInteraction appCompatCheckedTextView5 = onView(
                allOf(withId(android.R.id.text1), withText(ApplicationTest.strVillage), isDisplayed()));
        appCompatCheckedTextView5.perform(click());
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

    private static Matcher<View> childAtPosition(
            final Matcher<View> parentMatcher, final int position) {

        return new TypeSafeMatcher<View>() {
            @Override
            public void describeTo(Description description) {
                description.appendText("Child at position " + position + " in parent ");
                parentMatcher.describeTo(description);
            }

            @Override
            public boolean matchesSafely(View view) {
                ViewParent parent = view.getParent();
                return parent instanceof ViewGroup && parentMatcher.matches(parent)
                        && view.equals(((ViewGroup) parent).getChildAt(position));
            }
        };
    }
}
