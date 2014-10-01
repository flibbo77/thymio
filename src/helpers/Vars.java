package helpers;

public class Vars {
	public static final int NORTH_SOUTH = 0;
	public static final int SOUTH_NORTH = 1;
	public static final int WEST_EAST = 2;
	public static final int EAST_WEST = 3;

	public static final int NWEST_SEAST = 4;
	public static final int SEAST_NWEST = 5;
	public static final int NEAST_SWEST = 6;
	public static final int SWEST_NEAST = 7;

	public static final int MAP_X = 9;
	public static final int MAP_Y = 20;
	public static final double MAPFIELD_SIZE = 17;
	public static final int NUM_BARRIERS = 10;
	public static final boolean USE_DIAGONALS = true;

	public static final double THYMIO_SPEED_COEF = 3.75;
	public static final double THYMIO_BASEWIDTH = 95;
	public static final short MOTOR_CORR = 7;
	
	public static boolean rotate = false;

	public static final double ROT_MALUS = 3; // Wert, der in der Routenplanung
												// mit Dikstra jede nötige
												// Rotation mit einem Malus
												// versieht
	public static final short TURN_SPEED = 41;
	public static final short DRIVE_SPEED = 41;
	public static final long GET_TO_CENTER_OF_MAP_ELEMENT_DELAY = 1000;

	public static String toString(int var) {
		switch (var) {
		case 0:
			return "north-south";
		case 1:
			return "south-north";
		case 2:
			return "west-east";
		case 3:
			return "east-west";
		case 4:
			return "nwest-seast";
		case 5:
			return "seast-nwest";
		case 6:
			return "neast-swest";
		case 7:
			return "swest-neast";
		default:
			return "variable not found";
		}
	}

}
