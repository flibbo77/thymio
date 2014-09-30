package math;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;

public class MovingAverage {
	public static void main(String [] args) {
		try {
			BufferedReader fr = new BufferedReader(new FileReader(args[0]));
			PrintStream out = new PrintStream("/Users/bdludwig/out.csv");
			
			String line;
			int n = 0;
			double meanX = 0.0, meanY = 0.0;
			double varianceX = 0.0, varianceY = 0.0;
			double squareX = 0.0, squareY = 0.0;
 
			out.println("Time\tMeanX\tMeanY\tVarianceX\tVarianceY");
			
			while ((line = fr.readLine()) != null) {
				String [] tmp = line.split(" ");
				double speedX, speedY;
				n++;
				
				speedX = Double.valueOf(tmp[1]);
				squareX += (n-1)*(speedX-meanX)*(speedX-meanX)/n;
				speedY = Double.valueOf(tmp[2]);
				squareY += (n-1)*(speedY-meanY)*(speedY-meanY)/n;
				
				meanX = ((double)(n-1))/n*meanX + speedX/n;
				meanY = ((double)(n-1))/n*meanY + speedY/n;
				
				varianceX = Math.sqrt(squareX/(n-1));
				varianceY = Math.sqrt(squareY/(n-1));
				
				out.println(tmp[0] + "\t" + meanX + "\t" + meanY + "\t" + varianceX + "\t" + varianceY);
			}
			
			fr.close();
			out.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
	}
}
