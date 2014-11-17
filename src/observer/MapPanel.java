package observer;

import helpers.Vars;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;

import javax.swing.JFrame;
import javax.swing.JPanel;

import context.Map;
import context.MapElement;
import context.Path;

public class MapPanel extends JPanel {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public Map myMap;
	public static final int LENGTHSCALE = 30;
	public static final double LENGTH_EDGE_CM = 3.5;

	private int[] m_ThymioPos;

	public MapPanel(Map m, JFrame f) {
		myMap = m;
		m_ThymioPos = new int[2];
		m_ThymioPos[0] = 0;
		m_ThymioPos[1] = 0;

		this.setPreferredSize(new Dimension(myMap.getSizeX() * LENGTHSCALE,
				myMap.getSizeY() * LENGTHSCALE));
		this.setMaximumSize(new Dimension(myMap.getSizeX() * LENGTHSCALE, myMap
				.getSizeY() * LENGTHSCALE));
		this.setMinimumSize(new Dimension(myMap.getSizeX() * LENGTHSCALE, myMap
				.getSizeY() * LENGTHSCALE));
	}

	public void setPose(double x, double y, double theta) {
		myMap.setPose(x, y, theta);
		this.repaint();
	}

	public void updatePose(double dF, double dR, double dt) {
		myMap.updatePose(dF, dR, dt);
		this.repaint();
	}

	public void setNewThymioPosition(int posChanges, Path path) {
		if (posChanges >= path.size()) {
			return;
		}
		m_ThymioPos[0] = path.get(posChanges).getX();
		m_ThymioPos[1] = path.get(posChanges).getY();
		this.repaint();
	}

	public void paint(Graphics g) {

		g.setColor(Color.WHITE);
		g.clearRect(0, 0, this.getWidth(), this.getHeight());

		g.setColor(Color.BLACK);

		/*
		 * Draw fields
		 */
		g.setColor(Color.LIGHT_GRAY);
		for (int i = 0; i < myMap.getSizeX(); i++) {

			int curEvenCheck = 0;
			if (i % 2 == 1) {
				curEvenCheck = 1;
			} else {
				curEvenCheck = 0;
			}

			for (int j = 0; j < myMap.getSizeY(); j++) {
				if (j % 2 == curEvenCheck) {
					continue;
				}

				g.fillRect(LENGTHSCALE * i, LENGTHSCALE * j, LENGTHSCALE,
						LENGTHSCALE);
			}
		}

		for (int x = 0; x < myMap.getSizeX(); x++) {
			for (int y = 0; y < myMap.getSizeY(); y++) {

				// Draw Obstacles
				MapElement curElement = myMap.getArray()[x][y];
				if (curElement.isOccupied()) {
					g.setColor(Color.PINK);
					g.fillRect(LENGTHSCALE * x, LENGTHSCALE * y, LENGTHSCALE,
							LENGTHSCALE);
				}

				// Draw Thymio
				if (x == m_ThymioPos[0] && y == m_ThymioPos[1]) {
					g.setColor(Color.RED);
					g.fillRect(LENGTHSCALE * x + LENGTHSCALE / 2 - LENGTHSCALE
							/ 4, LENGTHSCALE * y + LENGTHSCALE / 2
							- LENGTHSCALE / 4, LENGTHSCALE / 2, LENGTHSCALE / 2);
				}

				// Draw Path
				if (curElement.isOnPath()) {
					g.setColor(Color.BLACK);
					g.fillRect(LENGTHSCALE * x + LENGTHSCALE / 2 - LENGTHSCALE
							/ 8, LENGTHSCALE * y + LENGTHSCALE / 2
							- LENGTHSCALE / 8, LENGTHSCALE / 4, LENGTHSCALE / 4);
				}

				// Draw field Ids (standard: deactivated)
				if (Vars.DRAW_FIELD_IDS) {
					g.setColor(Color.BLACK);
					g.setFont(new Font("TimesRoman", Font.PLAIN, 8));
					g.drawString("(" + x + "," + y + ")", LENGTHSCALE * x
							+ LENGTHSCALE / 10, LENGTHSCALE * y + LENGTHSCALE
							/ 5);
				}

			}
		}

	}

	public double getEstimPosX() {
		return myMap.getEstimPosX();
	}

	public double getEstimPosY() {
		return myMap.getEstimPosY();
	}

	public double getOrientation() {
		return myMap.getEstimOrientation();
	}
}
