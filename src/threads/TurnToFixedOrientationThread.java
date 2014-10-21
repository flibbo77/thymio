package threads;

import helpers.Vars;
import thymio.Thymio;

public class TurnToFixedOrientationThread extends Thread {

	int desiredOrientation;
	short speed;
	Thymio thy;

	public TurnToFixedOrientationThread(int orientation, Thymio myThymio) {
		desiredOrientation = orientation;
		this.speed = Vars.TURN_SPEED;
		thy = myThymio;
	}

	public void run() {
		// thy.rotate = true;
		Vars.rotate = true;
		
		thy.isDriving = true;
		thy.updatePose(System.currentTimeMillis());
		thy.setVRight(speed);
		thy.setVLeft((short) -speed);
		thy.updatePose(System.currentTimeMillis());

		double actualOrientation = calcThymioOrientation();
		while (Math.toDegrees(actualOrientation) < desiredOrientation) {

			System.out.println("Rotation_state: "
					+ Math.toDegrees(actualOrientation));
			thy.updatePose(System.currentTimeMillis());
			actualOrientation = calcThymioOrientation();
		}

		thy.setVLeft((short) 0);
		thy.setVRight((short) 0);
		thy.isDriving = false;

		thy.updatePose(System.currentTimeMillis());
		actualOrientation = calcThymioOrientation();

		doCorrectionIfNecessary(actualOrientation);
	}

	private double calcThymioOrientation() {
		double actOrientation = thy.myPanel.myMap.getThymioOrientation();
		actOrientation %= 2 * Math.PI;
		if (actOrientation < 0)
			actOrientation = 2 * Math.PI + actOrientation;
		return actOrientation;
	}

	private void doCorrectionIfNecessary(double actualOrientation) {
		//double temp = Math.toDegrees(desiredOrientation);
		System.out.println("Fehler: " + (Math.toDegrees(actualOrientation) - desiredOrientation));
		if (Math.toDegrees(actualOrientation) - desiredOrientation > 2) {
			
			thy.isDriving = true;
			speed = (short) -Vars.CORRECTION_SPEED;
			System.out.println("Rotation_state: "
					+ Math.toDegrees(actualOrientation));
			thy.updatePose(System.currentTimeMillis());
			thy.setVRight((short) (speed ));
			thy.setVLeft((short) (-speed ));
			thy.updatePose(System.currentTimeMillis());
			actualOrientation = calcThymioOrientation();
			while (Math.toDegrees(actualOrientation) > desiredOrientation) {
				System.out
						.println("Rotation_state: "
								+ Math.toDegrees(actualOrientation));
				thy.updatePose(System.currentTimeMillis());
				actualOrientation = calcThymioOrientation();
			}
			thy.setVLeft((short) 0);
			thy.setVRight((short) 0);
			thy.isDriving = false;
			// thy.rotate = false;
			Vars.rotate = false;
			thy.updatePose(System.currentTimeMillis());
		}
	}

}
