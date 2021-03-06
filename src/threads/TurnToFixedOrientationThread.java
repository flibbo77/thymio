package threads;

import helpers.Vars;
import thymio.Thymio;

public class TurnToFixedOrientationThread extends Thread {

	int desiredOrientation;
	short speed;
	int rotDirection;
	double actualOrientation;
	Thymio thy;

	public TurnToFixedOrientationThread(int orientation, Thymio myThymio) {
		System.out.println("orientation: " + orientation);
		desiredOrientation = orientation;
		System.out.println(Math.sin(Math.toRadians(desiredOrientation)));
		this.speed = Vars.TURN_SPEED;
		thy = myThymio;

		calcRotDirection();
	}

	private void calcRotDirection() {
		/*
		 * double actOrient = calcThymioOrientation(); if (desiredOrientation ==
		 * 0.0) { //if (actualFixedOrientation == 0) { //rotDirection =
		 * Vars.ROT_DIRECTION_NONE; //return; //} if (Math.toDegrees(actOrient)
		 * >= 180) rotDirection = Vars.ROT_DIRECTION_TO_LEFT; else rotDirection
		 * = Vars.ROT_DIRECTION_TO_RIGHT; return; } if (Math.cos(actOrient) > 0)
		 * { if (Math.sin(Math.toRadians(desiredOrientation)) > Math.sin(Math
		 * .toRadians(actOrient))) { rotDirection = Vars.ROT_DIRECTION_TO_LEFT;
		 * } else if (Math.sin(Math.toRadians(desiredOrientation)) == Math
		 * .sin(Math.toRadians(actOrient))) { rotDirection =
		 * Vars.ROT_DIRECTION_NONE; } else if
		 * (Math.sin(Math.toRadians(desiredOrientation)) < Math
		 * .sin(Math.toRadians(actOrient))) { rotDirection =
		 * Vars.ROT_DIRECTION_TO_RIGHT; } } else { if
		 * (Math.sin(Math.toRadians(desiredOrientation)) > Math.sin(Math
		 * .toRadians(actOrient))) { rotDirection = Vars.ROT_DIRECTION_TO_RIGHT;
		 * } else if (Math.sin(Math.toRadians(desiredOrientation)) == Math
		 * .sin(Math.toRadians(actOrient))) { rotDirection =
		 * Vars.ROT_DIRECTION_NONE; } else if
		 * (Math.sin(Math.toRadians(desiredOrientation)) < Math
		 * .sin(Math.toRadians(actOrient))) { rotDirection =
		 * Vars.ROT_DIRECTION_TO_LEFT; } }
		 * 
		 * Vars.actualRotDirection = getTurnDirection();
		 */

		switch (desiredOrientation) {
		case 0:
			switch (Vars.actualFixedOrientation) {
			case 0:
				rotDirection = Vars.ROT_DIRECTION_NONE;
				break;
			case 90:
				rotDirection = Vars.ROT_DIRECTION_TO_RIGHT;
				break;
			case 180:
				rotDirection = Vars.ROT_DIRECTION_TO_RIGHT;
				break;
			case 270:
				rotDirection = Vars.ROT_DIRECTION_TO_LEFT;
				break;
			}
			break;

		case 90:
			switch (Vars.actualFixedOrientation) {
			case 0:
				rotDirection = Vars.ROT_DIRECTION_TO_LEFT;
				break;
			case 90:
				rotDirection = Vars.ROT_DIRECTION_NONE;
				break;
			case 180:
				rotDirection = Vars.ROT_DIRECTION_TO_RIGHT;
				break;
			case 270:
				rotDirection = Vars.ROT_DIRECTION_TO_LEFT;
				break;
			}
			break;

		case 180:
			switch (Vars.actualFixedOrientation) {
			case 0:
				rotDirection = Vars.ROT_DIRECTION_TO_RIGHT;
				break;
			case 90:
				rotDirection = Vars.ROT_DIRECTION_TO_LEFT;
				break;
			case 180:
				rotDirection = Vars.ROT_DIRECTION_NONE;
				break;
			case 270:
				rotDirection = Vars.ROT_DIRECTION_TO_RIGHT;
				break;
			}
			break;

		case 270:
			switch (Vars.actualFixedOrientation) {
			case 0:
				rotDirection = Vars.ROT_DIRECTION_TO_RIGHT;
				break;
			case 90:
				rotDirection = Vars.ROT_DIRECTION_NONE;
				break;
			case 180:
				rotDirection = Vars.ROT_DIRECTION_TO_LEFT;
				break;
			case 270:
				rotDirection = Vars.ROT_DIRECTION_NONE;
				break;
			}
			break;
		}
		Vars.actualRotDirection = getTurnDirection();
	}

