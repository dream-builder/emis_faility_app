package org.sci.rhis.utilities;

import android.app.Activity;
import android.view.View;
import android.widget.EditText;

import org.sci.rhis.fwc.R;

/**
 * Created by arafat.hasan on 2/14/2017.
 */

public class CustomFocusChangeListener implements View.OnFocusChangeListener {
    private Activity mActivity;

    public CustomFocusChangeListener(Activity activity) {
        this.mActivity = activity;
    }

    @Override
    public void onFocusChange(View view, boolean b) {
        EditText mEditText = (EditText) mActivity.findViewById(view.getId());
        switch (view.getId()){
            /**
             * bp segment starts.................................................................................................
             */
            case R.id.ancBloodPresserValueSystolic:
            case R.id.pncBloodPresserValue:
            case R.id.gpBloodPresserValueSystolic:
            case R.id.pcBloodPresserValueSystolic:
            case R.id.injectableBPSystolicEditText:
            case R.id.pacBloodPresserValueSystolic:
                if(!mEditText.getText().toString().equals(""))
                {
                    int sysVal = Integer.valueOf(mEditText.getText().toString());

                    if(sysVal>300|| sysVal<50){
                        AlertDialogCreator.SimpleMessageDialog(mActivity,"আপনি রক্তচাপের যে তথ্য দিয়েছেন, তা স্বাভাবিক নয়। অনুগ্রহ করে নিশ্চিত হয়ে তথ্য দিন।","নিশ্চিতকরণ!",android.R.drawable.ic_dialog_alert);
                    }
                }
                break;

            case R.id.ancBloodPresserValueDiastolic:
            case R.id.pncBloodPresserValueD:
            case R.id.gpBloodPresserValueDiastolic:
            case R.id.pcBloodPresserValueDiastolic:
            case R.id.injectableBPDiastolicEditText:
            case R.id.pacBloodPresserValueDiastolic:
                if(!mEditText.getText().toString().equals(""))
                {
                    int diastolVal = Integer.valueOf(mEditText.getText().toString());

                    if(diastolVal>200|| diastolVal<20){
                        AlertDialogCreator.SimpleMessageDialog(mActivity,"আপনি রক্তচাপের যে তথ্য দিয়েছেন, তা স্বাভাবিক নয়। অনুগ্রহ করে নিশ্চিত হয়ে তথ্য দিন।","নিশ্চিতকরণ!",android.R.drawable.ic_dialog_alert);
                    }
                }
                break;
            /**
             * bp segment ends.................................................................................................
             */
        }
    }
}
