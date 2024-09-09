package org.sci.rhis.db.dbhelper;

import android.database.Cursor;
import android.util.Log;

import org.sci.rhis.utilities.Utilities;

import java.text.ParseException;
import org.sci.rhis.utilities.CustomSimpleDateFormat;
import java.util.Date;

/**
 * Created by jamil.zaman on 18/02/16.
 */
public class SimpleCursor  {
    private Cursor cursor;

    public SimpleCursor(Cursor cursor) {
        this.cursor = cursor;
    }

    public long getLong(String key) {
        return cursor.getLong(cursor.getColumnIndex(key));
    }

    public int getInt(String key) {
        return cursor.getInt(cursor.getColumnIndex(key));
    }

    public String getString(String key) {
        return cursor.getString(cursor.getColumnIndex(key));
    }

    public String getObject(String key) {
        return key.equals("") ? null : cursor.getString(cursor.getColumnIndex(key));
    }
    public String getDateString(String key) {
        return cursor.getString(cursor.getColumnIndex(key));
    }

    public Date getDate(String key) {
        return getDate(key, "yyyy-MM-dd");
    }

    public Date getDate(String key, String format) {
        CustomSimpleDateFormat sdf = new CustomSimpleDateFormat(format);
        Date date = null;
        try {
            date = sdf.parse(cursor.getString(cursor.getColumnIndex(key)));
        } catch (ParseException pe) {
            Log.e("FWC-CURSOR", pe.getMessage());
            Utilities.printTrace(pe.getStackTrace());
        }

        return date;
    }

    public boolean moveToNext() {
        return cursor.moveToNext();
    }

    public boolean moveToFirst() {
        return cursor.moveToFirst();
    }

    public boolean next() {
        return moveToNext();
    }

    public int count () {
        return cursor.getCount();
    }

    public void close() {
        cursor.close();
    }

    public boolean isClosed() {
        return cursor.isClosed();
    }

    public Cursor getCursor() {
        return cursor;
    }

}
