package observer;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.BoxLayout;

import threads.DriveNumOfFieldsThread;
import threads.TurnThread;
import threads.TurnToFixedOrientationThread;
import thymio.PathDriveController;
import thymio.Thymio;
import thymio.ThymioDrivingThread;

public class ThymioPanel extends JPanel implements ChangeListener, KeyListener,
		ActionListener {
	
	private static final long serialVersionUID = 1L;
	private Thymio myThymio;
	private ThymioInterface myUI;
	private JSlider vForward, theta;
	private JLabel valVelocity, valTheta;
	private JButton stop;
	private Thread thread = null;

	public ThymioPanel(Thymio t, ThymioInterface ui) {
		myThymio = t;
		myUI = ui;

		initUI();
		this.setPreferredSize(new Dimension(400, 400));
		this.addKeyListener(this);
		this.setFocusable(true);
		this.requestFocusInWindow();
	}

	private PathDriveController m_pdc = null;

	public void setDriveController(PathDriveController pdc) {
		m_pdc = pdc;
	}

	private void initUI() {
		this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

		vForward = new JSlider(JSlider.HORIZONTAL,
				-(short) (Thymio.MAXSPEED / (10 * Thymio.SPEEDCOEFF)),
				(short) (Thymio.MAXSPEED / (10 * Thymio.SPEEDCOEFF)), 0);
		vForward.addChangeListener(this);

		// Turn on labels at major tick marks.
		vForward.setMajorTickSpacing(10);
		vForward.setMinorTickSpacing(1);
		vForward.setPaintTicks(true);
		// vForward.setPaintLabels(true);

		theta = new JSlider(JSlider.HORIZONTAL, -90, 90, 0);
		theta.addChangeListener(this);

		// Turn on labels at major tick marks.
		theta.setMajorTickSpacing(10);
		theta.setMinorTickSpacing(1);
		theta.setPaintTicks(true);
		// vRight.setPaintLabels(true);

		valVelocity = new JLabel("velocity (cm/sec): " + vForward.getValue());
		valTheta = new JLabel("turn angle (degree): " + theta.getValue());
		stop = new JButton("STOP");
		stop.addActionListener(this);

		this.add(valVelocity);
		this.add(valTheta);
		this.add(new JLabel("Rotation Speed:"));
		this.add(theta);
		this.add(new JLabel("Forward Speed:"));
		this.add(vForward);
		this.add(stop);
	}

	@Override
	public void stateChanged(ChangeEvent e) {

		if (e.getSource().equals(vForward)) {
			valVelocity.setText("velocity (cm/sec): " + vForward.getValue());
			if (vForward.getValue() != 0)
				myThymio.isDriving = true;
			else
				myThymio.isDriving = false;
			updateThymio();
		} else if (e.getSource().equals(theta)) {
			valTheta.setText("turn angle (degree): " + theta.getValue());
			updateThymio();

		}

		myUI.repaint();
		if (!this.isFocusOwner())
			this.requestFocus();
	}

	private void updateThymio() {
		double v = ((double) vForward.getValue() * Thymio.SPEEDCOEFF * 10);
		double angle = -0.5 * (Math.PI / 180.0 * theta.getValue())
				* Thymio.BASE_WIDTH * Thymio.SPEEDCOEFF;
		double k = Math.max(Math.abs(v - angle), Math.abs(v + angle));

		if (k > Thymio.MAXSPEED) {
			v = v * Thymio.MAXSPEED / k;
			angle = angle * Thymio.MAXSPEED / k;
		}

		synchronized (myThymio) {
			myThymio.setVRight((short) (v / 2 + angle));
			myThymio.setVLeft((short) (v / 2 - angle));
			myThymio.updatePose(System.currentTimeMillis());

			myThymio.setVLeft((short) (v - angle));
			myThymio.setVRight((short) (v + angle));

			myThymio.updatePose(System.currentTimeMillis());
		}
	}

	public int getOrientation() {
		return -theta.getValue();
	}

	public int getVForward() {
		return vForward.getValue();
	}

	@Override
	public void keyPressed(KeyEvent e) {
		switch (e.getKeyCode()) {
		case KeyEvent.VK_LEFT:
			// startRotation(-90);
			theta.setValue(theta.getValue() - 20);
			break;
		case KeyEvent.VK_RIGHT:
			// startRotation(90);
			theta.setValue(theta.getValue() + 20);
			break;
		case KeyEvent.VK_UP:
			vForward.setValue(vForward.getValue() + 5);
			break;
		case KeyEvent.VK_DOWN:
			vForward.setValue(vForward.getValue() - 5);
			break;
		case KeyEvent.VK_SPACE:
			myThymio.stopMove();
			vForward.setValue(0);
			theta.setValue(0);
			break;
		case KeyEvent.VK_1:
			System.out.println(thread);
			if (thread == null || !thread.isAlive()) {
				thread = new TurnToFixedOrientationThread(0, myThymio);
				thread.start();
			}
			break;
		case KeyEvent.VK_2:
			System.out.println(thread);
			if (thread == null || !thread.isAlive()) {
				thread = new TurnToFixedOrientationThread(90, myThymio);
				thread.start();
			}
			break;
		case KeyEvent.VK_3:
			System.out.println(thread);
			if (thread == null || !thread.isAlive()) {
				thread = new TurnToFixedOrientationThread(180, myThymio);
				thread.start();
			}
			break;
		case KeyEvent.VK_4:
			System.out.println(thread);
			if (thread == null || !thread.isAlive()) {
				thread = new TurnToFixedOrientationThread(270, myThymio);
				thread.start();
			}
			break;
		case KeyEvent.VK_5:
			if (m_pdc != null) {
				m_pdc.start();
			}
			break;
		case KeyEvent.VK_Z:
			DriveNumOfFieldsThread myDriver = new DriveNumOfFieldsThread(1,
					myThymio);
			myDriver.start();
		default:
			break;
		}
		this.repaint();
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == stop) {
			theta.setValue(0);
			vForward.setValue(0);
			updateThymio();
		}
	}

	@Override
	public void keyReleased(KeyEvent arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void keyTyped(KeyEvent arg0) {
		// TODO Auto-generated method stub

	}
}
