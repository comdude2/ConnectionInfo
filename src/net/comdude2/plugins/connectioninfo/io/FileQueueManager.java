package net.comdude2.plugins.connectioninfo.io;

import net.comdude2.plugins.connectioninfo.misc.FileQueue;
import net.comdude2.plugins.connectioninfo.util.UnitConverter;

public class FileQueueManager implements Runnable{
	
	private FileLogger fl = null;
	private boolean halt = false;
	
	public FileQueueManager(FileLogger fl){
		this.fl = fl;
	}

	public void run() {
		while (!halt){
			for (FileQueue q : fl.fileQueue){
				if (q.getFile().lastUsed() != null){
					if (UnitConverter.getMillisecondsSinceTime(q.getFile().lastUsed().getTime()) > 10000){
						q.getFile().close();
						fl.fileQueue.remove(q);
					}
				}
			}
		}
	}
	
	public void halt(){
		halt = true;
	}
	
}
