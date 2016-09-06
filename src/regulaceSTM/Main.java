package regulaceSTM;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;

public class Main {
	public static void main(String[] args) throws IOException {
		final double kp = 0.3; // konstanty proporcni, integracni, derivacni
		final double ki = 12;
		final double kd = 0;
		final double Setpoint = 10;
		String evaluateResult = null;

		Pid pid1 = new Pid();
		double input = 8;

		// vytvorit jmeno souboru do ktereho ulozime data, aby byly zpetne
		// dohledatelne pocatecni podminky "results/" +
		final String nameOfFile = Double.toString(kp) + "_"
				+ Double.toString(ki) + "_" + Double.toString(kd) + "_"
				+ Double.toString(input) + ".txt";
		System.out.println("results/" + nameOfFile);

		PrintWriter out = new PrintWriter("results/" + nameOfFile);

		for (int i = 1; i <= 200; i++) {
			Double pidOutput = (pid1.solve(kp, ki, kd, input, Setpoint));
			Double previousPidOutput;
			// if (i == 1) {
			// previousPidOutput = pidOutput;
			// }

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
		}
		out.close();

		System.out.println(evaluateResult);
		// prejmenovani souboru podle toho, jestli osciluje
		// "results/test.txt"

		// File (or directory) with old name
		File file = new File("results/" + nameOfFile);

		// File (or directory) with new name
		File file2 = new File("results/" + evaluateResult + nameOfFile);

		if (file2.exists())
			throw new java.io.IOException("file exists");

		// Rename file (or directory)
		boolean success = file.renameTo(file2);

		if (!success) {
			// File was not successfully renamed
		}

	}
}
