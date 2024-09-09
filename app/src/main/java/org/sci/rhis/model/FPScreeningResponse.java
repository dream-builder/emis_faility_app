package org.sci.rhis.model;

import android.content.Context;
import android.widget.RadioButton;

import org.json.JSONException;
import org.json.JSONObject;
import org.sci.rhis.fwc.R;
import org.sci.rhis.utilities.Flag;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by arafat.hasan on 4/5/2016.
 */
public class FPScreeningResponse {
    private boolean response1;
    private boolean response2;
    private boolean response3;
    private boolean response4;
    private boolean response5;
    private boolean response6;
    private boolean response7;
    private boolean response8;
    private boolean response9;
    private boolean response10;
    private boolean response11;
    private boolean response12;
    private boolean response13;
    private boolean response14;
    private boolean response15;
    private boolean response16;
    private boolean response17;
    private boolean response18;
    private boolean response19;
    private boolean response20;
    private boolean response21;
    private boolean response22;
    private boolean response23;
    private boolean response24;
    private boolean response25;
    private boolean response26;
    private boolean response27;
    private boolean response28;
    private boolean response29;

    private String providerId;

    public FPScreeningResponse() {
    }

    public boolean isResponse1() {
        return response1;
    }

    public void setResponse1(boolean response1) {
        this.response1 = response1;
    }

    public boolean isResponse2() {
        return response2;
    }

    public void setResponse2(boolean response2) {
        this.response2 = response2;
    }

    public boolean isResponse3() {
        return response3;
    }

    public void setResponse3(boolean response3) {
        this.response3 = response3;
    }

    public boolean isResponse4() {
        return response4;
    }

    public void setResponse4(boolean response4) {
        this.response4 = response4;
    }

    public boolean isResponse5() {
        return response5;
    }

    public void setResponse5(boolean response5) {
        this.response5 = response5;
    }

    public boolean isResponse20() {
        return response20;
    }

    public void setResponse20(boolean response20) {
        this.response20 = response20;
    }

    public boolean isResponse19() {
        return response19;
    }

    public void setResponse19(boolean response19) {
        this.response19 = response19;
    }

    public boolean isResponse18() {
        return response18;
    }

    public void setResponse18(boolean response18) {
        this.response18 = response18;
    }

    public boolean isResponse17() {
        return response17;
    }

    public void setResponse17(boolean response17) {
        this.response17 = response17;
    }

    public boolean isResponse16() {
        return response16;
    }

    public void setResponse16(boolean response16) {
        this.response16 = response16;
    }

    public boolean isResponse15() {
        return response15;
    }

    public void setResponse15(boolean response15) {
        this.response15 = response15;
    }

    public boolean isResponse14() {
        return response14;
    }

    public void setResponse14(boolean response14) {
        this.response14 = response14;
    }

    public boolean isResponse13() {
        return response13;
    }

    public void setResponse13(boolean response13) {
        this.response13 = response13;
    }

    public boolean isResponse12() {
        return response12;
    }

    public void setResponse12(boolean response12) {
        this.response12 = response12;
    }

    public boolean isResponse7() {
        return response7;
    }

    public void setResponse7(boolean response7) {
        this.response7 = response7;
    }

    public boolean isResponse6() {
        return response6;
    }

    public void setResponse6(boolean response6) {
        this.response6 = response6;
    }

    public boolean isResponse8() {
        return response8;
    }

    public void setResponse8(boolean response8) {
        this.response8 = response8;
    }

    public boolean isResponse9() {
        return response9;
    }

    public void setResponse9(boolean response9) {
        this.response9 = response9;
    }

    public boolean isResponse10() {
        return response10;
    }

    public void setResponse10(boolean response10) {
        this.response10 = response10;
    }

    public boolean isResponse11() {
        return response11;
    }

    public void setResponse11(boolean response11) {
        this.response11 = response11;
    }

    public boolean isResponse21() {
        return response21;
    }

    public void setResponse21(boolean response21) {
        this.response21 = response21;
    }

    public boolean isResponse22() {
        return response22;
    }

    public void setResponse22(boolean response22) {
        this.response22 = response22;
    }

    public boolean isResponse23() {
        return response23;
    }

    public void setResponse23(boolean response23) {
        this.response23 = response23;
    }

    public boolean isResponse24() {
        return response24;
    }

    public void setResponse24(boolean response24) {
        this.response24 = response24;
    }

    public boolean isResponse25() {
        return response25;
    }

    public void setResponse25(boolean response25) {
        this.response25 = response25;
    }

    public boolean isResponse26() {
        return response26;
    }

    public void setResponse26(boolean response26) {
        this.response26 = response26;
    }

    public boolean isResponse27() {
        return response27;
    }

    public void setResponse27(boolean response27) {
        this.response27 = response27;
    }

    public boolean isResponse28() {
        return response28;
    }

    public void setResponse28(boolean response28) {
        this.response28 = response28;
    }

