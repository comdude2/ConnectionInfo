package net.comdude2.plugins.connectioninfo.io;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.util.Date;
import java.util.Formatter;

public class UUIDFile {
	
	private RandomAccessFile raf = null;
	private Formatter f = null;
	private FileChannel fc = null;
	private FileLock lock = null;
	private Date lastUsed = null;
	
	public UUIDFile(RandomAccessFile raf){
		this.raf = raf;
	}
	
	public void writeToFile(String data) throws IOException{
		this.lastUsed = new Date();
		if (f == null){
			fc = raf.getChannel();
			lock = fc.lock();
		}
		raf.writeBytes(data);
	}
	
	public Date lastUsed(){
		return this.lastUsed;
	}
	
	public boolean isClosed(){
		boolean closed = true;
		if (lock != null){
			if (!lock.isValid()){
				closed = false;
			}
		}
		if (f != null){
			closed = false;
		}
		return closed;
	}
	
	public void close(){
		if (lock != null){
			try{lock.release();}catch(IOException e){}
		}
		if (f != null){
			try{f.close();}catch(Exception e){}
		}
	}
	
}
