package de.jongo.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.UnknownHostException;
import java.util.Date;
import java.util.logging.Handler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.jetty.plus.servlet.ServletHandler;
import org.eclipse.jetty.server.Connector;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.DefaultHandler;
import org.eclipse.jetty.server.handler.HandlerList;
import org.eclipse.jetty.server.handler.ResourceHandler;
import org.eclipse.jetty.server.nio.SelectChannelConnector;
import org.eclipse.jetty.server.session.SessionHandler;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.eclipse.jetty.util.log.Log;

import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.Mongo;
import com.mongodb.MongoException;

import de.jongo.server.servlets.JongoServlet;


public class JongoServer
{
	public JongoServer(){
		try {
	        Server server = new Server();
	 
	        int port = Integer.parseInt(System.getProperty("port", "9090"));
	        String host = System.getProperty("host", "127.0.0.1");
	        
	        SelectChannelConnector db = new SelectChannelConnector();
	        db.setPort(9090);
	        db.setHost(host);
	        db.setName("database");
	 
	        server.setConnectors(new Connector[]{db});
	        
	        ServletContextHandler db_handler = new ServletContextHandler(ServletContextHandler.SESSIONS);
	        db_handler.setContextPath("/");
	        db_handler.addServlet(new ServletHolder(new JongoServlet()),"/*");
	        db_handler.setConnectorNames(new String[]{"database"});
	        
	        HandlerList handlers = new HandlerList();
	        handlers.addHandler(db_handler);
	        handlers.addHandler(new DefaultHandler());
	        
	        server.setHandler(handlers);
	        server.start();
	        Log.info("Jongo Server started.");
	        server.join();
			
			} catch (UnknownHostException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (MongoException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (Exception e) {
				e.printStackTrace();
			}
	}
	
	public static void main(String[] args){
		JongoServer js = new JongoServer();
	}
	
}