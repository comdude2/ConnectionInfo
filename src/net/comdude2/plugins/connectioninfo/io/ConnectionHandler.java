package net.comdude2.plugins.connectioninfo.io;

import java.util.LinkedList;

import org.bukkit.event.player.PlayerLoginEvent;

import net.comdude2.plugins.connectioninfo.main.ConnectionInfo;
import net.comdude2.plugins.connectioninfo.misc.LoggingMethod;
import net.comdude2.plugins.connectioninfo.net.Connection;

public class ConnectionHandler {
	
	private ConnectionInfo ci = null;
	private LinkedList <Integer> loggingMethods = new LinkedList <Integer> ();
	private LinkedList <Connection> connections = new LinkedList <Connection> ();
	private boolean logConnectionAttempts = true;
	
	public ConnectionHandler(ConnectionInfo ci){
		this.ci = ci;
	}
	
	public void setLogConnectionAttempts(boolean state){
		this.logConnectionAttempts = state;
	}
	
	public LinkedList <Integer> getLoggingMethods(){
		return loggingMethods;
	}
	
	public void clearLoggingMethods(){
		loggingMethods = new LinkedList <Integer> ();
	}
	
	public void addLoggingMethod(int method){
		if (!loggingMethods.contains(method)){
			loggingMethods.add(method);
		}
	}
	
	public void connectionAttempt(PlayerLoginEvent event){
		if (this.logConnectionAttempts){
			if (loggingMethods.contains(LoggingMethod.SINGLE_FILE)){
				
			}
			if (loggingMethods.contains(LoggingMethod.UUID_FILES)){
				
			}
			if (loggingMethods.contains(LoggingMethod.MYSQL)){
				
			}
			if (loggingMethods.contains(LoggingMethod.MINECRAFT_LOG)){
				ci.log.info("");
			}
		}
	}
	
	public void newConnection(PlayerLoginEvent event){
		connections.add(new Connection(event.getPlayer().getUniqueId(), event.getAddress(), event.getHostname()));
		if (loggingMethods.contains(LoggingMethod.SINGLE_FILE)){
			
		}
		if (loggingMethods.contains(LoggingMethod.UUID_FILES)){
			
		}
		if (loggingMethods.contains(LoggingMethod.MYSQL)){
			
		}
		if (loggingMethods.contains(LoggingMethod.MINECRAFT_LOG)){
			ci.log.info("");
		}
	}
	
	public void failedConnection(PlayerLoginEvent event){
		
	}
	
}
