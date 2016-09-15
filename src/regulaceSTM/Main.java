package regulaceSTM;

import java.io.IOException;
import java.io.PrintWriter;

public class Main {
	public static void main(String[] args) throws IOException {
		// konstanty
		final double kp = 2;
		final double ki = 5;
		final double kd = 0;
		final double kc = 0.1;

		int iterations = 20;
		double pixels = 512;

		Pid pid1 = new Pid();
		Filter filter1 = new Filter();
		filter1.initializeFilter();
		filter1.initializeFilterP();

		// pocet kroku smycky
		double numberOfSteps = iterations * pixels;

		double level = 2.0;
		double levelPrevious = level;
		double levelHeightened = level + (1.0 / 3.0);

		// inicializace writeru, ktery pise do souboru
		PrintWriter out = new PrintWriter("results3/hrot.txt");
		PrintWriter outSurface = new PrintWriter("results3/povrch.txt");
		PrintWriter outCurrent = new PrintWriter("results3/current.txt");
		PrintWriter outFilter = new PrintWriter("results3/filter.txt");
		PrintWriter outFilterP = new PrintWriter("results3/filterP.txt");

		Surface corrugation = new Surface();

		double position = 5;

		final double setpointCurrent = 0.95;
		// double zDistance = 3.0;
		double inputCurrent = pid1.convertZToCurrent(10, kc);
		// double position = 10;
		// double inputCurrent = 2;

		// hlavni smycka
		for (int i = 1; i <= (numberOfSteps); i++) {

			if (i < 400) {
				System.out.println(i + " Na počátku je iC " + inputCurrent);
			}
			// schod
			if ((i >= 4000) && (i < 6000)) {
				level = levelHeightened;
			} else {
				level = levelPrevious;
			}

			// korugace povrchu
			double setpointDistance = corrugation.corrugation(i, level, 1);
			double zDistance = position - setpointDistance;

			inputCurrent = pid1.convertZToCurrent(zDistance, kc);

			// vysledny proud se ziska poslanim aktualniho proudu a pozadovane
			// hodnoty do pid regulatoru

			double pidOutput = pid1.solve(kp, ki, kd, inputCurrent,
					setpointCurrent, kc);

			// prevod proudu na vzdalenost hrotu od vzorku
			zDistance -= pidOutput;

			// prevod vzdalenosti na pozici v ose z
			position = zDistance + level;

			// System.out.println(i + " " + zDistance + " " + pidOutput);
			if (i < 400) {
				System.out.println(i + " Na konci je pos " + position
						+ " setpoint " + setpointDistance + " rozdil "
						+ (position - setpointDistance));

				System.out.println();
			}
			out.println(i + " " + (position));
			outSurface.println(i + " " + setpointDistance);

			outCurrent.println(i + " " + (inputCurrent));
		}

		out.close();
		outSurface.close();
		outCurrent.close();
		outFilter.close();
		outFilterP.close();
	}
}
