package group.artifact;

import java.awt.Point;
import java.awt.geom.Point2D;

public class LocationQueryResult {
	private String mac;
	private Point2D.Float location;
	
	public LocationQueryResult(String mac, Point2D.Float location) {
		this.mac = mac;
		this.location = location;
	}
	
	public Point2D.Float getLocation() {
		return location;
	}
	
	public String getMac() {
		return mac;
	}
}
