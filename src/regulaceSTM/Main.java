package regulaceSTM;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Random;

public class Main {
	public static void main(String[] args) throws IOException {
		final double kp = 0.21; // konstanty proporcni, integracni, derivacni
								// 0.7 a 4 je velke kmitani
		final double ki = 4;
		final double kd = 0;
		final double kc = 1;
		int iterations = 20;
		double pixels = 512;
		double numberOfSteps = iterations * pixels; // pocet kroku
		double moleculeHeight = 0.1;

		final boolean overlap = true;

		boolean filter = false;
		boolean average = false;

		Pid pid1 = new Pid();

		double levelCurrent = 7; // v jednotkach proudu
		// double level = pid1.convertCurrentToZ(levelCurrent, kc);
		double level = 1;
		System.out.println("level je " + level);

		double input = 8;
		double inputCurrent = pid1.convertZToCurrent(input, kc);

		// double inputCurrent = 2; // pocet kroku
		// double input = pid1.convertCurrentToZ(inputCurrent, kc);

		// *****
		double averageOutputCounter = 0;

		// vytvorit jmeno souboru do ktereho ulozime data, aby byly zpetne
		// dohledatelne pocatecni podminky
		final String nameOfFile = Double.toString(kp) + "_"
				+ Double.toString(ki) + "_" + Double.toString(kd) + "_"
				+ Double.toString(levelCurrent) + ".txt";

		// vytiskne jmeno souboru pro uzivatele
		System.out.println(nameOfFile);

		// inicializace writeru, ktery pise do souboru
		// PrintWriter out = new PrintWriter("results2/" + nameOfFile);
		PrintWriter out = new PrintWriter("results2/test.txt");

		// cyklus
		Double pidOutput = 0.0;
		String oscillation = "-";

		// smycka meri kazdych 25 mikrosekund
		// v jednom pixelu hrot stravi pul milisekundy
		// rozhodnout si jestli brat posledni hodnotu na pixelu, nebo to
		// stredovat
		// spravit to, ze velika kp slozka haze NaN
		// spravit to, ze pri nizkych konstantach se to posunuje do zaporu -
		// nema se to posunovat vubec!!
		// zavest chybovy proud - kdyz klesa do diry, tak se proud snizi a
		// naopak

		boolean moleculePresent = false;
		int counter = 0;
		// hlavni smycka
		// pracujeme v nanometrech
		for (int i = 1; i <= (numberOfSteps); i++) {

			counter += 1;

			// schod
			if (i == Math.round((numberOfSteps / 100) * 40)) {
				level += (1d / 3d);
			}

			if (i == Math.round((numberOfSteps / 100) * 60)) {
				level -= (1d / 3d);
			}

			// korugace povrchu
			double SetpointDistance = (((1d / 30d) * Math.sin((240 / 3.14) * i)) + level);
			// vyvyseni zpusobene molekulou
			double SetpointDistanceWithMolecule = level + moleculeHeight;
			// ulozeni puvodni urovne povrchu
			double SetpointDistanceWithoutMolecule = SetpointDistance;

			// usek nahodneho vyskytu castice
			if (i >= Math.round((numberOfSteps / 100) * 35)
					&& i < Math.round((numberOfSteps / 100) * 40)) {
				if (moleculePresent == false) {
					Random randomGenerator = new Random();
					int randomInt = randomGenerator.nextInt(8);

					if (randomInt == 0) {
						moleculePresent = true;
						SetpointDistance = SetpointDistanceWithMolecule;
					}
				} else if (moleculePresent == true) {
					SetpointDistance = SetpointDistanceWithMolecule;

					Random randomGenerator = new Random();
					int randomInt = randomGenerator.nextInt(8);

					if (randomInt == 0) {
						moleculePresent = false;
						SetpointDistance = SetpointDistanceWithoutMolecule;
					}
				}
			}

			double Setpoint = pid1.convertZToCurrent(SetpointDistance, kc);

			// System.out.println("Rozdil je " + Setpoint + inputCurrent);

			// pred predanim solve se prevedou vzdalenosti na proud
			pidOutput = (pid1.solve(kp, ki, kd, inputCurrent, Setpoint, kc));

			// Random randomGenerator = new Random();
			// int randomInt = (randomGenerator.nextInt(2) + 1);
			// double fuzzy = 1 / (0.5 * randomInt);
			// pidOutput += fuzzy;

			// System.out.println("Hodnoty jsou ****   " + pidOutput);
			Double distance = pid1.convertCurrentToZ(pidOutput, kc);

			Double position = SetpointDistance + distance;

			if (overlap) {
				position = position - SetpointDistance;
			}

			// vola evaluaci dat - oscilace a jine
			Output output1 = new Output();
			// evaluateResult = output1.evaluateOutput(pidOutput, i, Setpoint);

			if (filter == false && average == false) {
				if ((counter) % (20) == 0) {
					// konzolovej output
					System.out.print(i + " "); // vytiskne pro gnuplot poradnik
					System.out.println((position) + " " + SetpointDistance);

					// print to file
					out.println(i + " " + position + " " + SetpointDistance);

				}
			} else if (filter == false && average == true) {
				averageOutputCounter += position;
				if ((counter) % (20) == 0) {
					position = averageOutputCounter / 20;
					// konzolovej output
					System.out.print(i + " "); // vytiskne pro gnuplot poradnik
					System.out.println((position) + " " + SetpointDistance);

					// print to file
					out.println(i + " " + position + " " + SetpointDistance);

					averageOutputCounter = 0;
				}
			} else if (filter == true && average == false) {
				return;
			}

			// ulozi si predchozi output pro porovnani
			// previousPidOutput = pidOutput;

			// if (randomInt != 0) {
			// pidOutput -= fuzzy;
			// }

			inputCurrent = pidOutput;

			// oscillation = output1.evaluateOscillation(previousPidOutput,
			// pidOutput);
		}
		out.close();

		// Output output = new Output();
		// output.evaluateResults(nameOfFile);
	}
}
