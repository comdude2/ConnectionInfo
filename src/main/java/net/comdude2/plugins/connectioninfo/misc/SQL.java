package main.java.net.comdude2.plugins.connectioninfo.misc;

import java.sql.Timestamp;

public class SQL {
	
	private String sql = null;
	private String tableName = null;
	private Timestamp timestamp = null;
	
	public SQL(String sql, String tableName, Timestamp timestamp){
		this.sql = sql;
		this.tableName = tableName;
		this.timestamp = timestamp;
	}
	
	public String getSQL(){
		return this.sql;
	}
	
	public void setSQL(String sql){
		this.sql = sql;
	}
	
	public String getTableName(){
		return this.tableName;
	}
	
	public Timestamp getTimestamp(){
		return this.timestamp;
	}
	
}
