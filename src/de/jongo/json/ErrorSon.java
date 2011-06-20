package de.jongo.json;

import org.eclipse.jetty.util.log.Log;
import org.json.simple.JSONObject;

public class ErrorSon extends JSONObject{

	public static final String ERROR_NO_DB_COLL = "Db and collection must be defined.";
	public static final String ERROR_NO_DOCS = "Missing docs.";
	public static final String ERROR_NO_CONNECTION = "Couldn't get connection to Mongo.";
	public static final String ERROR_INVALID_ACTION = "The passed URI does not represent a valid action.";
	public static final String ERROR_INVALID_COMMAND = "Command {0} not regognized.";
	public static final String ERROR_NO_CRITERIA = "Missing criteria.";
	public static final String ERROR_NO_NEW_OBJECT = "Missing new Object.";
	
	public ErrorSon(String msg) {
		super();
		this.put("ok", 0);
		this.put("errorMsg", msg);
		Log.info(msg);
	}

	
}
