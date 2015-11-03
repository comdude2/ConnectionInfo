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

import java.net.InetAddress;
import java.util.UUID;

public class Connection {
	
	private UUID uuid = null;
	private InetAddress address = null;
	private String hostname = null;
	private long joinTime = 0L;
	
	public Connection(UUID uuid, InetAddress address, String hostname, long jointime){
		this.uuid = uuid;
		this.address = address;
		this.hostname = hostname;
		this.joinTime = jointime;
	}
	
	public UUID getUUID(){
		return uuid;
	}
	
	public InetAddress getAddress(){
		return address;
	}
	
	public String getHostname(){
		return hostname;
	}
	
	public long getJoinTime(){
		return joinTime;
	}
	
}
