package net.comdude2.plugins.connectioninfo.net;

import java.sql.SQLException;

import org.bukkit.scheduler.BukkitRunnable;

import com.mysql.jdbc.PreparedStatement;

import net.comdude2.plugins.comlibrary.database.ConnectionException;
import net.comdude2.plugins.comlibrary.database.DatabaseConnector;
import net.comdude2.plugins.connectioninfo.main.ConnectionInfo;

public class DatabaseLogger extends BukkitRunnable{
	
	private ConnectionInfo ci = null;
	private DatabaseConnector db = null;
	private String URL = null;
	private String username = null;
	private String password = null;
	
	public DatabaseLogger(ConnectionInfo ci){
		this.ci = ci;
	}
	
	public void setupCredentials(){
		URL = ("jdbc:mysql://" + ci.getConfig().getString("database.address") + "/");
		username = ci.getConfig().getString("database.username");
		password = ci.getConfig().getString("database.password");
	}
	
	@Override
	public void run(){
		insertRecord();
	}
	
	public void insertRecord(){
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
