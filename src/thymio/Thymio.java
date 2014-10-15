package thymio;

import helpers.Vars;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import observer.MapPanel;
import observer.ThymioInterface;

public class Thymio {
	private short vleft;
	private short vright;
	public ThymioInterface myInterface;
	private ThymioDrivingThread myControlThread;
	private ThymioClient myClient;
	private long lastTimeStamp;
	private long startTimeStamp;
	private long timeCounter;
	public MapPanel myPanel;
	private PrintWriter logData;
	private double theta = 0;
	private short rightMotorSpeed;
	private short leftMotorSpeed;
	private int actualField;
	private int inFieldPosition;

	public boolean isDriving = false;
	public boolean rotate = false;
	private int degrees = 0;
	private long calcTurnTime = 0;
	private int turnSpeed;

	public static final double MAXSPEED = 500;
	public static final double SPEEDCOEFF = 2.93;
	public static final double BASE_WIDTH = 95;
	public static final int ODOM_THRESH = 30;

	public Thymio(MapPanel p) {
		vleft = vright = 0;

		myPanel = p;
		myClient = new ThymioClient();
		myInterface = new ThymioInterface(this);
		myControlThread = new ThymioDrivingThread(this);
		myControlThread.setName("DrivingThread");
		myControlThread.start();
		lastTimeStamp = Long.MIN_VALUE;
		actualField = Vars.START_FIELD_COLOR;
		inFieldPosition = Vars.POSITION_OK;

		setVLeft((short) 0);
		setVRight((short) 0);

		try {
			logData = new PrintWriter(new FileWriter("./logdata.csv"));
			logData.println("motor.left.speed\tmotor.right.speed\tdelta x observed\tdelta x computed\tdelta theta observed\tdelta theta computed\tthetaSum\tpos X\tposY\tvertical 0\tvertical 1");
			logData.flush();
		} catch (IOException e) {

			e.printStackTrace();
		}
	}

	public ThymioInterface getInterface() {
		return myInterface;
	}

	public int getVLeft() {
		return vleft;
	}

	public synchronized void setVLeft(short v) {
		ArrayList<Short> data = new ArrayList<Short>();
		this.vleft = v;

		data.add(new Short(v));
		myClient.setVariable("motor.left.target", data);
	}

	public synchronized void setVRight(short v) {
		ArrayList<Short> data = new ArrayList<Short>();
		this.vright = (short) (v);

		data.add(new Short(v));
		myClient.setVariable("motor.right.target", data);
	}

	public int getVRight() {
		return vright;
	}

	public synchronized void updatePose(long now) {

		List<Short> sensorData;
		if (lastTimeStamp > Long.MIN_VALUE) {

			long dt = now - lastTimeStamp;
			double secsElapsed = ((double) dt) / 1000.0;
			// System.out.println("time: " + secsElapsed);
			double distForward; // distance passed in secsElpased in forward
								// direction of the robot
			double distRotation; // angle covered in secsElapsed around Thymio's
									// center
			short odomLeft = Short.MIN_VALUE, odomRight = Short.MIN_VALUE;
			double odomForward;
			double odomRotation;
			short motorCorrection = 0;

			int proxGroundLeft;
			int proxGroundRight;
			if (!Vars.rotate)
				motorCorrection = Vars.MOTOR_CORR;

			sensorData = myClient.getVariable("motor.left.speed");
			if (sensorData != null) {
				odomLeft = sensorData.get(0);
				leftMotorSpeed = odomLeft;
			} else
				System.out.println("no data for motor.left.speed");
			sensorData = myClient.getVariable("motor.right.speed");
			if (sensorData != null) {
				odomRight = (short) ((short) sensorData.get(0) - motorCorrection);
				rightMotorSpeed = odomRight;
			} else
				System.out.println("no data for motor.right.speed");

			sensorData = myClient.getVariable("prox.ground.delta");
			proxGroundLeft = sensorData.get(0);
			proxGroundRight = sensorData.get(1);

			checkInFieldPosition(proxGroundLeft, proxGroundRight);
			checkActualFieldColor(proxGroundLeft, proxGroundRight);

			if (odomLeft == Short.MIN_VALUE || odomRight == Short.MIN_VALUE)
				return;
			if (Math.abs(odomLeft) <= ODOM_THRESH && !isDriving)
				odomLeft = 0;
			if (Math.abs(odomRight) <= ODOM_THRESH && !isDriving)
				odomRight = 0;

			logData.print(odomLeft + "\t" + odomRight + "\t");
			System.out.println("links: " + odomLeft + "rechts: " + odomRight);

			odomForward = secsElapsed * (odomLeft + odomRight)
					/ (2.0 * 10.0 * SPEEDCOEFF); // estimated distance in cm
													// travelled is secsElapsed
													// seconds.
			odomRotation = 0.5 * secsElapsed
					* Math.atan2(odomRight - odomLeft, BASE_WIDTH);
			// System.out.println("odomRot: " + odomRotation);

			distForward = myInterface.getVForward() * secsElapsed;
			distRotation = Math.PI / 180 * myInterface.getOrientation()
					* secsElapsed;

			theta += odomRotation;

			logData.print(odomForward + "\t" + distForward + "\t"
					+ new BigDecimal(odomRotation) + "\t" + distRotation + "\t"
					+ theta + "\t");

			// if (distForward == 0 && distRotation == 0)
			// myPanel.updatePose(distForward, distRotation, secsElapsed);
			// else
			myPanel.updatePose(odomForward, odomRotation, secsElapsed);

			logData.print(myPanel.getEstimPosX() + "\t"
					+ myPanel.getEstimPosY() + "\t");
			logData.println(sensorData.get(0) + "\t" + sensorData.get(1));
			logData.flush();
		}
		lastTimeStamp = now;

		// notify();
	}

	private void checkActualFieldColor(int proxGroundLeft, int proxGroundRight) {
		if (proxGroundLeft > 550 && proxGroundRight > 550)
			actualField = Vars.WHITE_FIELD;
		else if (proxGroundLeft < 550 && proxGroundRight < 550)
			actualField = Vars.BLACK_FIELD;
	}

	private void checkInFieldPosition(int proxGroundLeft, int proxGroundRight) {
		if (actualField == Vars.WHITE_FIELD && proxGroundLeft > 550
				&& proxGroundRight < 550)
			inFieldPosition = Vars.TO_FAR_RIGHT;
		else if (actualField == Vars.BLACK_FIELD && proxGroundLeft < 550
				&& proxGroundRight < 550)
			inFieldPosition = Vars.TO_FAR_LEFT;
	}
	
	public int getActualField() {
		return actualField;
	}

	public int getInFieldPosition() {
		return inFieldPosition;
	}

	public void stopMove() {
		setVLeft((short) (leftMotorSpeed / 2));
		setVRight((short) (rightMotorSpeed / 2));
		setVRight((short) 0);
		setVLeft((short) 0);
	}

	public void driveStraight(short driveSpeed) {
		setVRight((short) (driveSpeed / 2));
		setVLeft((short) (driveSpeed / 2));
		setVLeft(driveSpeed);
		setVRight(driveSpeed);
	}
}
