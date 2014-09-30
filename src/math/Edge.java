package math;
import context.MapElement;

public class Edge {
		public MapElement link;
		public double weight;
		private int orientation;

		public int getOrientation() {
			return orientation;
		}

		public void setOrientation(int orientation) {
			this.orientation = orientation;
		}

		public Edge(MapElement v, double w, int orientation) {
			this.orientation = orientation;
			link = v;
			weight = w;
		}
	}