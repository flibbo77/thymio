package threads;

import helpers.Vars;
import thymio.Thymio;

public class DriveNumOfFieldsThread extends Thread {
	private int numOfFields;
	private int fieldCount;
	private Thymio thy;

	private FieldDrivenListener m_fieldDrivenListener;
	
	private int previousFieldColor = -1;
	private long newFieldTimer;

	public DriveNumOfFieldsThread(int numOfFields, Thymio thymio) {
		this.numOfFields = numOfFields;
		this.m_fieldDrivenListener = null;
		this.thy = thymio;
		this.thy.updatePose(System.currentTimeMillis());
		this.previousFieldColor = thy.getActualField();
//		newFieldTimer = System.currentTimeMillis();
		System.out.println("feld bei start: " + previousFieldColor);
	}
	
	public void setFieldDrivenListener(FieldDrivenListener m_fdl) {
		m_fdl = m_fieldDrivenListener;
	}

	public void run() {
		fieldCount = 0;
		thy.driveStraight(Vars.DRIVE_SPEED);
		while ((fieldCount < numOfFields) && !isInterrupted()) {
			long startLoop = System.currentTimeMillis();
			thy.updatePose(System.currentTimeMillis());
			
			doSensorCorrection();
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
		
		if (!isInterrupted()) {
			try {
				Thread.sleep(Vars.GET_TO_CENTER_OF_MAP_ELEMENT_DELAY);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		thy.stopMove(Vars.DRIVE_SPEED);
	}

	@SuppressWarnings("null")
	private void doSensorCorrection() {
		int sensorId = thy.checkCollision();
		if (sensorId != -1) {
			
			switch(sensorId) {
			case 0: // left
			case 1:
				wallCollCorrection(true);
				break;
			case 2: // middle
				break;
			case 3:
			case 4: // right
				wallCollCorrection(false);
				break;
			}
		}
	}

	private void wallCollCorrection(boolean wallIsLeft) {
		thy.stopMove(Vars.DRIVE_SPEED);
		long startTime = System.currentTimeMillis();

		if (wallIsLeft) {
			// drive a bit to the right
			thy.setVRight((short) -Vars.CORRECTION_SPEED);
		} else {
			// drive a bit to the left
			thy.setVLeft((short) -Vars.CORRECTION_SPEED);

		}
		
		while(System.currentTimeMillis() - startTime < Vars.COLLISION_AVOIDANCE_TIME){
			//do nothing
		}
		thy.stopMove((short) -Vars.DRIVE_SPEED);
		thy.driveStraight(Vars.DRIVE_SPEED);
	}

	private void correctDirection(int direction) {
		short speed = -Vars.CORRECTION_SPEED;
		if(System.currentTimeMillis() - newFieldTimer < Vars.TIME_ONE_FIELD){
			speed = (short)(-speed * 4);
		}
		thy.stopMove(Vars.DRIVE_SPEED);
		if (direction == Vars.CORR_LEFT){
			//speed *= -1;
			thy.setVLeft(speed);
		}else if (direction == Vars.CORR_RIGHT){
			thy.setVRight(speed);
		}
		
		
		
		long startTime = System.currentTimeMillis();
		while(System.currentTimeMillis() - startTime < Vars.CORRECTION_TIME){
			//do nothing
		}
		if (direction == Vars.CORR_LEFT){
			//speed *= -1;
			thy.setVLeft((short) 0);
		}else if (direction == Vars.CORR_RIGHT){
			thy.setVRight((short) 0);
		}
		thy.driveStraight((short) -Vars.DRIVE_SPEED);
		startTime = System.currentTimeMillis();
		while(System.currentTimeMillis() - startTime < Vars.DRIVE_BACKWARDS_TIME){
			//do nothing
		}
		thy.stopMove((short) -Vars.DRIVE_SPEED);
		thy.driveStraight(Vars.DRIVE_SPEED);
		
		
	}
	
	public interface FieldDrivenListener {
		public void thymioDroveField();
	}

	private void updateFieldCount() {
		int currentFieldColor = thy.getActualField();

		if (currentFieldColor != previousFieldColor) {
			++fieldCount;
			newFieldTimer = System.currentTimeMillis();
			System.out.println((char)7);
			
			if (m_fieldDrivenListener != null) {
				m_fieldDrivenListener.thymioDroveField();
			}
		}

		previousFieldColor = currentFieldColor;
	}
}
