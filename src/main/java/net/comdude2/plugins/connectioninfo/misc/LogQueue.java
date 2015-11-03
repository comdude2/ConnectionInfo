package main.java.net.comdude2.plugins.connectioninfo.misc;

import java.util.UUID;

import net.comdude2.plugins.comlibrary.util.Log;

public class LogQueue {
	
	private UUID uuid = null;
	private Log log = null;
	
	public LogQueue(UUID uuid, Log log){
		this.uuid = uuid;
		this.log = log;
	}
	
	public UUID getUUID(){
		return this.uuid;
	}
	
	public Log getLog(){
		return this.log;
	}
	
	public void setLog(Log log){
		this.log = log;
	}
	
}
