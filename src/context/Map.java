package context;

import helpers.Vars;

import java.util.ArrayList;
import java.util.Random;

import math.Dikstra;
import math.KalmanFilter;

import org.ejml.data.DenseMatrix64F;

import observer.MapPanel;

public class Map {
	private int sizeX, sizeY; // number of elements in each direction
								// used to get a discrete model of the
								// environment
	private int thymioX, thymioY; // coordinates of MapElement where Thymio is
									// currently located on.
	private double posX, posY; // current position of Thymio in real units
	private double thymioTheta; // current orientation of Thymio in the global
								// coordinate system

	private double estPosX, estPosY; // estimated current position of Thymio in
										// real units
	private double estTheta; // estimated current orientation of Thymio in the
								// global coordinate system

	private MapElement[][] element; // Array of MapElement representing the
									// environment
	private double edgelength; // each element in this maps covers edgelength^2
								// square units.

	//public static final int N = 1000; // number of occupied elements
	//public static final boolean USE_DIAGONALS = true;

	private KalmanFilter posEstimate;
	
	private Path calculatedPath;

	public Map(int x, int y, double l) {
		edgelength = l;
		sizeX = x;
		sizeY = y;

		element = new MapElement[sizeX][sizeY];

		initMap();
		initFilter();
		calculatedPath = calculatePath(element[thymioX][thymioY], element[sizeX - 1][sizeY - 1]);
		printMap();
	}

	public Path getCalculatedPath() {
		return calculatedPath;
	}
	
	private void initFilter() {
		DenseMatrix64F F;
		DenseMatrix64F Q;
		DenseMatrix64F P;

		// state transition

		double[][] valF = { { 1, 0, 0, 0, 0 }, { 0, 1, 0, 0, 0 },
				{ 0, 0, 1, 0, 0 }, { 0, 0, 0, 0, 0 }, { 0, 0, 0, 0, 0 } };
		F = new DenseMatrix64F(valF);

		// process noise

		double[][] valQ = { { 0.0005, 0.00002, 0.00002, 0.00002, 0.00002 },
				{ 0.00002, 0.001, 0, 0, 0 }, { 0.00002, 0, 0.00001, 0, 0 },
				{ 0.00002, 0, 0, 0.001, 0 }, { 0.00002, 0, 0, 0, 0.00001 } };
		Q = new DenseMatrix64F(valQ);

		// initial state

		double[][] valP = { { 0.000001, 0, 0, 0, 0 }, { 0, 0.000001, 0, 0, 0 },
				{ 0, 0, 0.000001, 0, 0 }, { 0, 0, 0, 0.000001, 0 },
				{ 0, 0, 0, 0, 0.000001 } };
		P = new DenseMatrix64F(valP);

		double[] state = { 0, 0, 0, 0, 0 };

		posEstimate = new KalmanFilter();
		posEstimate.configure(F, Q);
		posEstimate.setState(DenseMatrix64F.wrap(5, 1, state), P);
	}

	public double getEdgeLength() {
		return edgelength;
	}

	public void setPose(double x, double y, double theta) {
		posX = x;
		posY = y;
		thymioTheta = theta;

		updateCurrentPos();
	}

	public void updatePose(double dF, double dR, double dt) {
		double[] delta = new double[5];

		delta[0] = Math.cos(thymioTheta) * dF;
		delta[1] = Math.sin(thymioTheta) * dF;
		delta[2] = dR;
		delta[3] = dF;
		delta[4] = dR;

		DenseMatrix64F Gu = DenseMatrix64F.wrap(5, 1, delta);

		thymioTheta = thymioTheta + dR;
		posX += delta[0];
		posY += delta[1];

		//System.out.println("Theta: " + Math.toDegrees(thymioTheta));

		// observation model

		double[][] valH = { { 0, 0, 0, 0, 0 }, { 0, 0, 0, 0, 0 } };
		valH[0][3] = 1 / dt;
		valH[1][4] = 1 / dt;
		DenseMatrix64F H = new DenseMatrix64F(valH);

		// sensor noise

		double[][] valR = { { 0.01, 0.0001 }, { 0.0001, 0.001 } };
		DenseMatrix64F R = new DenseMatrix64F(valR);

		// sensor values

		double[] speed = { dF / dt, dR / dt };

		posEstimate.predict(Gu);
		// System.out.println("cov x predict: " + posEstimate.getCovariance());
		posEstimate.update(DenseMatrix64F.wrap(2, 1, speed), H, R);

		DenseMatrix64F estimState = posEstimate.getState();
		estPosX = estimState.get(0);
		estPosY = estimState.get(1);
		estTheta = estimState.get(2);

		// System.out.println("cov x update : " + posEstimate.getCovariance());

		updateCurrentPos();
	}

	private void updateCurrentPos() {
		thymioX = (int) (posX / MapPanel.LENGTH_EDGE_CM);
		thymioY = (int) (posY / MapPanel.LENGTH_EDGE_CM);
	}

	public int getThymioX() {
		return thymioX;
	}

	public int getThymioY() {
		return thymioY;
	}

	public double getEstimPosX() {
		return estPosX;
	}

	public double getEstimPosY() {
		return estPosY;
	}
	
	public double getEstimOrientation() {
		return estTheta;
	}

	public double getPosX() {
		return posX;
	}

	public double getPosY() {
		return posY;
	}

	public double getThymioOrientation() {
		return thymioTheta;
	}

