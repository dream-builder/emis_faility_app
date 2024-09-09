package org.sci.rhis.fwc;

import android.support.test.espresso.ViewInteraction;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.test.suitebuilder.annotation.LargeTest;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.closeSoftKeyboard;
import static android.support.test.espresso.action.ViewActions.replaceText;
import static android.support.test.espresso.action.ViewActions.scrollTo;
import static android.support.test.espresso.assertion.ViewAssertions.doesNotExist;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withParent;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;

/**
 * Created by hajjaz.ibrahim on 9/12/2018.
 */

@LargeTest
@RunWith(AndroidJUnit4.class)
public class NRCTest {

    @Rule
    public ActivityTestRule<LoginActivity> mActivityTestRule = new ActivityTestRule<>(LoginActivity.class);

    @Test
    public void nRCTest() {
        
        ApplicationTest.login();

        ViewInteraction relativeLayout = onView(
                allOf(withId(R.id.layoutNRC),
                        withParent(allOf(withId(R.id.button_layout),
                                withParent(withId(R.id.fragment_dashboard)))),
                        isDisplayed()));
        relativeLayout.perform(click());

        ViewInteraction appCompatEditText3 = onView(
                allOf(withId(R.id.Client_name),
                        withParent(allOf(withId(R.id.layoutNRCName),
                                withParent(withId(R.id.nrc_layout))))));
        appCompatEditText3.perform(scrollTo(), replaceText("NAME TEST"), closeSoftKeyboard());

        ViewInteraction appCompatEditText4 = onView(
                allOf(withId(R.id.Clients_Father),
                        withParent(allOf(withId(R.id.layoutNRCFather),
                                withParent(withId(R.id.nrc_layout))))));
        appCompatEditText4.perform(scrollTo(), replaceText("FATHER TEST"), closeSoftKeyboard());

        ViewInteraction appCompatEditText5 = onView(
                allOf(withId(R.id.Clients_Mother),
                        withParent(allOf(withId(R.id.layoutNRCMother),
                                withParent(withId(R.id.nrc_layout))))));
        appCompatEditText5.perform(scrollTo(), replaceText("MOTHER TEST"), closeSoftKeyboard());

        ViewInteraction imageButton = onView(
                withId(R.id.DOBDatePicker));
        imageButton.perform(scrollTo(), click());

        ViewInteraction appCompatButton4 = onView(
                allOf(withId(android.R.id.button1), withText("OK"), isDisplayed()));
        appCompatButton4.perform(click());

        ViewInteraction appCompatSpinner = onView(
                allOf(withId(R.id.ClientsMaritalStatusSpinner),
                        withParent(allOf(withId(R.id.layoutNRCMaritalStatus),
                                withParent(withId(R.id.nrc_layout))))));
        appCompatSpinner.perform(scrollTo(), click());

        ViewInteraction appCompatCheckedTextView = onView(
                allOf(withId(android.R.id.text1), withText("অবিবাহিত"), isDisplayed()));
        appCompatCheckedTextView.perform(click());

        selectLocation();

        ViewInteraction appCompatEditText6 = onView(
                allOf(withId(R.id.NrcClients_Mobile_no),
                        withParent(allOf(withId(R.id.couple_mobile_no),
                                withParent(withId(R.id.nrc_layout))))));
        appCompatEditText6.perform(scrollTo(), replaceText("01811111111"), closeSoftKeyboard());

        ViewInteraction appCompatButton5 = onView(
                allOf(withId(R.id.nrcProceed), withText("SAVE"),
                        withParent(allOf(withId(R.id.layoutNationalityWithSaving),
                                withParent(withId(R.id.nrc_layout))))));
        appCompatButton5.perform(scrollTo(), click());

        ViewInteraction appCompatButton6 = onView(
                allOf(withId(R.id.nrcProceed), withText("Confirm"),
                        withParent(allOf(withId(R.id.layoutNationalityWithSaving),
                                withParent(withId(R.id.nrc_layout))))));
        appCompatButton6.perform(scrollTo(), click());

        ViewInteraction appCompatButton7 = onView(
                allOf(withId(R.id.buttonEditNRC), withText("EDIT"),
                        withParent(allOf(withId(R.id.client_intro_title),
                                withParent(withId(R.id.client_intro_layout))))));
        appCompatButton7.perform(scrollTo(), click());

        ViewInteraction editText = onView(
                allOf(withId(R.id.Client_name), withText("NAME"),
                        childAtPosition(
                                allOf(withId(R.id.layoutNRCName),
                                        childAtPosition(
                                                withId(R.id.nrc_layout),
                                                1)),
                                1),
                        isDisplayed()));
        editText.check(doesNotExist());

    }

    public void selectLocation(){
        ViewInteraction appCompatSpinner2 = onView(
                allOf(withId(R.id.Clients_District),
                        withParent(allOf(withId(R.id.layoutNRCDistUpz),
                                withParent(withId(R.id.nrc_layout))))));
        appCompatSpinner2.perform(scrollTo(), click());

        ViewInteraction appCompatCheckedTextView2 = onView(
                allOf(withId(android.R.id.text1), withText(ApplicationTest.strDistrict), isDisplayed()));
        appCompatCheckedTextView2.perform(click());

        ViewInteraction appCompatSpinner3 = onView(
                allOf(withId(R.id.Clients_Upazila),
                        withParent(allOf(withId(R.id.layoutNRCDistUpz),
                                withParent(withId(R.id.nrc_layout))))));
        appCompatSpinner3.perform(scrollTo(), click());

        ViewInteraction appCompatCheckedTextView3 = onView(
                allOf(withId(android.R.id.text1), withText(ApplicationTest.strUpazilla), isDisplayed()));
        appCompatCheckedTextView3.perform(click());

        ViewInteraction appCompatSpinner4 = onView(
                allOf(withId(R.id.Clients_Union),
                        withParent(allOf(withId(R.id.union_village),
                                withParent(withId(R.id.nrc_layout))))));
        appCompatSpinner4.perform(scrollTo(), click());

        ViewInteraction appCompatCheckedTextView4 = onView(
                allOf(withId(android.R.id.text1), withText(ApplicationTest.strUnion), isDisplayed()));
        appCompatCheckedTextView4.perform(click());

        ViewInteraction appCompatSpinner5 = onView(
                allOf(withId(R.id.Clients_Village),
                        withParent(allOf(withId(R.id.union_village),
                                withParent(withId(R.id.nrc_layout))))));
        appCompatSpinner5.perform(scrollTo(), click());

        ViewInteraction appCompatCheckedTextView5 = onView(
                allOf(withId(android.R.id.text1), withText(ApplicationTest.strVillage), isDisplayed()));
        appCompatCheckedTextView5.perform(click());
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
