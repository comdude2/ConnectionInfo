package main.java.net.comdude2.plugins.connectioninfo.net;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.logging.Logger;

import com.mysql.jdbc.PreparedStatement;

import main.java.net.comdude2.plugins.connectioninfo.main.ConnectionInfo;
import main.java.net.comdude2.plugins.connectioninfo.misc.SQL;
import net.comdude2.plugins.comlibrary.database.ConnectionException;
import net.comdude2.plugins.comlibrary.database.DatabaseConnector;

public class DatabaseLogger implements Runnable{
	
	private ConnectionInfo ci = null;
	private DatabaseConnector db = null;
	private String URL = null;
	private String username = null;
	private String password = null;
	@SuppressWarnings("unused")
	private String dbname = null;
	private String dbconnection_log_table_name = null;
	private String dbplugin_log_table_name = null;
	private ConcurrentLinkedQueue <SQL> sqlToExecute = new ConcurrentLinkedQueue <SQL> ();
	private boolean halt = false;
	private HashMap <String, Integer> tableCounts = new HashMap <String, Integer> ();
	private Logger log = null;
	
	//TODO THIS IS NOT THREAD SAFE! Change HashMap to ConcurrentLinkedQueue!
	
	public DatabaseLogger(ConnectionInfo ci, Logger log){
		this.ci = ci;
		this.log = log;
	}
	
	public void halt(){
		halt = true;
	}
	
	public void setupCredentials(){
		//TODO add port support
		this.URL = ("jdbc:mysql://" + ci.getConfig().getString("Database.Address") + ":3306/" + ci.getConfig().getString("Database.Name"));
		this.username = ci.getConfig().getString("Database.Username");
		this.password = ci.getConfig().getString("Database.Password");
		this.dbname = ci.getConfig().getString("Database.Name");
		this.dbconnection_log_table_name = ci.getConfig().getString("Database.Connection_log_table_name");
		this.dbplugin_log_table_name = ci.getConfig().getString("Database.Plugin_log_table_name");
	}
	
	public boolean createTableStructure(){
		ci.log.info("Creating database structure..");
		boolean perfect = true;
		if (!tableExists("SELECT COUNT(*) FROM " + this.dbconnection_log_table_name + ";")){
			try{executeSQL(new SQL("CREATE TABLE connection_log(logID BIGINT, timestamp DATETIME, uuid TINYTEXT, ip VARCHAR(15), message TEXT);", null, null));log.info("Table '" + this.dbconnection_log_table_name + "' created.");}catch(IllegalStateException e){perfect = false;log.info("Table '" + this.dbconnection_log_table_name + "' couldn't be created.");}
		}else{
			log.info("Table '" + this.dbconnection_log_table_name + "' exists.");
		}
		if (!tableExists("SELECT COUNT(*) FROM " + this.dbplugin_log_table_name + ";")){
			try{executeSQL(new SQL("CREATE TABLE plugin_log(logID BIGINT, timestamp TIMESTAMP, message TEXT);", null, null));log.info("Table '" + this.dbplugin_log_table_name + "' created.");}catch(IllegalStateException e){perfect = false;log.info("Table '" + this.dbplugin_log_table_name + "' couldn't be created.");}
		}else{
			log.info("Table '" + this.dbplugin_log_table_name + "' exists.");
		}
		ci.log.info("Finished creating structure.");
		return perfect;
	}
	
	public void scheduleSQLExecution(SQL sql){
		sqlToExecute.add(sql);
	}
	
	private boolean executeSQL(SQL sql) throws IllegalStateException{
		if (sql.getSQL().contains("##AUTO##")){
			autoIncrement(sql);
		}
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
			if (URL != null){
				db = new DatabaseConnector(URL, log);
			}else{
				throw new IllegalStateException("URL object is null.");
			}
		}
		try{
			db.setupConnection(username, password);
			db.connect();
			com.mysql.jdbc.Connection connection = db.getConnection();
			PreparedStatement statement = (PreparedStatement) connection.prepareStatement(sql.getSQL());
			statement.executeUpdate();
			return true;
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
		return false;
	}
	
	public void run(){
		boolean created = createTableStructure();
		if (created){
			while (!halt){
				for (SQL sql : sqlToExecute){
					insertRecord(sql);
					sqlToExecute.remove(sql);
				}
			}
		}else{
			ci.log.warning("Failed to create table structure, ending myself...");
		}
		ci.log.info("Halting DatabaseLogger...");
		return;
	}
	
	public void insertRecord(SQL sql){
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
			if (URL != null){
				db = new DatabaseConnector(URL, log);
			}else{
				throw new IllegalStateException("URL object is null.");
			}
		}
		
		//Check SQL for ##AUTO## Statements
		if (sql.getSQL().contains("##AUTO##")){
			//Replace auto
			ci.log.debug("Replacing SQL...");
			boolean changed = autoIncrement(sql);
			if (!changed){
				ci.log.warning("Dropped log with SQL: '" + sql.getSQL() + "' due to autoIncrement() method not changing the value of '##AUTO##'");
				return;
			}
		}
		
		ci.log.debug("SQL To Execute: " + sql.getSQL());
		
		try{
			db.setupConnection(username, password);
			db.connect();
			com.mysql.jdbc.Connection connection = db.getConnection();
			PreparedStatement statement = (PreparedStatement) connection.prepareStatement(sql.getSQL());
			statement.setTimestamp(1, sql.getTimestamp());
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
	
	public boolean tableExists(String sql){
		try{
			DatabaseWorker dbw = new DatabaseWorker(ci, sql, URL, username, password, log);
			int count = dbw.getCount();
			if (count != -1){
				return true;
			}else{
				return false;
			}
		}catch(Exception e){
			return false;
		}
	}
	
	public int getRowCount(String sql){
		try{
			DatabaseWorker dbw = new DatabaseWorker(ci, sql, URL, username, password, log);
			int count = dbw.getCount();
			return count;
		}catch(Exception e){
			return -1;
		}
	}
	
	public boolean autoIncrement(SQL sql){
		int count = -1;
		if (this.tableCounts.containsKey(sql.getTableName())){
			count = this.tableCounts.get(sql.getTableName()) + 1;
		}else{
			count = getRowCount("SELECT COUNT(*) FROM " + sql.getTableName() + ";") + 1;
		}
		if (count != -1){
			this.tableCounts.put(sql.getTableName(), count);
			sql.setSQL(sql.getSQL().replace("##AUTO##", String.valueOf(count + 1)));
			return true;
		}
		return false;
	}
	
	public static int find(String q, String s){
		String extract = null;
		String queryChar = q.substring(0,1);
		for (int i = 0; i < s.length(); i++){
			try{extract = s.substring(i, i + 1);}catch(Exception e){return -1;}
			if (extract.equals(queryChar)){
				if (s.substring(i, i + q.length()).equals(q)){
					return i;
				}
			}
		}
		return -1;
	}
	
	@Deprecated
	public static int findWhiteSpace(int startingPos, String s){
		String extract = null;
		for (int i = startingPos; i<s.length() - 1; i++){
			extract = s.substring(i, i+1);
			if (extract == " "){
				return i;
			}
		}
		return -1;
	}
	
}
