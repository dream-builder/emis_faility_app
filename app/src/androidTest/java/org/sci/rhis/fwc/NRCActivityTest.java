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
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withClassName;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withParent;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.is;

@LargeTest
@RunWith(AndroidJUnit4.class)
public class NRCActivityTest {

    @Rule
    public ActivityTestRule<LoginActivity> mActivityTestRule = new ActivityTestRule<>(LoginActivity.class);

    @Test
    public void nRCActivityTest() {
        ViewInteraction appCompatCheckedTextView = onView(
                allOf(withId(android.R.id.text1), withText("ঝালকঠি"),
                        childAtPosition(
                                allOf(withClassName(is("com.android.internal.app.AlertController$RecycleListView")),
                                        withParent(withClassName(is("android.widget.LinearLayout")))),
                                4),
                        isDisplayed()));
        appCompatCheckedTextView.perform(click());

        ViewInteraction appCompatButton = onView(
                allOf(withId(android.R.id.button1), withText("OK"), isDisplayed()));
        appCompatButton.perform(click());

        ViewInteraction appCompatEditText = onView(
                allOf(withId(R.id.providerId),
                        withParent(withId(R.id.providerIdLayout)),
                        isDisplayed()));
        appCompatEditText.perform(replaceText("424199"), closeSoftKeyboard());

        ViewInteraction appCompatEditText2 = onView(
                allOf(withId(R.id.providerPassword),
                        withParent(withId(R.id.passwordLayout)),
                        isDisplayed()));
        appCompatEditText2.perform(replaceText("123"), closeSoftKeyboard());

        ViewInteraction appCompatButton2 = onView(
                allOf(withId(R.id.loginbtn1), withText("লগ ইন"), isDisplayed()));
        appCompatButton2.perform(click());

        ViewInteraction appCompatButton10 = onView(
                allOf(withId(R.id.buttonPlacePopUpOK), withText("Select"), isDisplayed()));
        appCompatButton10.perform(click());

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
        appCompatEditText3.perform(scrollTo(), replaceText("NAME"), closeSoftKeyboard());

        ViewInteraction appCompatEditText4 = onView(
                allOf(withId(R.id.Clients_Father),
                        withParent(allOf(withId(R.id.layoutNRCFather),
                                withParent(withId(R.id.nrc_layout))))));
        appCompatEditText4.perform(scrollTo(), replaceText("F"), closeSoftKeyboard());

        ViewInteraction appCompatEditText5 = onView(
                allOf(withId(R.id.Clients_Mother),
                        withParent(allOf(withId(R.id.layoutNRCMother),
                                withParent(withId(R.id.nrc_layout))))));
        appCompatEditText5.perform(scrollTo(), replaceText("M"), closeSoftKeyboard());

        ViewInteraction imageButton = onView(
                withId(R.id.DOBDatePicker));
        imageButton.perform(scrollTo(), click());

        ViewInteraction appCompatButton11 = onView(
                allOf(withId(android.R.id.button1), withText("সেট করুন"), isDisplayed()));
        appCompatButton11.perform(click());

        ViewInteraction appCompatSpinner = onView(
                allOf(withId(R.id.ClientsSexSpinner),
                        withParent(allOf(withId(R.id.layoutNRCAgeSex),
                                withParent(withId(R.id.nrc_layout))))));
        appCompatSpinner.perform(scrollTo(), click());

        ViewInteraction appCompatCheckedTextView2 = onView(
                allOf(withId(android.R.id.text1), withText("মহিলা"), isDisplayed()));
        appCompatCheckedTextView2.perform(click());

        ViewInteraction appCompatSpinner2 = onView(
                allOf(withId(R.id.Clients_District),
                        withParent(allOf(withId(R.id.layoutNRCDistUpz),
                                withParent(withId(R.id.nrc_layout))))));
        appCompatSpinner2.perform(scrollTo(), click());

        ViewInteraction appCompatCheckedTextView3 = onView(
                allOf(withId(android.R.id.text1), withText("হবিগঞ্জ"), isDisplayed()));
        appCompatCheckedTextView3.perform(click());

        ViewInteraction appCompatSpinner3 = onView(
                allOf(withId(R.id.Clients_Upazila),
                        withParent(allOf(withId(R.id.layoutNRCDistUpz),
                                withParent(withId(R.id.nrc_layout))))));
        appCompatSpinner3.perform(scrollTo(), click());

        ViewInteraction appCompatCheckedTextView4 = onView(
                allOf(withId(android.R.id.text1), withText("আজমিরিগঞ্জ"), isDisplayed()));
        appCompatCheckedTextView4.perform(click());

        ViewInteraction appCompatSpinner4 = onView(
                allOf(withId(R.id.Clients_Union),
                        withParent(allOf(withId(R.id.union_village),
                                withParent(withId(R.id.nrc_layout))))));
        appCompatSpinner4.perform(scrollTo(), click());

        ViewInteraction appCompatCheckedTextView5 = onView(
                allOf(withId(android.R.id.text1), withText("কাকাইলছেও"), isDisplayed()));
        appCompatCheckedTextView5.perform(click());

        ViewInteraction appCompatSpinner5 = onView(
                allOf(withId(R.id.Clients_Village),
                        withParent(allOf(withId(R.id.union_village),
                                withParent(withId(R.id.nrc_layout))))));
        appCompatSpinner5.perform(scrollTo(), click());

        ViewInteraction appCompatCheckedTextView6 = onView(
                allOf(withId(android.R.id.text1), withText("কামালপুর"), isDisplayed()));
        appCompatCheckedTextView6.perform(click());

        ViewInteraction appCompatEditText6 = onView(
                allOf(withId(R.id.NrcClients_Mobile_no),
                        withParent(allOf(withId(R.id.couple_mobile_no),
                                withParent(withId(R.id.nrc_layout))))));
        appCompatEditText6.perform(scrollTo(), replaceText("01811111111"), closeSoftKeyboard());

        ViewInteraction appCompatButton12 = onView(
                allOf(withId(R.id.nrcProceed), withText("SAVE"),
                        withParent(allOf(withId(R.id.layoutNationalityWithSaving),
                                withParent(withId(R.id.nrc_layout))))));
        appCompatButton12.perform(scrollTo(), click());

        ViewInteraction appCompatButton13 = onView(
                allOf(withId(R.id.nrcProceed), withText("Confirm"),
                        withParent(allOf(withId(R.id.layoutNationalityWithSaving),
                                withParent(withId(R.id.nrc_layout))))));
        appCompatButton13.perform(scrollTo(), click());

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
