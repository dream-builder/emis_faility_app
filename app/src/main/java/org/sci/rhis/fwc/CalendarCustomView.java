package org.sci.rhis.fwc;

/**
 * Created by hajjaz.ibrahim on 12/20/2017.
 */

import android.app.Activity;
import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.sci.rhis.db.satellite_session_planning.DBOperationSatellitePlanningInfo;
import org.sci.rhis.model.ProviderInfo;
import org.sci.rhis.utilities.ConstantMaps;
import org.sci.rhis.utilities.CustomSimpleDateFormat;


import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class CalendarCustomView extends LinearLayout {
    private static final String TAG = CalendarCustomView.class.getSimpleName();
    private ImageView previousButton, nextButton;
    private TextView currentDate, textViewStatus, textViewRejectMessage;
    private GridView calendarGridView;
    private static final int MAX_CALENDAR_COLUMN = 42;
    private CustomSimpleDateFormat formatter = new CustomSimpleDateFormat("MMMM yyyy");
    private Calendar calendar;
    private Context context;
    private GridAdapter mAdapter;
    LinearLayout linearLayoutRejectionDetails;


    public CalendarCustomView(Context context) {
        super(context);
    }

    public CalendarCustomView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;

        initializeUILayout();
        setUpCalendarAdapter();
        setPreviousButtonClickEvent();
        setNextButtonClickEvent();
    }

    public CalendarCustomView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void initializeUILayout() {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.calendar_layout, this);
        previousButton = (ImageView) view.findViewById(R.id.previous_month);
        nextButton = (ImageView) view.findViewById(R.id.next_month);
        currentDate = (TextView) view.findViewById(R.id.display_current_date);
        textViewStatus = (TextView) view.findViewById(R.id.textViewPlanningStatus);
        calendarGridView = (GridView) view.findViewById(R.id.calendar_grid);
        //Setting Border in GridView
        calendarGridView.setVerticalSpacing(3);
        calendarGridView.setHorizontalSpacing(3);
        linearLayoutRejectionDetails = (LinearLayout) findViewById(R.id.linearLayoutRejectionMessage);
        textViewRejectMessage = (TextView) findViewById(R.id.textViewRejectionMessage);
    }

    private void setPreviousButtonClickEvent() {
        previousButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (nextButton.getVisibility() != View.VISIBLE) {
                    nextButton.setVisibility(View.VISIBLE);
                }
                calendar.add(Calendar.MONTH, -1);
                setUpCalendarAdapter();
            }
        });
    }

    private void setNextButtonClickEvent() {
        nextButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (previousButton.getVisibility() != View.VISIBLE) {
                    previousButton.setVisibility(View.VISIBLE);
                }
                calendar.add(Calendar.MONTH, 1);
                setUpCalendarAdapter();
            }
        });
    }

    public void setCalendar(Integer year) {
        calendar = Calendar.getInstance(Locale.ENGLISH);
        calendar.set(Calendar.YEAR, year);
        setUpCalendarAdapter();
    }

    public void setUpCalendarAdapter() {
        if (calendar != null) {
            if (calendar.get(Calendar.MONTH) == 0) {
                previousButton.setVisibility(View.INVISIBLE);
            } else if (calendar.get(Calendar.MONTH) == 11) {
                nextButton.setVisibility(View.INVISIBLE);
            }

            List<Date> dayValueInCells = new ArrayList<Date>();

            Calendar mCal = (Calendar) calendar.clone();
            mCal.set(Calendar.DAY_OF_MONTH, 1);
            int firstDayOfTheMonth = mCal.get(Calendar.DAY_OF_WEEK) - 1;
            mCal.add(Calendar.DAY_OF_MONTH, -firstDayOfTheMonth);
            while (dayValueInCells.size() < MAX_CALENDAR_COLUMN) {
                dayValueInCells.add(mCal.getTime());
                mCal.add(Calendar.DAY_OF_MONTH, 1);
            }
            Log.d(TAG, "Number of date " + dayValueInCells.size());
            String sDate = formatter.format(calendar.getTime());
            currentDate.setText(sDate);
            int status = DBOperationSatellitePlanningInfo.getStatus(calendar.get(Calendar.YEAR),
                    calendar.get(Calendar.MONTH) + 1, ProviderInfo.getProvider().getmFacilityId());

            if (status == 0) {
                linearLayoutRejectionDetails.setVisibility(View.GONE);
            }

            /**
             * Hiding /Showing edit & cancel button on ssp status
             * if the status is submitted / approved buttons will be hidden
             * if not it will show the buttons
             * */
            if(status <= 1){
                ((Activity)context).findViewById(R.id.layoutEditSatellitePlanning).setVisibility(View.GONE);
                ((Activity)context).findViewById(R.id.buttonSubmitSatelliteSessionPlanning).setVisibility(View.GONE);
                ((Activity)context).findViewById(R.id.linearLayoutSatelliteHint).setVisibility(View.GONE);
            } else {
                ((Activity)context).findViewById(R.id.layoutEditSatellitePlanning).setVisibility(View.VISIBLE);
                ((Activity)context).findViewById(R.id.buttonSubmitSatelliteSessionPlanning).setVisibility(View.VISIBLE);
                ((Activity)context).findViewById(R.id.linearLayoutSatelliteHint).setVisibility(View.VISIBLE);
            }

            if (status >= 0) {

                if (textViewStatus.getVisibility() != View.VISIBLE) {
                    textViewStatus.setVisibility(View.VISIBLE);
                }
                textViewStatus.setText(ConstantMaps.SateliteStatusTextsMapping.get(status));
                textViewStatus.setTextColor(ConstantMaps.SateliteStatusColorMapping.get(status));

                if (status == 2) {
                    String comment = DBOperationSatellitePlanningInfo.getComments(calendar.get(Calendar.YEAR),
                            calendar.get(Calendar.MONTH) + 1, ProviderInfo.getProvider().getmFacilityId());
                    linearLayoutRejectionDetails.setVisibility(View.VISIBLE);
                    textViewRejectMessage.setText(comment);
                } else {
                    if (linearLayoutRejectionDetails.getVisibility() != View.GONE) {
                        linearLayoutRejectionDetails.setVisibility(View.GONE);
                    }
                }
            } else {
                if (textViewStatus.getVisibility() != View.GONE) {
                    textViewStatus.setVisibility(View.GONE);
                }
            }
            mAdapter = new GridAdapter(context, dayValueInCells, calendar, SatelliteSessionPlanningActivity.getEventObjects(),status);
            calendarGridView.setAdapter(mAdapter);
        }

    }
}
