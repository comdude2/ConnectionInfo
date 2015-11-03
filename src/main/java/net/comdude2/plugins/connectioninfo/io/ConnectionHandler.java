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

package main.java.net.comdude2.plugins.connectioninfo.io;

import java.io.File;
import java.sql.Timestamp;
import java.util.Date;
import java.util.LinkedList;
import java.util.UUID;

import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import main.java.net.comdude2.plugins.connectioninfo.main.ConnectionInfo;
import main.java.net.comdude2.plugins.connectioninfo.misc.LoggingMethod;
import main.java.net.comdude2.plugins.connectioninfo.misc.SQL;
import main.java.net.comdude2.plugins.connectioninfo.net.Connection;
import main.java.net.comdude2.plugins.connectioninfo.net.DatabaseLogger;
import main.java.net.comdude2.plugins.connectioninfo.util.UnitConverter;
import net.comdude2.plugins.comlibrary.util.Log;

public class ConnectionHandler {
	
	private ConnectionInfo ci = null;
	private LinkedList <Integer> loggingMethods = new LinkedList <Integer> ();
	private LinkedList <Connection> connections = new LinkedList <Connection> ();
	private boolean logConnectionAttempts = true;
	private Log singleFileLog = null;
	private DatabaseLogger dbl = null;
	
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
	
