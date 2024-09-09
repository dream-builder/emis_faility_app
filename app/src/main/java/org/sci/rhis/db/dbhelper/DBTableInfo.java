package org.sci.rhis.db.dbhelper;

import org.json.JSONObject;

/**
 * @author sabah.mugab
 * @since December, 2016
 */
abstract class DBTableInfo {

    protected String serviceName = "";
    protected String serviceType = "";
	
	protected JSONObject serviceJSON = new JSONObject();
	
	protected JSONKeyMapper keyMapper= new JSONKeyMapper();

}