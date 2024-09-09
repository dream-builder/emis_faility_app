package org.sci.rhis.fwc;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.Typeface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

import org.sci.rhis.utilities.DisplayValue;
import org.sci.rhis.utilities.MultiIndexedValue;

import java.util.HashMap;
import java.util.List;

public class ExpandableDisplayListAdapter extends BaseExpandableListAdapter {

	private Context _context;
	private List<String> _listDataHeader; // header titles
	// child data in format of header title, child title
	private HashMap<String, List<String>> _listDataChild;
	private HashMap<String, List<DisplayValue>> _listDataValues;

	public ExpandableDisplayListAdapter(Context context, List<String> listDataHeader,
										HashMap<String, List<DisplayValue>> listChildData, boolean avoidTYpeErasure) {
		this._context = context;
		this._listDataHeader = listDataHeader;
		this._listDataValues = listChildData;
	}

	@Override
	public Object getChild(int groupPosition, int childPosititon) {
		return this._listDataValues.get(this._listDataHeader.get(groupPosition))
				.get(childPosititon);
	}

	@Override
	public long getChildId(int groupPosition, int childPosition) {
		return childPosition;
	}

	@Override
	public View getChildView(int groupPosition, final int childPosition,
			boolean isLastChild, View convertView, ViewGroup parent) {

		final DisplayValue child = (DisplayValue) getChild(groupPosition, childPosition);
		final String childText =  getChild(groupPosition, childPosition).toString();

		if (convertView == null) {
			LayoutInflater infalInflater = (LayoutInflater) this._context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = infalInflater.inflate(R.layout.list_item, null);
		}

		TextView txtListChild = (TextView) convertView
				.findViewById(R.id.lblListItem);

		txtListChild.setText(childText);

		if(child.isDangerous()) {
			txtListChild.setTextColor(Color.RED);
		} else {
			txtListChild.setTextColor(Color.BLACK);
		}

		//txtListChild.setTextColor(child.isDangerous() ? Color.RED : Color.BLACK);


		convertView.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {

				if (child instanceof MultiIndexedValue) {
					String value = child.getValue();
					if (!value.isEmpty() && !value.equals("Invalid")) {
						AlertMessage.showMessage(_context, child.getDisplayLabel(), value);
					}
				}
			}
		});

		return convertView;
	}

	private String parseString(String st) {

		String s = null;

			s = st;
			s = s.replaceAll("[^0-9]+", " ");

		//AlertMessage.showMessage(con,"Details",temp);
		return s;
	}

	@Override
	public int getChildrenCount(int groupPosition) {
		return this._listDataValues.get(this._listDataHeader.get(groupPosition))
				.size();
	}


	@Override
	public Object getGroup(int groupPosition) {
		return this._listDataHeader.get(groupPosition);
	}

	@Override
	public int getGroupCount() {
		return this._listDataHeader.size();
	}

	@Override
	public long getGroupId(int groupPosition) {
		return groupPosition;
	}

	@Override
	public View getGroupView(int groupPosition, boolean isExpanded,
			View convertView, ViewGroup parent) {
		String headerTitle = (String) getGroup(groupPosition);
		if (convertView == null) {
			LayoutInflater infalInflater = (LayoutInflater) this._context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = infalInflater.inflate(R.layout.list_group, null);
		}

		TextView lblListHeader = (TextView) convertView
				.findViewById(R.id.lblListHeader);
		lblListHeader.setTypeface(null, Typeface.BOLD);
		lblListHeader.setText(headerTitle);
		Log.d("::::----"+headerTitle, "ontest===== ");

		return convertView;
	}

	@Override
	public boolean hasStableIds() {
		return false;
	}

	@Override
	public boolean isChildSelectable(int groupPosition, int childPosition) {
		return true;
	}

}
