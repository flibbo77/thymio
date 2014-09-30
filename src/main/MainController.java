package main;

import helpers.Vars;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.util.Random;

import javax.swing.JFrame;

import context.Map;
import context.Path;
import observer.MapPanel;
import observer.ThymioInterface;
import thymio.Thymio;

public class MainController extends JFrame {
	private static final long serialVersionUID = 1L;
	private ThymioInterface observer;
	private Map myMap;
	private Thymio myThymio;
	private MapPanel myPanel;
	
	//public static final int MAPSIZE_X =109;
	//public static final int MAPSIZE_Y = 36;
	//public static final double LENGTH = 3.5;
	
	public MainController() {
		super("Map");
		
		myMap = new Map(Vars.MAP_X, Vars.MAP_Y, Vars.MAPFIELD_SIZE);
		myPanel = new MapPanel(myMap, this);
		myThymio = new Thymio(myPanel);
		observer = myThymio.getInterface();
		
	}
	
	public void init() {		
		myPanel.setPose(0*myMap.getEdgeLength(), 0*myMap.getEdgeLength(), Math.PI/180*observer.getOrientation());
		
		this.setContentPane(myPanel);
		this.pack();
		this.setVisible(true);
	}
	
	public void run() {
		myPanel.repaint();
	}

	public static void main(String [] args) {
		System.out.println("StartThread: " + Thread.currentThread().getName());

		MainController mc = new MainController();
		
		mc.init();
		
		mc.run();
	}
}
