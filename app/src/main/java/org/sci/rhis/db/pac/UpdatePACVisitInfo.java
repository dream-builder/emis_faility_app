package org.sci.rhis.db.pac;

import org.json.JSONObject;
import org.sci.rhis.db.dbhelper.DatabaseWrapper;
import org.sci.rhis.db.dbhelper.QueryBuilder;
import org.sci.rhis.utilities.JsonHandler;

/**
 * @author sabah.mugab
 * @since February,2017
 */
public class UpdatePACVisitInfo {
	static boolean status;

	public static boolean updatePACVisit(JSONObject PACInfo, QueryBuilder dynamicQueryBuilder) {

		try{
			DatabaseWrapper.getDatabase().execSQL(dynamicQueryBuilder.getUpdateQuery(new JsonHandler().addJsonKeyValueEdit(PACInfo, "PAC")));
			status=true;
		}
		catch(Exception e){
			status=false;
			e.printStackTrace();
		}
		return status;
	}
}