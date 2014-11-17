package context;

import helpers.Vars;

import java.util.ArrayList;

import math.Edge;

public class MapElement {
	private int posX; // position in the map
	private int posY;

	private boolean occupied; // element is known in advance to be occupied
	private double probOccupied; // estimation of the state of this element
									// from observations

	private boolean onBeam; // set temporarily if the element is hit by infrared
							// beam
	private boolean onPath;

	// private int direction;

	// variables for search algorithm
	public ArrayList<Edge> edges;
	public Edge prevEdge;

	public int visited;
	public double distance;
	public MapElement prev;

	public MapElement(int posX, int posY, boolean occupied) {
		super();
		this.posX = posX;
		this.posY = posY;
		this.occupied = occupied;
		onPath = false;
		prevEdge = null;

		visited = 0;
		distance = 1000000;
		edges = new ArrayList<Edge>();
	}

	public int getPosX() {
		return posX;
	}

	public int getPosY() {
		return posY;
	}

	public boolean isOccupied() {
		return occupied;
	}

	public void setOccupied() {
		occupied = true;
	}

	public double getProbOccupied() {
		return probOccupied;
	}

	public MapElement(int posX, int posY) {
		this(posX, posY, false);
	}

	public boolean onBeam() {
		return onBeam;
	}

	public void setOnBeam(boolean onBeam) {
		this.onBeam = onBeam;
	}

	public void setOnPath() {
		onPath = true;
	}

	public boolean isOnPath() {
		return onPath;
	}

	public void addEdge(MapElement el, double weight, int orientation) {
		Edge e = new Edge(el, weight, orientation);

		edges.add(e);
	}

	public void setPreviousEdge() {
		if (prev != null && prev.edges != null)
			for (Edge e : prev.edges) {
				if (e.link == this)
					prevEdge = e;
			}
	}

	public Edge getPreviousEdge() {
		return prevEdge;
	}

	public String toString() {

		if (prevEdge == null)
			return "XPos: " + getPosX() + "\tYPos: " + getPosY();

		return "XPos: " + getPosX() + "\tYPos: " + getPosY() + "\t"
				+ Vars.toString(prevEdge.getOrientation());
	}

}
