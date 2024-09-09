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
public class FPTest {

    @Rule
    public ActivityTestRule<LoginActivity> mActivityTestRule = new ActivityTestRule<>(LoginActivity.class);

    @Test
    public void fPTest() {

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
                        5),
                        isDisplayed()));
        linearLayout.perform(click());

        ViewInteraction appCompatButton4 = onView(
                allOf(withId(R.id.FPButton), withText("পরিবার পরিকল্পনা সেবা"),
                        withParent(allOf(withId(R.id.Type_tableRow1),
                                withParent(withId(R.id.serviceHeaderLayout))))));
        appCompatButton4.perform(scrollTo(), click());

        ViewInteraction appCompatEditText = onView(
                allOf(withId(R.id.editTextFPMarriageDate),
                        withParent(withId(R.id.marriageDateLayout)),
                        isDisplayed()));
        appCompatEditText.perform(replaceText(ApplicationTest.marriageDate), closeSoftKeyboard());

        ViewInteraction appCompatEditText4 = onView(
                allOf(withId(R.id.editTextFPSonNum),
                        withParent(allOf(withId(R.id.fpStartupSonDaughterNumLayout),
                                withParent(withId(R.id.layoutFPInfoForm))))));
        appCompatEditText4.perform(scrollTo(), replaceText("1"), closeSoftKeyboard());

        ViewInteraction appCompatEditText5 = onView(
                allOf(withId(R.id.editTextFPDaughterNum),
                        withParent(allOf(withId(R.id.fpStartupSonDaughterNumLayout),
                                withParent(withId(R.id.layoutFPInfoForm))))));
        appCompatEditText5.perform(scrollTo(), replaceText("1"), closeSoftKeyboard());

        ViewInteraction appCompatEditText6 = onView(
                allOf(withId(R.id.editTextFPLastChildYear),
                        withParent(allOf(withId(R.id.fpAgeLastChild),
                                withParent(withId(R.id.layoutFPInfoForm))))));
        appCompatEditText6.perform(scrollTo(), replaceText("1"), closeSoftKeyboard());

        ViewInteraction appCompatEditText7 = onView(
                allOf(withId(R.id.editTextFPLastChildMonth),
                        withParent(allOf(withId(R.id.fpAgeLastChild),
                                withParent(withId(R.id.layoutFPInfoForm))))));
        appCompatEditText7.perform(scrollTo(), replaceText("1"), closeSoftKeyboard());

        //
        ViewInteraction appCompatEditText1 = onView(
                allOf(withId(R.id.editTextFPLastDeliveryDate),
                        withParent(withId(R.id.lastDeliveryLayout)),
                        isDisplayed()));
        appCompatEditText1.perform(replaceText(ApplicationTest.lastDeliveryDate), closeSoftKeyboard());

        //onView(isRoot()).perform(ApplicationTest.waitFor(5000));

//        ViewInteraction appCompatEditText2 = onView(
//                allOf(withId(R.id.editTextFPLmpDate),
//                        withParent(withId(R.id.fpStartupLmpLayout)),
//                        isDisplayed()));
//        appCompatEditText2.perform(replaceText(ApplicationTest.lmpDate), closeSoftKeyboard());

        ViewInteraction imageButton2 = onView(
                allOf(withId(R.id.imageButtonFpInfoLmpDate),
                        withParent(allOf(withId(R.id.fpStartupLmpLayout),
                                withParent(withId(R.id.layoutFPInfoForm))))));
        imageButton2.perform(scrollTo(), click());

        //onView(isRoot()).perform(ApplicationTest.waitFor(5000));

        ViewInteraction appCompatButton7 = onView(
                allOf(withId(android.R.id.button1), withText("OK"), isDisplayed()));
        appCompatButton7.perform(click());

        //onView(isRoot()).perform(ApplicationTest.waitFor(5000));

