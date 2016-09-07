package regulaceSTM;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;

public class Main {
	public static void main(String[] args) throws IOException {
		final double kp = 0.21; // konstanty proporcni, integracni, derivacni
		final double ki = 5;
		final double kd = 0;
		double level = 5; // mel by byt v jednotkach proudu
		double numberOfSteps = 2000; // pocet kroku
		int speed = 1;

		String evaluateResult = null;

		Pid pid1 = new Pid();
		double input = 0;

		// vytvorit jmeno souboru do ktereho ulozime data, aby byly zpetne
		// dohledatelne pocatecni podminky
		final String nameOfFile = Double.toString(kp) + "_"
				+ Double.toString(ki) + "_" + Double.toString(kd) + "_"
				+ Double.toString(level) + ".txt";
		// vytiskne jmeno souboru pro uzivatele
		System.out.println(nameOfFile);

		// inicializace writeru, ktery pise do souboru
		PrintWriter out = new PrintWriter("results2/" + nameOfFile);

		// cyklus
		Double previousPidOutput = 0.0;
		String oscillation = "-";

		// pracujeme v nanometrech
		for (int i = 1; i <= numberOfSteps; i += speed) {
			double Setpoint = ((1d / 30d) * Math.sin((60 / 3.14) * i)) + level;

			// System.out.println("Sinus je "
			// + ((1d / 30d) * Math.sin((60d / Math.PI) * i)));

			Double pidOutput = (pid1.solve(kp, ki, kd, input, Setpoint));

			// schod
			if (i == (numberOfSteps / 100) * 40) {
				level += (1d / 3d);
			}

			else if (i == ((numberOfSteps / 100) * 60)) {
				level -= (1d / 3d);
			}

			// vola evaluaci dat - oscilace a jine
			Output output1 = new Output();
			evaluateResult = output1.evaluateOutput(pidOutput, i, Setpoint);

			System.out.print(i + " "); // vytiskne pro gnuplot poradnik
			System.out.println(pidOutput + " " + Setpoint);

			// prevede output na string
			String outputAsString = Double.toString(pidOutput);

			// print to file
			out.println(i + " " + outputAsString + " " + Setpoint);

			// ulozi si predchozi output pro porovnani
			previousPidOutput = pidOutput;

			// znovu vola sebe samu s outputem jako novou, aktualni hodnotou,
			// tedy inputem
			input = pidOutput;
			oscillation = output1.evaluateOscillation(previousPidOutput,
					pidOutput);
		}
		out.close();

		System.out.println(oscillation + evaluateResult);
		// prejmenovani souboru podle toho, jestli osciluje
		// "results/test.txt"

		// File (or directory) with old name
		File file = new File("results2/" + nameOfFile);

		// File (or directory) with new name
		File file2 = new File("results2/" + oscillation + evaluateResult
				+ nameOfFile);

		// if (file2.exists())
		// throw new java.io.IOException("file exists");

		// Rename file (or directory)
		boolean success = file.renameTo(file2);

		if (!success) {
			// File was not successfully renamed
		}

	}
}
