package math;

import helpers.Vars;

import java.util.ArrayList;
import java.util.Collections;

import context.Coordinate;
import context.Map;
import context.MapElement;
import context.Path;

public class Dikstra {
	MapElement start;
	MapElement goal;
	MapElement[][] map;
	ArrayList<MapElement> vertices;

	public Path calculatePath(Map map, MapElement from, MapElement to) {
		this.map = map.getArray();
		vertices = new ArrayList<MapElement>();
		fillArrayList(map);

		Path path = new Path();

		start = from;
		goal = to;

		start.distance = 0;

		dijkstra();
		System.out.println("goal: " + goal.getPosX() + "\t" + goal.getPosY());
		System.out.println("Gesamtstrecke: " + goal.distance);
		int orientationSave = -1;
		int turnCount = 0;
		while (goal != null) {

			path.add(new Coordinate(goal.getPosX(), goal.getPosY()));
			goal.setOnPath();
			goal.setPreviousEdge();
			if (orientationSave != -1 && goal.getPreviousEdge() != null) {
				if (orientationSave != goal.getPreviousEdge().getOrientation())
					turnCount++;
			}
			if (goal.getPreviousEdge() != null)
				orientationSave = goal.getPreviousEdge().getOrientation();
			System.out.println("goal:  " + goal.toString());
			goal = goal.prev;

		}
		System.out.println("Anzahl der Drehungen: " + turnCount);
		Collections.reverse(path);

		return path;
	}

	private void dijkstra() {
		MapElement v;
		while ((v = find_smallest(vertices)) != null) {
			System.out.println("Knotne: " + v.toString() + "dist: "
					+ v.distance);
			v.visited = 1;
			v.setPreviousEdge();
			for (int i = 0; i < v.edges.size(); i++) {
				MapElement next = v.edges.get(i).link;

				// Aufschlag für Ausführen von Rotation
				double rotationMalus = 0;
				if (v.getPreviousEdge() != null)
					if (v.edges.get(i).getOrientation() != v.getPreviousEdge()
							.getOrientation()) {
						rotationMalus = Vars.ROT_MALUS;
					}
				double newdist = v.distance + v.edges.get(i).weight
						+ rotationMalus;
				System.out.println("new dist: " + newdist);
				if (next.distance > newdist) {
					next.distance = newdist;
					next.prev = v;
				}
			}
			if (v == goal)
				break;
		}
	}

	// Dijkstra helper function
	MapElement find_smallest(ArrayList<MapElement> list) {
		double value = 10000000;
		MapElement result = null;
		for (int i = 0; i < list.size(); i++)

			if ((list.get(i).distance < value) && (list.get(i).visited == 0)) {
				value = list.get(i).distance;
				System.out.println("value: " + value);

				result = list.get(i);
			}
		return result;
	}

	private void fillArrayList(Map map) {
		MapElement[][] mapArray = map.getArray();
		for (int i = 0; i < mapArray.length; i++) {
			for (int j = 0; j < mapArray[0].length; j++) {
				vertices.add(mapArray[i][j]);
			}

		}
	}
}
