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
		while(fieldCount <= numOfFields){
			long startLoop = System.currentTimeMillis();
			thy.updatePose(System.currentTimeMillis());
			updateFieldCount();
			System.out.println("Time for DriveLoop: " +(System.currentTimeMillis() - startLoop));
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
		int currentField = thy.getActualField();
		
		if (currentField != previousFieldNum) {
			++fieldCount;
		}
		
		previousFieldNum = currentField;
	}
}
