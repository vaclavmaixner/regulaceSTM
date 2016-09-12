package regulaceSTM;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Random;

public class Main {
	public static void main(String[] args) throws IOException {
		final double kp = 0.2; // konstanty proporcni, integracni, derivacni
								// 0.7 a 4 je velke kmitani
		final double ki = 0.7;
		final double kd = 0;
		final double kc = 1;
		int iterations = 20;
		double pixels = 512;
		double moleculeHeight = (1d / 10d);
		int step = 1;

		boolean overlap = true;
		boolean whiteNoise = true;
		boolean filter = false;
		boolean average = true;
		boolean averageCurrent = true;
		int filterFrequency = 1;

		if (filter == true) {
			step = filterFrequency;
		}

		double numberOfSteps = iterations * pixels; // pocet kroku

		// nove promenne
		double heightOfTip = 17;
		double setpointCurrentF = 0.5;

		Pid pid1 = new Pid();

		double levelCurrent = 7; // v jednotkach proudu
		// double level = pid1.convertCurrentToZ(levelCurrent, kc);
		double level = 1;
		double levelPrevious = level;
		double levelHeightened = (level + (1d / 3d));

		double input = 8;
		double inputCurrent = pid1.convertZToCurrent(input, kc);

		// double inputCurrent = 2; // pocet kroku
		// double input = pid1.convertCurrentToZ(inputCurrent, kc);

		// *****
		double averageOutputCounter = 0;
		double averageCurrentCounter = 0;

		// vytvorit jmeno souboru do ktereho ulozime data, aby byly zpetne
		// dohledatelne pocatecni podminky
		final String nameOfFile = Double.toString(kp) + "_"
				+ Double.toString(ki) + "_" + Double.toString(kd) + "_"
				+ Double.toString(levelCurrent) + ".txt";

		// vytiskne jmeno souboru pro uzivatele
		System.out.println(nameOfFile);

		// inicializace writeru, ktery pise do souboru
		// PrintWriter out = new PrintWriter("results2/" + nameOfFile);
		PrintWriter out = new PrintWriter("results3/hrot.txt");
		PrintWriter outSurface = new PrintWriter("results3/povrch.txt");
		PrintWriter outCurrent = new PrintWriter("results3/current.txt");

		// cyklus
		Double pidOutput = 0.0;

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
		double SetpointCurrent = 0.3;

		// hlavni smycka
		// pracujeme v nanometrech
		for (int i = 1, j = 1; j <= (numberOfSteps); i += step, j++) {

			counter += 1;

			// schod
			if ((i >= Math.round((numberOfSteps / 100) * 40))
					&& (i < Math.round(numberOfSteps / 100) * 60)) {
				level = levelHeightened;
			} else {
				level = levelPrevious;
			}

			// korugace povrchu
			Surface corrugation = new Surface();
			double SetpointDistance = corrugation.corrugation(i, level, step);
			// vyvyseni zpusobene molekulou
			double SetpointDistanceWithMolecule = level + moleculeHeight;
			// ulozeni puvodni urovne povrchu
			double SetpointDistanceWithoutMolecule = SetpointDistance;

			// nove promenne
			double distanceF = heightOfTip - SetpointDistance;
			double inputCurrentF = pid1.convertZToCurrent(distanceF, kc);

			// usek nahodneho vyskytu castice
			if (i >= Math.round((numberOfSteps / 100) * 35)
					&& i < Math.round((numberOfSteps / 100) * 40)) {

				if (moleculePresent == false) {
					Random randomGenerator = new Random();
					int randomInt = randomGenerator.nextInt(iterations * 4);

					if (randomInt == 0) {
						moleculePresent = true;
						SetpointDistance = SetpointDistanceWithMolecule;
					}
				} else if (moleculePresent == true) {
					SetpointDistance = SetpointDistanceWithMolecule;

					Random randomGenerator = new Random();
					int randomInt = randomGenerator.nextInt(iterations * 4);

					if (randomInt == 0) {
						moleculePresent = false;
						SetpointDistance = SetpointDistanceWithoutMolecule;
					}
				}

			}

			// double SetpointCurrent = pid1.convertZToCurrent(SetpointDistance,
			// kc);

			// pred predanim solve se prevedou vzdalenosti na proud
			pidOutput = (pid1.solve(kp, ki, kd, inputCurrentF,
					setpointCurrentF, kc));

			// double SetpointCurrentFinal = 4;
			// double inputCurrentFinal = heightOfTip + SetpointDistance;
			// double currentFinal = pid1.solve(kp, ki, kd, inputCurrentFinal,
			// SetpointCurrentFinal, kc);

			// white noise of current, up to 10 %
			if (whiteNoise == true) {
				Random randomGenerator = new Random();
				int whiteNoiseFrequency = randomGenerator.nextInt(3);
				double whiteNoisevalue = randomGenerator.nextInt(10);

				if (whiteNoiseFrequency == 1) {
					pidOutput = pidOutput * (1 + (whiteNoisevalue / 100));
				} else if (whiteNoiseFrequency == 2) {
					pidOutput = pidOutput * (1 - (whiteNoisevalue / 100));
				}
			}

			Double distance = pid1.convertCurrentToZ(pidOutput, kc);

			Double position = SetpointDistance + distance;
			heightOfTip = position;

			// ruzne druhy vystupu
			if (filter == false && average == false) {

				System.out.println(j + " " + position + " " + SetpointDistance);
				out.println(j + " " + (position));
				outSurface.println(j + " " + SetpointDistance);

			} else if (filter == false && average == true) {
				averageOutputCounter += position;
				outSurface.println(j + " " + SetpointDistance);
				if ((counter) % (20) == 0) {
					position = averageOutputCounter / 20;
					// konzolovej output
					System.out.print(i + " "); // vytiskne poradnik
					System.out.println((position) + " " + SetpointDistance);

					// print to file
					out.println(i + " " + position);
					averageOutputCounter = 0;
				}
			} else if (filter == true && average == false) {
				averageOutputCounter += position;
				if ((counter) % (filterFrequency) == 0) {
					position = averageOutputCounter / filterFrequency;
					// konzolovej output
					System.out.print(i + " "); // vytiskne pro gnuplot poradnik
					System.out.println((position) + " " + SetpointDistance);

					// print to file
					out.println(i + " " + position + " " + SetpointDistance);

					averageOutputCounter = 0;
				}
			}

			if (averageCurrent == true) {
				averageCurrentCounter += pidOutput;

				if ((counter) % (20) == 0) {
					double pidOutputCurrent = averageCurrentCounter / 20;

					// print to file
					outCurrent.println(i + " " + pidOutputCurrent);
					averageCurrentCounter = 0;
				}
			} else if (averageCurrent == false) {
				// vytiskne proud do souboru
				outCurrent.println(j + " " + (pidOutput));
			}

			// if (randomInt != 0) {
			// pidOutput -= fuzzy;
			// }

			inputCurrent = pidOutput;
		}
		out.close();
		outSurface.close();
		outCurrent.close();
	}
}
