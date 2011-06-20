package de.jongo.test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.testng.Assert;
import org.testng.annotations.Test;


public class JongoFindTest extends JongoTestCase {

	@Test
  public void insert() throws IOException {
		SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss.S");
		JSONArray a = new JSONArray();
		for(int i = 0; i < 150; i++){
			JSONObject o = new JSONObject();
			o.put("x", i);
			Date d = new Date();
			o.put("time", sdf.format(new Date()));
			a.add(o);
		}
		JSONObject r = (JSONObject) JSONValue.parse(doFooBarInsert(a.toString()));
		Assert.assertEquals(r.get("ok").toString(),"1"); 
  }
	
	
	@Test
  public void findAll() throws IOException {
	  JSONObject r = (JSONObject) JSONValue.parse(doFooBarFindAll());
	  Assert.assertEquals(((JSONArray)r.get("data")).size(),150); 
  }
	
	@Test
  public void findBatch() throws IOException {
  	InputStreamReader isr = new InputStreamReader(getResponse("foo/bar/_find?&limit=50&batch_size=50"));
	  BufferedReader br = new BufferedReader(isr);
	  JSONObject r = (JSONObject) JSONValue.parse(br.readLine());
	  Assert.assertEquals(((JSONArray)r.get("data")).size(),50); 
  	
	  isr = new InputStreamReader(getResponse("foo/bar/_find?&batch_size=-40"));
	  br = new BufferedReader(isr);
	  r = (JSONObject) JSONValue.parse(br.readLine());
	  Assert.assertEquals(((JSONArray)r.get("data")).size(),40);
	  
	  isr = new InputStreamReader(getResponse("foo/bar/_find?&limit=-40"));
	  br = new BufferedReader(isr);
	  r = (JSONObject) JSONValue.parse(br.readLine());
	  Assert.assertEquals(((JSONArray)r.get("data")).size(),40);
  }
	
	@Test
  public void findCriteria() throws IOException {
  	InputStreamReader isr = new InputStreamReader(getResponse("foo/bar/_find?criteria={\"x\":{\"$lt\":50}}"));
	  BufferedReader br = new BufferedReader(isr);
	  JSONObject r = (JSONObject) JSONValue.parse(br.readLine());
	  Assert.assertEquals(((JSONArray)r.get("data")).size(),50); 
  }
	

}
