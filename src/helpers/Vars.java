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

	public static final int MAP_X = 9;// 9
	public static final int MAP_Y = 20;// 20
	public static final double MAPFIELD_SIZE = 17;
	public static final int NUM_BARRIERS = 10;
	public static final boolean DRAW_FIELD_IDS = false;
	public static final boolean USE_DIAGONALS = false;

	public static final double THYMIO_SPEED_COEF = 3.73;
	public static final double THYMIO_BASEWIDTH = 95;
	public static final short MOTOR_CORR_STRAIGHT = 5; // 6
	public static final short MOTOR_CORR_ROT = 5;

	public static final int BLACK_FIELD = 0;
	public static final int WHITE_FIELD = 1;

	public static final int POSITION_OK = 0;
	public static final int TOO_FAR_TURNED_TO_LEFT = 1;
	public static final int TOO_FAR_TURNED_TO_RIGHT = 2;
	public static final int CORRECT_STRAIGHTNESS = 3;

	public static boolean rotate = false;
	public static int actualRotDirection = 0;

	/**
	 * Wert, der in der Routenplanung mit Dikstra jede n�tige Rotation mit einem
	 * Malus versieht
	 */
	public static final double ROT_MALUS = 2;

	public static final short TURN_SPEED = 55;
	public static final short CORRECTION_SPEED = 42;

	public static final short DRIVE_SPEED = (short) (42 * THYMIO_SPEED_COEF);
	public static final long GET_TO_CENTER_OF_MAP_ELEMENT_DELAY = 2400;
	public static final int ROT_DIRECTION_TO_RIGHT = 0;
	public static final int ROT_DIRECTION_TO_LEFT = 1;
	public static final int ROT_DIRECTION_NONE = 2;
	public static final boolean USE_ROT_CORRECTION = true;
	public static final int CORR_RIGHT = -1;
	public static final int CORR_LEFT = 1;
	public static final int FRONT_SENSOR_SIGN_DIFF = 190;
	public static final long CORRECTION_TIME = 700;
	public static final int COLLISION_SENSOR_VALUE = 2525;
	public static final long TURN_TIME = 4000;
	public static final long DRIVE_BACKWARDS_TIME = 400;
	public static final long COLLISION_AVOIDANCE_TIME = 800;
	public static final short COLLISION_AVOIDANCE_SPEED = 50;
	public static int actualFixedOrientation = 0;
	public static final long TIME_ONE_FIELD = 2800;

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
