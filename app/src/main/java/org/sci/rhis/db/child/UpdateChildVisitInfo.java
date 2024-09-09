package org.sci.rhis.db.child;

import android.widget.Toast;

import org.json.JSONObject;
import org.sci.rhis.db.dbhelper.CommonQueryExecution;
import org.sci.rhis.db.dbhelper.QueryBuilder;
import org.sci.rhis.utilities.JsonHandler;


/**
 * @author sabah.mugab
 * @since April, 2018
 */
public class UpdateChildVisitInfo {

	public static boolean updateChildVisit(JSONObject childServiceInfo, QueryBuilder dynamicQueryBuilder) {
		
		try{

			//Update child service
			CommonQueryExecution.executeQuery(dynamicQueryBuilder.getUpdateQuery
					(new JsonHandler().addJsonKeyValueEdit(childServiceInfo, "CHILD",false)));

			//For service details delete and reinsert the new values
			DeleteChildVisitInfo.deleteChildVisitDetail(childServiceInfo, dynamicQueryBuilder);
			InsertChildVisitInfo.insertChildVisitDetail(childServiceInfo, dynamicQueryBuilder);

			return true;

		}
		catch(Exception e){
			e.printStackTrace();
			return false;
		}					
	}
}