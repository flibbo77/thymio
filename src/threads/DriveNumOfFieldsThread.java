package threads;

import helpers.Vars;
import thymio.Thymio;

public class DriveNumOfFieldsThread extends Thread {
	private int numOfFields;
	private int fieldCount;
	private Thymio thy;
	

	public DriveNumOfFieldsThread(int numOfFields, Thymio thy) {
		this.numOfFields = numOfFields;
		this.thy = thy;
	}

	public void run() {
		fieldCount = 0;
		thy.driveStraight(Vars.DRIVE_SPEED);
		while(fieldCount <= numOfFields){
			thy.updatePose(System.currentTimeMillis());
			updateFieldCount();
		}
		try {
			Thread.sleep(Vars.GET_TO_CENTER_OF_MAP_ELEMENT_DELAY);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		thy.stopMove();
	}

	private void updateFieldCount() {
		// TODO Auto-generated method stub
		thy.getActualField();
	}
}
