package org.sci.rhis.utilities;

import java.io.InputStream;
import java.util.Properties;
import java.io.IOException;

/**
 * @author sabah.mugab
 * @created November, 2015
 */
public class ConfInfoRetrieve {

	public static Properties readingConf(String propLoc){
				
		Properties properties = new Properties();
		try{
			properties.load(GlobalActivity.context.getAssets().open(propLoc));
		}
		catch(IOException e){
			e.printStackTrace();
		}
	    return properties;
	}
}