	public void run() {
		// thy.rotate = true;
		// rotDirection = getTurnDirection();
		if (rotDirection == Vars.ROT_DIRECTION_NONE) {
			System.out.println("rotDirecton: None");
			return;
		}
		speed *= getTurnDirection();

		thy.isDriving = true;
		// thy.updatePose(System.currentTimeMillis());

		rotate();

	}

	private int getTurnDirection() {
		if (rotDirection == Vars.ROT_DIRECTION_TO_LEFT)
			return 1;
		else if (rotDirection == Vars.ROT_DIRECTION_TO_RIGHT)
			return -1;

		return 0;
	}

	private void rotate() {
		boolean didAnything = false;
		Vars.rotate = true;
		long startTime = System.currentTimeMillis();
		thy.updatePose(System.currentTimeMillis());

		int colorBeforeTurn = thy.getActualField();
		thy.setVLeft((short) -speed);
		thy.setVRight(speed);

		// thy.updatePose(System.currentTimeMillis());

		// actualOrientation = calcThymioOrientation();

		while (checkCondition(startTime)) {
			didAnything = true;
			// thy.updatePose(System.currentTimeMillis());
			// actualOrientation = calcThymioOrientation();
			// System.out.println("Rotation_state: "
			// + Math.toDegrees(actualOrientation));
		}
		// thy.updatePose(System.currentTimeMillis());
		thy.setVLeft((short) 0);
		thy.setVRight((short) 0);
		Vars.rotate = false;
		thy.isDriving = false;

		thy.updatePose(System.currentTimeMillis());
		// actualOrientation = calcThymioOrientation();

		if (thy.getActualField() != colorBeforeTurn) {
			long startDriveBackTime = System.currentTimeMillis();
			thy.driveStraight((short) -Vars.DRIVE_SPEED);
			while (System.currentTimeMillis() - startDriveBackTime < Vars.CORRECTION_TIME) {
				// do nothing
			}
			thy.stopMove((short) -Vars.CORRECTION_SPEED);
		}

		if (Vars.USE_ROT_CORRECTION)
			if (didAnything) {
				// doCorrectionIfNecessary();
			}

		Vars.actualFixedOrientation = desiredOrientation;
		System.out.println("actualFixedOrient: " + Vars.actualFixedOrientation);
	}

	private boolean checkCondition(long startTime) {
		/*
		 * System.out.println("act: " + Math.toDegrees(actualOrientation));
		 * System.out.println("desired: " + desiredOrientation); switch
		 * (desiredOrientation) { case 0: if (rotDirection ==
		 * Vars.ROT_DIRECTION_TO_RIGHT) { if (Math.sin(actualOrientation) > 0) {
		 * return true; } } else if (rotDirection == Vars.ROT_DIRECTION_TO_LEFT)
		 * { if (Math.sin(actualOrientation) < 0) { return true; } }
		 * 
		 * break; case 90: if (rotDirection == Vars.ROT_DIRECTION_TO_RIGHT) { if
		 * (Math.cos(actualOrientation) < 0) { return true; } } else if
		 * (rotDirection == Vars.ROT_DIRECTION_TO_LEFT) { if
		 * (Math.cos(actualOrientation) > 0) { return true; } } break; case 180:
		 * if (rotDirection == Vars.ROT_DIRECTION_TO_RIGHT) { if
		 * (Math.sin(actualOrientation) < 0) { return true; } } else if
		 * (rotDirection == Vars.ROT_DIRECTION_TO_LEFT) { if
		 * (Math.sin(actualOrientation) > 0) { return true; } }
		 * 
		 * break;
		 * 
		 * case 270: if (rotDirection == Vars.ROT_DIRECTION_TO_RIGHT) { if
		 * (Math.cos(actualOrientation) > 0) { return true; } } else if
		 * (rotDirection == Vars.ROT_DIRECTION_TO_LEFT) { if
		 * (Math.cos(actualOrientation) < 0) { return true; } } break; }
		 */

		while (System.currentTimeMillis() - startTime < Vars.TURN_TIME) {
			return true;
		}

		return false;
	}

	/*
	 * private double calcThymioOrientation() { double actOrientation =
	 * thy.myPanel.myMap.getThymioOrientation(); actOrientation %= 2 * Math.PI;
	 * if (actOrientation < 0) actOrientation = 2 * Math.PI + actOrientation;
	 * return actOrientation; }
	 */

	/*
	 * private void doCorrectionIfNecessary() { double failure =
	 * Math.toDegrees(actualOrientation) - desiredOrientation;
	 * 
	 * System.out.println("Fehler: " + failure); if (Math.abs(failure) > 1) {
	 * speed = Vars.CORRECTION_SPEED; calcRotDirection(); run();
	 * 
	 * } }
	 */

}
