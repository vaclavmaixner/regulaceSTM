package regulaceSTM;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;

public class Main {
	public static void main(String[] args) throws IOException {
		final double kp = 0.35; // konstanty proporcni, integracni, derivacni
		final double ki = 11.8;
		final double kd = 0;
		double level = 5; // mel by byt v jednotkach proudu
		String evaluateResult = null;

		Pid pid1 = new Pid();
		double input = 0;

		// vytvorit jmeno souboru do ktereho ulozime data, aby byly zpetne
		// dohledatelne pocatecni podminky "results/" +
		final String nameOfFile = Double.toString(kp) + "_"
				+ Double.toString(ki) + "_" + Double.toString(kd) + "_"
				+ Double.toString(level) + ".txt";
		// vytiskne jmeno souboru pro uzivatele
		System.out.println("results/" + nameOfFile);

		// inicializace writeru, ktery pise do souboru
		PrintWriter out = new PrintWriter("results2/" + nameOfFile);

		// cyklus
		Double previousPidOutput = 0.0;
		String oscillation = "-";
		for (int i = 1; i <= 100; i++) {
			double Setpoint = (0.1 * sin(0.1 * i)) + level;
			Double pidOutput = (pid1.solve(kp, ki, kd, input, Setpoint));

			// schod
			if (i == 40) {
				level = 6;
			}

			else if (i == 60) {
				level = 5;
			}

			// vola evaluaci dat - oscilace a jine
			Output output1 = new Output();
			evaluateResult = output1.evaluateOutput(pidOutput, i, Setpoint);

			System.out.print(i + " "); // vytiskne pro gnuplot poradnik
			System.out.println(pidOutput);

			// prevede output na string
			String outputAsString = Double.toString(pidOutput);

			// print to file
			out.println(i + " " + outputAsString);

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

		if (file2.exists())
			throw new java.io.IOException("file exists");

		// Rename file (or directory)
		boolean success = file.renameTo(file2);

		if (!success) {
			// File was not successfully renamed
		}

	}

	private static double sin(double d) {
		// TODO Auto-generated method stub
		return 0;
	}
}
