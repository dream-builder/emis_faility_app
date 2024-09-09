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
import static android.support.test.espresso.Espresso.pressBack;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.closeSoftKeyboard;
import static android.support.test.espresso.action.ViewActions.replaceText;
import static android.support.test.espresso.action.ViewActions.scrollTo;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
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
public class GPTest {
    @Rule
    public ActivityTestRule<LoginActivity> mActivityTestRule = new ActivityTestRule<>(LoginActivity.class);

    @Test
    public void mobileSearchTest() {

        ApplicationTest.login();

        ViewInteraction appCompatSpinner = onView(
                withId(R.id.ClientsIdentityDropdown));
        appCompatSpinner.perform(scrollTo(), click());

        ViewInteraction appCompatCheckedTextView = onView(
                allOf(withId(android.R.id.text1), withText("Mobile No"), isDisplayed()));
        appCompatCheckedTextView.perform(click());

        ViewInteraction appCompatEditText3 = onView(
                allOf(withId(R.id.search_et_mobile_no),
                        withParent(allOf(withId(R.id.mobile_layout),
                                withParent(withId(R.id.mobileSearchMainLayout)))),
                        isDisplayed()));
        appCompatEditText3.perform(replaceText("01811111111"), closeSoftKeyboard());

        ViewInteraction appCompatButton3 = onView(
                allOf(withId(R.id.advSearchBtn), withText("SEARCH"),
                        withParent(allOf(withId(R.id.search_button_layout),
                                withParent(withId(R.id.adv_search_layout)))),
                        isDisplayed()));
        appCompatButton3.perform(click());

        ViewInteraction linearLayout = onView(
                allOf(childAtPosition(
                        allOf(withId(R.id.search_result),
                                withParent(withId(R.id.adv_search_layout))),
                        0),
                        isDisplayed()));
        linearLayout.perform(click());

        ViewInteraction appCompatButton4 = onView(
                allOf(withId(R.id.GPButton), withText("সাধারণ রোগী সেবা"),
                        withParent(allOf(withId(R.id.Type_tableRow1),
                                withParent(withId(R.id.serviceHeaderLayout))))));
        appCompatButton4.perform(scrollTo(), click());

        ViewInteraction imageButton = onView(
                allOf(withId(R.id.Date_Picker_Button),
                        withParent(allOf(withId(R.id.gpServiceDate),
                                withParent(withId(R.id.gpText))))));
        imageButton.perform(scrollTo(), click());

        ViewInteraction appCompatButton5 = onView(
                allOf(withId(android.R.id.button1), withText("OK"), isDisplayed()));
        appCompatButton5.perform(click());

        ViewInteraction multiSelectionSpinner = onView(
                allOf(withId(R.id.gpSymptomSpinner),
                        withParent(allOf(withId(R.id.gpDrawback),
                                withParent(withId(R.id.gpText))))));
        multiSelectionSpinner.perform(scrollTo(), click());

        ViewInteraction appCompatCheckedTextView2 = onView(
                allOf(withId(android.R.id.text1), withText("জ্বর"),
                        childAtPosition(
                                allOf(withClassName(is("com.android.internal.app.AlertController$RecycleListView")),
                                        withParent(withClassName(is("android.widget.FrameLayout")))),
                                1),
                        isDisplayed()));
        appCompatCheckedTextView2.perform(click());

        ViewInteraction appCompatButton6 = onView(
                allOf(withId(android.R.id.button1), withText("Submit")));
        appCompatButton6.perform(scrollTo(), click());

        ViewInteraction appCompatEditText4 = onView(
                allOf(withId(R.id.gpWeightValue),
                        withParent(allOf(withId(R.id.gpWeight),
                                withParent(withId(R.id.gpPhysicalExamination))))));
        appCompatEditText4.perform(scrollTo(), replaceText("60"), closeSoftKeyboard());

        ViewInteraction multiSelectionSpinner2 = onView(
                allOf(withId(R.id.gpDiseaseSpinner),
                        withParent(allOf(withId(R.id.gpDisease),
                                withParent(withId(R.id.gpText))))));
        multiSelectionSpinner2.perform(scrollTo(), click());

        ViewInteraction appCompatCheckedTextView3 = onView(
                allOf(withId(android.R.id.text1), withText("ভাইরাস জ্বর"),
                        childAtPosition(
                                allOf(withClassName(is("com.android.internal.app.AlertController$RecycleListView")),
                                        withParent(withClassName(is("android.widget.FrameLayout")))),
                                1),
                        isDisplayed()));
        appCompatCheckedTextView3.perform(click());

        ViewInteraction appCompatButton7 = onView(
                allOf(withId(android.R.id.button1), withText("Submit")));
        appCompatButton7.perform(scrollTo(), click());

        ViewInteraction multiSelectionSpinner3 = onView(
                allOf(withId(R.id.gpTreatmentSpinner),
                        withParent(allOf(withId(R.id.gpTreatment),
                                withParent(withId(R.id.gpText))))));
        /*multiSelectionSpinner3.perform(scrollTo(), click());

        ViewInteraction appCompatCheckedTextView4 = onView(
                allOf(withId(android.R.id.text1), withText("Tab. Paracetamol 500 mg"),
                        childAtPosition(
                                allOf(withClassName(is("com.android.internal.app.AlertController$RecycleListView")),
                                        withParent(withClassName(is("android.widget.FrameLayout")))),
                                45),
                        isDisplayed()));
        appCompatCheckedTextView4.perform(click());

        ViewInteraction appCompatButton8 = onView(
                allOf(withId(android.R.id.button1), withText("Submit")));
        appCompatButton8.perform(scrollTo(), click());*/

        ViewInteraction appCompatButton9 = onView(
                allOf(withId(R.id.gpSaveButton), withText("Save"),
                        withParent(allOf(withId(R.id.gpButton),
                                withParent(withId(R.id.gpText))))));
        appCompatButton9.perform(scrollTo(), click());

        ViewInteraction appCompatButton10 = onView(
                allOf(withId(R.id.gpSaveButton), withText("Confirm"),
                        withParent(allOf(withId(R.id.gpButton),
                                withParent(withId(R.id.gpText))))));
        appCompatButton10.perform(scrollTo(), click());

        pressBack();

        ViewInteraction appCompatButton11 = onView(
                allOf(withId(android.R.id.button3), withText("OK")));
        appCompatButton11.perform(scrollTo(), click());

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
