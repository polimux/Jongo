package de.jongo.server.servlets;

import java.io.IOException;
import java.io.Writer;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.jetty.util.ajax.JSON;
import org.eclipse.jetty.util.log.Log;
import org.json.simple.JSONObject;

import com.mongodb.Mongo;

import de.jongo.server.service.JongoService;

public class JongoServlet extends HttpServlet
{

	@Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
    	boolean scriptTag = false;
    	String cb = request.getParameter("callback");
    	
    	String uri = request.getRequestURI();
    	Map<String, String[]> params = request.getParameterMap();
    	
    	JSONObject result = JongoService.doAction(request.getSession(), uri, params);
    	
    	if(result.containsKey("ok") && Double.valueOf(result.get("ok").toString()) == 1){
    		result.put("ok", 1);
    	}else if(result.containsKey("ok") && Double.valueOf(result.get("ok").toString()) == 0){
    		result.put("ok", 0);
    	}

    	response.setStatus(HttpServletResponse.SC_OK);
    	if (cb != null) {
    	    scriptTag = true;
    	    response.setContentType("text/javascript");
    	} else {
    	    response.setContentType("application/x-json");
    	}
    	Writer out = response.getWriter();
    	if (scriptTag) {
    	    out.write(cb + "(");
    	}
    	out.write(JSON.toString(result));
    	if (scriptTag) {
    	    out.write(");");
    	}
    }
}