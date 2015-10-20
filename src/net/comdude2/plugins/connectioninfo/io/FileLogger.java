package net.comdude2.plugins.connectioninfo.io;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.UUID;

public class FileLogger implements Runnable{
	
	//TODO Add synchronisation?
	private HashMap <UUID, String> queue = new HashMap <UUID, String> ();
	private boolean halt = false;
	private String path = null;
	
	//This class will be for threaded file writing for the LoggingMethod.UUID_FILES unless i find a better alternative that can be thread safe.
	
	public FileLogger(String path){
		this.path = path;
	}
	
	public void halt(){
		halt = true;
	}
	
	public void logMessage(UUID uuid, String message){
		queue.put(uuid, message);
	}

	@Override
	public void run() {
		HashMap <UUID,String> localQueue = null;
		while(!halt){
			localQueue = queue;
			queue = new HashMap <UUID,String> ();
			if (!localQueue.isEmpty()){
				Set<Entry<UUID, String>> set = localQueue.entrySet();
				Iterator<Entry<UUID, String>> i = set.iterator();
				while(i.hasNext()){
					Map.Entry<UUID, String> entry = (Map.Entry<UUID, String>)i.next();
					File f = new File(path + entry.getKey().toString() + ".txt");
					boolean skip = false;
					if (!f.exists()){
						try{
							f.createNewFile();
						}catch(Exception e){
							//Drop the message
							skip = true;
						}
					}
					if (!skip){
						try {
							Files.write(Paths.get(f.toURI()), entry.getValue().getBytes(), StandardOpenOption.APPEND);
						} catch (IOException e) {
							//Drop the message
						}
					}
				}
			}
		}
	}
	
}
