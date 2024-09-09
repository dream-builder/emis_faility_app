package org.sci.rhis.utilities;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TextView;

import org.sci.rhis.db.dbhelper.DatabaseWrapper;
import org.sci.rhis.fwc.PersonAdapter;
import org.sci.rhis.fwc.R;
import org.sci.rhis.model.Person;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by arafat.hasan on 2/7/2016.
 */

public class AlertDialogCreator {

    public interface DialogButtonClickedListener {
        void onPositiveButtonClicked(DialogInterface dialog);
        void onNegativeButtonClicked(DialogInterface dialog);
        void onNeutralButtonClicked(DialogInterface dialog);
    }

    public interface SingleChoiceListener {
        void onChoiceSelected(DialogInterface dialog,int selection);
    }

    public interface SingleInputListener {
        void onNeutralButtonClicked(DialogInterface dialog, String input);
    }

    public interface ListViewItemClickListener {
        void onListItemClicked(DialogInterface dialog, int position);
    }

    /**
     * This method is to create common dialog during exiting activity
     * @param eContext
     * @param messagePart
     */
    public static void ExitActivityDialog(Context eContext, String messagePart){
        final Context mContext = eContext;
        AlertDialog alertDialog = new AlertDialog.Builder(eContext).create();
        alertDialog.getWindow().getAttributes().windowAnimations=R.style.dialog_animation;

        alertDialog.setTitle("EXIT CONFIRMATION");
        alertDialog.setMessage("আপনি কি "+messagePart+" থেকে বের হয়ে যেতে চান? \nনিশ্চিত করতে OK চাপুন, ফিরে যেতে CANCEL চাপুন ");

        alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "CANCEL",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        ((Activity) mContext).finish();
                    }
                });

        alertDialog.show();
    }

    public static void ExitActivityDialog(Context eContext, final boolean isCSBA){
        final Context mContext = eContext;

        AlertDialog alertDialog = new AlertDialog.Builder(eContext).create();
        alertDialog.getWindow().getAttributes().windowAnimations=R.style.dialog_animation;

        alertDialog.setTitle("LOGOUT CONFIRMATION");
        alertDialog.setMessage("আপনি কি বের হয়ে যেতে চান? \nনিশ্চিত করতে OK চাপুন, ফিরে যেতে CANCEL চাপুন ");



        alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "CANCEL",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        if (isCSBA){
                            try {
                                new DatabaseWrapper(mContext).switchdatabase();

                            } catch (IOException ioe) {
                                Log.e("FWC-LOGIN", "Cannot open FWC DATABASE");
                                new AlertDialog.Builder(mContext)
                                        .setMessage("Cannot open FWC DATABASE !")
                                        .setIcon(android.R.drawable.ic_dialog_alert)
                                        .show();
                                System.exit(1);
                            }
                        }

                        ((Activity) mContext).finish();

                    }
                });

        alertDialog.show();
    }

    public static void ExitActivityDialogWithResult(Context eContext, String messagePart,
                                                    final int resultCode, final int activityResultCode){
        final Context mContext = eContext;
        AlertDialog alertDialog = new AlertDialog.Builder(eContext).create();
        alertDialog.getWindow().getAttributes().windowAnimations=R.style.dialog_animation;
        alertDialog.setTitle("EXIT CONFIRMATION");
        alertDialog.setMessage("আপনি কি "+messagePart+" থেকে বের হয়ে যেতে চান? \nনিশ্চিত করতে OK চাপুন, ফিরে যেতে CANCEL চাপুন ");

        alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "CANCEL",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        Intent intent = new Intent();
                        ((Activity) mContext).setResult(resultCode, intent);
                        ((Activity) mContext).finishActivity(activityResultCode);
                        ((Activity) mContext).finish();
                    }
                });

        alertDialog.show();
    }

    public static void SimpleMessageDialog(Context eContext, String message,String title, int iconId){
        AlertDialog alertDialog = new AlertDialog.Builder(eContext, AlertDialog.THEME_HOLO_DARK).create();
        alertDialog.getWindow().getAttributes().windowAnimations=R.style.dialog_animation2;
        alertDialog.setMessage(message);
        alertDialog.setTitle(title);
        alertDialog.setIcon(iconId);
        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

        alertDialog.show();
    }

    public static void SimpleMessageWithNoTitle(Context eContext, String message){
        AlertDialog alertDialog = new AlertDialog.Builder(eContext).create();
        alertDialog.getWindow().getAttributes().windowAnimations=R.style.dialog_animation2;
        alertDialog.setMessage("\n"+message+"\n");
        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

        alertDialog.show();
        TextView msgTxt = (TextView) alertDialog.findViewById(android.R.id.message);
        msgTxt.setTextSize(22);
    }

    public static void SimpleScrollableMessageDialog(Context eContext, String message, String title, int iconId){
        AlertDialog alertDialog = new AlertDialog.Builder(eContext).create();
        alertDialog.getWindow().getAttributes().windowAnimations=R.style.dialog_animation2;
        alertDialog.setTitle(title);
        alertDialog.setIcon(iconId);
        alertDialog.setCancelable(false);
        LayoutInflater inflater = (LayoutInflater) eContext.getSystemService( Context.LAYOUT_INFLATER_SERVICE );
        View view = inflater.inflate( R.layout.view_scrollable_message, null );
        alertDialog.setView(view);
        TextView messageTv = (TextView) view.findViewById(R.id.textViewDialogMessage);
        messageTv.setText(message);
        messageTv.setMovementMethod(new ScrollingMovementMethod());
        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

        alertDialog.show();
    }

    public static void SimpleDecisionDialog(Context eContext, String message, String title, int iconId){
        final Activity calleeActivity = (Activity)eContext;
        AlertDialog alertDialog = new AlertDialog.Builder(eContext).create();
        alertDialog.getWindow().getAttributes().windowAnimations=R.style.dialog_animation;
        alertDialog.setMessage(message);
        alertDialog.setTitle(title);
        alertDialog.setIcon(iconId);
        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "OK",
                (dialog, which) -> {
                    //dialog.dismiss();
                    ((DialogButtonClickedListener)calleeActivity).onPositiveButtonClicked(dialog);
                });
        alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "CANCEL",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        //dialog.dismiss();
                        ((DialogButtonClickedListener)calleeActivity).onNegativeButtonClicked(dialog);
                    }
                });

        alertDialog.show();
    }

    public static void SpecialDecisionDialog(Context eContext, String message, String title, int iconId, String[] buttonTags, boolean isCancelable){
        final Activity calleeActivity = (Activity)eContext;
        final String[] mButtonTags = buttonTags;
        AlertDialog.Builder builder = new AlertDialog.Builder(eContext);
        builder.setMessage(message);
        builder.setTitle(title);
        builder.setIcon(iconId);
        builder.setCancelable(isCancelable);
        builder.setPositiveButton(mButtonTags[0],null);
        if(mButtonTags.length>1){
            builder.setNegativeButton(mButtonTags[1],null);
        }
        if (mButtonTags.length>2){
            builder.setNeutralButton(buttonTags[2],null);
        }

        final AlertDialog alertDialog = builder.create();
        alertDialog.setOnShowListener(new DialogInterface.OnShowListener() {

            @Override
            public void onShow(DialogInterface dialog) {
                final DialogInterface mDialog= dialog;
                alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View view) {
                        ((DialogButtonClickedListener)calleeActivity).onPositiveButtonClicked(mDialog);
                    }
                });
                if(mButtonTags.length>1){
                    alertDialog.getButton(AlertDialog.BUTTON_NEGATIVE).setOnClickListener(new View.OnClickListener() {

                        @Override
                        public void onClick(View view) {
                            ((DialogButtonClickedListener)calleeActivity).onNeutralButtonClicked(mDialog);
                        }
                    });
                }
                if(mButtonTags.length>2){
                    alertDialog.getButton(AlertDialog.BUTTON_NEUTRAL).setOnClickListener(new View.OnClickListener() {

                        @Override
                        public void onClick(View view) {
                            ((DialogButtonClickedListener)calleeActivity).onNegativeButtonClicked(mDialog);
                        }
                    });
                }
            }
        });
        alertDialog.getWindow().getAttributes().windowAnimations=R.style.dialog_animation;
        alertDialog.show();
        TextView msgTxt = (TextView) alertDialog.findViewById(android.R.id.message);
        msgTxt.setTextSize(22);
    }

    public static void SpecialDecisionDialog(Context eContext, String message, String title, int iconId, String[] buttonTags, boolean isCancelable, int customTheme, int customIconColor, int customTitleColor){
        final Activity calleeActivity = (Activity)eContext;
        final String[] mButtonTags = buttonTags;
        AlertDialog.Builder builder;
        if (customTheme != 0) {
            builder = new AlertDialog.Builder(eContext, customTheme);
        } else {
            builder = new AlertDialog.Builder(eContext);
        }

        builder.setMessage(message);
        builder.setTitle(title);
        builder.setIcon(iconId);
        builder.setCancelable(isCancelable);
        builder.setPositiveButton(mButtonTags[0],null);
        if(mButtonTags.length>1){
            builder.setNegativeButton(mButtonTags[1],null);
        }
        if (mButtonTags.length>2){
            builder.setNeutralButton(buttonTags[2],null);
        }

        final AlertDialog alertDialog = builder.create();
        alertDialog.setOnShowListener(new DialogInterface.OnShowListener() {

            @Override
            public void onShow(DialogInterface dialog) {
                final DialogInterface mDialog= dialog;
                alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View view) {
                        ((DialogButtonClickedListener)calleeActivity).onPositiveButtonClicked(mDialog);
                    }
                });
                if(mButtonTags.length>1){
                    alertDialog.getButton(AlertDialog.BUTTON_NEGATIVE).setOnClickListener(new View.OnClickListener() {

                        @Override
                        public void onClick(View view) {
                            ((DialogButtonClickedListener)calleeActivity).onNegativeButtonClicked(mDialog);
                        }
                    });
                }
                if(mButtonTags.length>2){
                    alertDialog.getButton(AlertDialog.BUTTON_NEUTRAL).setOnClickListener(new View.OnClickListener() {

                        @Override
                        public void onClick(View view) {
                            ((DialogButtonClickedListener)calleeActivity).onNeutralButtonClicked(mDialog);
                        }
                    });
                }
            }
        });
        alertDialog.getWindow().getAttributes().windowAnimations=R.style.dialog_animation;
        alertDialog.show();

        if (customTitleColor != 0) {
            int textViewId = eContext.getResources().getIdentifier("android:id/alertTitle", null, null);
            TextView titleTxt = (TextView) alertDialog.findViewById(textViewId);
            titleTxt.setTextColor(customTitleColor);
        }

        TextView msgTxt = (TextView) alertDialog.findViewById(android.R.id.message);
        msgTxt.setTextSize(22);

        if (customIconColor != 0) {
            ImageView imageView  = (ImageView) alertDialog.findViewById(android.R.id.icon);
            imageView.setColorFilter(customIconColor, android.graphics.PorterDuff.Mode.SRC_IN);
        }
    }

    public static void SpecialDecisionCustomDialog(Context eContext, String message, String title, String footerText,
                                                   int iconId, String[] buttonTags, boolean isCancelable){
        final Activity calleeActivity = (Activity)eContext;
        final String[] mButtonTags = buttonTags;
        AlertDialog.Builder builder = new AlertDialog.Builder(eContext);
        builder.setTitle(title);
        builder.setIcon(iconId);
        builder.setCancelable(isCancelable);
        LayoutInflater inflater = (LayoutInflater) eContext.getSystemService( Context.LAYOUT_INFLATER_SERVICE );
        View view = inflater.inflate( R.layout.view_scrollable_message, null );
        builder.setView(view);
        TextView messageTv = (TextView) view.findViewById(R.id.textViewDialogMessage);
        messageTv.setText(message);
        TextView footerTv = (TextView) view.findViewById(R.id.textViewDialogMessageFooter);
        footerTv.setText(footerText);
        builder.setPositiveButton(mButtonTags[0],null);
        if(mButtonTags.length>1){
            builder.setNegativeButton(mButtonTags[1],null);
        }
        if (mButtonTags.length>2){
            builder.setNeutralButton(buttonTags[2],null);
        }

        final AlertDialog alertDialog = builder.create();
        alertDialog.setOnShowListener(new DialogInterface.OnShowListener() {

            @Override
            public void onShow(DialogInterface dialog) {
                final DialogInterface mDialog= dialog;
                alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View view) {
                        ((DialogButtonClickedListener)calleeActivity).onPositiveButtonClicked(mDialog);
                    }
                });
                if(mButtonTags.length>1){
                    alertDialog.getButton(AlertDialog.BUTTON_NEGATIVE).setOnClickListener(new View.OnClickListener() {

                        @Override
                        public void onClick(View view) {
                            ((DialogButtonClickedListener)calleeActivity).onNeutralButtonClicked(mDialog);
                        }
                    });
                }
                if(mButtonTags.length>2){
                    alertDialog.getButton(AlertDialog.BUTTON_NEUTRAL).setOnClickListener(new View.OnClickListener() {

                        @Override
                        public void onClick(View view) {
                            ((DialogButtonClickedListener)calleeActivity).onNegativeButtonClicked(mDialog);
                        }
                    });
                }
            }
        });
        alertDialog.getWindow().getAttributes().windowAnimations=R.style.dialog_animation;
        alertDialog.show();
    }

    public static void showSingleChoiceDialog(Context eContext, String[] choiceList, String title){
        final Activity calleeActivity = (Activity)eContext;
        AlertDialog.Builder builder = new AlertDialog.Builder(eContext);
        builder.setTitle(title);
        builder.setCancelable(false);
        builder.setSingleChoiceItems(choiceList, -1,new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                ((SingleChoiceListener)calleeActivity).onChoiceSelected(dialog,which);
                dialog.cancel();
            }
        });
        AlertDialog alertDialog =  builder.create();
        alertDialog.show();
    }
    /**
     * This method can create input dialog box and get

     */
    public static void showSpecialInputDialog(Context eContext, String title, final boolean singleInputOnly){
        final Activity calleeActivity = (Activity)eContext;
        AlertDialog.Builder builder = new AlertDialog.Builder(eContext);
        builder.setTitle(title);
        builder.setNeutralButton("Ok",null);
        builder.setNegativeButton("Cancel",null);
        LayoutInflater li = LayoutInflater.from(eContext);
        View promptsView = li.inflate(R.layout.dialog_singe_input, null);
        final EditText userInput = (EditText) promptsView.findViewById(R.id.editTextInput1Dialog);
        final EditText userInput2 = (EditText) promptsView.findViewById(R.id.editTextInput2Dialog);
        final Spinner userInput3 = (Spinner) promptsView.findViewById(R.id.spinnerUserInput3Dialog);
        if(singleInputOnly){

        }
        builder.setView(promptsView);

        final AlertDialog alertDialog =  builder.create();
        alertDialog.setOnShowListener(new DialogInterface.OnShowListener() {

            @Override
            public void onShow(DialogInterface dialog) {
                final DialogInterface mDialog= dialog;
                alertDialog.getButton(AlertDialog.BUTTON_NEUTRAL).setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View view) {
                        if(Validation.hasText(userInput) && Validation.hasText(userInput2))
                        {
                            String input, input1, input2 = "";
                            input1 = userInput.getEditableText().toString();
                            int input3 = -1;
                            if (!singleInputOnly) {
                                input2 = userInput2.getEditableText().toString();
                                input3 = userInput3.getSelectedItemPosition() + 1;
                            }
                            input = singleInputOnly ? input1 : ("(" + input1 + "," + input2 + "," + input3 + ")");
                            ((SingleInputListener) calleeActivity).onNeutralButtonClicked(mDialog, input);
                            mDialog.dismiss();
                        }
                        else{
                            Utilities.showAlertToast(calleeActivity,"দয়া করে সদস্যের সকল তথ্য প্রদান করে Ok চাপুন");
                        }
                    }
                });
                alertDialog.getButton(AlertDialog.BUTTON_NEGATIVE).setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View view) {
                        mDialog.dismiss();
                    }
                });
            }
        });

        alertDialog.show();
    }

    public static void ListViewDialog(Context eContext, String message, String title, int iconId, ArrayList<Person> personsList){
        AlertDialog alertDialog = new AlertDialog.Builder(eContext, AlertDialog.THEME_HOLO_DARK).create();
        alertDialog.getWindow().getAttributes().windowAnimations=R.style.dialog_animation;
        alertDialog.setTitle(title);
        alertDialog.setIcon(iconId);

        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "OK", (dialog, which) -> {
            ((DialogButtonClickedListener)eContext).onPositiveButtonClicked(dialog);
        });
        alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "CANCEL", (dialog, which) -> {
            ((DialogButtonClickedListener)eContext).onNegativeButtonClicked(dialog);
        });

        LayoutInflater inflater = (LayoutInflater) eContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View customView = inflater.inflate(R.layout.alert_dialog_list_view, null, false);

        ((TextView)customView.findViewById(R.id.tv_message)).setText(message);
        ((TextView)customView.findViewById(R.id.txtHeader)).setText(eContext.getString(R.string.string_search_result) +
                Utilities.ConvertNumberToBangla(String.valueOf(personsList.size())) + eContext.getString(R.string.string_search_result_person));

        ListView listView = (ListView) customView.findViewById(R.id.lv_search_result);
        PersonAdapter personAdapter = new PersonAdapter(eContext, R.layout.radio_list_item, personsList, true);
        listView.setAdapter(personAdapter);

        alertDialog.setView(customView);

        listView.setOnItemClickListener((parent, view, position, id) -> {
            personAdapter.setSelectedPosition(position);

            RadioButton radioButton = (RadioButton) view.findViewById(R.id.radio_list_item_select);
            radioButton.setChecked(true);

            alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(true);

            ((ListViewItemClickListener)eContext).onListItemClicked(alertDialog, position);
        });

        alertDialog.show();
        alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(false);
    }

    public static void showDateInputDialog(Context eContext, String title){
        AlertDialog.Builder builder = new AlertDialog.Builder(eContext);
        builder.setTitle(title);
        builder.setNeutralButton("Ok",null);
        builder.setNegativeButton("Cancel",null);

        LayoutInflater li = LayoutInflater.from(eContext);
        View promptsView = li.inflate(R.layout.dialog_date_input, null);
        builder.setView(promptsView);

        final EditText etMonth = (EditText) promptsView.findViewById(R.id.Clients_AgeMonth);
        final EditText etDay = (EditText) promptsView.findViewById(R.id.Clients_AgeDay);

        final AlertDialog alertDialog =  builder.create();
        alertDialog.setOnShowListener(dialog -> {
            final DialogInterface mDialog= dialog;
            alertDialog.getButton(AlertDialog.BUTTON_NEUTRAL).setOnClickListener(view -> {
                if(Validation.hasText(etMonth) || Validation.hasText(etDay))
                {
                    int day = 0,month = 0,year = 0;

                    String strDay = etDay.getText().toString();
                    String strMonth = etMonth.getText().toString();

                    if (strDay != null && !strDay.equals("")) {
                        day = Integer.parseInt(strDay);
                        //validate date if someone tries to enter the day value as greater than 30
                        if(day>29){
                            etDay.setText("");
                            Utilities.showAlertToast(eContext,"দিনের সংখ্যা ২৯ এর বেশী হতে পারে না!");
                            return;
                        }
                    }
                    if (strMonth != null && !strMonth.equals("")) {
                        month = Integer.parseInt(strMonth);
                        //validate date if someone tries to enter the month value as greater than 30
                        if(month>11){
                            etMonth.setText("");
                            Utilities.showAlertToast(eContext,"মাসের সংখ্যা ১১ এর বেশী হতে পারে না!");
                            return;
                        }
                    }
                    String date = Converter.dateOfBirthFromAge(day, month, year, Constants.SHORT_SLASH_FORMAT_BRITISH);

                    ((SingleInputListener) eContext).onNeutralButtonClicked(mDialog, date);
                    mDialog.dismiss();
                }
                else{
                    Utilities.showAlertToast(eContext,"দয়া করে যে কোন একটি ঘরে তথ্য প্রদান করে Ok চাপুন");
                }
            });
            alertDialog.getButton(AlertDialog.BUTTON_NEGATIVE).setOnClickListener(view -> mDialog.dismiss());
        });

        alertDialog.show();
    }
}