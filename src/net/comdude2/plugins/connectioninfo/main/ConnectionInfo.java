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

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

import net.comdude2.plugins.connectioninfo.util.Log;

import org.bukkit.plugin.java.JavaPlugin;

public class ConnectionInfo extends JavaPlugin{
	
	protected Log log = null;
	protected Listeners listeners = null;
	
	public void onEnable(){
		//Initialise log
		log = new Log(this.getDescription().getName(),this.getDataFolder().getAbsolutePath() + "/",true, this.getLogger());
		
		// Export licence
		File path = new File("");
		File f = new File(path.getAbsolutePath() + "ConnectionInfo_Licence.txt");
		if (!f.exists()){
			try{
				exportResource("LICENCE.txt", f);
			}catch(Exception e){
				
				e.printStackTrace();
			}
		}
		
		//Main
		listeners = new Listeners(this);
		listeners.register();
		log.info("[" + this.getDescription().getName() + "] Version: " + this.getDescription().getVersion() + " is now Enabled!");
	}
	
	public void onDisable(){
		listeners.unregister();
		log.info("[" + this.getDescription().getName() + "] Version: " + this.getDescription().getVersion() + " is now Disabled!");
	}
	
	private void exportResource(String resourceName, File destination) throws Exception {
		InputStream stream = null;
        OutputStream resStreamOut = null;
        try {
        	stream = this.getClass().getResourceAsStream(resourceName);
            if(stream == null) {
            	throw new Exception("Can't get resource '" + resourceName + "' from Jar file.");
            }
            int readBytes;
            byte[] buffer = new byte[4096];
            resStreamOut = new FileOutputStream(destination);
            while ((readBytes = stream.read(buffer)) > 0) {
            	resStreamOut.write(buffer, 0, readBytes);
            }
        } catch (Exception ex) {
        	throw ex;
        } finally {
        	stream.close();
        	resStreamOut.close();
        }
	}
	
}
