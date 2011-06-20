package de.jongo.json;

import java.util.Map;

import org.json.simple.JSONObject;

public class OKSon extends JSONObject{
	public static final String OK_CONNECTION = "Connection established.";
	public static final String OK_CLOSED = "Connection closed.";
	
	public OKSon() {
		super();
		this.put("ok", 1);
	}
	
	public OKSon(String msg) {
		super();
		this.put("ok", 1);
		this.put("msg", msg);
	}
	
	public OKSon(Object key, Object value) {
		super();
		this.put("ok", 1);
		this.put(key, value);
	}
	
	public OKSon(Map m) {
		super();
		this.put("ok", 1);
		this.putAll(m);
	}


	
}