//        ViewInteraction appCompatButton8 = onView(
//                allOf(withId(android.R.id.button3), withText("OK")));
//        appCompatButton8.perform(scrollTo(), click());

        ViewInteraction appCompatRadioButton = onView(
                allOf(withId(R.id.radioFpInfoIsPregnantNo), withText("গর্ভবতী নয়"),
                        withParent(allOf(withId(R.id.fpIsPregnantRadioGroup),
                                withParent(withId(R.id.fpStartupIsPregnantLayout)))),
                        isDisplayed()));
        appCompatRadioButton.perform(click());

        ViewInteraction appCompatRadioButton2 = onView(
                allOf(withId(R.id.radioFpCycleRegular), withText("নিয়মিত"),
                        withParent(allOf(withId(R.id.fpCycleRadioGroup),
                                withParent(withId(R.id.fpCycleLayout)))),
                        isDisplayed()));
        appCompatRadioButton2.perform(click());

        ViewInteraction appCompatEditText8 = onView(
                allOf(withId(R.id.editTextFpCycleDuration),
                        withParent(allOf(withId(R.id.fpCycleDurationLayout),
                                withParent(withId(R.id.layoutFPInfoForm))))));
        appCompatEditText8.perform(scrollTo(), replaceText("5"), closeSoftKeyboard());

        ViewInteraction appCompatRadioButton3 = onView(
                allOf(withId(R.id.radioFpMenstrualAmountNormal), withText("স্বাভাবিক"),
                        withParent(allOf(withId(R.id.fpMenstrualAmountRadioGroup),
                                withParent(withId(R.id.fpMenstrualAmountLayout)))),
                        isDisplayed()));
        appCompatRadioButton3.perform(click());

        ViewInteraction appCompatRadioButton4 = onView(
                allOf(withId(R.id.radioFpMenstrualPain_no), withText("না"),
                        withParent(allOf(withId(R.id.fpMenstrualPainRadioGroup),
                                withParent(withId(R.id.fpMenstrualPainLayout)))),
                        isDisplayed()));
        appCompatRadioButton4.perform(click());

        ViewInteraction appCompatRadioButton5 = onView(
                allOf(withId(R.id.radioFpHasCurrentMethod_yes), withText("হ্যাঁ"),
                        withParent(allOf(withId(R.id.radioGroupHasCurrentMethod),
                                withParent(withId(R.id.hasCurrentMethodLayout)))),
                        isDisplayed()));
        appCompatRadioButton5.perform(click());

        ViewInteraction appCompatSpinner2 = onView(
                allOf(withId(R.id.spinnerMethodFPStartup),
                        withParent(allOf(withId(R.id.fpStartupMethodLayout),
                                withParent(withId(R.id.layoutFPInfoForm))))));
        appCompatSpinner2.perform(scrollTo(), click());

        ViewInteraction appCompatCheckedTextView2 = onView(
                allOf(withId(android.R.id.text1), withText("কনডম"), isDisplayed()));
        appCompatCheckedTextView2.perform(click());

        ViewInteraction appCompatSpinner3 = onView(
                allOf(withId(R.id.spinnerFPVisitReasons),
                        withParent(allOf(withId(R.id.fpStartupVisitReasonLayout),
                                withParent(withId(R.id.fpStartupFragment))))));
        appCompatSpinner3.perform(scrollTo(), click());

        ViewInteraction appCompatCheckedTextView3 = onView(
                allOf(withId(android.R.id.text1), withText("পদ্ধতি পরিবর্তন"), isDisplayed()));
        appCompatCheckedTextView3.perform(click());

        ViewInteraction appCompatButton9 = onView(
                allOf(withId(R.id.buttonFPStartupProceed), withText("Next")));
        appCompatButton9.perform(scrollTo(), click());

        ViewInteraction appCompatRadioButton6 = onView(
                allOf(withId(R.id.squestion2radioButtonNo), withText("না"),
                        withParent(allOf(withId(R.id.squestion2radioGroup),
                                withParent(withId(R.id.squestion2Layout))))));
        appCompatRadioButton6.perform(scrollTo(), click());

        ViewInteraction appCompatRadioButton7 = onView(
                allOf(withId(R.id.squestion3radioButtonNo), withText("না"),
                        withParent(allOf(withId(R.id.squestion3radioGroup),
                                withParent(withId(R.id.squestion3Layout))))));
        appCompatRadioButton7.perform(scrollTo(), click());

        ViewInteraction appCompatRadioButton8 = onView(
                allOf(withId(R.id.squestion6radioButtonNo), withText("না"),
                        withParent(allOf(withId(R.id.squestion6radioGroup),
                                withParent(withId(R.id.squestion6Layout))))));
        appCompatRadioButton8.perform(scrollTo(), click());

        ViewInteraction appCompatRadioButton9 = onView(
                allOf(withId(R.id.squestion9radioButtonNo), withText("না"),
                        withParent(allOf(withId(R.id.squestion9radioGroup),
                                withParent(withId(R.id.squestion9Layout))))));
        appCompatRadioButton9.perform(scrollTo(), click());

        ViewInteraction appCompatRadioButton10 = onView(
                allOf(withId(R.id.squestion10radioButtonNo), withText("না"),
                        withParent(allOf(withId(R.id.squestion10radioGroup),
                                withParent(withId(R.id.squestion10Layout))))));
        appCompatRadioButton10.perform(scrollTo(), click());

        ViewInteraction appCompatRadioButton11 = onView(
                allOf(withId(R.id.squestion11radioButtonNo), withText("না"),
                        withParent(allOf(withId(R.id.squestion11radioGroup),
                                withParent(withId(R.id.squestion11Layout))))));
        appCompatRadioButton11.perform(scrollTo(), click());

        ViewInteraction appCompatRadioButton12 = onView(
                allOf(withId(R.id.squestion12radioButtonNo), withText("না"),
                        withParent(allOf(withId(R.id.squestion12radioGroup),
                                withParent(withId(R.id.squestion12Layout))))));
        appCompatRadioButton12.perform(scrollTo(), click());

        ViewInteraction appCompatRadioButton13 = onView(
                allOf(withId(R.id.squestion13radioButtonNo), withText("না"),
                        withParent(allOf(withId(R.id.squestion13radioGroup),
                                withParent(withId(R.id.squestion13Layout))))));
        appCompatRadioButton13.perform(scrollTo(), click());

        ViewInteraction appCompatRadioButton14 = onView(
                allOf(withId(R.id.squestion14radioButtonNo), withText("না"),
                        withParent(allOf(withId(R.id.squestion14radioGroup),
                                withParent(withId(R.id.squestion14Layout))))));
        appCompatRadioButton14.perform(scrollTo(), click());

        ViewInteraction appCompatRadioButton15 = onView(
                allOf(withId(R.id.squestion15radioButtonNo), withText("না"),
                        withParent(allOf(withId(R.id.squestion15radioGroup),
                                withParent(withId(R.id.squestion15Layout))))));
        appCompatRadioButton15.perform(scrollTo(), click());

        ViewInteraction appCompatRadioButton16 = onView(
                allOf(withId(R.id.squestion16radioButtonNo), withText("না"),
                        withParent(allOf(withId(R.id.squestion16radioGroup),
                                withParent(withId(R.id.squestion16Layout))))));
        appCompatRadioButton16.perform(scrollTo(), click());

        ViewInteraction appCompatRadioButton17 = onView(
                allOf(withId(R.id.squestion18radioButtonNo), withText("না"),
                        withParent(allOf(withId(R.id.squestion18radioGroup),
                                withParent(withId(R.id.squestion18Layout))))));
        appCompatRadioButton17.perform(scrollTo(), click());

        ViewInteraction appCompatRadioButton18 = onView(
                allOf(withId(R.id.squestion19radioButtonNo), withText("না"),
                        withParent(allOf(withId(R.id.squestion19radioGroup),
                                withParent(withId(R.id.squestion19Layout))))));
        appCompatRadioButton18.perform(scrollTo(), click());

        ViewInteraction appCompatRadioButton19 = onView(
                allOf(withId(R.id.squestion20radioButtonNo), withText("না"),
                        withParent(allOf(withId(R.id.squestion20radioGroup),
                                withParent(withId(R.id.squestion20Layout))))));
        appCompatRadioButton19.perform(scrollTo(), click());

        ViewInteraction appCompatRadioButton20 = onView(
                allOf(withId(R.id.squestion21radioButtonNo), withText("না"),
                        withParent(allOf(withId(R.id.squestion21radioGroup),
                                withParent(withId(R.id.squestion21Layout))))));
        appCompatRadioButton20.perform(scrollTo(), click());

        ViewInteraction appCompatRadioButton21 = onView(
                allOf(withId(R.id.squestion24radioButtonNo), withText("না"),
                        withParent(allOf(withId(R.id.squestion24radioGroup),
                                withParent(withId(R.id.squestion24Layout))))));
        appCompatRadioButton21.perform(scrollTo(), click());

        ViewInteraction appCompatRadioButton22 = onView(
                allOf(withId(R.id.squestion25radioButtonNo), withText("না"),
                        withParent(allOf(withId(R.id.squestion25radioGroup),
                                withParent(withId(R.id.squestion25Layout))))));
        appCompatRadioButton22.perform(scrollTo(), click());

        ViewInteraction appCompatRadioButton23 = onView(
                allOf(withId(R.id.squestion26radioButtonNo), withText("না"),
                        withParent(allOf(withId(R.id.squestion26radioGroup),
                                withParent(withId(R.id.squestion26Layout))))));
        appCompatRadioButton23.perform(scrollTo(), click());

        ViewInteraction appCompatRadioButton24 = onView(
                allOf(withId(R.id.squestion29radioButtonNo), withText("না"),
                        withParent(allOf(withId(R.id.squestion29radioGroup),
                                withParent(withId(R.id.squestion29Layout))))));
        appCompatRadioButton24.perform(scrollTo(), click());

        ViewInteraction appCompatButton10 = onView(
                allOf(withId(R.id.screeningSubmitButton), withText("Next")));
        appCompatButton10.perform(scrollTo(), click());

        ViewInteraction appCompatSpinner4 = onView(
                allOf(withId(R.id.fpAnemiaSpinner),
                        withParent(allOf(withId(R.id.fpAnemia),
                                withParent(withId(R.id.fp_clients_info_layout))))));
        appCompatSpinner4.perform(scrollTo(), click());

        ViewInteraction appCompatCheckedTextView4 = onView(
                allOf(withId(android.R.id.text1), withText("নাই"), isDisplayed()));
        appCompatCheckedTextView4.perform(click());

        ViewInteraction appCompatSpinner5 = onView(
                allOf(withId(R.id.fpJaundiceSpinner),
                        withParent(allOf(withId(R.id.fpJaundiceLayout),
                                withParent(withId(R.id.fp_clients_info_layout))))));
        appCompatSpinner5.perform(scrollTo(), click());

        ViewInteraction appCompatCheckedTextView5 = onView(
                allOf(withId(android.R.id.text1), withText("নাই"), isDisplayed()));
        appCompatCheckedTextView5.perform(click());

        ViewInteraction appCompatEditText9 = onView(
                allOf(withId(R.id.fpBloodPressureValueSystolic),
                        withParent(allOf(withId(R.id.fpBloodPressure),
                                withParent(withId(R.id.fp_clients_info_layout))))));
        appCompatEditText9.perform(scrollTo(), replaceText("123"), closeSoftKeyboard());

        ViewInteraction appCompatEditText10 = onView(
                allOf(withId(R.id.fpBloodPressureValueDiastolic),
                        withParent(allOf(withId(R.id.fpBloodPressure),
                                withParent(withId(R.id.fp_clients_info_layout))))));
        appCompatEditText10.perform(scrollTo(), replaceText("80"), closeSoftKeyboard());

        ViewInteraction appCompatSpinner6 = onView(
                allOf(withId(R.id.fpBreastConditionSpinner),
                        withParent(allOf(withId(R.id.fpBreastCondition),
                                withParent(withId(R.id.fp_clients_info_layout))))));
        appCompatSpinner6.perform(scrollTo(), click());

        ViewInteraction appCompatCheckedTextView6 = onView(
                allOf(withId(android.R.id.text1), withText("স্বাভাবিক"), isDisplayed()));
        appCompatCheckedTextView6.perform(click());

        ViewInteraction appCompatButton11 = onView(
                allOf(withId(R.id.fpExamSubmitButton), withText("Next"),
                        withParent(allOf(withId(R.id.fpExamNextButtonLayout),
                                withParent(withId(R.id.fp_clients_info_layout))))));
        appCompatButton11.perform(scrollTo(), click());

        ViewInteraction appCompatButton12 = onView(
                allOf(withId(android.R.id.button3), withText("OK")));
        appCompatButton12.perform(scrollTo(), click());

        ViewInteraction appCompatButton13 = onView(
                allOf(withId(android.R.id.button1), withText("OK")));
        appCompatButton13.perform(scrollTo(), click());

        ViewInteraction appCompatRadioButton25 = onView(
                allOf(withId(R.id.impCounselling1radioButtonYes), withText("হ্যাঁ"),
                        withParent(allOf(withId(R.id.impCounselling1radioGroup),
                                withParent(withId(R.id.impCounselling1Layout)))),
                        isDisplayed()));
        appCompatRadioButton25.perform(click());

        ViewInteraction appCompatRadioButton26 = onView(
                allOf(withId(R.id.impCounselling2radioButtonYes), withText("হ্যাঁ"),
                        withParent(allOf(withId(R.id.impCounselling2radioGroup),
                                withParent(withId(R.id.impCounselling2Layout)))),
                        isDisplayed()));
        appCompatRadioButton26.perform(click());

        ViewInteraction appCompatRadioButton27 = onView(
                allOf(withId(R.id.impCounselling3radioButtonYes), withText("হ্যাঁ"),
                        withParent(allOf(withId(R.id.impCounselling3radioGroup),
                                withParent(withId(R.id.impCounselling3Layout)))),
                        isDisplayed()));
        appCompatRadioButton27.perform(click());

        ViewInteraction appCompatRadioButton28 = onView(
                allOf(withId(R.id.impCounselling4radioButtonYes), withText("হ্যাঁ"),
                        withParent(allOf(withId(R.id.impCounselling4radioGroup),
                                withParent(withId(R.id.impCounselling4Layout)))),
                        isDisplayed()));
        appCompatRadioButton28.perform(click());

        ViewInteraction appCompatRadioButton29 = onView(
                allOf(withId(R.id.impCounselling5radioButtonYes), withText("হ্যাঁ"),
                        withParent(allOf(withId(R.id.impCounselling5radioGroup),
                                withParent(withId(R.id.impCounselling5Layout)))),
                        isDisplayed()));
        appCompatRadioButton29.perform(click());

        ViewInteraction appCompatRadioButton30 = onView(
                allOf(withId(R.id.impCounselling6radioButtonYes), withText("হ্যাঁ"),
                        withParent(allOf(withId(R.id.impCounselling6radioGroup),
                                withParent(withId(R.id.impCounselling6Layout)))),
                        isDisplayed()));
        appCompatRadioButton30.perform(click());

        ViewInteraction appCompatRadioButton31 = onView(
                allOf(withId(R.id.impCounselling7radioButtonYes), withText("হ্যাঁ"),
                        withParent(allOf(withId(R.id.impCounselling7radioGroup),
                                withParent(withId(R.id.impCounselling7Layout)))),
                        isDisplayed()));
        appCompatRadioButton31.perform(click());

        ViewInteraction appCompatRadioButton32 = onView(
                allOf(withId(R.id.iudCounselling1radioButtonYes), withText("হ্যাঁ"),
                        withParent(allOf(withId(R.id.iudCounselling1radioGroup),
                                withParent(withId(R.id.iudCounselling1Layout)))),
                        isDisplayed()));
        appCompatRadioButton32.perform(click());

        ViewInteraction appCompatRadioButton33 = onView(
                allOf(withId(R.id.iudCounselling2radioButtonYes), withText("হ্যাঁ"),
                        withParent(allOf(withId(R.id.iudCounselling2radioGroup),
                                withParent(withId(R.id.iudCounselling2Layout)))),
                        isDisplayed()));
        appCompatRadioButton33.perform(click());

        ViewInteraction appCompatRadioButton34 = onView(
                allOf(withId(R.id.iudCounselling3radioButtonYes), withText("হ্যাঁ"),
                        withParent(allOf(withId(R.id.iudCounselling3radioGroup),
                                withParent(withId(R.id.iudCounselling3Layout)))),
                        isDisplayed()));
        appCompatRadioButton34.perform(click());

        ViewInteraction appCompatRadioButton35 = onView(
                allOf(withId(R.id.injCounselling1radioButtonYes), withText("হ্যাঁ"),
                        withParent(allOf(withId(R.id.injCounselling1radioGroup),
                                withParent(withId(R.id.injCounselling1Layout)))),
                        isDisplayed()));
        appCompatRadioButton35.perform(click());

        ViewInteraction appCompatRadioButton36 = onView(
                allOf(withId(R.id.injCounselling2radioButtonYes), withText("হ্যাঁ"),
                        withParent(allOf(withId(R.id.injCounselling2radioGroup),
                                withParent(withId(R.id.injCounselling2Layout)))),
                        isDisplayed()));
        appCompatRadioButton36.perform(click());

        ViewInteraction appCompatRadioButton37 = onView(
                allOf(withId(R.id.injCounselling3radioButtonYes), withText("হ্যাঁ"),
                        withParent(allOf(withId(R.id.injCounselling3radioGroup),
                                withParent(withId(R.id.injCounselling3Layout)))),
                        isDisplayed()));
        appCompatRadioButton37.perform(click());

        ViewInteraction appCompatRadioButton38 = onView(
                allOf(withId(R.id.injCounselling4radioButtonYes), withText("হ্যাঁ"),
                        withParent(allOf(withId(R.id.injCounselling4radioGroup),
                                withParent(withId(R.id.injCounselling4Layout)))),
                        isDisplayed()));
        appCompatRadioButton38.perform(click());

        ViewInteraction appCompatRadioButton39 = onView(
                allOf(withId(R.id.injCounselling5radioButtonYes), withText("হ্যাঁ"),
                        withParent(allOf(withId(R.id.injCounselling5radioGroup),
                                withParent(withId(R.id.injCounselling5Layout)))),
                        isDisplayed()));
        appCompatRadioButton39.perform(click());

        ViewInteraction appCompatRadioButton40 = onView(
                allOf(withId(R.id.injCounselling6radioButtonYes), withText("হ্যাঁ"),
                        withParent(allOf(withId(R.id.injCounselling6radioGroup),
                                withParent(withId(R.id.injCounselling6Layout)))),
                        isDisplayed()));
        appCompatRadioButton40.perform(click());

        ViewInteraction appCompatRadioButton41 = onView(
                allOf(withId(R.id.apCounselling1radioButtonYes), withText("হ্যাঁ"),
                        withParent(allOf(withId(R.id.apCounselling1radioGroup),
                                withParent(withId(R.id.apCounselling1Layout)))),
                        isDisplayed()));
        appCompatRadioButton41.perform(click());

        ViewInteraction appCompatRadioButton42 = onView(
                allOf(withId(R.id.suCounselling1radioButtonYes), withText("হ্যাঁ"),
                        withParent(allOf(withId(R.id.suCounselling1radioGroup),
                                withParent(withId(R.id.suCounselling1Layout)))),
                        isDisplayed()));
        appCompatRadioButton42.perform(click());

        ViewInteraction appCompatButton14 = onView(
                allOf(withId(R.id.buttonFPCounselling), withText("Next")));
        appCompatButton14.perform(scrollTo(), click());

        ViewInteraction appCompatCheckedTextView7 = onView(
                allOf(withId(android.R.id.text1), withText("ইনজেকশন (ডিএমপিএ)"),
                        childAtPosition(
                                allOf(withClassName(is("com.android.internal.app.AlertController$RecycleListView")),
                                        withParent(withClassName(is("android.widget.FrameLayout")))),
                                3),
                        isDisplayed()));
        appCompatCheckedTextView7.perform(click());

        ViewInteraction appCompatEditText11 = onView(
                allOf(withId(R.id.injectableWeightEditText),
                        withParent(allOf(withId(R.id.injectableWeightLayout),
                                withParent(withId(R.id.injectableTextLayout))))));
        appCompatEditText11.perform(scrollTo(), replaceText("60"), closeSoftKeyboard());

        ViewInteraction appCompatButton15 = onView(
                allOf(withId(R.id.injectableSaveButton), withText("Save"),
                        withParent(allOf(withId(R.id.injectableButtonLayout),
                                withParent(withId(R.id.injectableTextLayout))))));
        appCompatButton15.perform(scrollTo(), click());

        ViewInteraction appCompatButton16 = onView(
                allOf(withId(R.id.injectableSaveButton), withText("Confirm"),
                        withParent(allOf(withId(R.id.injectableButtonLayout),
                                withParent(withId(R.id.injectableTextLayout))))));
        appCompatButton16.perform(scrollTo(), click());

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
