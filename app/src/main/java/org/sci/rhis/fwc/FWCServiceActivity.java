package org.sci.rhis.fwc;

import android.content.res.Configuration;
import android.graphics.Rect;
import android.os.Build;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TableRow;
import android.widget.TextView;

import org.sci.rhis.connectivityhandler.AsyncCallback;
import org.sci.rhis.utilities.ConstantMaps;
import org.sci.rhis.utilities.Flag;
import org.sci.rhis.utilities.MethodUtils;
import org.sci.rhis.utilities.SharedPref;

import java.util.Locale;

/**
 * Created by jamil.zaman on 8/29/2015.
 */
public abstract class FWCServiceActivity extends AppCompatActivity implements AsyncCallback {
    protected CheckBox getCheckbox(int id) {
        return (CheckBox)findViewById(id);
    }

    protected Spinner getSpinner(int id) {
        return (Spinner)findViewById(id);
    }

    protected MultiSelectionSpinner getMultiSelectionSpinner(int id) {
        return (MultiSelectionSpinner)findViewById(id);
    }

    protected RadioGroup getRadioGroup(int id) {
        return (RadioGroup)findViewById(id);
    }

    protected RadioButton getRadioButton(int id) {
        return (RadioButton)findViewById(id);
    }

    protected Button getButton(int id) {
        return (Button)findViewById(id);
    }

    protected EditText getEditText(int id) {
        return (EditText)findViewById(id);
    }

    protected TextView getTextView(int id) {
        return (TextView)findViewById(id);
    }

    protected ImageView getImageView(int id) {
        return (ImageView)findViewById(id);
    }

    protected LinearLayout getLinearLayout(int id) {
        return (LinearLayout)findViewById(id);
    }
    protected RelativeLayout getRelativeLayout(int id) {
        return (RelativeLayout)findViewById(id);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        int langPref = SharedPref.getSelectedLang(getApplicationContext());
        if(langPref>=0){
            String localeCode = ConstantMaps.localeMapping.get(Flag.langauageArray[langPref]);
            Locale loc = new Locale(localeCode);
            Configuration config = new Configuration();
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                config.setLocale(loc);
                getBaseContext().getResources().updateConfiguration(config,
                        getBaseContext().getResources().getDisplayMetrics());
            }
        }else{
            Locale loc = new Locale(Flag.LANG_BENGALI_CODE);
            Configuration config = new Configuration();
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                config.setLocale(loc);
                getBaseContext().getResources().updateConfiguration(config,
                        getBaseContext().getResources().getDisplayMetrics());
            }
        }
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            View v = getCurrentFocus();
            if ( v instanceof EditText) {
                Rect outRect = new Rect();
                v.getGlobalVisibleRect(outRect);
                if (!outRect.contains((int)event.getRawX(), (int)event.getRawY())) {
                    v.clearFocus();
                    MethodUtils.hideSoftKeyboard(this);
                }
            }
        }
        return super.dispatchTouchEvent( event );
    }
}