	private void initMap() {
		Random r = new Random();
		ArrayList<Integer> occupiedElements = new ArrayList<Integer>();

		// initialize each element of the map

		for (int x = 0; x < sizeX; x++) {
			for (int y = 0; y < sizeY; y++) {
				element[x][y] = new MapElement(x, y);
			}
		}

		// collect N distinct random numbers between 0 and the max number of
		// MapElements in this Map

		while (occupiedElements.size() < Vars.NUM_BARRIERS) {
			Integer pos = new Integer(r.nextInt(sizeX * sizeY - 1));
			if (!occupiedElements.contains(pos) && pos != 0)
				occupiedElements.add(pos);
		}
		
//		occupiedElements.set(0, 3);
//		occupiedElements.set(1, 5);
//		occupiedElements.set(2, 8);
//		occupiedElements.set(3, 12);

		// find MapElement corresponding to each of the numbers and set its
		// state to occupied

		for (int i = 0; i < Vars.NUM_BARRIERS; i++) {
			Integer pos = occupiedElements.get(i);
			int x = pos / sizeY; // integer division by number of columns
			int y = pos % sizeY; // rest of integer division by number of rows

			element[x][y].setOccupied();
		}
		setEdges();
		// printMap();

	}

	private void setEdges() {
		for (int i = 0; i < element.length; i++) {
			for (int j = 0; j < element[0].length; j++) {
				if (!element[i][j].isOccupied()) {
					setElementsEdges(element[i][j]);
				}
			}
		}
	}

	private void setElementsEdges(MapElement el) {
		// System.out.println("Parent: x: " + el.getPosX() + "y: " +
		// el.getPosY());

		MapElement link;
		// create ortogonal links;
		if (el.getPosX() > 0) {
			link = element[el.getPosX() - 1][el.getPosY()];
			if (!link.isOccupied())
				el.addEdge(link, 1, Vars._270_DEGREES);
		}
		if (el.getPosY() > 0) {
			link = element[el.getPosX()][el.getPosY() - 1];
			if (!link.isOccupied())
				el.addEdge(link, 1, Vars._180_DEGREES);
		}
		if (el.getPosX() < element.length - 1) {
			link = element[el.getPosX() + 1][el.getPosY()];
			if (!link.isOccupied())
				el.addEdge(link, 1, Vars._90_DEGREES);
		}
		if (el.getPosY() < element[0].length - 1) {
			link = element[el.getPosX()][el.getPosY() + 1];
			if (!link.isOccupied())
				el.addEdge(link, 1, Vars._0_DEGREES);
		}
		if (Vars.USE_DIAGONALS) {
			// create diagonal links
			if (el.getPosX() > 0 && el.getPosY() > 0) {
				link = element[el.getPosX() - 1][el.getPosY() - 1];
				if (!link.isOccupied())
					el.addEdge(link, Math.sqrt(2), Vars.SEAST_NWEST);
			}
			if (el.getPosY() > 0 && el.getPosX() < element.length - 1) {
				link = element[el.getPosX() + 1][el.getPosY() - 1];
				if (!link.isOccupied())
					el.addEdge(link, Math.sqrt(2), Vars.NEAST_SWEST);
			}
			if (el.getPosX() < element.length - 1
					&& el.getPosY() < element[0].length - 1) {
				link = element[el.getPosX() + 1][el.getPosY() + 1];
				if (!link.isOccupied())
					el.addEdge(link, Math.sqrt(2), Vars.NWEST_SEAST);
			}
			if (el.getPosX() > 0 && el.getPosY() < element[0].length - 1) {
				link = element[el.getPosX() - 1][el.getPosY() + 1];
				if (!link.isOccupied())
					el.addEdge(link, Math.sqrt(2), Vars.SWEST_NEAST);
			}
		}
	}

	public void printMap() {
		for (int x = 0; x < sizeY; x++) {
			for (int y = 0; y < sizeX; y++) {

				MapElement e = element[y][x];

				System.out.print(e.isOccupied() ? "X" : "F");
				System.out.print(e.onBeam() ? "B" : "-");
				System.out.print(e.isOnPath() ? "P" : "");
				System.out.print("\t");
			}

			System.out.print("\n");
		}
	}

	public Path followBeam(int x1, int y1, int x2, int y2) {
		Path p = new Path();
		int x = x1, y = y1;

		int w = x2 - x;
		int h = y2 - y;
		int dx1 = 0, dy1 = 0, dx2 = 0, dy2 = 0;
		if (w < 0)
			dx1 = -1;
		else if (w > 0)
			dx1 = 1;
		if (h < 0)
			dy1 = -1;
		else if (h > 0)
			dy1 = 1;
		if (w < 0)
			dx2 = -1;
		else if (w > 0)
			dx2 = 1;
		int longest = Math.abs(w);
		int shortest = Math.abs(h);
		if (!(longest > shortest)) {
			longest = Math.abs(h);
			shortest = Math.abs(w);
			if (h < 0)
				dy2 = -1;
			else if (h > 0)
				dy2 = 1;
			dx2 = 0;
		}
		int numerator = longest >> 1;
		for (int i = 0; i <= longest; i++) {
			p.add(new Coordinate(x, y));
			numerator += shortest;
			if (!(numerator < longest)) {
				numerator -= longest;
				x += dx1;
				y += dy1;
			} else {
				x += dx2;
				y += dy2;
			}
		}

		return p;
	}

	public int getSizeX() {
		return sizeX;
	}

	public int getSizeY() {
		return sizeY;
	}

	public boolean isOnBeam(int x, int y) {
		return element[x][y].onBeam();
	}

	private Path calculatePath(MapElement from, MapElement to) {
		Path path = new Path();
		Dikstra dikstra = new Dikstra();
		path = dikstra.calculatePath(this, from, to);
		System.out.println("Länge des Pfades: " + path.size());
		return path;
	}

	public MapElement[][] getArray() {
		return element;
	}
}
