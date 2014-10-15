package threads;

import helpers.Vars;
import thymio.Thymio;

public class TurnThread extends Thread {

	int degr;
	short speed;
	Thymio thy;

	public TurnThread(int degrees, Thymio myThymio) {
		degr = degrees;
		this.speed = Vars.TURN_SPEED;
		thy = myThymio;
	}

	public void run() {
		//thy.rotate = true;
		Vars.rotate = true;
		double orientation = thy.myPanel.myMap.getThymioOrientation();
		thy.isDriving = true;
		if (degr < 0)
			speed *= -1;
		thy.setVLeft(speed);
		thy.setVRight((short) -speed);
		while (Math.abs(Math.toDegrees(orientation
				- thy.myPanel.myMap.getThymioOrientation())) < Math.abs(degr)) {

			System.out.println("Rotation_state: "
					+ Math.toDegrees(orientation
							- thy.myPanel.myMap.getThymioOrientation()));
			thy.updatePose(System.currentTimeMillis());
		}
		thy.setVLeft((short) 0);
		thy.setVRight((short) 0);
		thy.isDriving = false;
		thy.updatePose(System.currentTimeMillis());

		if(Math.toDegrees(orientation
				- thy.myPanel.myMap.getThymioOrientation()) - degr > 2){
			thy.isDriving = true;
			speed = (short)-speed;
			System.out.println("Rotation_state: "
					+ Math.toDegrees(orientation
							- thy.myPanel.myMap.getThymioOrientation()));
			thy.setVLeft((short)(speed/5));
			thy.setVRight((short) (-speed/5));
			while(Math.toDegrees(orientation
				- thy.myPanel.myMap.getThymioOrientation()) > degr){
				System.out.println("Rotation_state: "
						+ Math.toDegrees(orientation
								- thy.myPanel.myMap.getThymioOrientation()));
				thy.updatePose(System.currentTimeMillis());
			}
			thy.setVLeft((short) 0);
			thy.setVRight((short) 0);
			thy.isDriving = false;
//			thy.rotate = false;
			thy.updatePose(System.currentTimeMillis());
			Vars.rotate = false;
			
		}
	}

}
