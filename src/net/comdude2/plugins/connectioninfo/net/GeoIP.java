package net.comdude2.plugins.connectioninfo.net;

import java.io.File;
import java.io.IOException;
import java.net.InetAddress;

import net.comdude2.plugins.connectioninfo.main.ConnectionInfo;

import com.maxmind.geoip2.DatabaseReader;
import com.maxmind.geoip2.exception.GeoIp2Exception;
import com.maxmind.geoip2.model.CityResponse;

public class GeoIP {
	
	private ConnectionInfo ci = null;
	private DatabaseReader reader = null;
	
	public GeoIP(ConnectionInfo ci, String path) throws IOException{
		this.ci = ci;
		File f = new File(path);
		reader = new DatabaseReader.Builder(f).build();
	}
	
	public Location getLocation(InetAddress address){
		Location location = null;
		try {
			CityResponse response = reader.city(address);
			location = new Location();
			location.setCountryCode(response.getCountry().getIsoCode());
			location.setCountryName(response.getCountry().getName());
			location.setRegion(response.getMostSpecificSubdivision().getIsoCode());
			location.setRegionName(response.getMostSpecificSubdivision().getName());
			location.setCity(response.getCity().getName());
			location.setPostalCode(response.getPostal().toString());
			location.setLatitude(response.getLocation().getLatitude().toString());
			location.setLongitude(response.getLocation().getLongitude().toString());
		} catch (IOException e) {
			ci.log.error(e.getMessage(), e);
		} catch (GeoIp2Exception e) {
			ci.log.error(e.getMessage(), e);
		}
		return location;
	}
	
}
