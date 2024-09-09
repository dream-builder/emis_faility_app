package org.sci.rhis.utilities;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.os.Build;
import android.util.Log;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;

import org.sci.rhis.utilities.CustomSimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by jamil.zaman on 9/2/2015.
 */
public class CustomDatePickerDialog implements DatePickerDialog.OnDateSetListener {
    /*public CustomDatePickerDialog(Context context, OnDateSetListener callBack, int year, int monthOfYear, int dayOfMonth) {
        super(context, callBack, year, monthOfYear, dayOfMonth);
    }*/

    private DatePickerDialog datePickerDialog;
    private Calendar calendar;
    private CustomSimpleDateFormat dateFormat;
    private boolean requiredFutureDate=false;
    private boolean hasMinDate=false;
    private boolean monthOnly=false;
    private Date minDate = null;
    EditText editTextFielId;

    public CustomDatePickerDialog(Context context,  int year, int monthOfYear, int dayOfMonth) {
        calendar = Calendar.getInstance();
        dateFormat = new CustomSimpleDateFormat("dd/MM/yyyy");
        //calendar.set(year, monthOfYear, dayOfMonth);
        //todays, date
        datePickerDialog = new DatePickerDialog(context, this, year, monthOfYear, dayOfMonth);
        editTextFielId = null;
        monthOnly=false;
    }

