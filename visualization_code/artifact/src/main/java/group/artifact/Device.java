package group.artifact;

import java.awt.Point;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Vector;

public class Device {

	public String mac;
	public int x;
	public int y;
	public Vector<Point> pastPositions = new Vector<Point>();
	public static final int MAX_PAST_POSITIONS = 5;
	
	public Device(String mac, int x, int y) {
		this.mac = mac;
		this.x = x;
		this.y = y;
	}
	
	public void updatePosition(int newX, int newY) {
		if (newX != x || newY != y) { // If this is actually a new Position...
			pastPositions.add(new Point(x,y));
			if (pastPositions.size() > MAX_PAST_POSITIONS) {
				pastPositions.remove(0);
			}
			
			this.x = newX;
			this.y = newY;
		}
	}
	public String getCoordinateString() {
		return "(" + x + "," + y + ")";
	}
	
}
