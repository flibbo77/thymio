package helpers;

public class Vars {
	public static final int _90_DEGREES = 0;
	public static final int _270_DEGREES = 1;
	public static final int _180_DEGREES = 2;
	public static final int _0_DEGREES = 3;

	public static final int NWEST_SEAST = 4;
	public static final int SEAST_NWEST = 5;
	public static final int NEAST_SWEST = 6;
	public static final int SWEST_NEAST = 7;

	public static final int MAP_X = 4;//9
	public static final int MAP_Y = 6;//20
	public static final double MAPFIELD_SIZE = 17;
	public static final int NUM_BARRIERS = 4;
	public static final boolean USE_DIAGONALS = false;

	public static final double THYMIO_SPEED_COEF = 3.73;
	public static final double THYMIO_BASEWIDTH = 95;
	public static final short MOTOR_CORR_STRAIGHT = 6;
	public static final short MOTOR_CORR_ROT = 0;
	
	public static final int BLACK_FIELD = 0;
	public static final int WHITE_FIELD = 1;
	
	public static final int POSITION_OK = 0;
	public static final int TO_FAR_RIGHT = 1;
	public static final int TO_FAR_LEFT = 2;
	
	public static final int START_FIELD_COLOR = BLACK_FIELD;
	
	public static boolean rotate = false;
	public static int actualRotDirection = 0;
	

	public static final double ROT_MALUS = 3; // Wert, der in der Routenplanung
												// mit Dikstra jede n�tige
												// Rotation mit einem Malus
												// versieht
	public static final short TURN_SPEED = 45;
	public static final short CORRECTION_SPEED = 5;
	public static final short DRIVE_SPEED = (short) (41 * THYMIO_SPEED_COEF);
	public static final long GET_TO_CENTER_OF_MAP_ELEMENT_DELAY = 2000;
	public static final int ROT_DIRECTION_TO_RIGHT = 0;
	public static final int ROT_DIRECTION_TO_LEFT = 1;
	public static final int ROT_DIRECTION_NONE = 2;
	public static final boolean USE_ROT_CORRECTION = true;

	public static String toString(int var) {
		switch (var) {
		case _90_DEGREES:
			return "90 Grad";
		case _270_DEGREES:
			return "270 Grad";
		case _180_DEGREES:
			return "180 Grad";
		case _0_DEGREES:
			return "0 Grad";
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
