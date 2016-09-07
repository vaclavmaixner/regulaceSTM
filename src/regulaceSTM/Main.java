package regulaceSTM;

import java.io.IOException;
import java.io.PrintWriter;

public class Main {
	public static void main(String[] args) throws IOException {
		final double kp = 0.28; // konstanty proporcni, integracni, derivacni
		final double ki = 5;
		final double kd = 0;
		final double kc = 1;
		// double level = 5; // mel by byt v jednotkach proudu ****************
		double numberOfSteps = 2000; // pocet kroku
		int speed = 1;

		Pid pid1 = new Pid();
		double levelCurrent = 5; // mel by byt v jednotkach proudu
		double inputCurrent = 0; // pocet kroku
		double level = pid1.convertCurrentToZ(levelCurrent, kc);
		double input = pid1.convertCurrentToZ(inputCurrent, kc);

		String evaluateResult = null;

		// double input = 0; ****************

		// vytvorit jmeno souboru do ktereho ulozime data, aby byly zpetne
		// dohledatelne pocatecni podminky
		final String nameOfFile = Double.toString(kp) + "_"
				+ Double.toString(ki) + "_" + Double.toString(kd) + "_"
				+ Double.toString(levelCurrent) + ".txt";

		// vytiskne jmeno souboru pro uzivatele
		System.out.println(nameOfFile);

		// inicializace writeru, ktery pise do souboru
		PrintWriter out = new PrintWriter("results2/" + nameOfFile);

		// cyklus
		Double previousPidOutput = 0.0;
		String oscillation = "-";

		// pracujeme v nanometrech
		for (int i = 1; i <= numberOfSteps; i += speed) {
			double Setpoint = (((1d / 30d) * Math.sin((60 / 3.14) * i)) + levelCurrent);
			double SetpointDistance = (((1d / 30d) * Math.sin((60 / 3.14) * i)) + levelCurrent);
			// pred predanim solve se prevedou vzdalenosti na proud
			Double pidOutput = (pid1.solve(kp, ki, kd, inputCurrent, Setpoint,
					kc));

			Double distance = pid1.convertCurrentToZ(pidOutput, kc);
			Double position = distance + SetpointDistance;

			System.out.println("lalala " + SetpointDistance);

			// schod
			if (i == (numberOfSteps / 100) * 40) {
				levelCurrent += (1d / 3d);
			}

			else if (i == ((numberOfSteps / 100) * 60)) {
				levelCurrent -= (1d / 3d);
			}

			// vola evaluaci dat - oscilace a jine
			Output output1 = new Output();
			evaluateResult = output1.evaluateOutput(pidOutput, i, Setpoint);

			// konzolovej output
			System.out.print(i + " "); // vytiskne pro gnuplot poradnik
			System.out.println((position) + " " + SetpointDistance);

			// prevede output na string
			String outputAsString = Double.toString(pidOutput);

			// print to file
			out.println(i + " " + position + " " + SetpointDistance);

			// ulozi si predchozi output pro porovnani
			previousPidOutput = pidOutput;

			// znovu vola sebe samu s outputem jako novou, aktualni hodnotou,
			// tedy inputem
			inputCurrent = pidOutput;

			// oscillation = output1.evaluateOscillation(previousPidOutput,
			// pidOutput);
		}
		out.close();

		// Output output = new Output();
		// output.evaluateResults(nameOfFile);
	}
}
