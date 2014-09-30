package math;

public class SensorModel {
	private NormalDistribution nd;
	
	public SensorModel() {
		nd = new NormalDistribution(0, 0.001);
	}
	
	public void run(double currentPos) {
		double measurement = currentPos + nd.drawFromDistribution();
	}
}
