package group.artifact;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferStrategy;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Vector;
import java.util.concurrent.ConcurrentHashMap;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;


public class App 
{
	// Gui variables
	private JFrame frame;
	private MapCanvas canvas;
	private JLabel activeDevices = new JLabel("Active Devices");
	private JButton add_device_button = new JButton("Add Test Device");
	private JPanel activeDevicesPanel = new JPanel();
	private JCheckBox renderPastLocationsCheckbox = new JCheckBox("Render Past Locations");
	private JScrollPane scroll;
	private JPanel testingLocations = new JPanel();
	private JButton seminarRoomButton = new JButton("Seminar Room");
	private JButton labRoomButton = new JButton("Lab Room");
	private JButton outsideButton = new JButton("Outside");
	private JLabel textArea = new JLabel();
	// Mapped Variables
	public ConcurrentHashMap<String, Device> devices = new ConcurrentHashMap<String, Device>();
	private JPanel buttonBoard = new JPanel();
	private BufferStrategy buffer;
	
	// Amazon Variables
	private LocationFetcher locationFetcher;
	private String[] macs = {"3c:28:6d:1e:07:fa"};
	public HashMap<String, String> device_names = new HashMap<>();
	private TestingArea seminarRoom;
	private TestingArea labRoom;
	
	public App() {
		
	}
	
	public void createTestAreas() {
		// Create Seminar Room
		int seminar_topleft_x = 290;
		int seminar_topleft_y = 20;
		int seminar_width = 405;
		int seminar_height = 501;
		//Node seminar_node1 = new Node("node_1", 25,0);
		//Node seminar_node2 = new Node("node_2", 0, 33);
		//Node seminar_node3 = new Node("node_3", 25,62);
		Node seminar_node1 = new Node("node_1", 3,6);
		Node seminar_node2 = new Node("node_2", 0,0);
		Node seminar_node3 = new Node("node_3", 0,10);
		Zone seminar_entrance = new Zone("Entrance", 0,0,10,11);
		Zone seminar_exit = new Zone("Exit", 0,56,10,7);
		Node[] seminar_nodes = {seminar_node1, seminar_node2, seminar_node3};
		//Zone[] seminar_zones = {seminar_entrance, seminar_exit};
		Zone[] seminar_zones = {};
		String seminar_filepath = "C:\\Users\\Eric\\Pictures\\plans.jpg";
		seminarRoom = new TestingArea("Seminar Room", seminar_filepath, 
				seminar_topleft_x, seminar_topleft_y, seminar_width, seminar_height, seminar_zones, seminar_nodes);
		
		int lab_topleft_x = 258;
		int lab_topleft_y = 36;
		int lab_width = 461;
		int lab_height = 468;
		Node lab_node1 = new Node("node_1", 25,0);
		Node lab_node2 = new Node("node_2", 0,25);
		Node lab_node3 = new Node("node_3", 25,51);
		Zone lab_entrance = new Zone("Entrance", 0,43,10,6);
		Node[] lab_nodes = {lab_node1, lab_node2, lab_node3};
		Zone[] lab_zones = {lab_entrance};
		String lab_filepath = "C:\\Users\\Eric\\Pictures\\LabRoom.JPG";
		labRoom = new TestingArea("Lab Room", lab_filepath, 
				lab_topleft_x, lab_topleft_y, lab_width, lab_height, lab_zones, lab_nodes);
		
		device_names.put("3c:28:6d:1e:07:fa", "Aliyah's Phone");
		device_names.put("34:f3:9a:1a:b9:c7", "Eric's Laptop");
	}
	
	public void startDisplay() {
		canvas = new MapCanvas(this, seminarRoom);
		locationFetcher = new LocationFetcher();
		frame = new JFrame("Location Displayer");
		frame.setSize(900,600);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		canvas.setSize(709,544);
		textArea.setSize(50,200);
		textArea.setForeground(Color.MAGENTA);
		scroll = new JScrollPane(canvas);
		frame.add(scroll, "Center");
		buttonBoard.setLayout(new BoxLayout(buttonBoard, BoxLayout.Y_AXIS));
		testingLocations.setLayout(new BoxLayout(testingLocations, BoxLayout.Y_AXIS));
		testingLocations.setBorder(BorderFactory.createTitledBorder("Testing Locations"));
		activeDevicesPanel.setLayout(new BoxLayout(activeDevicesPanel, BoxLayout.Y_AXIS));
		activeDevicesPanel.add(activeDevices);
		activeDevicesPanel.add(textArea);
		activeDevicesPanel.setBorder(BorderFactory.createLoweredBevelBorder());
		buttonBoard.add(add_device_button);
		buttonBoard.add(renderPastLocationsCheckbox);
		buttonBoard.add(activeDevicesPanel);
		buttonBoard.add(testingLocations);
		add_device_button.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				devices.put("D8:F2:11:DE:F5:46", new Device("D8:F2:11:DE:F5:46", 5,1));
			}
			
		});
		seminarRoomButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				canvas.setTestingArea(seminarRoom);
			}
			
		});
		labRoomButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				canvas.setTestingArea(labRoom);
			}		
		});
		testingLocations.add(seminarRoomButton);
		testingLocations.add(labRoomButton);
		testingLocations.add(outsideButton);
		frame.add(buttonBoard, "East");
		frame.setResizable(false);
		frame.setVisible(true);
		canvas.createBufferStrategy(2);
		buffer = canvas.getBufferStrategy();
		Thread updateThread = new Thread() {
			public void run() {
				while (true) {
					String output = "<html>";
					for (String s : devices.keySet()) {
						Device d = devices.get(s);
						if (d.y < 58) {
							//d.updatePosition(d.x, d.y + 2);
						}
						output += d.mac + ": " + d.getCoordinateString() + "<br>";
					}
					output += "</html>";
					try {
						Thread.sleep(125);
						Graphics2D g = (Graphics2D) buffer.getDrawGraphics();
						g.clearRect(0,0,800,600);
						canvas.render(g);
						textArea.setText(output);
						buffer.show();
					} catch (InterruptedException e) {
						e.printStackTrace();
					}		
				}
			}
		};
		
		Thread locationUpdateThread = new Thread() {
			@Override
			public void run() {
				while (true) {
					for (String mac : macs) {
						LocationQueryResult result = locationFetcher.getLocationofDevice(mac);
						if (result != null) {
							devices.put(mac, new Device(mac, (int)result.getLocation().x, (int)result.getLocation().y));
						}
					}
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e) {e.printStackTrace();}
				}
			}
		};
		locationUpdateThread.start();
		updateThread.start();
	}
	
	
	public boolean renderPastLocations() {
		return renderPastLocationsCheckbox.isSelected();
	}
    public static void main( String[] args )
    {
    	//LocationFetcher fetcher = new LocationFetcher();
        App app = new App();
        app.createTestAreas();
        app.startDisplay();
    }
}
