package de.jongo.server.service;

import java.util.Map;

import javax.servlet.http.HttpSession;

import org.json.simple.JSONObject;

import com.mongodb.Mongo;

import de.jongo.json.ErrorSon;
import de.jongo.server.helper.JongoHelper;

public class JongoService {

	private static final String REST_COMMAND_COMMAND = "_cmd";
	private static final String REST_COMMAND_CONNECT = "_connect";
	private static final String REST_COMMAND_CLOSE = "_close";
	private static final String REST_COMMAND_HELLO = "_hello";
	private static final String REST_COMMAND_STATUS = "_status";
	private static final String REST_COMMAND_FIND = "_find";
	private static final String REST_COMMAND_MORE = "_more";
	private static final String REST_COMMAND_INSERT = "_insert";
	private static final String REST_COMMAND_UPDATE = "_update";
	private static final String REST_COMMAND_REMOVE = "_remove";
	private static final String REST_COMMAND_BATCH = "_batch";
	private static final String REST_COMMAND_SERVER_STATUS = "_serverstatus";
	
	public static JSONObject doAction(HttpSession session, String uri, Map<String, String[]> params) {
		uri = uri.replace("//", "/");
		String [] req = uri.startsWith("/")?uri.substring(1).split("/"):uri.split("/");
		
		String db = null;
		String coll = null;
		String command = null;
		
		if(req.length == 1 && req[0].startsWith("_")){
			db = "admin";
			command = req[0].toLowerCase();
			return doMongoCommand(session, db, coll, command, params);
		}else if(req.length == 2 && req[1].startsWith("_")){
			db = req[0].toLowerCase();
			command = req[1].toLowerCase();
			return doMongoCommand(session, db, coll, command, params);
		}else if(req.length == 3 && req[2].startsWith("_")){
			db = req[0].toLowerCase();
			coll = req[1].toLowerCase();
			command = req[2].toLowerCase();
			return doMongoCommand(session, db, coll, command, params);
		}else{
			return new ErrorSon(ErrorSon.ERROR_INVALID_ACTION+" "+uri);
			
		}
		
	}

	private static JSONObject doMongoCommand(HttpSession session, String db, String coll, String command, Map<String, String[]> params) {
		if(command.equals(REST_COMMAND_CONNECT)){
			return JongoHelper.doConnect(session, params);
		}
		try{
			Mongo mongo = (Mongo) session.getAttribute("mongo");
			if(command.equals(REST_COMMAND_BATCH)){
				return JongoHelper.doBatch(mongo, db, coll, params);
			}else if(command.equals(REST_COMMAND_CLOSE)){
				return JongoHelper.doClose(mongo, db, coll, params);
			}else if(command.equals(REST_COMMAND_COMMAND)){
				return JongoHelper.doCmd(mongo, db, coll, params);
			}else if(command.equals(REST_COMMAND_FIND)){	
				return JongoHelper.doFind(mongo, db, coll, params);
			}else if(command.equals(REST_COMMAND_HELLO)){
				return JongoHelper.doHello(mongo);
			}else if(command.equals(REST_COMMAND_INSERT)){
				return JongoHelper.doInsert(mongo, db, coll, params);
			}else if(command.equals(REST_COMMAND_MORE)){
				return JongoHelper.doMore(mongo, db, coll, params);
			}else if(command.equals(REST_COMMAND_REMOVE)){
				return JongoHelper.doRemove(mongo, db, coll, params);
			}else if(command.equals(REST_COMMAND_STATUS)){
				return JongoHelper.doStatus(mongo, db);
			}else if(command.equals(REST_COMMAND_UPDATE)){
				return JongoHelper.doUpdate(mongo, db, coll, params);
			}else if(command.equals(REST_COMMAND_SERVER_STATUS)){
				return JongoHelper.doServerStatus(mongo, db, params);
			}else{
				return new ErrorSon(ErrorSon.ERROR_INVALID_COMMAND);
			}
		}catch(Exception e){
			return new ErrorSon(ErrorSon.ERROR_NO_CONNECTION + "\n\t" +e.getMessage());
		}
	}
}
