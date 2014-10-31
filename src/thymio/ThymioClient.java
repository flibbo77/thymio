package thymio;

import helpers.Vars;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class ThymioClient {
	private Socket conn;
	private PrintWriter printWriter;
	private BufferedReader bufferedReader;

	public ThymioClient() {
	}

	private void write(String message) throws IOException {
		// long connectStartTime = System.currentTimeMillis();

		printWriter.println(message);
		printWriter.flush();
		// System.out.println("Time for write:  " + (System.currentTimeMillis()
		// - connectStartTime));

	}

	private String read() throws IOException {
		long connectStartTime = System.currentTimeMillis();
		String message = "";
		// char[] charB = new char[20];
		// bufferedReader.read(charB, 0, 20);
		// for(char c:charB){
		// System.out.println(c);
		// message +=c;
		// }
		message = bufferedReader.readLine();
		// System.out.println("Time for read:  " + (System.currentTimeMillis() -
		// connectStartTime));
		// System.out.println(message);

		return message;
	}

	private void connect() throws IOException {
		long connectStartTime = System.currentTimeMillis();

		conn = new Socket("192.168.178.64", 6789);
		// System.out.println("Time for new Socket:  " +
		// (System.currentTimeMillis() - connectStartTime));
		printWriter = new PrintWriter(new OutputStreamWriter(
				conn.getOutputStream()));
		// System.out.println("Time for printWriter:  " +
		// (System.currentTimeMillis() - connectStartTime));

		bufferedReader = new BufferedReader(new InputStreamReader(
				conn.getInputStream()));
		// System.out.println("Time for bufferedReader:  " +
		// (System.currentTimeMillis() - connectStartTime));
	}

	public void setVariable(String variable, List<Short> data) {
		if (!Vars.rotate && variable.equals("motor.right.target")
				&& data.get(0) > 50) {
			short temp = data.get(0);
			temp += Vars.MOTOR_CORR_STRAIGHT;
			data.set(0, temp);
		}

		if (Vars.rotate && variable.equals("motor.right.target")
				&& data.get(0) != 0) {
			short temp = data.get(0);
			temp += Vars.MOTOR_CORR_ROT * Vars.actualRotDirection;
			System.out.println("motor.right.target correction:"
					+ Vars.MOTOR_CORR_ROT * Vars.actualRotDirection);
			data.set(0, temp);
		}

		try {
			String msg = "set " + variable;

			conn = null;
			connect();

			for (int i = 0; i < data.size(); i++) {
				msg += " ";
				msg += data.get(i).toString();
			}

			write(msg);
//			msg = read();
//			if (!msg.startsWith("ok:"))
//				System.out.println(msg);

			conn.close();
		} catch (IOException e) {
			System.out.println("error while setVariable: " + e);
			e.printStackTrace();
			if (conn != null)
				try {
					conn.close();
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
		}
	}

	public List<Short> getVariable(String variable) {
		try {
			String msg;
			ArrayList<Short> res = new ArrayList<Short>();

			conn = null;
			connect();

			write("get " + variable);
			msg = read();

			if (msg.startsWith("ok: ")) {
				String[] data = msg.substring(4).split(" ");

				for (int i = 0; i < data.length; i++)
					res.add(new Short(Short.parseShort(data[i])));
			} else {
				System.out.println(msg);
			}

			conn.close();
			return res;
		} catch (IOException e) {
			System.out.println("error while getVariable: " + e);
			e.printStackTrace();
			try {
				conn.close();
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			return null;
		}
	}

	public static void main(String[] args) {
		try {
			ThymioClient t = new ThymioClient();
			List<Short> res;
			int count;

			ArrayList<Short> val = new ArrayList<>();
			val.clear();
			val.add(new Short((short) 200));
			t.setVariable("motor.left.target", val);
			t.setVariable("motor.right.target", val);

			count = 0;
			while (count < 10) {
				Thread.sleep(250);
				res = t.getVariable("motor.left.speed");
				if (res != null) {
					for (int i = 0; i < res.size(); i++)
						System.out.print(res.get(i) + " ");
					System.out.print("\n");
				}

				count++;
			}

			val.clear();
			val.add(new Short((short) 0));
			t.setVariable("motor.left.target", val);
			t.setVariable("motor.right.target", val);
		} catch (Exception e) {
			System.out.println("connection failure: " + e);
		}
	}
}