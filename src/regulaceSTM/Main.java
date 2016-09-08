package regulaceSTM;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Random;

public class Main {
	public static void main(String[] args) throws IOException {
		final double kp = 0.2; // konstanty proporcni, integracni, derivacni
		final double ki = 2;
		final double kd = 0;
		final double kc = 1;
		// double level = 5; // mel by byt v jednotkach proudu ****************
		double numberOfSteps = 2000; // pocet kroku
		int speed = 1;
		final boolean overlap = true;

		Pid pid1 = new Pid();

		double levelCurrent = 7; // v jednotkach proudu
		// double level = pid1.convertCurrentToZ(levelCurrent, kc);
		double level = 1;
		System.out.println("level je " + level);

		double input = 8;
		double inputCurrent = pid1.convertZToCurrent(input, kc);

		// double inputCurrent = 2; // pocet kroku
		// double input = pid1.convertCurrentToZ(inputCurrent, kc);

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
		// pracujeme v nanometrech
		for (int i = 1; i <= numberOfSteps; i += speed) {
			// schod
			if (i == (numberOfSteps / 100) * 40) {
				level += (1d / 3d);
			}

			if (i == ((numberOfSteps / 100) * 60)) {
				level -= (1d / 3d);
			}

			// korugace povrchu
			double SetpointDistance = (((1d / 30d) * Math.sin((60 / 3.14) * i)) + level);
			// vyvyseni zpusobene molekulou
			double SetpointDistanceWithMolecule = level + 0.1;
			// ulozeni puvodni urovne povrchu
			double SetpointDistanceWithoutMolecule = SetpointDistance;

			// usek nahodneho vyskytu castice
			// if ((i >= 700) && (i < 800)) {
			// if (moleculePresent == false) {
			// Random rnA = new Random();
			// int chanceOfAppearance = rnA.nextInt(10);
			//
			// if (chanceOfAppearance == 1) {
			// SetpointDistance = SetpointDistanceWithMolecule;
			// moleculePresent = true;
			// }
			// System.out.println("molekula uvnitr" + moleculePresent);
			// } else if (moleculePresent == true) {
			// Random rnD = new Random();
			// int chanceOfDisappearance = rnD.nextInt(10);
			//
			// if (chanceOfDisappearance == 1) {
			// SetpointDistance = SetpointDistanceWithoutMolecule;
			// moleculePresent = false;
			// } else {
			// moleculePresent = true;
			// }
			// }
			// }

			if (i >= 700 && i < 800) {
				if (moleculePresent == false) {
					Random randomGenerator = new Random();
					int randomInt = randomGenerator.nextInt(2);

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

			System.out.println("molekula " + moleculePresent);

			double Setpoint = pid1.convertZToCurrent(SetpointDistance, kc);

			// System.out.println("Rozdil je " + Setpoint + inputCurrent);

			// pred predanim solve se prevedou vzdalenosti na proud
			pidOutput = (pid1.solve(kp, ki, kd, inputCurrent, Setpoint, kc));

			// System.out.println("Hodnoty jsou ****   " + pidOutput);
			Double distance = pid1.convertCurrentToZ(pidOutput, kc);

			// System.out.println("Hodnoty jsou ****   " + distance);

			// *********
			Double position = SetpointDistance + distance;

			if (overlap) {
				position = position - SetpointDistance;
			}

			// System.out.println("lalala " + SetpointDistance);

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
			// previousPidOutput = pidOutput;

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
