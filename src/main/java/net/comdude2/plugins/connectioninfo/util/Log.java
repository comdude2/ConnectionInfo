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

package main.java.net.comdude2.plugins.connectioninfo.util;

import java.io.File;
import java.util.logging.Logger;

public class Log {
	
	private String name = null;
	private Logger mc = null;
	private net.comdude2.plugins.comlibrary.util.Log log = null;
	
	public Log(String name, File file, boolean debug, Logger mc){
		this.name = name;
		this.mc = mc;
		this.log = new net.comdude2.plugins.comlibrary.util.Log(name, file, debug);
	}
	
	public void info(String message){
		this.log.info(me() + message);
		this.mc.info(message);
	}
	
	public void warning(String message){
		this.log.warning(me() + message);
		this.mc.warning(message);
	}
	
	public void severe(String message){
		this.log.severe(me() + message);
		this.mc.severe(message);
	}
	
	public void error(String message, Exception e){
		this.log.error(me() + message, e);
		this.mc.severe(message);
		e.printStackTrace();
	}
	
	public void debug(String message){
		this.log.debug(message);
	}
	
	public void debug(String message, Exception e){
		this.log.debug(message, e);
	}
	
	private String me(){
		return "[" + name + "] ";
	}
	
}
