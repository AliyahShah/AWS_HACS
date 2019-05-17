package group.artifact;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

public class TestingArea {

	private Zone[] zones;
	private Node[] nodes;
	private String name;
	private BufferedImage planImage;
	public int top_left_pixel_x;
	public int top_left_pixel_y;
	public int width;
	public int height;
	
	public int gridMagicNumber = 10;
	public int pixelSeperation;
	
	public TestingArea(String name, String planPath, int top_left_x, int top_left_y, int width, int height, Zone[] zones, Node[] nodes) {
		this.name = name;
		this.zones = zones;
		this.nodes = nodes;
		this.name = name;
		this.width = width;
		this.height = height;
		this.top_left_pixel_x = top_left_x;
		this.top_left_pixel_y = top_left_y;
		this.pixelSeperation = (width /  gridMagicNumber);
		try {
				planImage = ImageIO.read(new File(planPath));
				System.out.println("Loaded Image");
			} catch (IOException e) {e.printStackTrace();
		}

	}
		
	public BufferedImage getPlanImage() { 
		return planImage;
	}
	public Zone[] getZones() {
		return zones;
	}
	
	public Node[] getNodes() {
		return nodes;
	}
}
