package org.sci.rhis.utilities;

import java.text.SimpleDateFormat;
import java.util.Locale;

public class CustomSimpleDateFormat extends SimpleDateFormat {
    public CustomSimpleDateFormat(String pattern){
        super(pattern, Locale.ENGLISH);
    }
}
