package regulaceSTM;

import java.io.IOException;
import java.io.PrintWriter;

public class Main {
	public static void main(String[] args) throws IOException {
		final double kp = 4; // konstanty proporcni, integracni, derivacni
		final double ki = 3;
		final double kd = 0;

		Pid pid1 = new Pid();
		double input = 7;

		// DataOutputStream dataOut = new DataOutputStream(new FileOutputStream(
		// "test.txt"));

		PrintWriter out = new PrintWriter("results2.txt");

		for (int i = 1; i <= 200; i++) {
			System.out.print(i + " "); // vytiskne pro gnuplot poradnik
			System.out.println(pid1.solve(kp, ki, kd, input));

			// prevede neobratne output
			Double output2 = (pid1.solve(kp, ki, kd, input));
			String output3 = Double.toString(output2);
			// print to file
			out.println(i + " " + output3);

			// znovu vola sebe samu s outputem jako novou, aktualni hodnotou,
			// tedy inputem
			input = pid1.solve(kp, ki, kd, input);
		}
		out.close();
	}
}
