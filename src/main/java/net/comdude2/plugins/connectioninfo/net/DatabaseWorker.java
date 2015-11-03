package main.java.net.comdude2.plugins.connectioninfo.net;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Logger;

import com.mysql.jdbc.Statement;

import main.java.net.comdude2.plugins.connectioninfo.main.ConnectionInfo;
import net.comdude2.plugins.comlibrary.database.ConnectionException;
import net.comdude2.plugins.comlibrary.database.DatabaseConnector;

public class DatabaseWorker{
	
	@SuppressWarnings("unused")
	private ConnectionInfo ci = null;
	private DatabaseConnector db = null;
	private String URL = null;
	private String username = null;
	private String password = null;
	private String sql = null;
	private int count = -1;
	private Logger log = null;
	
	public DatabaseWorker(ConnectionInfo ci, String sql, String url, String username, String password, Logger log){
		this.ci = ci;
		this.sql = sql;
		this.URL = url;
		this.username = username;
		this.password = password;
		this.log = log;
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
				db = new DatabaseConnector(URL, log);
			}else{
				throw new IllegalStateException("URL object is null.");
			}
		}
		Statement statement = null;
		try{
			db.setupConnection(username, password);
			db.connect();
			com.mysql.jdbc.Connection connection = db.getConnection();
			statement = (Statement) connection.createStatement();
			ResultSet results = statement.executeQuery(sql);
			//statement.closeOnCompletion();
			results.next();
			count = results.getInt(1);
		}catch (Exception e){
			log.warning("Failed to get database row count using sql: '" + sql + "'");
		}finally{
			try{statement.close();}catch(Exception e){};
			try{db.disconnect();}catch(Exception e){};
		}
		return count;
	}
	
}
