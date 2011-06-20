package de.jongo.server.helper;

import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpSession;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.CommandResult;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.Mongo;
import com.mongodb.ServerAddress;
import com.mongodb.WriteResult;
import com.mongodb.util.JSON;

import de.jongo.json.ErrorSon;
import de.jongo.json.OKSon;

public class JongoHelper {

	private static final String PARAM_CMD = "cmd";
	private static final String PARAM_SERVER = "server";
	private static final String PARAM_CRITERIA = "criteria";
	private static final String PARAM_FIELDS = "fields";
	private static final String PARAM_SKIP = "skip";
	private static final String PARAM_LIMIT = "limit";
	private static final String PARAM_SORT = "sort";
	private static final String PARAM_EXPLAIN = "explain";
	private static final String PARAM_BATCH_SIZE = "batch_size";
	private static final String PARAM_NEW_OBJECT = "newobj";
	private static final String PARAM_MULTI = "multi";
	private static final String PARAM_UPSERT = "upsert";
	private static final String PARAM_SAFE = "safe";
	private static final String PARAM_DOCUMENTS = "docs";
	
	private static final String PARAM_EXTJS = "ext";
	
	public static JSONObject doBatch(Mongo mongo, String db, String coll, Map<String, String[]> params) {
		// TODO Auto-generated method stub
		return null;
	}

	public static JSONObject doCmd(Mongo mongo,String db, String coll, Map<String, String[]> params) {
		DBObject cmd = (DBObject) JSON.parse(params.containsKey(PARAM_CMD)?params.get(PARAM_CMD)[0]:"{}");
		return new OKSon(mongo.getDB(db).command(cmd).toMap());
	}

	public static JSONObject doFind(Mongo mongo,String db, String coll,	Map<String, String[]> params) {
		DBObject criteria = (DBObject) JSON.parse(params.containsKey(PARAM_CRITERIA)?params.get(PARAM_CRITERIA)[0]:"{}");
		DBObject fields = (DBObject) JSON.parse(params.containsKey(PARAM_FIELDS)?params.get(PARAM_FIELDS)[0]:"{}");
		int skip = Integer.valueOf(params.containsKey(PARAM_SKIP)?params.get(PARAM_SKIP)[0]:"0");
		int limit =  Integer.valueOf(params.containsKey(PARAM_LIMIT)?params.get(PARAM_LIMIT)[0]:"0");
		DBObject sort = (DBObject) JSON.parse(params.containsKey(PARAM_SORT)?params.get(PARAM_SORT)[0]:"{}");
		boolean explain =  expectBooleanParameter(PARAM_EXPLAIN, params);
		int batch_size = Integer.valueOf(params.containsKey(PARAM_BATCH_SIZE)?params.get(PARAM_BATCH_SIZE)[0]:"25");
		
		JSONArray docs = new JSONArray();
		if(explain){
			docs.add(mongo.getDB(db).getCollection(coll).find(criteria, fields, skip, batch_size).sort(sort).limit(limit).explain());
		}else{
			DBCursor cur = mongo.getDB(db).getCollection(coll).find(criteria, fields).sort(sort).limit(limit).skip(skip).batchSize(batch_size);
			while(cur.hasNext()){
				docs.add(cur.next());
			}
		}
		
		return new OKSon("data",docs);
	}

	public static JSONObject doHello(Mongo mc) {
		try{
			ServerAddress sa = mc.getConnector().getAddress();
			Socket socket = new Socket(sa.getHost(),sa.getPort());
			//I was going to cite Chewbacca...
			return new OKSon("What's up, Doc?");
		}catch(Exception e){		
			return new ErrorSon(ErrorSon.ERROR_NO_CONNECTION + " Message: " +e.getMessage());
		}
	}

	public static JSONObject doUpdate(Mongo mongo, String db, String coll, Map<String, String[]> params) {
		if(db == null || coll == null || db.length() == 0 || coll.length() == 0){
			return new ErrorSon(ErrorSon.ERROR_NO_DB_COLL);
		}else if(!params.containsKey(PARAM_CRITERIA)){
			return new ErrorSon(ErrorSon.ERROR_NO_CRITERIA);
		}else if(!params.containsKey(PARAM_NEW_OBJECT)){
			return new ErrorSon(ErrorSon.ERROR_NO_NEW_OBJECT);
		}
		DBObject criteria = (DBObject) JSON.parse(params.get(PARAM_CRITERIA)[0]);
		if(criteria == null) return new ErrorSon(ErrorSon.ERROR_NO_CRITERIA);
		DBObject newObj = (DBObject) JSON.parse(params.get(PARAM_NEW_OBJECT)[0]);
		boolean upsert = expectBooleanParameter(PARAM_UPSERT, params);
		boolean multi = expectBooleanParameter(PARAM_MULTI, params);
		boolean safe = expectBooleanParameter(PARAM_SAFE, params);
		
		WriteResult wr = mongo.getDB(db).getCollection(coll).update(criteria, newObj, upsert, multi);
		OKSon result = new OKSon("n", wr.getN());
		if(safe){
  		result.put("status",wr.getLastError());
  	}
		return result;
	}

