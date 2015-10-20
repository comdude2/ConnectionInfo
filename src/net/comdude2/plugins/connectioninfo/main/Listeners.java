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

package net.comdude2.plugins.connectioninfo.main;

import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;

public class Listeners implements Listener{
	
	private ConnectionInfo ci = null;
	
	public Listeners(ConnectionInfo ci){
		this.ci = ci;
	}
	
	public void register(){
		ci.getServer().getPluginManager().registerEvents(this, ci);
		ci.getLogger().info("Events registered.");
	}
	
	public void unregister(){
		HandlerList.unregisterAll(this);
		ci.getLogger().info("Events unregistered.");
	}
	
}