	public void stop(){
		if (dbl != null){
			dbl.halt();
		}
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
				this.singleFileLog.info("Client with UUID: '" + event.getPlayer().getUniqueId().toString() + "' is attempting to connect using IP: '" + event.getAddress().getHostAddress().toString() + "' via: '" + event.getHostname() + "'");
			}
			if (loggingMethods.contains(LoggingMethod.UUID_FILES)){
				
			}
			if (loggingMethods.contains(LoggingMethod.MYSQL)){
				if (dbl == null){
					dbl = new DatabaseLogger(ci, ci.getLogger());
					dbl.setupCredentials();
					ci.getServer().getScheduler().runTaskLaterAsynchronously(ci, dbl, 0L);
				}
				String msg = "Client with UUID: '" + event.getPlayer().getUniqueId().toString() + "' is attempting to connect using IP: '" + event.getAddress().getHostAddress().toString() + "' via: '" + event.getHostname() + "'";
				dbl.scheduleSQLExecution(new SQL("INSERT INTO " + ci.getConfig().getString("Database.Connection_log_table_name") + " (logID, timestamp, uuid, ip, message) VALUES (##AUTO##, ?, '" + event.getPlayer().getUniqueId().toString() + "', '" + event.getAddress().getHostAddress().toString() + "', '" + msg.replace("'", "") + "');", ci.getConfig().getString("Database.Connection_log_table_name"), new Timestamp(UnitConverter.getCurrentTimestamp())));
			}
			if (loggingMethods.contains(LoggingMethod.MINECRAFT_LOG)){
				ci.log.info("Client with UUID: '" + event.getPlayer().getUniqueId().toString() + "' is attempting to connect using IP: '" + event.getAddress().getHostAddress().toString() + "' via: '" + event.getHostname() + "'");
			}
		}
	}
	
	public void newConnection(PlayerLoginEvent event){
		Date date = new Date();
		connections.add(new Connection(event.getPlayer().getUniqueId(), event.getAddress(), event.getHostname(), date.getTime()));
		if (loggingMethods.contains(LoggingMethod.SINGLE_FILE)){
			if (this.singleFileLog == null){
				File f = new File(ci.getDataFolder() + "/connection_logs/connection_log.txt");
				this.singleFileLog = new Log("Connection_Log", f, true);
			}
			//TODO Ensure that getAddress() is the one i need.
			this.singleFileLog.info("Client with UUID: '" + event.getPlayer().getUniqueId().toString() + "' ##SUCCESSFULLY## connected using IP: '" + event.getAddress().getHostAddress().toString() + "' via: '" + event.getHostname() + "'");
		}
		if (loggingMethods.contains(LoggingMethod.UUID_FILES)){
			
		}
		if (loggingMethods.contains(LoggingMethod.MYSQL)){
			if (dbl == null){
				dbl = new DatabaseLogger(ci, ci.getLogger());
				dbl.setupCredentials();
				ci.getServer().getScheduler().runTaskLaterAsynchronously(ci, dbl, 0L);
			}
			String msg = "Client with UUID: '" + event.getPlayer().getUniqueId().toString() + "' ##SUCCESSFULLY## connected using IP: '" + event.getAddress().getHostAddress().toString() + "' via: '" + event.getHostname() + "'";
			dbl.scheduleSQLExecution(new SQL("INSERT INTO " + ci.getConfig().getString("Database.Connection_log_table_name") + " (logID, timestamp, uuid, ip, message) VALUES (##AUTO##, ?, '" + event.getPlayer().getUniqueId().toString() + "', '" + event.getAddress().getHostAddress().toString() + "', '" + msg.replace("'", "") + "');", ci.getConfig().getString("Database.Connection_log_table_name"), new Timestamp(UnitConverter.getCurrentTimestamp())));
		}
		if (loggingMethods.contains(LoggingMethod.MINECRAFT_LOG)){
			ci.log.info("Client with UUID: '" + event.getPlayer().getUniqueId().toString() + "' ##SUCCESSFULLY## connected using IP: '" + event.getAddress().getHostAddress().toString() + "' via: '" + event.getHostname() + "'");
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
				this.singleFileLog.info("Client with UUID: '" + event.getPlayer().getUniqueId().toString() + "' ##FAILED## to connect using IP: '" + event.getAddress().getHostAddress().toString() + "' via: '" + event.getHostname() + "' reason: '" + event.getResult().toString() + "'");
			}
			if (loggingMethods.contains(LoggingMethod.UUID_FILES)){
				
			}
			if (loggingMethods.contains(LoggingMethod.MYSQL)){
				if (dbl == null){
					dbl = new DatabaseLogger(ci, ci.getLogger());
					dbl.setupCredentials();
					ci.getServer().getScheduler().runTaskLaterAsynchronously(ci, dbl, 0L);
				}
				String msg = "Client with UUID: '" + event.getPlayer().getUniqueId().toString() + "' ##FAILED## to connect using IP: '" + event.getAddress().getHostAddress().toString() + "' via: '" + event.getHostname() + "' reason: '" + event.getResult().toString() + "'";
				dbl.scheduleSQLExecution(new SQL("INSERT INTO " + ci.getConfig().getString("Database.Connection_log_table_name") + " (logID, timestamp, uuid, ip, message) VALUES (##AUTO##, ?, '" + event.getPlayer().getUniqueId().toString() + "', '" + event.getAddress().getHostAddress().toString() + "', '" + msg.replace("'", "") + "');", ci.getConfig().getString("Database.Connection_log_table_name"), new Timestamp(UnitConverter.getCurrentTimestamp())));
			}
			if (loggingMethods.contains(LoggingMethod.MINECRAFT_LOG)){
				ci.log.info("Client with UUID: '" + event.getPlayer().getUniqueId().toString() + "' ##FAILED## to connect using IP: '" + event.getAddress().getHostAddress().toString() + "' via: '" + event.getHostname() + "' reason: '" + event.getResult().toString() + "'");
			}
		}
	}
	
	public void endConnection(PlayerQuitEvent event){
		Connection connection = getConnection(event.getPlayer().getUniqueId());
		if (connection != null){
			if (loggingMethods.contains(LoggingMethod.SINGLE_FILE)){
				if (this.singleFileLog == null){
					File f = new File(ci.getDataFolder() + "/connection_logs/connection_log.txt");
					this.singleFileLog = new Log("Connection_Log", f, true);
				}
				Date date = new Date();
				date.setTime(connection.getJoinTime());
				this.singleFileLog.info("Client with UUID: '" + event.getPlayer().getUniqueId().toString() + "' ##DISCONNECTED## Client was connected for: '" + UnitConverter.getDateDiff(new Date(), date) + "'");
			}
			if (loggingMethods.contains(LoggingMethod.UUID_FILES)){
				
			}
			if (loggingMethods.contains(LoggingMethod.MYSQL)){
				if (dbl == null){
					dbl = new DatabaseLogger(ci, ci.getLogger());
					dbl.setupCredentials();
					ci.getServer().getScheduler().runTaskLaterAsynchronously(ci, dbl, 0L);
				}
				Date date = new Date();
				date.setTime(connection.getJoinTime());
				String msg = "Client with UUID: '" + event.getPlayer().getUniqueId().toString() + "' ##DISCONNECTED## Client was connected for: '" + UnitConverter.getDateDiff(new Date(), date) + "'";
				dbl.scheduleSQLExecution(new SQL("INSERT INTO " + ci.getConfig().getString("Database.Connection_log_table_name") + " (logID, timestamp, uuid, ip, message) VALUES (##AUTO##, ?, '" + event.getPlayer().getUniqueId().toString() + "', '" + connection.getAddress().getHostAddress().toString() + "', '" + msg.replace("'", "") + "');", ci.getConfig().getString("Database.Connection_log_table_name"), new Timestamp(UnitConverter.getCurrentTimestamp())));
			}
			if (loggingMethods.contains(LoggingMethod.MINECRAFT_LOG)){
				Date date = new Date();
				date.setTime(connection.getJoinTime());
				ci.log.info("Client with UUID: '" + event.getPlayer().getUniqueId().toString() + "' ##DISCONNECTED## Client was connected for: '" + UnitConverter.getDateDiff(new Date(), date) + "'");
			}
			connections.remove(connection);
		}
	}
	
	public void endConnection(PlayerKickEvent event){
		Connection connection = getConnection(event.getPlayer().getUniqueId());
		if (connection != null){
			if (loggingMethods.contains(LoggingMethod.SINGLE_FILE)){
				if (this.singleFileLog == null){
					File f = new File(ci.getDataFolder() + "/connection_logs/connection_log.txt");
					this.singleFileLog = new Log("Connection_Log", f, true);
				}
				Date date = new Date();
				date.setTime(connection.getJoinTime());
				this.singleFileLog.info("Client with UUID: '" + event.getPlayer().getUniqueId().toString() + "' ##KICKED## with reason: '" + event.getReason() + "' with kick message: '" + event.getLeaveMessage() + "' Client was connected for: '" + UnitConverter.getDateDiff(new Date(), date) + "'");
			}
			if (loggingMethods.contains(LoggingMethod.UUID_FILES)){
				
			}
			if (loggingMethods.contains(LoggingMethod.MYSQL)){
				if (dbl == null){
					dbl = new DatabaseLogger(ci, ci.getLogger());
					dbl.setupCredentials();
					ci.getServer().getScheduler().runTaskLaterAsynchronously(ci, dbl, 0L);
				}
				Date date = new Date();
				date.setTime(connection.getJoinTime());
				String msg = "Client with UUID: '" + event.getPlayer().getUniqueId().toString() + "' ##KICKED## Reason: '" + event.getReason().toString() + "' with kick message: '" + event.getLeaveMessage() + "' Client was connected for: '" + UnitConverter.getDateDiff(new Date(), date) + "'";
				dbl.scheduleSQLExecution(new SQL("INSERT INTO " + ci.getConfig().getString("Database.Connection_log_table_name") + " (logID, timestamp, uuid, ip, message) VALUES (##AUTO##, ?, '" + event.getPlayer().getUniqueId().toString() + "', '" + connection.getAddress().getHostAddress().toString() + "', '" + msg.replace("'", "") + "');", ci.getConfig().getString("Database.Connection_log_table_name"), new Timestamp(UnitConverter.getCurrentTimestamp())));
			}
			if (loggingMethods.contains(LoggingMethod.MINECRAFT_LOG)){
				Date date = new Date();
				date.setTime(connection.getJoinTime());
				ci.log.info("Client with UUID: '" + event.getPlayer().getUniqueId().toString() + "' ##KICKED## with reason: '" + event.getReason() + "' with kick message: '" + event.getLeaveMessage() + "' Client was connected for: '" + UnitConverter.getDateDiff(new Date(), date) + "'");
			}
			connections.remove(connection);
		}
	}
	
	public Connection getConnection(UUID uuid){
		for (Connection c : connections){
			if (c.getUUID().equals(uuid)){
				return c;
			}
		}
		return null;
	}
	
}
