package org.sci.rhis.utilities;

import org.sci.rhis.model.FPExaminations;
import org.sci.rhis.model.FPScreeningResponse;

import java.util.Arrays;

/**
 * Created by arafat.hasan on 5/8/2016.
 */
public class FPScreeningEngine {
    FPScreeningResponse mScreening;
    FPExaminations mExams;
    StringBuilder reasonForEliminated;

    public FPScreeningEngine(FPScreeningResponse eScreening, FPExaminations eExams) {
        this.mScreening = eScreening;
        this.mExams = eExams;
    }

    public boolean isAponEligible(){
        boolean eligibility=true;

        if(mScreening.isResponse2() || mScreening.isResponse3() ||  mScreening.isResponse5()||  mScreening.isResponse6()
                || mScreening.isResponse10() ||mScreening.isResponse12()
                ||mScreening.isResponse13() ||mScreening.isResponse18()){
           eligibility=false;
        }

        if(Arrays.asList(new Integer[]{2, 3, 4}).contains(Integer.valueOf(mExams.getJaundice()))) eligibility=false;
        if(Integer.valueOf(mExams.getBreastCondition())==2) eligibility=false;
        if(mExams.isCurrentlyPregnant()) eligibility=false;
        if(!mScreening.isResponse8()) eligibility=false;

        return eligibility;
    }

    public boolean isSukhiEligible(){
        boolean eligibility=true;

        if(mScreening.isResponse3() || mScreening.isResponse5() ||  mScreening.isResponse6() ||mScreening.isResponse7() ||
                mScreening.isResponse8() ||mScreening.isResponse9() ||mScreening.isResponse10() ||mScreening.isResponse12() ||
                mScreening.isResponse13() || mScreening.isResponse14() || mScreening.isResponse16() || mScreening.isResponse17()
                || mScreening.isResponse18()) {
           eligibility=false;
        }

        if(Arrays.asList(new Integer[]{2, 3, 4}).contains(Integer.valueOf(mExams.getJaundice()))) eligibility=false;
        if(!mExams.getBpSystolic().equals("")){
            if(Integer.valueOf(mExams.getBpSystolic())>= 140) eligibility=false;
        }
        if(!mExams.getBpDiastolic().equals("")){
            if(Integer.valueOf(mExams.getBpDiastolic())>= 90) eligibility=false;
        }
        if(Integer.valueOf(mExams.getBreastCondition())==2) eligibility=false;
        if(mExams.isCurrentlyPregnant()) eligibility=false;

        return eligibility;
    }

    public boolean isInjectableEligible(){
        boolean eligibility=true;

        if(mScreening.isResponse1() || mScreening.isResponse2() ||  mScreening.isResponse3() ||mScreening.isResponse5() ||
                mScreening.isResponse6() ||mScreening.isResponse7() ||mScreening.isResponse9() ||mScreening.isResponse10() ||
                mScreening.isResponse12() || mScreening.isResponse13() || mScreening.isResponse14() || mScreening.isResponse18()){
           eligibility=false;
        }

        if(!mExams.getBpSystolic().equals("")){
            if(Integer.valueOf(mExams.getBpSystolic())>= 160) eligibility=false;
        }
        if(!mExams.getBpDiastolic().equals("")){
            if(Integer.valueOf(mExams.getBpDiastolic())>= 100) eligibility=false;
        }
        if(Integer.valueOf(mExams.getBreastCondition())==2) eligibility=false;
        if(mExams.isCurrentlyPregnant()) eligibility=false;


        return eligibility;
    }

    public boolean isImplantEligible(){
        boolean eligibility=true;

        if(mScreening.isResponse1() || mScreening.isResponse2() ||  mScreening.isResponse3() ||mScreening.isResponse5() ||
                mScreening.isResponse6() ||mScreening.isResponse9() ||mScreening.isResponse10()
                || mScreening.isResponse12() || mScreening.isResponse13() ||  mScreening.isResponse18()) {
            eligibility=false;
        }

        if(Integer.valueOf(mExams.getJaundice())== 4) eligibility=false;
        if(!mExams.getBpSystolic().equals("")){
            if(Integer.valueOf(mExams.getBpSystolic())>= 140) eligibility=false;
        }
        if(!mExams.getBpDiastolic().equals("")){
            if(Integer.valueOf(mExams.getBpDiastolic())>= 90) eligibility=false;
        }
        if(Integer.valueOf(mExams.getBreastCondition())==2) eligibility=false;
        if(mExams.isCurrentlyPregnant()) eligibility=false;
        if(!mExams.getHemoglobin().equals("")){
            if(Double.valueOf(mExams.getHemoglobin())<45) eligibility=false;
        }

        return eligibility;
    }

