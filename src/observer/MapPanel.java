package observer;

import helpers.Vars;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;

import javax.swing.JFrame;
import javax.swing.JPanel;

import context.Map;

public class MapPanel extends JPanel {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public Map myMap;
	public static final int LENGTHSCALE = 20;
	public static final double LENGTH_EDGE_CM = Vars.MAPFIELD_SIZE;

	public MapPanel(Map m, JFrame f) {
		myMap = m;

		this.setPreferredSize(new Dimension(myMap.getSizeY() * LENGTHSCALE,
				myMap.getSizeX() * LENGTHSCALE));
		this.setMaximumSize(new Dimension(myMap.getSizeY() * LENGTHSCALE, myMap
				.getSizeX() * LENGTHSCALE));
		this.setMinimumSize(new Dimension(myMap.getSizeY() * LENGTHSCALE, myMap
				.getSizeX() * LENGTHSCALE));
	}

	public void setPose(double x, double y, double theta) {
		myMap.setPose(x, y, theta);
		this.repaint();
	}

	public void updatePose(double dF, double dR, double dt) {
		myMap.updatePose(dF, dR, dt);
		this.repaint();
	}

	public void paint(Graphics g) {
		//System.out.println("thread, in dem Paint ausgeführt wird: " + Thread.currentThread().getName());
		double angle = myMap.getThymioOrientation();

		g.setColor(Color.WHITE);
		g.clearRect(0, 0, this.getHeight(), this.getWidth());

		g.setColor(Color.BLACK);

		for (int i = 1; i < myMap.getSizeY(); i++)
			g.drawLine(LENGTHSCALE * i - 1, 0, LENGTHSCALE * i - 1,
					this.getHeight());
		for (int i = 1; i < myMap.getSizeX(); i++)
			g.drawLine(0, LENGTHSCALE * i - 1, this.getWidth(), LENGTHSCALE * i
					- 1);

		for (int x = 0; x < myMap.getSizeX(); x++) {
			for (int y = 0; y < myMap.getSizeY(); y++) {
				/*
				 * if (x == myMap.getThymioX() && y == myMap.getThymioY()) { int
				 * [] rotX = new int[3]; int [] rotY = new int[3];
				 * 
				 * double endX = LENGTHSCALE*(x+Math.cos(angle)); double endY =
				 * LENGTHSCALE*(y+Math.sin(angle));
				 * 
				 * g.setColor(Color.BLUE); rotX[0]= (int)endX; rotY[0]= (int)
				 * Math.round(this.getHeight()-endY-0.5*LENGTHSCALE);
				 * 
				 * endX = LENGTHSCALE*(x+0.5*Math.cos(-Math.PI/2+angle)); endY =
				 * LENGTHSCALE*(y+0.5*Math.sin(angle-Math.PI/2)); rotX[1]=
				 * (int)endX; rotY[1]=
				 * (int)Math.round(this.getHeight()-endY-0.5*LENGTHSCALE);
				 * 
				 * endX = LENGTHSCALE*(x+0.5*Math.cos(Math.PI/2+angle)); endY =
				 * LENGTHSCALE*(y+0.5*Math.sin(Math.PI/2+angle)); rotX[2]=
				 * (int)endX; rotY[2]=
				 * (int)Math.round(this.getHeight()-endY-0.5*LENGTHSCALE);
				 * 
				 * g.fillPolygon(rotX, rotY, 3); } else
				 */
				if (myMap.isOnBeam(x, y)) {
					int posy = myMap.getSizeY() - y;

					g.setColor(Color.DARK_GRAY);
					g.fillRect(LENGTHSCALE * x, LENGTHSCALE * (posy - 1),
							LENGTHSCALE, LENGTHSCALE);
				}
			}
		}

		g.fillRect(
				(int) (myMap.getPosX() / MapPanel.LENGTH_EDGE_CM * MapPanel.LENGTHSCALE),
				this.getHeight()//!!! evtl weg!
						- 5
						- (int) (myMap.getPosY() / MapPanel.LENGTH_EDGE_CM * MapPanel.LENGTHSCALE),
				5, 5);

		g.setColor(Color.RED);
		g.drawRect(
				(int) (myMap.getEstimPosX() / MapPanel.LENGTH_EDGE_CM * MapPanel.LENGTHSCALE),
				this.getHeight()///!!!
						- 5
						- (int) (myMap.getEstimPosY() / MapPanel.LENGTH_EDGE_CM * MapPanel.LENGTHSCALE),
				5, 5);

		double diffSensor = 20.0 * Math.PI / 180.0; // calc bogenmaß

		g.drawLine(
				(int) (myMap.getEstimPosX() / MapPanel.LENGTH_EDGE_CM * MapPanel.LENGTHSCALE),
				this.getHeight()
						- (int) (myMap.getEstimPosY() / MapPanel.LENGTH_EDGE_CM * MapPanel.LENGTHSCALE),
				(int) (myMap.getEstimPosX() / MapPanel.LENGTH_EDGE_CM
						* MapPanel.LENGTHSCALE + 10 * LENGTHSCALE
						* Math.cos(angle - 2 * diffSensor)),
				(int) (this.getHeight() - myMap.getEstimPosY()
						/ MapPanel.LENGTH_EDGE_CM * MapPanel.LENGTHSCALE + 10
						* LENGTHSCALE * Math.sin(-(angle - 2 * diffSensor))));

		g.drawLine(
				(int) (myMap.getEstimPosX() / MapPanel.LENGTH_EDGE_CM * MapPanel.LENGTHSCALE),
				this.getHeight()
						- (int) (myMap.getEstimPosY() / MapPanel.LENGTH_EDGE_CM * MapPanel.LENGTHSCALE),
				(int) (myMap.getEstimPosX() / MapPanel.LENGTH_EDGE_CM
						* MapPanel.LENGTHSCALE + 10 * LENGTHSCALE
						* Math.cos(angle - diffSensor)),
				(int) (this.getHeight() - myMap.getEstimPosY()
						/ MapPanel.LENGTH_EDGE_CM * MapPanel.LENGTHSCALE + 10
						* LENGTHSCALE * Math.sin(-(angle - diffSensor))));

		g.drawLine(
				(int) (myMap.getEstimPosX() / MapPanel.LENGTH_EDGE_CM * MapPanel.LENGTHSCALE),
				this.getHeight()
						- (int) (myMap.getEstimPosY() / MapPanel.LENGTH_EDGE_CM * MapPanel.LENGTHSCALE),
				(int) (myMap.getEstimPosX() / MapPanel.LENGTH_EDGE_CM
						* MapPanel.LENGTHSCALE + 10 * LENGTHSCALE
						* Math.cos(angle)),
				(int) (this.getHeight() - myMap.getEstimPosY()
						/ MapPanel.LENGTH_EDGE_CM * MapPanel.LENGTHSCALE + 10
						* LENGTHSCALE * Math.sin(-angle)));

		g.drawLine(
				(int) (myMap.getEstimPosX() / MapPanel.LENGTH_EDGE_CM * MapPanel.LENGTHSCALE),
				this.getHeight()
						- (int) (myMap.getEstimPosY() / MapPanel.LENGTH_EDGE_CM * MapPanel.LENGTHSCALE),
				(int) (myMap.getEstimPosX() / MapPanel.LENGTH_EDGE_CM
						* MapPanel.LENGTHSCALE + 10 * LENGTHSCALE
						* Math.cos(angle + diffSensor)),
				(int) (this.getHeight() - myMap.getEstimPosY()
						/ MapPanel.LENGTH_EDGE_CM * MapPanel.LENGTHSCALE + 10
						* LENGTHSCALE * Math.sin(-(angle + diffSensor))));

		g.drawLine(
				(int) (myMap.getEstimPosX() / MapPanel.LENGTH_EDGE_CM * MapPanel.LENGTHSCALE),
				this.getHeight()
						- (int) (myMap.getEstimPosY() / MapPanel.LENGTH_EDGE_CM * MapPanel.LENGTHSCALE),
				(int) (myMap.getEstimPosX() / MapPanel.LENGTH_EDGE_CM
						* MapPanel.LENGTHSCALE + 10 * LENGTHSCALE
						* Math.cos(angle + 2 * diffSensor)),
				(int) (this.getHeight() - myMap.getEstimPosY()
						/ MapPanel.LENGTH_EDGE_CM * MapPanel.LENGTHSCALE + 10
						* LENGTHSCALE * Math.sin(-(angle + 2 * diffSensor))));
	}

	public double getEstimPosX() {
		return myMap.getEstimPosX();
	}

	public double getEstimPosY() {
		return myMap.getEstimPosY();
	}
}