    public boolean isResponse29() {
        return response29;
    }

    public void setResponse29(boolean response29) {
        this.response29 = response29;
    }

    public String getProviderId() {
        return providerId;
    }

    public void setProviderId(String providerId) {
        this.providerId = providerId;
    }

    public String toJsonString() {

        JSONObject responseJson = new JSONObject();
        try {
            responseJson.put("response1",response1);
            responseJson.put("response2",response2);
            responseJson.put("response3",response3);
            responseJson.put("response4",response4);
            responseJson.put("response5",response5);
            responseJson.put("response6",response6);
            responseJson.put("response7",response7);
            responseJson.put("response8",response8);
            responseJson.put("response9",response9);
            responseJson.put("response10",response10);
            responseJson.put("response11",response11);
            responseJson.put("response12",response12);
            responseJson.put("response13",response13);
            responseJson.put("response14",response14);
            responseJson.put("response15",response15);
            responseJson.put("response16",response16);
            responseJson.put("response17",response17);
            responseJson.put("response18",response18);
            responseJson.put("response19",response19);
            responseJson.put("response20",response20);
            responseJson.put("response21",response21);
            responseJson.put("response22",response22);
            responseJson.put("response23",response23);
            responseJson.put("response24",response24);
            responseJson.put("response25",response25);
            responseJson.put("response26",response26);
            responseJson.put("response27",response27);
            responseJson.put("response28",response28);
            responseJson.put("response29",response29);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return responseJson.toString();
    }

    public String toStringPreview(Context eContext) {
        return "প্রশ্নমালাঃ \n" +
                "_______________________________________________\n" +
                (response1? " - "+ eContext.getResources().getString(R.string.sq1) +": হ্যাঁ \n":"") +
                (response2? " - "+eContext.getResources().getString(R.string.sq2) +": হ্যাঁ \n":"") +
                (response3? " - "+eContext.getResources().getString(R.string.sq3) +": হ্যাঁ \n":"") +
                (response4? " - "+eContext.getResources().getString(R.string.sq4) +": হ্যাঁ \n":"") +
                (response5? " - "+eContext.getResources().getString(R.string.sq5) +": হ্যাঁ \n":"") +
                (response6? " - "+eContext.getResources().getString(R.string.sq6) +": হ্যাঁ \n":"") +
                (response7? " - "+eContext.getResources().getString(R.string.sq7) +": হ্যাঁ \n":"") +
                (response8? " - "+eContext.getResources().getString(R.string.sq8) +": হ্যাঁ \n":"") +
                (response9? " - "+eContext.getResources().getString(R.string.sq9) +": হ্যাঁ \n":"") +
                (response10? " - "+eContext.getResources().getString(R.string.sq10) +": হ্যাঁ \n":"") +
                (response11? " - "+eContext.getResources().getString(R.string.sq11) +": হ্যাঁ \n":"") +
                (response12? " - "+eContext.getResources().getString(R.string.sq12) +": হ্যাঁ \n":"") +
                (response13? " - "+eContext.getResources().getString(R.string.sq13) +": হ্যাঁ \n":"") +
                (response14? " - "+eContext.getResources().getString(R.string.sq14) +": হ্যাঁ \n":"") +
                (response15? " - "+eContext.getResources().getString(R.string.sq15) +": হ্যাঁ \n":"") +
                (response16? " - "+eContext.getResources().getString(R.string.sq16) +": হ্যাঁ \n":"") +
                (response17? " - "+eContext.getResources().getString(R.string.sq17) +": হ্যাঁ \n":"") +
                (response18? " - "+eContext.getResources().getString(R.string.sq18) +": হ্যাঁ \n":"") +
                (response19? " - "+eContext.getResources().getString(R.string.sq19) +": হ্যাঁ \n":"") +
                (response20? " - "+eContext.getResources().getString(R.string.sq20) +": হ্যাঁ \n":"") +
                (response21? " - "+eContext.getResources().getString(R.string.sq21) +": হ্যাঁ \n":"") +
                (response22? " - "+eContext.getResources().getString(R.string.sq22) +": হ্যাঁ \n":"") +
                (response23? " - "+eContext.getResources().getString(R.string.sq23) +": হ্যাঁ \n":"") +
                (response24? " - "+eContext.getResources().getString(R.string.sq24) +": হ্যাঁ \n":"") +
                (response25? " - "+eContext.getResources().getString(R.string.sq25) +": হ্যাঁ \n":"") +
                (response26? " - "+eContext.getResources().getString(R.string.sq26) +": হ্যাঁ \n":"") +
                (response27? " - "+eContext.getResources().getString(R.string.sq27) +": হ্যাঁ \n":"") +
                (response28? " - "+eContext.getResources().getString(R.string.sq28) +": হ্যাঁ \n":"") +
                (response29? " - "+eContext.getResources().getString(R.string.sq29) +": হ্যাঁ \n":"") +
                "\n\n";
    }
}
