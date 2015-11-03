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
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.text.ParseException;
import java.util.Date;
import java.util.LinkedList;

import net.comdude2.plugins.connectioninfo.io.ConnectionHandler;
import net.comdude2.plugins.connectioninfo.misc.LoggingMethod;
import net.comdude2.plugins.connectioninfo.net.DatabaseLogger;
import net.comdude2.plugins.connectioninfo.net.GeoIP;
import net.comdude2.plugins.connectioninfo.net.Location;
import net.comdude2.plugins.connectioninfo.util.Log;
import net.comdude2.plugins.connectioninfo.util.UnitConverter;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

@SuppressWarnings("unused")
public class ConnectionInfo extends JavaPlugin{
	
	public Log log = null;
	public Listeners listeners = null;
	public ConnectionHandler handle = null;
	public boolean geoLocation = true;
	
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
		File f = new File(path.getAbsolutePath() + "/ConnectionInfo_Licence.txt");
		if (!f.exists()){
			try{
				exportResource("/LICENSE.txt", f);
			}catch(Exception e){
				log.error(e.getMessage(), e);
				e.printStackTrace();
			}
		}
		
		// Export database
		f = new File(this.getDataFolder() + "/GeoLite2-City.mmdb");
		if (!f.exists()){
			try{
				exportResource("/libraries/other/MaxMind GEOIP/GeoLite2-City.mmdb", f);
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
			boolean logConnectionAttempts = this.getConfig().getBoolean("Database.logConnectionAttempts");
			if (logConnectionAttempts){
				this.log.info("Server is logging connection attempts.");
			}else{
				this.log.info("Server is not logging connection attempts.");
			}
			handle.setLogConnectionAttempts(logConnectionAttempts);
		}else{
			log.warning("No logging methods added, connections and attempts will not be logged!");
		}
		
		//Test
		log.info("Testing date difference method...");
		Date n = new Date();
		Date o = null;
		try {
			o = UnitConverter.getSDF().parse("2011-10-23 12:45:34.432");
			log.info("Newer date: " + n.getTime() + " Older date: " + o.getTime());
			String result = UnitConverter.getDateDiff(n, o);
			log.info("Result: " + result);
			log.info("Test passed.");
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			log.warning("Test failed.");
		}
		
		
		this.logDatabaseCredentials();
		
		//Enable
		listeners.register();
		log.info("This plugin includes GeoLite2 data created by MaxMind, available from http://www.maxmind.com");
		log.info("Version: " + this.getDescription().getVersion() + " is now Enabled!");
	}
	
	public void onDisable(){
		listeners.unregister();
		handle.stop();
		this.getServer().getScheduler().cancelTasks(this);
		//TODO Add any save data sections here
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
			if (validDatabaseCredentials()){
				methods.add(LoggingMethod.MYSQL);
			}else{
				this.log.warning("Database credentials are not valid, database logging disabled.");
				this.getServer().getScheduler().scheduleSyncRepeatingTask(this, new Runnable(){
					
					public void run() {
						Bukkit.getLogger().warning("[ConnectionInfo] Database credentials are not valid, database logging disabled.");
					}
					
				}, 0L, 1200L);
			}
		}
		if (this.getConfig().getBoolean("LoggingMethod.MINECRAFTLOG")){
			methods.add(LoggingMethod.MINECRAFT_LOG);
		}
		if (methods.size() < 1){
			methods = null;
		}
		return methods;
	}
	
	public boolean validDatabaseCredentials(){
		boolean ok = true;
		if (this.getConfig().getString("Database.Address") != null){
			if (this.getConfig().getString("Database.Address") == "none"){
				ok = false;
			}
		}else{
			ok = false;
		}
		if (this.getConfig().getString("Database.Username") != null){
			if (this.getConfig().getString("Database.Username") == "none"){
				ok = false;
			}
		}else{
			ok = false;
		}
		if (this.getConfig().getString("Database.Name") != null){
			if (this.getConfig().getString("Database.Name") == "none"){
				ok = false;
			}
		}else{
			ok = false;
		}
		if (this.getConfig().getString("Database.Connection_log_table_name") != null){
			if (this.getConfig().getString("Database.Connection_log_table_name") == "none"){
				ok = false;
			}
		}else{
			ok = false;
		}
		if (this.getConfig().getString("Database.Plugin_log_table_name") != null){
			if (this.getConfig().getString("Database.Plugin_log_table_name") == "none"){
				ok = false;
			}
		}else{
			ok = false;
		}
		if (this.getConfig().getString("Database.Password") == null){
			this.getConfig().set("Database.Password", "");
			this.saveConfig();
			this.reloadConfig();
		}
		return ok;
	}
	
	public void logDatabaseCredentials(){
		log.info("URL: " + "jdbc:mysql://" + this.getConfig().getString("Database.Address") + ":3306/" + this.getConfig().getString("Database.Name"));
		log.info("Username: " + this.getConfig().getString("Database.Username"));
		log.info("Password: " + this.getConfig().getString("Database.Password"));
		log.info("DB Name: " + this.getConfig().getString("Database.Name"));
		log.info("DB Con Table Name: " + this.getConfig().getString("Database.Connection_log_table_name"));
		log.info("DB Pl Table Name: " + this.getConfig().getString("Database.Plugin_log_table_name"));
	}
	
}
