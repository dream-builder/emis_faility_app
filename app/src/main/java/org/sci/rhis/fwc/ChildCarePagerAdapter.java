package org.sci.rhis.fwc;

import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;


/**
 * Created by hajjaz.ibrahim on 3/14/2018.
 */

public class ChildCarePagerAdapter extends FragmentStatePagerAdapter {
    int mNumOfTabs;

    public ChildCarePagerAdapter(FragmentManager fm, int NumOfTabs) {
        super(fm);
        this.mNumOfTabs = NumOfTabs;
    }


    @Override
    public Fragment getItem(int position) {

        switch (position) {
            //Child Care Physical Examination Fragment
            case 0:
                ChildCareFragmentPhysicalExamination physicalExamination = new ChildCareFragmentPhysicalExamination();
                return physicalExamination;

            //Child Care Symptom Fragment
            case 1:
                ChildCareFragmentSymptom symptom = new ChildCareFragmentSymptom();
                return symptom;

            //Child Care Preview Fragment
            case 2:
                ChildCareFragmentPreview preview = new ChildCareFragmentPreview();
                return preview;

            //Child Care Classification Fragment
            case 3:
                ChildCareFragmentClassification classification = new ChildCareFragmentClassification();
                return classification;
            //Child Care Management
            case 4:
                ChildCareFragmentManagement management = new ChildCareFragmentManagement();
                return management;
            default:
                return null;
        }
    }
    @Override
    public int getCount() {
        return mNumOfTabs;
    }

}