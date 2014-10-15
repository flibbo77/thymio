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
		double orientationAtStart = thy.myPanel.myMap.getThymioOrientation();

		thy.isDriving = true;
		if (desiredOrientation < 0)
			speed *= -1;

		thy.setVLeft(speed);
		thy.setVRight((short) -speed);
		double actualOrientation = thy.myPanel.myMap.getThymioOrientation();
		while (Math.abs(Math.toDegrees(orientationAtStart - actualOrientation)) < Math
				.abs(desiredOrientation)) {

			System.out.println("Rotation_state: "
					+ Math.toDegrees(orientationAtStart - actualOrientation));
			thy.updatePose(System.currentTimeMillis());
			actualOrientation = thy.myPanel.myMap.getThymioOrientation();
		}
		thy.setVLeft((short) 0);
		thy.setVRight((short) 0);
		thy.isDriving = false;
		thy.updatePose(System.currentTimeMillis());
		actualOrientation = thy.myPanel.myMap.getThymioOrientation();

		doCorrectionIfNecessary(orientationAtStart, actualOrientation);
	}

	private void doCorrectionIfNecessary(double orientationAtStart,
			double actualOrientation) {
		if (Math.toDegrees(orientationAtStart - actualOrientation)
				- desiredOrientation > 2) {
			thy.isDriving = true;
			speed = (short) -speed;
			System.out.println("Rotation_state: "
					+ Math.toDegrees(orientationAtStart - actualOrientation));
			thy.setVLeft((short) (speed / 5));
			thy.setVRight((short) (-speed / 5));
			thy.updatePose(System.currentTimeMillis());
			actualOrientation = thy.myPanel.myMap.getThymioOrientation();
			while (Math.toDegrees(orientationAtStart - actualOrientation) > desiredOrientation) {
				System.out
						.println("Rotation_state: "
								+ Math.toDegrees(orientationAtStart
										- actualOrientation));
				thy.updatePose(System.currentTimeMillis());
				actualOrientation = thy.myPanel.myMap.getThymioOrientation();
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
