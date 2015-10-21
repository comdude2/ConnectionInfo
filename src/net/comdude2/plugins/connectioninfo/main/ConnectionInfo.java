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
import net.comdude2.plugins.connectioninfo.misc.LoggingMethod;
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
		File file = new File(this.getDataFolder().getAbsolutePath() + "/plugin_logs/");
		file.mkdirs();
		file = new File(this.getDataFolder().getAbsolutePath() + "/plugin_logs/log.txt");
		try{if(!file.exists()){boolean created = file.createNewFile();if (!created){this.getLogger().warning("Failed to create logger file!");}}
		log = new Log(this.getDescription().getName(),file,true, this.getLogger());
		}catch(Exception e){this.getServer().getPluginManager().disablePlugin(this);this.getLogger().severe("Failed to initialise logger.");return;}
		
		// Export licence
		File path = new File("");
		File f = new File(path.getAbsolutePath() + "ConnectionInfo_Licence.txt");
		if (!f.exists()){
			try{
				exportResource("/LICENSE.txt", f);
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
			log.warning("No logging methods added, connections and attempts will not be logged!");
		}
		
		//Enable
		listeners.register();
		log.info("Version: " + this.getDescription().getVersion() + " is now Enabled!");
	}
	
	public void onDisable(){
		listeners.unregister();
		log.info("Version: " + this.getDescription().getVersion() + " is now Disabled!");
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
        	try{stream.close();}catch(Exception e){}
        	try{resStreamOut.close();}catch(Exception e){}
        }
	}
	
	public LinkedList <Integer> determineLoggingMethods(){
		LinkedList <Integer> methods = new LinkedList <Integer> ();
		if (this.getConfig().getBoolean("LoggingMethod.SingleFile")){
			methods.add(LoggingMethod.SINGLE_FILE);
		}
		if (this.getConfig().getBoolean("LoggingMethod.UUIDFiles")){
			methods.add(LoggingMethod.UUID_FILES);
		}
		if (this.getConfig().getBoolean("LoggingMethod.MYSQL")){
			methods.add(LoggingMethod.MYSQL);
		}
		if (this.getConfig().getBoolean("LoggingMethod.MINECRAFTLOG")){
			methods.add(LoggingMethod.MINECRAFT_LOG);
		}
		if (methods.size() < 1){
			methods = null;
		}
		return methods;
	}
	
}
