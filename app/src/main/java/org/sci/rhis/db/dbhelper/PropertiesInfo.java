package org.sci.rhis.db.dbhelper;

import java.util.Properties;

import org.sci.rhis.utilities.ConfInfoRetrieve;

/**
 * @author sabah.mugab
 * @since January, 2018
 */
public class PropertiesInfo {
	private final static String SERVICE_JSON_PROPERTIES_LOCATION = "service.properties";
	private final static String SERVICE_JSON_MAP_PROPERTIES_LOCATION = "json_map.properties";

	public static final Properties SERVICE_JSON_PROPERTIES = new Properties(ConfInfoRetrieve.readingConf(SERVICE_JSON_PROPERTIES_LOCATION));
    public static final Properties SERVICE_JSON_MAP_PROPERTIES = new Properties(ConfInfoRetrieve.readingConf(SERVICE_JSON_MAP_PROPERTIES_LOCATION));
		
}