    public boolean isIudEligible(){
        boolean eligibility=true;

        if(mScreening.isResponse2() || mScreening.isResponse3() ||  mScreening.isResponse4()
                || mScreening.isResponse5() || mScreening.isResponse19() ||  mScreening.isResponse20()
                ||mScreening.isResponse21()) {
            eligibility=false;
        }

        if(Integer.valueOf(mExams.getAnemia())==4) eligibility=false;
        if(Integer.valueOf(mExams.getCervix())==2) eligibility=false;
        if(Integer.valueOf(mExams.getMenstruation())==2) eligibility=false;
        if(Integer.valueOf(mExams.getVaginalWall())==2) eligibility=false;
        if(Integer.valueOf(mExams.getCervicitis())==2) eligibility=false;
        if(Integer.valueOf(mExams.getCervicalErosion())==2) eligibility=false;
        if(Integer.valueOf(mExams.getCervicalPolyp())==2) eligibility=false;
        if(Integer.valueOf(mExams.getContactBleeding())==2) eligibility=false;
        if(Integer.valueOf(mExams.getUterusSize())==2) eligibility=false;
        if(Integer.valueOf(mExams.getUterusShape())==2) eligibility=false;
        if(Integer.valueOf(mExams.getUterusMovement())==2) eligibility=false;
        if(Integer.valueOf(mExams.getUterusMovementPain())==1) eligibility=false;
        if(Integer.valueOf(mExams.getVaginalFornix())==2) eligibility=false;
        if(mExams.isCurrentlyPregnant()) eligibility=false;
        if(!mExams.getHemoglobin().equals("")){
            if(Double.valueOf(mExams.getHemoglobin())<45) eligibility=false;
        }

        return eligibility;
    }

    public boolean isPermanentForWomanEligible(){
        boolean eligibility=true;

        if(mScreening.isResponse5() || mScreening.isResponse9() ||  mScreening.isResponse10() ||mScreening.isResponse11()
            ||mScreening.isResponse15() ||mScreening.isResponse16() ||mScreening.isResponse22() ||mScreening.isResponse24()
            || mScreening.isResponse25() || mScreening.isResponse26() ||  mScreening.isResponse29()) {
            eligibility=false;
        }

        if(Integer.valueOf(mExams.getAnemia())==4) eligibility=false;
        if(Integer.valueOf(mExams.getVaginalWall())==2) eligibility=false;
        if(Integer.valueOf(mExams.getCervicitis())==2) eligibility=false;
        if(mExams.isCurrentlyPregnant()) eligibility=false;
        if(Arrays.asList(new Integer[]{2, 3, 4}).contains(Integer.valueOf(mExams.getUrineSugar()))) eligibility=false;
        if(!mExams.getHemoglobin().equals("")){
            if(Double.valueOf(mExams.getHemoglobin())<45) eligibility=false;
        }

        return eligibility;
    }

    public boolean isPermanentForManEligible(){
        boolean eligibility=true;

        if(mScreening.isResponse9() ||  mScreening.isResponse10() ||mScreening.isResponse12()||mScreening.isResponse16()
                || mScreening.isResponse22() || mScreening.isResponse24() ||  mScreening.isResponse27()
                ||mScreening.isResponse28() ||mScreening.isResponse29() ) {
            eligibility=false;
        }

        if(Arrays.asList(new Integer[]{2, 3, 4}).contains(Integer.valueOf(mExams.getUrineSugar()))) eligibility=false;
        return eligibility;
    }

    public String getMessageWithEligibleMethodList(boolean isWoman, boolean isNuliPara){
        StringBuilder message = new StringBuilder();
        boolean hasWomanPermMethod = false;

        if(!isWoman){
            if(!isNuliPara) {
                if (isPermanentForManEligible())
                    message.append(" - " + Flag.PERMANENT_METHOD_MAN_TEXT + "\n");
            }
        }else{
            if(!isNuliPara) {
                if(isPermanentForWomanEligible()){
                    message.append( " - "+Flag.PERMANENT_METHOD_WOMAN_TEXT+"\n");
                    hasWomanPermMethod = true;
                }
            }

            if(isImplantEligible()) message.append(" - "+Flag.IMPLANT_TEXT+"\n");
            if(!isNuliPara){
                if(isIudEligible()) message.append(" - "+Flag.IUD_TEXT+"\n");
                if(isInjectableEligible()) message.append(" - "+Flag.INJECTION_DMPA_TEXT+"\n");
            }
            if(isAponEligible()) message.append(" - "+Flag.PILL_APON_TEXT+"\n");
            if(isSukhiEligible()) message.append(" - "+Flag.PILL_SUKHI_TEXT+"\n");
        }

        message.append(" - কনডম\n\n");
        if(hasWomanPermMethod){
            message.append("** বাছাইকরণ পরীক্ষা সাপেক্ষে "+Flag.PERMANENT_METHOD_MAN_TEXT+" ও উপযুক্ত\n\n");
        }
        return message.toString();
    }
}
