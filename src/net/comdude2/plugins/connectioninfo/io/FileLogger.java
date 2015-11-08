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
import java.io.RandomAccessFile;
import java.util.Queue;
import java.util.UUID;
import java.util.concurrent.ConcurrentLinkedQueue;

import net.comdude2.plugins.connectioninfo.main.ConnectionInfo;
import net.comdude2.plugins.connectioninfo.misc.FileQueue;
import net.comdude2.plugins.connectioninfo.misc.MessageQueue;
import net.comdude2.plugins.connectioninfo.misc.Variable;

public class FileLogger implements Runnable{
	
	private Queue <MessageQueue> queue = new ConcurrentLinkedQueue <MessageQueue> ();
	protected Queue <FileQueue> fileQueue = new ConcurrentLinkedQueue <FileQueue> ();
	private boolean halt = false;
	private File folder = null;
	private ConnectionInfo ci = null;
	private FileQueueManager fqm = null;
	
	//FIXED I believe this class is now thread safe due to the implementation of ConcurrentLinkedQueue instead of HashMap
	
	public FileLogger(File folder, ConnectionInfo ci){
		this.folder = folder;
		this.ci = ci;
		fqm = new FileQueueManager(this);
		
		//Register debugger variables
		ci.debugger.registerVariable(new Variable("queue",queue,this.getClass().getName()));
		ci.debugger.registerVariable(new Variable("fileQueue",fileQueue,this.getClass().getName()));
		ci.debugger.registerVariable(new Variable("halt",halt,this.getClass().getName()));
		ci.debugger.registerVariable(new Variable("folder",folder,this.getClass().getName()));
	}
	
	public FileQueueManager getFileQueueManager(){
		return fqm;
	}
	
	public void halt(){
		halt = true;
		fqm.halt();
	}
	
	public void logMessage(UUID uuid, String message){
		queue.add(new MessageQueue(uuid, message));
	}
	
	public void playerDisconnected(UUID uuid){
		for (FileQueue q : fileQueue){
			if (q.getUUID().equals(uuid)){
				q.getFile().close();
				fileQueue.remove(q);
				return;
			}
		}
	}
	
	private boolean logsContains(UUID uuid){
		for (FileQueue q : fileQueue){
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
	
	private FileQueue getFileQueue(UUID uuid){
		for (FileQueue q : fileQueue){
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
					ci.log.debug("Logging to file: " + m.getMessage());
					logToFile(m.getUUID(), m.getMessage());
				}catch(Exception e){
					ci.log.warning("ERROR: " + e.getMessage() + " CAUSE: " + e.getCause());
					e.printStackTrace();
				}
				try{queue.remove(m);}catch(Exception e){}
			}
		}
		for (FileQueue q : this.fileQueue){
			if (q.getFile() != null){
				if (!q.getFile().isClosed()){
					q.getFile().close();
				}
			}
		}
	}
	
	private void logToFile(UUID uuid, String message) throws Exception{
		if (!logsContains(uuid)){
			File f = new File(folder + "/connection_logs/" + uuid.toString() + ".txt");
			if (!f.exists()){
				try{f.createNewFile();}catch(Exception e){}
			}
			fileQueue.add(new FileQueue(uuid, new UUIDFile(new RandomAccessFile(f, ""))));
		}
		FileQueue l = getFileQueue(uuid);
		if (l != null){
			if (l.getFile() != null){
				l.getFile().writeToFile(message);
			}else{
				File f = new File(folder + "/connection_logs/" + uuid.toString() + ".txt");
				if (!f.exists()){
					try{f.createNewFile();}catch(Exception e){}
				}
				l.setFile(new UUIDFile(new RandomAccessFile(f, "rwd")));
				l.getFile().writeToFile(message);
			}
		}else{
			throw new Exception("Unknown reason for FileQueue to be blank - Report to developer.");
		}
	}
	
}
