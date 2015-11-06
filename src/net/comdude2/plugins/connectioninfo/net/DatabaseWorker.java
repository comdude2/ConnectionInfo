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

package net.comdude2.plugins.connectioninfo.net;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Logger;

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
