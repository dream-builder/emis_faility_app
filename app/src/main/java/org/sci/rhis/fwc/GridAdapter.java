package org.sci.rhis.fwc;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;

import android.content.DialogInterface;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.sci.rhis.model.LocationHolder;
import org.sci.rhis.model.ProviderInfo;
import org.sci.rhis.model.SatelliteSessionEvents;
import org.sci.rhis.utilities.Validation;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Created by hajjaz.ibrahim on 12/20/2017.
 */


public class GridAdapter extends ArrayAdapter {
    private LayoutInflater mInflater;
    private List<Date> monthlyDates;
    private Calendar currentDate;
    ArrayList<LocationHolder> villageList;
    private List<SatelliteSessionEvents> allEvents;
    Context context = null;
    int status;

    private ProviderInfo provider = ProviderInfo.getProvider();

    public GridAdapter(Context context, List<Date> monthlyDates, Calendar currentDate, List<SatelliteSessionEvents> allEvents, int status) {
        super(context, R.layout.single_cell_layout);
        this.monthlyDates = monthlyDates;
        this.currentDate = currentDate;
        this.allEvents = allEvents;
        mInflater = LayoutInflater.from(context);
        this.context = context;
        villageList = new ArrayList<>();
        this.status = status;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        SatelliteSessionPlanningActivity.clicked = false;
        Date mDate = monthlyDates.get(position);
        Calendar dateCal = Calendar.getInstance();
        dateCal.setTime(mDate);
        final int dayValue = dateCal.get(Calendar.DAY_OF_MONTH);
        final int displayMonth = dateCal.get(Calendar.MONTH) + 1;
        final int displayDay = dateCal.get(Calendar.DAY_OF_WEEK);
        final int displayYear = dateCal.get(Calendar.YEAR);
        int currentMonth = currentDate.get(Calendar.MONTH) + 1;
        int currentYear = currentDate.get(Calendar.YEAR);
        View view = convertView;
        if (view == null) {
            view = mInflater.inflate(R.layout.single_cell_layout, parent, false);
        }

        //Add day to calendar
        final TextView cellNumber = (TextView) view.findViewById(R.id.calendar_date_id);
        //Add events to the calendar
        final TextView eventIndicator = (TextView) view.findViewById(R.id.event_id);
        final TextView satelliteDetailText = (TextView) view.findViewById(R.id.ll_id);
        //Calendar cell with preview data
        final LinearLayout calendarCell = (LinearLayout) view.findViewById(R.id.calendar_cell);

        if (displayMonth == currentMonth && displayYear == currentYear) {
            //view.setBackgroundColor(Color.parseColor("#7ec0ee"));
            view.setBackgroundColor(Color.parseColor("#ffffff"));
            if (displayDay == 6) {
                cellNumber.setEnabled(false);
                cellNumber.setTextColor(Color.RED);
            }

            /**
             * Disabling/enabling calendar edit mode based on status
             * if the status is submitted / approved calendar will be disabled for editing
             * else it will be available for edit mode
             * */
            if (status <= 1) {
                calendarCell.setOnClickListener(null);

            } else {
                calendarCell.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Calendar c = Calendar.getInstance();
                        c.set(Calendar.YEAR, displayYear);
                        c.set(Calendar.MONTH, displayMonth - 1);
                        c.set(Calendar.DAY_OF_MONTH, dayValue);
                        final Date dateObj = c.getTime();

                        String s = eventIndicator.getText().toString();
                        if (!s.isEmpty() && s != null && !SatelliteSessionPlanningActivity.clicked) {

                            AlertDialog.Builder adb = new AlertDialog.Builder(context, AlertDialog.THEME_HOLO_DARK);
                            adb.setTitle(R.string.satellitePreview);
                            //adb.setMessage(satelliteDetails);
                            adb.setMessage(satelliteDetailText.getText().toString());
                            adb.setIcon(android.R.drawable.ic_dialog_info);

                            if (SatelliteSessionPlanningActivity.editModeOn) {
                                adb.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        SatelliteSessionPlanningActivity.removeEvent(dateObj);
                                        eventIndicator.setText("");
                                        eventIndicator.setBackgroundColor(Color.parseColor("#ffffff"));
                                        cellNumber.setBackgroundColor(Color.parseColor("#ffffff"));
                                    }
                                });
                            }

                            adb.setNegativeButton("Okay", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {

                                }
                            });
                            adb.show();

                        } else {
                            if (SatelliteSessionPlanningActivity.editModeOn) {

                                LayoutInflater inflater = ((Activity) context).getLayoutInflater();
                                View dialogView = inflater.inflate(R.layout.single_satelite_entry, null);

                                AlertDialog dialog = new AlertDialog.Builder(context)
                                        .setView(dialogView)
                                        .setTitle("নতুন স্যাটেলাইটের তথ্য দিন")
                                        .setPositiveButton(android.R.string.ok, null) //Set to null. We override the onclick
                                        .setNegativeButton(android.R.string.cancel, null)
                                        .create();

                                //setting village spinner
                                ArrayAdapter<LocationHolder> villageAdapter = new ArrayAdapter(context, android.R.layout.simple_spinner_item);
                                villageAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

                                villageAdapter.clear();

                                SatelliteSessionPlanningActivity.loadVillageFromJson((provider.getZillaID()).split("_")[0], provider.getUpazillaID(), provider.getUnionID(), villageList);


                                villageAdapter.addAll(villageList);
                                ((Spinner) dialogView.findViewById(R.id.spinnerSateliteVillage)).setAdapter(villageAdapter);

                                dialog.setOnShowListener(new DialogInterface.OnShowListener() {

                                    @Override
                                    public void onShow(DialogInterface dialogInterface) {

                                        Button okButton = ((AlertDialog) dialog).getButton(AlertDialog.BUTTON_POSITIVE);
                                        Button cancelButton = ((AlertDialog) dialog).getButton(AlertDialog.BUTTON_NEGATIVE);
                                        cancelButton.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View view) {
                                                dialog.dismiss();
                                            }
                                        });
                                        okButton.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View view) {
                                                EditText satelliteNameET = (EditText) dialogView.findViewById(R.id.editTextSatelliteName);
                                                boolean valid = true;
                                                if (!Validation.hasText(satelliteNameET))
                                                    valid = false;
                                                if (!valid) {
                                                    Toast toast = Toast.makeText(context, R.string.SatelliteNameWarning, Toast.LENGTH_LONG);
                                                    LinearLayout toastLayout = (LinearLayout) toast.getView();
                                                    TextView toastTV = (TextView) toastLayout.getChildAt(0);
                                                    toastTV.setTextSize(20);
                                                    toast.show();
                                                } else {
                                                    processSatelliteData(dialogView, dialog, dateObj, eventIndicator, cellNumber);
                                                }
                                            }
                                        });
                                    }
                                });
                                dialog.show();
                            }
                        }
                        //}
                    }
                });
            }


        } else {
            view.setBackgroundColor(Color.parseColor("#cccccc"));
        }

        cellNumber.setText(String.valueOf(dayValue));

        //Showing satellite details in calendar
        Calendar eventCalendar = Calendar.getInstance();
        for (int i = 0; i < SatelliteSessionPlanningActivity.getEventObjects().size(); i++) {
            SatelliteSessionEvents obj = SatelliteSessionPlanningActivity.getEventObjects().get(i);
            eventCalendar.setTime(SatelliteSessionPlanningActivity.getEventObjects().get(i).getDate());

            if (dayValue == eventCalendar.get(Calendar.DAY_OF_MONTH) && displayMonth == eventCalendar.get(Calendar.MONTH) + 1
                    && displayYear == eventCalendar.get(Calendar.YEAR)) {
                if (displayMonth == currentMonth && displayYear == currentYear) {
                    //for displaying in the preview
                    eventIndicator.setText(SatelliteSessionPlanningActivity.getEventObjects().get(i).getSatellite()+
                            "\n" + SatelliteSessionPlanningActivity.getEventObjects().get(i).getStrVillage());
                    //for displaying in the details
                    satelliteDetailText.setText(" গ্রাম: "+SatelliteSessionPlanningActivity.getEventObjects().get(i).getStrVillage() +
                            "\n স্যাটেলাইট নাম: "+SatelliteSessionPlanningActivity.getEventObjects().get(i).getSatellite()+
                            "\n FWA নাম(আইডি): "+SatelliteSessionPlanningActivity.getEventObjects().get(i).getFWAName()+
                                    " ("+SatelliteSessionPlanningActivity.getEventObjects().get(i).getStrFWAId()+")");

                    eventIndicator.setBackgroundColor(Color.parseColor("#a6f9ff"));
                    eventIndicator.setTextColor(Color.BLACK);
                    cellNumber.setBackgroundColor(Color.parseColor("#a6f9ff"));
                }
            }
        }

        return view;
    }

    /**
     * Extra Satellite Details adding Segment in Dialog
     * With Satellite name mandatory validation
     */
    private void processSatelliteData(View dialogView, AlertDialog dialog, Date dateObj, TextView eventIndicator, TextView cellNumber) {
        EditText satelliteNameET = (EditText) dialogView.findViewById(R.id.editTextSatelliteName);
        EditText fwaNameET = (EditText) dialogView.findViewById(R.id.editTextSatelliteFWAName);
        EditText fwaIdET = (EditText) dialogView.findViewById(R.id.editTextSatelliteFWAId);
        String mouzaVillageIdPair = ((LocationHolder) ((Spinner) dialogView.findViewById(R.id.spinnerSateliteVillage)).getSelectedItem()).getCode();
        String villageName = ((Spinner) dialogView.findViewById(R.id.spinnerSateliteVillage)).getSelectedItem().toString();
        boolean validateForm = true;
        if (!Validation.hasText(satelliteNameET)) validateForm = false;
        /*if (!Validation.hasText(fwaNameET)) validateForm = false;
        if (!Validation.hasText(fwaIdET)) validateForm = false;*/
        if (validateForm) {
            String sateliteName = satelliteNameET.getEditableText().toString();
            String fwaName = fwaNameET.getEditableText().toString();
            String fwaId = fwaIdET.getEditableText().toString();

            SatelliteSessionPlanningActivity.addEvent(sateliteName, dateObj, fwaName, fwaId,
                    mouzaVillageIdPair.split("_")[1], mouzaVillageIdPair.split("_")[0],
                    villageName);

            eventIndicator.setText(sateliteName +
                    "\n" + villageName);


            eventIndicator.setBackgroundColor(Color.parseColor("#a6f9ff"));
            eventIndicator.setTextColor(Color.BLACK);
            cellNumber.setBackgroundColor(Color.parseColor("#a6f9ff"));
            SatelliteSessionPlanningActivity.clicked = false;
            dialog.dismiss();
        }
    }

    @Override
    public int getCount() {
        return monthlyDates.size();
    }

    @Nullable
    @Override
    public Object getItem(int position) {
        return monthlyDates.get(position);
    }

    @Override
    public int getPosition(Object item) {
        return monthlyDates.indexOf(item);
    }

}