    public CustomDatePickerDialog(Context context) {
        calendar = Calendar.getInstance();
        dateFormat = new CustomSimpleDateFormat("dd/MM/yyyy");
        //calendar.set(year, monthOfYear, dayOfMonth);
        //todays, date
        createPickerDialog(context);
        editTextFielId = null;
    }
    // this constructor is called if only month is has to be selected
    public CustomDatePickerDialog(Context context, boolean onlyMonth) {
        calendar = Calendar.getInstance();
        dateFormat = new CustomSimpleDateFormat("dd/MM/yyyy");
        createPickerDialog(context);
        editTextFielId = null;
        if(onlyMonth){
            monthOnly = true;

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
                DatePicker datePicker = datePickerDialog.getDatePicker();
                int daySpinnerId = Resources.getSystem().getIdentifier("day", "id", "android");
                if (daySpinnerId != 0)
                {
                    View daySpinner = datePicker.findViewById(daySpinnerId);
                    if (daySpinner != null)
                    {
                        daySpinner.setVisibility(View.GONE);
                    }
                }

                int monthSpinnerId = Resources.getSystem().getIdentifier("month", "id", "android");
                if (monthSpinnerId != 0)
                {
                    View monthSpinner = datePicker.findViewById(monthSpinnerId);
                    if (monthSpinner != null)
                    {
                        monthSpinner.setVisibility(View.VISIBLE);
                    }
                }

                int yearSpinnerId = Resources.getSystem().getIdentifier("year", "id", "android");
                if (yearSpinnerId != 0)
                {
                    View yearSpinner = datePicker.findViewById(yearSpinnerId);
                    if (yearSpinner != null)
                    {
                        yearSpinner.setVisibility(View.VISIBLE);
                    }
                }
            }else{
                try {
                    java.lang.reflect.Field[] datePickerDialogFields = datePickerDialog.getClass().getDeclaredFields();
                    for (java.lang.reflect.Field datePickerDialogField : datePickerDialogFields) {
                        if (datePickerDialogField.getName().equals("mDatePicker")) {
                            datePickerDialogField.setAccessible(true);
                            DatePicker datePicker = (DatePicker) datePickerDialogField.get(datePickerDialog);
                            java.lang.reflect.Field[] datePickerFields = datePickerDialogField.getType().getDeclaredFields();
                            for (java.lang.reflect.Field datePickerField : datePickerFields) {
                                Log.i("test", datePickerField.getName());
                                if ("mDaySpinner".equals(datePickerField.getName())) {
                                    datePickerField.setAccessible(true);
                                    Object dayPicker = datePickerField.get(datePicker);
                                    ((View) dayPicker).setVisibility(View.GONE);
                                }
                            }
                        }
                    }
                }
                catch (Exception ex) {
                }
            }

            datePickerDialog.setTitle("Set Month and Year");

        }

    }

    public CustomDatePickerDialog(Context context, CustomSimpleDateFormat simpleDateFormat) {
        calendar = Calendar.getInstance();
        dateFormat = simpleDateFormat;
        //calendar.set(year, monthOfYear, dayOfMonth);
        //todays, date
        createPickerDialog(context);
        editTextFielId = null;
    }

    //May not be safe, no format validity checking
    public CustomDatePickerDialog(Context context, String dateFormatString) throws NullPointerException {
        calendar = Calendar.getInstance();
        if(dateFormatString == null) {
            throw new NullPointerException();
        }
        try {
            dateFormat = new CustomSimpleDateFormat(dateFormatString);
        } catch (IllegalArgumentException IE) {
            Log.e("Utility", "format string is not recognized using default format dd/MM/yyyy");
            dateFormat = new CustomSimpleDateFormat("dd/MM/yyyy");
        }
        //calendar.set(year, monthOfYear, dayOfMonth);
        //todays, date
        createPickerDialog(context);

        editTextFielId = null;
    }

    //Can be called if future date is required..........
    public CustomDatePickerDialog(Context context, String dateFormatString, boolean requireFutureDate) throws NullPointerException {
        if(requireFutureDate){
            requiredFutureDate = true;
        }
        calendar = Calendar.getInstance();
        if(dateFormatString == null) {
            throw new NullPointerException();
        }
        try {
            dateFormat = new CustomSimpleDateFormat(dateFormatString);
        } catch (IllegalArgumentException IE) {
            Log.e("Utility", "format string is not recognized using default format dd/MM/yyyy");
            dateFormat = new CustomSimpleDateFormat("dd/MM/yyyy");
        }
        //calendar.set(year, monthOfYear, dayOfMonth);
        //todays, date
        createPickerDialog(context);
        editTextFielId = null;


    }

    //Can be called if future date is required..........
    public CustomDatePickerDialog(Context context, String dateFormatString, Date lastPastDate) throws NullPointerException {
        if(lastPastDate!=null){
            hasMinDate = true;
            minDate = lastPastDate;
        }
        calendar = Calendar.getInstance();
        if(dateFormatString == null) {
            throw new NullPointerException();
        }
        try {
            dateFormat = new CustomSimpleDateFormat(dateFormatString);
        } catch (IllegalArgumentException IE) {
            Log.e("Utility", "format string is not recognized using default format dd/MM/yyyy");
            dateFormat = new CustomSimpleDateFormat("dd/MM/yyyy");
        }
        //calendar.set(year, monthOfYear, dayOfMonth);
        //todays, date
        createPickerDialog(context);
        editTextFielId = null;


    }

    @Override
    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
        if(monthOnly){
            if(editTextFielId != null) {//if something is set
                editTextFielId.setText((monthOfYear+1)+"/"+year);
            }
        }else{
            calendar.set(year, monthOfYear, dayOfMonth);
            if(editTextFielId != null) {//if something is set
                editTextFielId.setText(dateFormat.format(calendar.getTime()));
            }
        }

        editTextFielId = null;
    }

    public void show(EditText editTextFieldId ) {
        editTextFielId = editTextFieldId;
        datePickerDialog.show();
    }

    public void showWithCustomButton(EditText editTextFieldId, String negativeBtnText, String positiveBtnText ) {
        editTextFielId = editTextFieldId;
        datePickerDialog.setButton(DialogInterface.BUTTON_NEGATIVE,
                negativeBtnText,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog,
                                        int which) {
                        if (which == DialogInterface.BUTTON_NEGATIVE) {
                            dialog.cancel();
                            editTextFielId.setText("");
                        }
                    }
                });
        datePickerDialog.setButton(DatePickerDialog.BUTTON_POSITIVE,positiveBtnText,datePickerDialog);
        datePickerDialog.show();
    }

    private void createPickerDialog(Context context){
        calendar = Calendar.getInstance();
        datePickerDialog = new DatePickerDialog(
                context,
                AlertDialog.THEME_HOLO_LIGHT,
                this,
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
        );
        if(hasMinDate){
            //setting minimum date
            if(minDate!=null && !minDate.after(new Date())){
                datePickerDialog.getDatePicker().setMinDate(minDate.getTime());
                datePickerDialog.getDatePicker().setCalendarViewShown(false);
            }
        }
        if(!requiredFutureDate){
            //setting current date as max date to block future date...............
            datePickerDialog.getDatePicker().setMaxDate(new Date().getTime());
        }
    }
}
