package org.sci.rhis.fwc;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnMultiChoiceClickListener;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.util.Log;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.sci.rhis.utilities.SelectableItemAdapter;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

@SuppressLint("AppCompatCustomView")
public class MultiSelectionSpinner extends Spinner
        implements OnMultiChoiceClickListener {
    String[] _items = null;
    boolean[] mSelection = null;
    boolean[] mSelectionAtStart = null;
    String _itemsAtStart = null;

    public boolean isQuantifiable() {
        return quantifiable;
    }
    public boolean isReadFromDb() {
        return readFromDb;
    }

    public void setQuantifiable(boolean isQuantifiable) {
        this.quantifiable = isQuantifiable;
    }

    private boolean quantifiable;
    private boolean readFromDb = false;


    ArrayAdapter<String> simple_adapter;
    SelectableItemAdapter json_adapter;

    ItemQuantityAdapter item_adapter = new ItemQuantityAdapter(getContext(), R.layout.item_quantity);


    public MultiSelectionSpinner(Context context) {
        super(context);

        simple_adapter = new ArrayAdapter<>(context,
                android.R.layout.simple_spinner_item);
        super.setAdapter(simple_adapter);
    }

    public MultiSelectionSpinner(Context context, AttributeSet attrs) {
        super(context, attrs);

        simple_adapter = new ArrayAdapter<>(context,
                android.R.layout.simple_spinner_item);
        super.setAdapter(simple_adapter);
        TypedArray arr = context.obtainStyledAttributes(attrs, R.styleable.MultiSelectionSpinner);
        quantifiable = arr.getBoolean(R.styleable.MultiSelectionSpinner_quantifiable, false);
        readFromDb = arr.getBoolean(R.styleable.MultiSelectionSpinner_readFromDb, false);

        if(readFromDb) {
            //json_adapter = new SelectableItemAdapter()
        }
    }

    @Override
    public void onClick(DialogInterface dialog, int which, boolean isChecked) {
        if (mSelection != null && which < mSelection.length) {
            mSelection[which] = isChecked;
            //TODO - may need to check empty string in the future [potential bug]
            if(which == 0) {
                for(int i = 0; i < mSelection.length; i++) {
                    ((AlertDialog) dialog).getListView().setItemChecked(i, isChecked);
                    mSelection[i] = isChecked;
                }
            }
        } else {
            throw new IllegalArgumentException(
                    "Argument 'which' is out of bounds.");
        }
    }

    @Override
    public boolean performClick() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setMultiChoiceItems(_items, mSelection, this);

        builder.setPositiveButton("Submit", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                _itemsAtStart = getSelectedItemsAsString();
                simple_adapter.clear();
                simple_adapter.add(_itemsAtStart);

                System.arraycopy(mSelection, 0, mSelectionAtStart, 0, mSelection.length);
                if(isQuantifiable()) {
                    getQuantityForItems();
                }
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //Arrays.fill(mSelection, false);
                for(int i = 0; i < mSelection.length; i++) {
                    if(mSelection[i]) {
                        AlertDialog ad = ((AlertDialog) dialog);
                        ListView lv = ad.getListView();
                                lv.setItemChecked(i, false);
                        mSelection[i] = false;
                    }
                }

                System.arraycopy(mSelectionAtStart, 0, mSelection, 0, mSelectionAtStart.length);
                simple_adapter.clear();
                _itemsAtStart = getSelectedItemsAsString();
                simple_adapter.add(_itemsAtStart);
            }
        });
        builder.show();
        return true;
    }

    @Override
    public void setAdapter(SpinnerAdapter adapter) {
        //throw new RuntimeException(
          //      "setAdapter is not supported by MultiSelectSpinner.");
        super.setAdapter(adapter);
    }


    public void setAdapter(ArrayAdapter<String> adapter) {
        //throw new RuntimeException(
        //      "setAdapter is not supported by MultiSelectSpinner.");
        super.setAdapter(adapter);
    }

    public void setAdapter(SelectableItemAdapter adapter) throws IllegalAccessException {
        if (readFromDb) {
            //super.setAdapter(adapter);
            json_adapter = adapter;

        } else {
            throw new IllegalAccessException("The spinner is not marked to read from json");
        }
    }

    public void setItems(String[] items) {
        _items = items;
        mSelection = new boolean[_items.length];
        mSelectionAtStart = new boolean[_items.length];
        simple_adapter.clear();
        simple_adapter.add(_items[0]);
        Arrays.fill(mSelection, false);
        mSelection[0] = true;
        mSelectionAtStart[0] = true;
    }

    public void setItems(JSONArray items, String name) {
        _items = new String[items.length()];
        for (int i = 0; i < _items.length; i++) {
            try {
                _items[i] = items.getJSONObject(i).getString(name);
            } catch (JSONException jse) {
                Log.e("MULTI-SPINNER", "Could not set items from JSON: " + jse.getMessage());
            }
        }
        mSelection = new boolean[items.length()];
        mSelectionAtStart  = new boolean[items.length()];
        Arrays.fill(mSelection, false);
        //mSelection[0] = true;
        mSelectionAtStart[0] = true;
    }

    public void setItems(List<String> items) {
        _items = items.toArray(new String[items.size()]);
        mSelection = new boolean[_items.length];
        mSelectionAtStart  = new boolean[_items.length];
        simple_adapter.clear();
        simple_adapter.add(_items[0]);
        Arrays.fill(mSelection, false);
        mSelection[0] = true;
    }

    public void setSelection(String[] selection) {
        for (String cell : selection) {
            for (int j = 0; j < _items.length; ++j) {
                if (_items[j].equals(cell)) {
                    mSelection[j] = true;
                    mSelectionAtStart[j] = true;
                }
            }
        }
    }

    public void setSelection(List<String> selection) {
        for (int i = 0; i < mSelection.length; i++) {
            mSelection[i] = false;
            mSelectionAtStart[i] = false;
        }
        for (String sel : selection) {
            for (int j = 0; j < _items.length; ++j) {
                if (_items[j].equals(sel)) {
                    mSelection[j] = true;
                    mSelectionAtStart[j] = true;
                }
            }
        }
        simple_adapter.clear();
        simple_adapter.add(buildSelectedItemString());
    }

    public void setSelection(int index) {
        for (int i = 0; i < mSelection.length; i++) {
            mSelection[i] = false;
            mSelectionAtStart[i] = false;
        }
        if (index >= 0 && index < mSelection.length) {
            mSelection[index] = true;
            mSelectionAtStart[index] = true;
        } else {
            throw new IllegalArgumentException("Index " + index
                    + " is out of bounds.");
        }
        simple_adapter.clear();
        simple_adapter.add(buildSelectedItemString());
    }

    public void setSelection(int[] selectedIndices) {
        for (int i = 0; i < mSelection.length; i++) {
            mSelection[i] = false;
            mSelectionAtStart[i] = false;
        }
        for (int index : selectedIndices) {
            if (index >= 0 && index < mSelection.length) {
                mSelection[index] = true;
                mSelectionAtStart[index] = true;
            } else {
                throw new IllegalArgumentException("Index " + index
                        + " is out of bounds.");
            }
        }
        simple_adapter.clear();
        simple_adapter.add(buildSelectedItemString());
    }

    public List<String> getSelectedStrings() {
        List<String> selection = new LinkedList<>();
        for (int i = 0; i < _items.length; ++i) {
            if (mSelection[i]) {
                selection.add(_items[i]);
            }
        }
        return selection;
    }

    public List<Integer> getSelectedIndices() {
        List<Integer> selection = new LinkedList<>();
        for (int i = 0; i < _items.length; ++i) {
            if (mSelection[i]) {
                selection.add(i);
            }
        }
        return selection;
    }

    public List<String> getSelectedIndicesInText(int indexOffset) {
        List<String> selection = new LinkedList<>();

        for (int i = 0; i < (isReadFromDb()? json_adapter.getCount():_items.length); ++i) {
            if (mSelection[i]) {
                selection.add(String.valueOf(isReadFromDb() ? json_adapter.getItemId(i) : (i + indexOffset)));
            }
        }
        return selection;
    }

    private String buildSelectedItemString() {
        /*StringBuilder sb = new StringBuilder();
        boolean foundOne = false;

        for (int i = 0; i < _items.length; ++i) {
            if (mSelection[i]) {
                if (foundOne) {
                    sb.append(", ");
                }
                foundOne = true;

                sb.append(_items[i]);
            }
        }
        return sb.toString();*/
        return getSelectedItemsAsString();
    }

    public String getSelectedItemsAsString() {
        StringBuilder sb = new StringBuilder();
        boolean foundOne = false;

        for (int i = 0; i < (isReadFromDb()?json_adapter.getCount():_items.length); ++i) {
            if (mSelection[i]) {
                if (foundOne) {
                    sb.append(", ");
                }
                foundOne = true;
                sb.append(isReadFromDb()?json_adapter.getLabel(i):_items[i]);
            }
        }
        return sb.toString();
    }


    //TODO - Modify this function to produce JSON result required by the get function in the utilities class
    private void saveSelectedItemWithQuantity(ListView dialogView) throws JSONException {
        JSONObject json = new JSONObject();

        int count = dialogView.getCount();
        for(int i = dialogView.getHeaderViewsCount(); i < count && count > 0; i++) {

            View childView = dialogView.getChildAt(i);
            String quantityStr =  ((EditText)childView.findViewById(R.id.edTreatmentItemQuantity)).getText().toString();
            int quantity = 0;
            if(!quantityStr.equals("")) {
                try {
                    quantity = Integer.valueOf(quantityStr);
                    //item_adapter.getItem(i).second = quantity;
                } catch (NumberFormatException nfe) {
                    quantity = 0;
                }
            }
            json.put(item_adapter.getItem(i - dialogView.getHeaderViewsCount()).first.split(":")[0], //item code
                    quantity);

            Log.d("MULTISELECT-SPINNER", childView.getClass().getName());

        }

        //TODO - Instead showing the json is Toast it should be submitted through
        //       Utilities.getMultiSelectionSpinnerGet<Index/Values> method

        Toast toast = Toast.makeText(getContext(), json.toString(), Toast.LENGTH_LONG);
        toast.show();
    }

    private void getQuantityForItems() {

        item_adapter.clear();
        for(int i =0; i < mSelection.length; i++) {
            if(mSelection[i]) {
                item_adapter.add(Pair.create(String.valueOf(i)+":"+_items[i], 0));
            }
        }

        if(item_adapter.isEmpty()) { //no need to display box if nothing is selected
            return;
        }

        //final AlertDialog.Builder builder = new AlertDialog.Builder(getContext());

        //
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        LayoutInflater inflater = ((Activity)getContext()).getLayoutInflater();
        final View convertView = (View) inflater.inflate(R.layout.treatment_details, null);
        final ListView lv = (ListView) convertView.findViewById(R.id.lvTreatmentDetails);
        builder.setView(convertView) //setView
            .setPositiveButton("Save", new DialogInterface.OnClickListener() { //setButton
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    try {
                        saveSelectedItemWithQuantity(lv);
                    } catch (JSONException jse) {

                    }
                }
            })

            .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            });

        lv.setAdapter(item_adapter);

        try {

            Dialog dialog = builder.create();
            dialog.show();

            dialog.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE);
            dialog.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);
            dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);

        } catch (Exception e) {
            Log.e("MULTISELECT-SPINNER", e.getMessage());
            e.printStackTrace();
        }
    }
}
