package threads;

import helpers.Vars;
import thymio.Thymio;

public class DriveNumOfFieldsThread extends Thread {
	private int numOfFields;
	private int fieldCount;
	private Thymio thy;

	private int previousFieldNum = -1;

	public DriveNumOfFieldsThread(int numOfFields, Thymio thy) {
		this.numOfFields = numOfFields;
		this.thy = thy;
	}

	public void run() {
		fieldCount = 0;
		thy.driveStraight(Vars.DRIVE_SPEED);
		while (fieldCount < numOfFields) {
			long startLoop = System.currentTimeMillis();
			thy.updatePose(System.currentTimeMillis());
			if (thy.getStraightness() == Vars.TOO_FAR_TURNED_TO_LEFT) {
				System.out.println("too far left");
				correctDirection(Vars.CORR_RIGHT);
			} else if (thy.getStraightness() == Vars.TOO_FAR_TURNED_TO_RIGHT) {
				System.out.println("too far right");
				correctDirection(Vars.CORR_LEFT);
			}

			updateFieldCount();
			System.out.println("Time for DriveLoop: "
					+ (System.currentTimeMillis() - startLoop));
		}
		try {
			Thread.sleep(Vars.GET_TO_CENTER_OF_MAP_ELEMENT_DELAY);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		thy.stopMove();
	}

	private void correctDirection(int direction) {
		short speed = -Vars.CORRECTION_SPEED;
		thy.stopMove();
		if (direction == Vars.CORR_LEFT){
			//speed *= -1;
			thy.setVRight(speed);
		}else if (direction == Vars.CORR_RIGHT){
			thy.setVLeft(speed);
		}
		
		
		
		thy.setVLeft((short) speed);
		thy.setVRight((short) -speed);
		long startTime = System.currentTimeMillis();
		while(System.currentTimeMillis() - startTime < Vars.CORRECTION_TIME){
			//do nothing
		}
		thy.stopMove();
		thy.driveStraight(Vars.DRIVE_SPEED);
		
		
	}

	private void updateFieldCount() {
		int currentField = thy.getActualField();

		if (currentField != previousFieldNum) {
			++fieldCount;
		}

		previousFieldNum = currentField;
	}
}
