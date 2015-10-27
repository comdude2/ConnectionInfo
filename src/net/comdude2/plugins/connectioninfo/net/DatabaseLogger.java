package net.comdude2.plugins.connectioninfo.net;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.LinkedList;

import com.mysql.jdbc.PreparedStatement;

import net.comdude2.plugins.comlibrary.database.ConnectionException;
import net.comdude2.plugins.comlibrary.database.DatabaseConnector;
import net.comdude2.plugins.connectioninfo.main.ConnectionInfo;
import net.comdude2.plugins.connectioninfo.misc.SQL;

public class DatabaseLogger implements Runnable{
	
	private ConnectionInfo ci = null;
	private DatabaseConnector db = null;
	private String URL = null;
	private String username = null;
	private String password = null;
	private String dbname = null;
	private String dbconnection_log_table_name = null;
	private String dbplugin_log_table_name = null;
	private LinkedList <SQL> sqlToExecute = new LinkedList <SQL> ();
	private boolean halt = false;
	private HashMap <String, Integer> tableCounts = new HashMap <String, Integer> ();
	
	public DatabaseLogger(ConnectionInfo ci){
		this.ci = ci;
	}
	
	public void halt(){
		halt = true;
	}
	
	public void setupCredentials(){
		this.URL = ("jdbc:mysql://" + ci.getConfig().getString("Database.Address") + "/");
		this.username = ci.getConfig().getString("Database.Username");
		this.password = ci.getConfig().getString("Database.Password");
		this.dbname = ci.getConfig().getString("Database.Name");
		this.dbconnection_log_table_name = ci.getConfig().getString("Database.Connection_log_table_name");
		this.dbplugin_log_table_name = ci.getConfig().getString("Database.Plugin_log_table_name");
	}
	
	public boolean createTableStructure(){
		boolean perfect = true;
		if (!tableExists("SELECT COUNT(*) FROM " + this.dbname + "." + this.dbconnection_log_table_name)){
			try{executeSQL(new SQL("CREATE TABLE connection_log(logID BIGINT, timestamp TIMESTAMP, uuid TINYTEXT, ip VARCHAR(15), message TEXT);", null));}catch(IllegalStateException e){perfect = false;}
		}
		if (!tableExists("SELECT COUNT(*) FROM " + this.dbname + "." + this.dbplugin_log_table_name)){
			try{executeSQL(new SQL("CREATE TABLE plugin_log(logID BIGINT, timestamp TIMESTAMP, message TEXT);", null));}catch(IllegalStateException e){perfect = false;}
		}
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
				db = new DatabaseConnector(URL);
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
	
	@Override
	public void run(){
		while (!halt){
			LinkedList <SQL> localSQL = this.sqlToExecute;
			this.sqlToExecute = new LinkedList <SQL> ();
			for (SQL sql : localSQL){
				insertRecord(sql);
			}
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
				db = new DatabaseConnector(URL);
			}else{
				throw new IllegalStateException("URL object is null.");
			}
		}
		
		//Check SQL for ##AUTO## Statements
		if (sql.getSQL().contains("##AUTO##")){
			//Replace auto
			autoIncrement(sql);
		}
		
		try{
			db.setupConnection(username, password);
			db.connect();
			com.mysql.jdbc.Connection connection = db.getConnection();
			PreparedStatement statement = (PreparedStatement) connection.prepareStatement(sql.getSQL());
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
			DatabaseWorker dbw = new DatabaseWorker(ci, sql, URL, username, password);
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
			DatabaseWorker dbw = new DatabaseWorker(ci, sql, URL, username, password);
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
			count = getRowCount("SELECT COUNT(*) " + sql.getTableName()) + 1;
		}
		if (count != -1){
			this.tableCounts.put(sql.getTableName(), count);
			sql.getSQL().replace("##AUTO##", String.valueOf(count + 1));
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
