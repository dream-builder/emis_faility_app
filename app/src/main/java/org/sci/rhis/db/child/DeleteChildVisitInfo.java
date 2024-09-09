package org.sci.rhis.db.child;

import org.json.JSONObject;
import org.sci.rhis.db.dbhelper.CommonQueryExecution;
import org.sci.rhis.db.dbhelper.QueryBuilder;
import org.sci.rhis.utilities.JsonHandler;

/**
 * @author sabah.mugab
 * @since April, 2018
 */
public class DeleteChildVisitInfo {

	public static boolean deleteChildVisit(JSONObject childServiceInfo, QueryBuilder dynamicQueryBuilder) {
		
		try{
            CommonQueryExecution.executeQuery(dynamicQueryBuilder.getDeleteQuery
                    (new JsonHandler().addJsonKeyValueEdit(childServiceInfo, "CHILD",false)));

			deleteChildVisitDetail(childServiceInfo, dynamicQueryBuilder);

			return true;			
		}catch(Exception e){
			e.printStackTrace();
			return false;
		}					
	}
	
	public static boolean deleteChildVisitDetail(JSONObject childServiceInfo, QueryBuilder dynamicQueryBuilder){
		try{
			childServiceInfo.put("entryDate", childServiceInfo.getString("systemEntryDate"));
			CommonQueryExecution.executeQuery(dynamicQueryBuilder.getDeleteQuery
					(new JsonHandler().addJsonKeyValueEdit(childServiceInfo, "CHILDSERVICERECORD",false)));
			return true;
		}
		catch(Exception e){
			e.printStackTrace();
			return false;
		}		
	}
}