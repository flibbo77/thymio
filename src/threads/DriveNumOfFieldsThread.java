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
		fieldCount = -1;
		newFieldTimer = System.currentTimeMillis();
		thy.driveStraight(Vars.DRIVE_SPEED);
		while ((fieldCount < numOfFields) && !isInterrupted()) {
			long startLoop = System.currentTimeMillis();
			thy.updatePose(System.currentTimeMillis());
			
			doSensorCorrection();
			
			updateFieldCount();
			
			if (thy.getStraightness() == Vars.TOO_FAR_TURNED_TO_LEFT) {
				System.out.println("too far left");
				correctDirection(Vars.CORR_RIGHT);
			} else if (thy.getStraightness() == Vars.TOO_FAR_TURNED_TO_RIGHT) {
				System.out.println("too far right");
				correctDirection(Vars.CORR_LEFT);
			}
			
			//doFieldCheckCorrection();

			
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

	
	/**
	 * T
	 */
	private void doFieldCheckCorrection() {
		// Get direction to correct first
		int direction = 0;
		if (thy.getStraightness() == Vars.TOO_FAR_TURNED_TO_LEFT) {
			direction = Vars.CORR_RIGHT;
		} else if (thy.getStraightness() == Vars.TOO_FAR_TURNED_TO_RIGHT) {
			direction = Vars.CORR_LEFT;
		} else {
			return;
		}
		
		long timeDifOfFieldChange = System.currentTimeMillis() - newFieldTimer;
		final short CORRECTION_VALUE = 55;
		
		if (timeDifOfFieldChange <= 270) {
			thy.stopMove(Vars.DRIVE_SPEED);
			if (direction == Vars.CORR_RIGHT) {
				thy.setVRight((short) -CORRECTION_VALUE);
			} else if (direction == Vars.CORR_LEFT) {
				thy.setVLeft((short) -CORRECTION_VALUE);
			}
			long time = System.currentTimeMillis();
			while (System.currentTimeMillis() - time < 600) {
				// nix
			}
			thy.stopMove();
			thy.driveStraight(Vars.DRIVE_SPEED);
		}
	}

	/**
	 * This method checks wether thymio is very close to wall.
	 * This will be detected by the 2 left and the 2 right sensors. Middle one is ignored.
	 * If thymio is too close, it starts a correction.
	 */
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

	/**
	 * This method gets called by doSensorCorrection. It will stop the motor. Depending on
	 * the paramter it will start the left or right motor to correct for 0,8sec.
	 * After that driving start will be started again.
	 * @param wallIsLeft true when he has to correct to the right hand side, else false
	 */
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
		
		if(System.currentTimeMillis() - newFieldTimer > Vars.TIME_ONE_FIELD){
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

	/**
	 * This method gets called by the run method all time.
	 * It checks if the current field color differs from the old one. If so thymio has one field less to drive.
	 * 
	 * Furthermore it will notify a Listener (FieldDrivenListener) that a field was driven (if one is set)
	 */
	private void updateFieldCount() {
		int currentFieldColor = thy.getActualField();

		if (currentFieldColor != previousFieldColor) {
			++fieldCount;
			newFieldTimer = System.currentTimeMillis();
			
			if (m_fieldDrivenListener != null) {
				m_fieldDrivenListener.thymioDroveField();
			}
		}

		previousFieldColor = currentFieldColor;
	}
}