	public static JSONObject doStatus(Mongo mongo,String db) {
		JSONObject result = new JSONObject();
		CommandResult cr = mongo.getDB(db).getStats();
		result.putAll(cr.toMap());
		return result;
	}

	public static JSONObject doServerStatus(Mongo mongo,String db, Map<String, String[]> params) {
		OKSon result = new OKSon();
		CommandResult cr = mongo.getDB(db).command("serverStatus");
		if(params.containsKey(PARAM_EXTJS) && params.get(PARAM_EXTJS)[0].equals("true")){
		/*
		 * Ext direct message format
		 * {"type":"event","name":"message","data":"Successfully polled at: 11:19:30 am"}
		 */
			result.put("type", "event");
			result.put("name", "message");
			result.put("data", cr.toMap());
		}else{
			result.putAll(cr.toMap());
		}
		return result;
	}
	
	public static JSONObject doRemove(Mongo mongo,String db, String coll, Map<String, String[]> params) {
		if(db == null || coll == null || db.length() == 0 || coll.length() == 0){
			return new ErrorSon(ErrorSon.ERROR_NO_DB_COLL);
		}else if(!params.containsKey(PARAM_CRITERIA)){
			return new ErrorSon(ErrorSon.ERROR_NO_CRITERIA);
		}
		DBObject criteria = (DBObject) JSON.parse(params.get(PARAM_CRITERIA)[0]);
		if(criteria == null) return new ErrorSon(ErrorSon.ERROR_NO_CRITERIA);
		boolean safe =  expectBooleanParameter(PARAM_SAFE, params);
		
		WriteResult wr = mongo.getDB(db).getCollection(coll).remove(criteria);
		OKSon result = new OKSon("n", wr.getN());
  	if(safe){
  		result.put("status",wr.getLastError());
  	}
		return result;
	}

	public static JSONObject doMore(Mongo mongo,String db, String coll,	Map<String, String[]> params) {
		// TODO Auto-generated method stub
		return null;
	}

	public static JSONObject doInsert(Mongo mongo,String db, String coll, Map<String, String[]> params){
		if(db == null || coll == null || db.length() == 0 || coll.length() == 0){
			return new ErrorSon(ErrorSon.ERROR_NO_DB_COLL);
		}else if(!params.containsKey(PARAM_DOCUMENTS)){
			return new ErrorSon(ErrorSon.ERROR_NO_DOCS);
		}
		Object ob = JSON.parse(params.get(PARAM_DOCUMENTS)[0]);
		if(ob instanceof BasicDBObject){
			DBObject docs = (DBObject) ob;
			boolean safe =  expectBooleanParameter(PARAM_SAFE, params);
			WriteResult wr = mongo.getDB(db).getCollection(coll).insert(docs); 
			JSONObject result = new JSONObject();
	  	result.put("n", wr.getN());
	    return result;
		}else if (ob instanceof BasicDBList){
			BasicDBList dbList = (BasicDBList) ob;
			ArrayList<DBObject> docs = new ArrayList<DBObject>();
			for(Object o : dbList){
				docs.add((DBObject) o);
			}
			boolean safe =  expectBooleanParameter(PARAM_SAFE, params);
			WriteResult wr = mongo.getDB(db).getCollection(coll).insert(docs); 
			JSONObject result = new OKSon("n", wr.getN());
	  	if(safe){
	  		result.put("status",wr.getLastError());
	  	}
	    return result;
		}else{
			return null;
		}
		
	}

	public static JSONObject doConnect(HttpSession session,	Map<String, String[]> params) {
		String server = params.containsKey(PARAM_SERVER)?params.get(PARAM_SERVER)[0]:"127.0.0.1:27017";
		try{
			Mongo mc = (Mongo) session.getAttribute("mongo");
			if(mc != null){
				mc.close();
			}
			mc = new Mongo(server.split(":")[0],Integer.valueOf(server.split(":")[1]));
			ServerAddress sa = mc.getConnector().getAddress();
			Socket socket = new Socket(sa.getHost(),sa.getPort());
			session.setAttribute("mongo", mc);
			return new OKSon(OKSon.OK_CONNECTION);
		}catch(Exception e){		
			return new ErrorSon(ErrorSon.ERROR_NO_CONNECTION + " Message: " +e.getMessage());
		}
	}
	
	public static JSONObject doClose(Mongo mongo,String db, String coll,	Map<String, String[]> params) {
		mongo.close();
		return new OKSon(OKSon.OK_CLOSED);
	}
	
	private static boolean expectBooleanParameter(String key, Map<String, String[]> params){
		boolean result = false;
		if(params.containsKey(key)){
			String value = params.get(key)[0];
			if(value.equals("1")){
				return true;
			}
			result = Boolean.valueOf(value);
		}
		return result;
	}
	
}
