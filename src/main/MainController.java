package main;

import helpers.Vars;

import javax.swing.JFrame;

import context.Map;
import observer.MapPanel;
import observer.ThymioInterface;
import thymio.PathDriveController;
import thymio.Thymio;

public class MainController extends JFrame {
	private static final long serialVersionUID = 1L;
	private ThymioInterface observer;
	private Map myMap;
	private Thymio myThymio;
	private MapPanel myPanel;

	public MainController() {
		super("Map");

		myMap = new Map(Vars.MAP_X, Vars.MAP_Y, Vars.MAPFIELD_SIZE);
		myPanel = new MapPanel(myMap, this);
		myThymio = new Thymio(myPanel);

		PathDriveController pathDriver = new PathDriveController(myThymio);
		pathDriver.setMapPanel(myPanel);
		pathDriver.setCalculatedPath(myMap.getCalculatedPath());

		observer = myThymio.getInterface();
		observer.getThymioPanel().setDriveController(pathDriver);
	}

	public void init() {
		myPanel.setPose(0 * myMap.getEdgeLength(), 0 * myMap.getEdgeLength(),
				Math.PI / 180 * observer.getOrientation());

		this.setContentPane(myPanel);
		this.pack();
		this.setVisible(true);
	}

	public void run() {
		myPanel.repaint();
	}

	public static void main(String[] args) {
		MainController mc = new MainController();

		mc.init();

		mc.run();
	}
}
