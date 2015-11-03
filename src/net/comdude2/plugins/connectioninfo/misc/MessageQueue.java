package net.comdude2.plugins.connectioninfo.misc;

import java.util.UUID;

public class MessageQueue {
	
	private UUID uuid = null;
	private String message = null;
	
	public MessageQueue(UUID uuid, String message){
		this.uuid = uuid;
		this.message = message;
	}
	
	public UUID getUUID(){
		return this.uuid;
	}
	
	public String getMessage(){
		return this.message;
	}
	
}
