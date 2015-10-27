package net.comdude2.plugins.connectioninfo.net;

import java.sql.ResultSet;
import java.sql.SQLException;

import com.mysql.jdbc.Statement;

import net.comdude2.plugins.comlibrary.database.ConnectionException;
import net.comdude2.plugins.comlibrary.database.DatabaseConnector;
import net.comdude2.plugins.connectioninfo.main.ConnectionInfo;

public class DatabaseWorker{
	
	@SuppressWarnings("unused")
	private ConnectionInfo ci = null;
	private DatabaseConnector db = null;
	private String URL = null;
	private String username = null;
	private String password = null;
	private String sql = null;
	private int count = -1;
	
	public DatabaseWorker(ConnectionInfo ci, String sql, String url, String username, String password){
		this.ci = ci;
		this.sql = sql;
		this.URL = url;
		this.username = username;
		this.password = password;
	}
	
	public int getCount() throws SQLException, IllegalStateException, ConnectionException, Exception{
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
			Statement statement = (Statement) connection.createStatement();
			ResultSet results = statement.executeQuery(sql);
			statement.closeOnCompletion();
			results.next();
			count = results.getInt(1);
		}finally{
			try{db.disconnect();}catch(Exception e){};
		}
		return count;
	}
	
}
