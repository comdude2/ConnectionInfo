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

package net.comdude2.plugins.connectioninfo.misc;

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
