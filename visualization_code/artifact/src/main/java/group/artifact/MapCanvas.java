package group.artifact;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JPanel;

public class MapCanvas extends java.awt.Canvas {
	
	private TestingArea area;
	
	private App app;
	public MapCanvas(App app, TestingArea area) {
		this.app = app;
		this.area = area;
	}
	
	private int xCordToMap(int x) {
		return area.top_left_pixel_x + x * area.pixelSeperation;
	}
	
	private int yCordToMap(int y) {
		return area.top_left_pixel_y + y * area.pixelSeperation;
	}
	
	public void render(Graphics graphics) {
		Graphics2D g = (Graphics2D) graphics;
		g.drawImage(area.getPlanImage(), 0,0, null);
	
		g.setColor(Color.LIGHT_GRAY);
		for (int i = 0; i < area.gridMagicNumber; i++) {
			int x_location = area.top_left_pixel_x + i * area.pixelSeperation;
			g.drawLine(x_location, area.top_left_pixel_y, x_location, area.top_left_pixel_y + area.height);
		}
		for (int y_location = area.top_left_pixel_y; y_location <= area.top_left_pixel_y + area.height; y_location += area.pixelSeperation) {
			g.drawLine(area.top_left_pixel_x, y_location, area.top_left_pixel_x + area.width, y_location);
		}
	
		
		Color c = new Color(0,255,0,128);
		g.setFont(new Font("TimesRoman", Font.BOLD, 10));
		for (Zone z : area.getZones()) {
			g.setColor(c);
			int x = xCordToMap(z.getX());
			int y = yCordToMap(z.getY());
			g.fillRect(x, y, z.getWidth() * area.pixelSeperation, z.getHeight() * area.pixelSeperation);
			g.setColor(Color.BLACK);
			g.drawString(z.getName(), x + (z.getWidth() * area.pixelSeperation) / 2 - g.getFontMetrics().stringWidth(z.getName()) / 2, y + (z.getHeight() * area.pixelSeperation) / 2);
		}
		g.setColor(Color.MAGENTA);
		g.setFont(new Font("TimesRoman", Font.BOLD, 15));
		for (String s : app.devices.keySet()) {
			Device d = app.devices.get(s);
			int x = xCordToMap(d.x);
			int y = yCordToMap(d.y);
			g.fillOval(x, y, 15, 15);
			Zone intersect = null;
			for (Zone z : area.getZones()) {
				if (z.isInZone(d.x, d.y)) {
					intersect = z;
					break;
				} 
			}
			
			String name = d.mac;
			if (app.device_names.containsKey(d.mac)) {
				name = app.device_names.get(d.mac);
			}
			if (intersect != null) {
				g.drawString(name + " [" + intersect.getName() + "]",x, y);
			} else {
				g.drawString(name + " " + d.getCoordinateString(),x, y);
			}
			
			Point previous_point = null;
			int number = 0;
			if (app.renderPastLocations()) {
				for (int i = d.pastPositions.size() - 1; i >= 0; i--) {
					number += 1;
					Point pastLocation = d.pastPositions.get(i);
					
					g.setColor(new Color(Color.MAGENTA.getRed(), Color.magenta.getGreen(), Color.MAGENTA.getBlue(), 128 / number));
					g.fillOval(xCordToMap(pastLocation.x), yCordToMap(pastLocation.y), 15, 15);
					g.setColor(new Color(255,0,0,128 / number));
					previous_point = pastLocation;
				}
			}
		}
		g.setColor(Color.RED);
		g.drawRect(area.top_left_pixel_x, area.top_left_pixel_y, area.width, area.height);
		g.setFont(new Font("TimesRoman", Font.BOLD, 18));
		for (Node n : area.getNodes()) {
			g.setColor(Color.DARK_GRAY);
			int x_pixel = xCordToMap(n.getX());
			int y_pixel = yCordToMap(n.getY());
			g.fillOval(x_pixel, y_pixel, 10, 10);
			g.setColor(Color.black);
			g.drawString(n.getName() + " " + n.getCoordinateString(), x_pixel + 12, y_pixel + 10);
		}
		g.setColor(Color.GREEN);
	}
	
	public void setTestingArea(TestingArea newArea) {
		this.area = newArea;
	}
}
