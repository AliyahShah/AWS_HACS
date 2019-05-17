package group.artifact;

public class Zone {

	private String name;
	private int x;
	private int y;
	private int width;
	private int height;
	
	public Zone(String zone_name, int x, int y, int width, int height) {
		this.name = zone_name;
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
	}
	
	public int getX() {
		return x;
	}
	
	public int getY() {
		return y;
	}
	
	public int getWidth() {
		return width;
	}
	
	public int getHeight() {
		return height;
	}
	
	public String getName() {
		return name;
	}
	
	public boolean isInZone(int x_loc, int y_loc) {
		return (x_loc > x && x_loc < x + width && y_loc > y && y_loc < y + height);
	}
}
