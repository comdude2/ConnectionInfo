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

package net.comdude2.plugins.connectioninfo.main;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.LinkedList;

import net.comdude2.plugins.connectioninfo.io.ConnectionHandler;
import net.comdude2.plugins.connectioninfo.util.Log;

import org.bukkit.plugin.java.JavaPlugin;

public class ConnectionInfo extends JavaPlugin{
	
	public Log log = null;
	public Listeners listeners = null;
	public ConnectionHandler handle = null;
	
	public void onEnable(){
		//Save default config
		this.saveDefaultConfig();
		//Initialise log
		log = new Log(this.getDescription().getName(),new File(this.getDataFolder().getAbsolutePath() + "/"),true, this.getLogger());
		
		// Export licence
		File path = new File("");
		File f = new File(path.getAbsolutePath() + "ConnectionInfo_Licence.txt");
		if (!f.exists()){
			try{
				exportResource("LICENCE.txt", f);
			}catch(Exception e){
				log.error(e.getMessage(), e);
				e.printStackTrace();
			}
		}
		
		//Main
		listeners = new Listeners(this);
		handle = new ConnectionHandler(this);
		LinkedList <Integer> methods = determineLoggingMethods();
		if (methods != null){
			for (Integer method : methods){
				handle.addLoggingMethod(method);
			}
		}else{
			log.warning("");
		}
		//Enable
		listeners.register();
		log.info("[" + this.getDescription().getName() + "] Version: " + this.getDescription().getVersion() + " is now Enabled!");
	}
	
	public void onDisable(){
		listeners.unregister();
		log.info("[" + this.getDescription().getName() + "] Version: " + this.getDescription().getVersion() + " is now Disabled!");
	}
	
	private void exportResource(String resourceName, File destination) throws Exception {
		InputStream stream = null;
        OutputStream resStreamOut = null;
        try {
        	stream = this.getClass().getResourceAsStream(resourceName);
            if(stream == null) {
            	throw new Exception("Can't get resource '" + resourceName + "' from Jar file.");
            }
            int readBytes;
            byte[] buffer = new byte[4096];
            resStreamOut = new FileOutputStream(destination);
            while ((readBytes = stream.read(buffer)) > 0) {
            	resStreamOut.write(buffer, 0, readBytes);
            }
        } catch (Exception ex) {
        	throw ex;
        } finally {
        	stream.close();
        	resStreamOut.close();
        }
	}
	
	public LinkedList <Integer> determineLoggingMethods(){
		if (this.getConfig().getBoolean("LoggingMethod.SingleFile")){
			
		}
		if (this.getConfig().getBoolean("LoggingMethod.UUIDFiles")){
			
		}
		if (this.getConfig().getBoolean("LoggingMethod.MYSQL")){
			
		}
		if (this.getConfig().getBoolean("LoggingMethod.MINECRAFTLOG")){
			
		}
		return null;
	}
	
}
