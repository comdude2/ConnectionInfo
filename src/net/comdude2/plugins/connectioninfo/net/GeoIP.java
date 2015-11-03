package net.comdude2.plugins.connectioninfo.net;

import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;

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
	
	public Location getLocation(InetAddress address) throws UnacceptableAddressException{
		try {
			if (!isLocalScope(address)){
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
			}else{
				throw new UnacceptableAddressException("The InetAddress provided is not acceptable.");
			}
		} catch (UnknownHostException e) {
			ci.log.error(e.getMessage(), e);
			throw new UnacceptableAddressException("The InetAddress provided is not acceptable.");
		}
	}
	
	//Note: I know some of these if statements aren't needed but I intend to use them for a future feature
	public boolean isLocalScope(InetAddress address) throws UnknownHostException{
		if (address.isAnyLocalAddress() || address.isLoopbackAddress()){
			return true;
		}else if (withinIPRange("0.0.0.0", "0.255.255.255", address)){
			return true;
		}else if (withinIPRange("10.0.0.0", "10.255.255.255", address)){
			return true;
		}else if (withinIPRange("100.64.0.0", "100.127.255.255", address)){
			return true;
		}else if (withinIPRange("127.0.0.0", "127.255.255.255", address)){
			return true;
		}else if (withinIPRange("169.254.0.0", "169.254.255.255", address)){
			return true;
		}else if (withinIPRange("172.16.0.0", "172.31.255.255", address)){
			return true;
		}else if (withinIPRange("192.0.0.0", "192.0.0.255", address)){
			return true;
		}else if (withinIPRange("192.0.2.0", "192.0.2.255", address)){
			return true;
		}else if (withinIPRange("192.88.99.0", "192.88.99.255", address)){
			return true;
		}else if (withinIPRange("192.168.0.0", "192.168.255.255", address)){
			return true;
		}else if (withinIPRange("192.18.0.0", "192.19.255.255", address)){
			return true;
		}else if (withinIPRange("192.51.100.0", "192.51.100.255", address)){
			return true;
		}else if (withinIPRange("203.0.113.0", "203.0.113.255", address)){
			return true;
		}else if (withinIPRange("224.0.0.0", "239.255.255.255", address)){
			return true;
		}else if (withinIPRange("240.0.0.0", "255.255.255.254", address)){
			return true;
		}else if (address.getHostAddress().toString().equals("255.255.255.255")){
			return true;
		}else{
			return false;
		}
	}
	
	private boolean withinIPRange(String ip1, String ip2, InetAddress address) throws UnknownHostException{
		InetAddress lower = InetAddress.getByName(ip1);
		InetAddress upper = InetAddress.getByName(ip2);
		int i = 0;
		for (Byte b : address.getAddress()){
			if (!(b.intValue() > lower.getAddress()[i]) && !(b.intValue() < upper.getAddress()[i])){
				return false;
			}
			i++;
		}
		return true;
	}
	
}
