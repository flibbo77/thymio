package thymio;

import helpers.Vars;

import java.util.ArrayList;

import threads.DriveNumOfFieldsThread;
import threads.TurnToFixedOrientationThread;
import context.Coordinate;
import context.Path;

public class PathDriveController extends Thread {
	
	/** Path found by Dijkstra-Algorithm */
	Path m_calculatedPath;
	
	Thymio m_Thymio;
	
	/** Is all needed data set?*/
	boolean m_bIsInitialized;
	
	/** Path is analyzed? */
	boolean m_bIsAnalyzed;
	
	ArrayList<NavigationPoint> m_navigationPoints;
	
	/**
	 * Constructor of class PathDriveController
	 * @param thymio Thymio reference
	 */
	public PathDriveController(Thymio thymio) {
		m_calculatedPath = null;
		m_bIsInitialized = false;
		m_bIsAnalyzed = false;
		
		m_Thymio = thymio;
	}
	
	/**
	 * Sets the path calculated by a path-finding algorithm.
	 * It directly starts the analysis of the path to be interpreted by Thymio (if given Path is valid)
	 * @param calculatedPath
	 */
	public void setCalculatedPath(Path calculatedPath) {
		m_calculatedPath = calculatedPath;
		
		if (m_calculatedPath != null) {
			m_bIsInitialized = true;		
			analyzePath();
			printNavigationPoints();
		}
	}
	
	/**
	 * First thing to do for Thymio is a turn in the correct position. Then it drives...
	 * This class only saves the direction and the distance to drive.
	 */
	private class NavigationPoint {
		public int m_turnDirection;
		public int m_fieldsToDrive;
		
		public NavigationPoint(int turnDirection, int fieldsToDrive) {
			m_turnDirection = turnDirection;
			m_fieldsToDrive = fieldsToDrive;
		}
	}
	
	private void printNavigationPoints() {
		for (int i = 0; i < m_navigationPoints.size(); i++) {
			NavigationPoint np = m_navigationPoints.get(i);
			System.out.println("Nav-Point " + i + " Direction: " + getDegreesForPreset(np.m_turnDirection) + " - Length: " + np.m_fieldsToDrive);
		}
	}
	
	/**
	 * This method analyzes a path and creates an array out of NavigationPoints
	 */
	private void analyzePath() {
		if (!m_bIsInitialized || (m_calculatedPath.size() <= 1)) {
			return;
		}
		
		m_navigationPoints = new ArrayList<NavigationPoint>();
		
		int lastDirection = -1;
		int fieldCounter = 0; 
		
		Coordinate lastCoord = m_calculatedPath.get(0);
		
		// Get first direction
		lastDirection = getDirectionBetweenCoordinates(lastCoord, m_calculatedPath.get(1));
		
		int pathSize = m_calculatedPath.size();
		int curDirection = -1;
		
		for (int i = 1; i < pathSize; i++) {
			Coordinate curCoord = m_calculatedPath.get(i);
			
			curDirection = getDirectionBetweenCoordinates(lastCoord, curCoord);		
			
			if (lastDirection != curDirection) {
				
				m_navigationPoints.add(new NavigationPoint(lastDirection, fieldCounter));
				
				lastDirection = curDirection;
				fieldCounter = 1;
				
				if ((i == pathSize-1)) {
					/*
					 * So this is the case when the last pathnode has an other direction than the ones before
					 * FieldCounter is always 1 at this point.
					 * Direction of the last field will be saved in lastDirection
					 */
					m_navigationPoints.add(new NavigationPoint(lastDirection, fieldCounter));
				}
	
			} else {
				fieldCounter++;
			}
			
			lastCoord = curCoord;
		}
		
		if (fieldCounter > 1) {
			m_navigationPoints.add(new NavigationPoint(lastDirection, fieldCounter));
		}
		
		m_bIsAnalyzed = true;
	}
	
	private int getDirectionBetweenCoordinates(Coordinate lastCoord, Coordinate curCoord) {
		int curDirection = 0;
		if (lastCoord.getX() < curCoord.getX()) {
			curDirection = Vars._90_DEGREES;
		} else if (lastCoord.getX() > curCoord.getX()) {
			curDirection = Vars._270_DEGREES;
		} else if (lastCoord.getY() < curCoord.getY()) {
			curDirection = Vars._0_DEGREES;
		} else if (lastCoord.getY() > curCoord.getY()) {
			curDirection = Vars._180_DEGREES;
		}
		return curDirection;
	}
	
	private int getDegreesForPreset(int degPreset) {
		switch (degPreset) {
		case Vars._0_DEGREES:
			return 0;
		case Vars._90_DEGREES:
			return 90;
		case Vars._180_DEGREES:
			return 180;
		case Vars._270_DEGREES:
			return 270;
		}
		return 0;
	}
	
	private void drivePath() throws InterruptedException {
		for (int i = 0; i < m_navigationPoints.size(); i++) {
			NavigationPoint curNaviPoint = m_navigationPoints.get(i);
			
			// Do Turn
			int turnDirection = getDegreesForPreset(curNaviPoint.m_turnDirection);
			TurnToFixedOrientationThread turnThread = new TurnToFixedOrientationThread(turnDirection, m_Thymio);
			turnThread.setName("NavThread:Turn: + " + i);
			turnThread.start();
			turnThread.join();
			
			// Drive Fields
			DriveNumOfFieldsThread driveThread = new DriveNumOfFieldsThread(curNaviPoint.m_fieldsToDrive, m_Thymio);
			driveThread.setName("NavThread:Drive: + " + i);
			driveThread.start();
			driveThread.join();
		}
	}

	@Override
	public void run() {
		waitForInitialization();
		
		try {
			drivePath();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/**
	 * If thread is started to early for whatever reason, it will not continue until it is complete
	 */
	private void waitForInitialization() {
		while (!m_bIsAnalyzed || !m_bIsInitialized || m_Thymio == null) {
			try {
				wait(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
}
