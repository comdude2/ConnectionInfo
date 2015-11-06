/*
ConnectionInfo - A network monitoring plugin for Minecraft
Copyright (C) 2015  comdude2 (Matt Armer)

This program is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program.  If not, see <http://www.gnu.org/licenses/>.

Contact: admin@mcviral.net
*/

package net.comdude2.plugins.connectioninfo.io;

import java.io.File;
import java.util.Queue;
import java.util.UUID;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.logging.Logger;

import net.comdude2.plugins.comlibrary.util.Log;
import net.comdude2.plugins.connectioninfo.misc.LogQueue;
import net.comdude2.plugins.connectioninfo.misc.MessageQueue;

public class FileLogger implements Runnable{
	
	private Queue <MessageQueue> queue = new ConcurrentLinkedQueue <MessageQueue> ();
	private Queue <LogQueue> logs = new ConcurrentLinkedQueue <LogQueue> ();
	private boolean halt = false;
	private File folder = null;
	private Logger log = null;
	
	//FIXED I believe this class is now thread safe due to the implementation of ConcurrentLinkedQueue instead of HashMap
	
	public FileLogger(File folder, Logger log){
		this.folder = folder;
		this.log = log;
	}
	
	public void halt(){
		halt = true;
	}
	
	public void logMessage(UUID uuid, String message){
		queue.add(new MessageQueue(uuid, message));
	}
	
	public void playerDisconnected(UUID uuid){
		for (LogQueue q : logs){
			if (q.getUUID().equals(uuid)){
				logs.remove(q);
				return;
			}
		}
	}
	
	private boolean logsContains(UUID uuid){
		for (LogQueue q : logs){
			if (q.getUUID().equals(uuid)){
				return true;
			}
		}
		return false;
	}
	
	@SuppressWarnings("unused")
	private boolean queueContains(UUID uuid){
		for (MessageQueue q : queue){
			if (q.getUUID().equals(uuid)){
				return true;
			}
		}
		return false;
	}
	
	@SuppressWarnings("unused")
	private MessageQueue getMessageQueue(UUID uuid){
		for (MessageQueue q : queue){
			if (q.getUUID().equals(uuid)){
				return q;
			}
		}
		return null;
	}
	
	private LogQueue getLogQueue(UUID uuid){
		for (LogQueue q : logs){
			if (q.getUUID().equals(uuid)){
				return q;
			}
		}
		return null;
	}

	public void run() {
		while(!halt){
			for (MessageQueue m : queue){
				try{
					logToFile(m.getUUID(), m.getMessage());
				}catch(Exception e){
					log.warning("ERROR: " + e.getMessage() + " CAUSE: " + e.getCause());
					e.printStackTrace();
				}
				try{queue.remove(m);}catch(Exception e){}
			}
		}
	}
	
	private void logToFile(UUID uuid, String message) throws Exception{
		if (!logsContains(uuid)){
			File f = new File(folder + "/connection_logs/" + uuid.toString() + ".txt");
			if (!f.exists()){
				try{f.createNewFile();}catch(Exception e){}
			}
			logs.add(new LogQueue(uuid, new Log(uuid.toString(), f, false)));
		}
		LogQueue l = getLogQueue(uuid);
		if (l != null){
			if (l.getLog() != null){
				l.getLog().info(message);
			}else{
				File f = new File(folder + "/connection_logs/" + uuid.toString() + ".txt");
				if (!f.exists()){
					try{f.createNewFile();}catch(Exception e){}
				}
				l.setLog(new Log(uuid.toString(), f, false));
				l.getLog().info(message);
			}
		}else{
			throw new Exception("Unknown reason for LogQueue to be blank :o - Report to developer.");
		}
	}
	
}
