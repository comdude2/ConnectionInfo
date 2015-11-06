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

import java.util.UUID;

import net.comdude2.plugins.comlibrary.util.Log;

public class FileQueue {
	
	private UUID uuid = null;
	private Log log = null;
	
	public FileQueue(UUID uuid, Log log){
		this.uuid = uuid;
		this.log = log;
	}
	
	public UUID getUUID(){
		return this.uuid;
	}
	
	public Log getLog(){
		return this.log;
	}
	
	public void setLog(Log log){
		this.log = log;
	}
	
}
