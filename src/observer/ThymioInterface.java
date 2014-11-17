package observer;

import java.awt.Dimension;
import javax.swing.JFrame;

import thymio.Thymio;


public class ThymioInterface extends JFrame {
	private static final long serialVersionUID = 1L;
	public ThymioPanel myPanel;
	
	public ThymioInterface(Thymio c) {
		super("Thymio");

		myPanel = new ThymioPanel(c, this);
		this.setContentPane(myPanel);
		
		this.setMinimumSize(new Dimension(400, 400));
		this.setMaximumSize(new Dimension(400, 400));
		this.pack();
		this.setVisible(true);
	}

	public int getOrientation() {
		return myPanel.getOrientation();
	}
	
	public int getVForward() {
		return myPanel.getVForward();
	}
	
	public ThymioPanel getThymioPanel() {
		return myPanel;
	}
}
