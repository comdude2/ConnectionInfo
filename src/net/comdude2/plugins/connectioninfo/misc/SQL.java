package net.comdude2.plugins.connectioninfo.misc;

public class SQL {
	
	private String sql = null;
	private String tableName = null;
	
	public SQL(String sql, String tableName){
		this.sql = sql;
		this.tableName = tableName;
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
	
}
