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
import java.sql.SQLException;
import java.util.LinkedList;

import org.bukkit.event.player.PlayerLoginEvent;

import com.mysql.jdbc.MySQLConnection;
import com.mysql.jdbc.PreparedStatement;

import net.comdude2.plugins.comlibrary.database.ConnectionException;
import net.comdude2.plugins.comlibrary.database.DatabaseConnector;
import net.comdude2.plugins.comlibrary.util.Log;
import net.comdude2.plugins.connectioninfo.main.ConnectionInfo;
import net.comdude2.plugins.connectioninfo.misc.LoggingMethod;
import net.comdude2.plugins.connectioninfo.net.Connection;

public class ConnectionHandler {
	
	private ConnectionInfo ci = null;
	private LinkedList <Integer> loggingMethods = new LinkedList <Integer> ();
	private LinkedList <Connection> connections = new LinkedList <Connection> ();
	private boolean logConnectionAttempts = true;
	private Log singleFileLog = null;
	private DatabaseConnector db = null;
	
	public ConnectionHandler(ConnectionInfo ci){
		this.ci = ci;
		File f = new File(ci.getDataFolder() + "/connection_logs/");
		if (!f.exists()){
			f.mkdir();
		}
		/*
		f = new File(ci.getDataFolder() + "/connection_logs/");
		if (!f.exists()){
			f.mkdir();
		}
		*/
	}
	
	public void setLogConnectionAttempts(boolean state){
		this.logConnectionAttempts = state;
	}
	
	public LinkedList <Integer> getLoggingMethods(){
		return loggingMethods;
	}
	
	public void clearLoggingMethods(){
		loggingMethods = new LinkedList <Integer> ();
	}
	
	public void addLoggingMethod(int method){
		if (!loggingMethods.contains(method)){
			loggingMethods.add(method);
		}
	}
	
	public void connectionAttempt(PlayerLoginEvent event){
		if (this.logConnectionAttempts){
			if (loggingMethods.contains(LoggingMethod.SINGLE_FILE)){
				if (this.singleFileLog == null){
					File f = new File(ci.getDataFolder() + "/connection_logs/connection_log.txt");
					this.singleFileLog = new Log("Connection_Log", f, true);
				}
				//TODO Ensure that getAddress() is the one i need.
				this.singleFileLog.info("Client with UUID: '" + event.getPlayer().getUniqueId().toString() + "' is attempting to connect using IP: '" + event.getAddress().getAddress().toString() + "' via: '" + event.getHostname() + "'");
			}
			if (loggingMethods.contains(LoggingMethod.UUID_FILES)){
				
			}
			if (loggingMethods.contains(LoggingMethod.MYSQL)){
				//TODO Add MySQL
				insertRecord("jdbc:mysql://comdude2.net:3306/", "test", "pieisnice");
			}
			if (loggingMethods.contains(LoggingMethod.MINECRAFT_LOG)){
				ci.log.info("Client with UUID: '" + event.getPlayer().getUniqueId().toString() + "' is attempting to connect using IP: '" + event.getAddress().getAddress().toString() + "' via: '" + event.getHostname() + "'");
			}
		}
	}
	
	public void newConnection(PlayerLoginEvent event){
		connections.add(new Connection(event.getPlayer().getUniqueId(), event.getAddress(), event.getHostname()));
		if (loggingMethods.contains(LoggingMethod.SINGLE_FILE)){
			if (this.singleFileLog == null){
				File f = new File(ci.getDataFolder() + "/connection_logs/connection_log.txt");
				this.singleFileLog = new Log("Connection_Log", f, true);
			}
			//TODO Ensure that getAddress() is the one i need.
			this.singleFileLog.info("Client with UUID: '" + event.getPlayer().getUniqueId().toString() + "' ##SUCCESSFULLY## connected using IP: '" + event.getAddress().getAddress().toString() + "' via: '" + event.getHostname() + "'");
		}
		if (loggingMethods.contains(LoggingMethod.UUID_FILES)){
			
		}
		if (loggingMethods.contains(LoggingMethod.MYSQL)){
			//TODO Add MySQL
		}
		if (loggingMethods.contains(LoggingMethod.MINECRAFT_LOG)){
			ci.log.info("Client with UUID: '" + event.getPlayer().getUniqueId().toString() + "' ##SUCCESSFULLY## connected using IP: '" + event.getAddress().getAddress().toString() + "' via: '" + event.getHostname() + "'");
		}
	}
	
	public void failedConnection(PlayerLoginEvent event){
		if (this.logConnectionAttempts){
			if (loggingMethods.contains(LoggingMethod.SINGLE_FILE)){
				if (this.singleFileLog == null){
					File f = new File(ci.getDataFolder() + "/connection_logs/connection_log.txt");
					this.singleFileLog = new Log("Connection_Log", f, true);
				}
				//TODO Ensure that getAddress() is the one i need.
				this.singleFileLog.info("Client with UUID: '" + event.getPlayer().getUniqueId().toString() + "' ##FAILED## to connect using IP: '" + event.getAddress().getAddress().toString() + "' via: '" + event.getHostname() + "' reason: '" + event.getResult().toString() + "'");
			}
			if (loggingMethods.contains(LoggingMethod.UUID_FILES)){
				
			}
			if (loggingMethods.contains(LoggingMethod.MYSQL)){
				//TODO Add MySQL
			}
			if (loggingMethods.contains(LoggingMethod.MINECRAFT_LOG)){
				ci.log.info("Client with UUID: '" + event.getPlayer().getUniqueId().toString() + "' ##FAILED## to connect using IP: '" + event.getAddress().getAddress().toString() + "' via: '" + event.getHostname() + "' reason: '" + event.getResult().toString() + "'");
			}
		}
	}
	
	//TODO PUT THIS ON A SEPERATE THREAD
	private void insertRecord(String URL, String username, String password){
		//NOTE INSERT INTO `test`.`table` (`myKey`, `myValue`) VALUES ('1', 'Test');
		if (db != null){
			if (db.getConnection() != null){
				try {
					if (!db.getConnection().isClosed()){
						//Connected
						db.disconnect();
					}
				} catch (SQLException e) {}
			}
		}else{
			db = new DatabaseConnector(URL);
		}
		try{
			db.setupConnection(username, password);
			db.connect();
			MySQLConnection connection = db.getConnection();
			PreparedStatement statement = (PreparedStatement) connection.prepareStatement("INSERT INTO `test`.`table` (`myKey`, `myValue`) VALUES (?, ?);");
			statement.setInt(1, 2);
			statement.setString(2, "Test");
			statement.executeUpdate();
		}catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalStateException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ConnectionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally{
			try{db.disconnect();}catch(Exception e){};
		}
	}
	
}
