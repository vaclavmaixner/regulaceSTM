package regulaceSTM;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Random;

public class Main {
	public static void main(String[] args) throws IOException {
		double inputCurrent = 1;

		final double kp = 1; // konstanty proporcni, integracni, derivacni
		final double ki = 0.1;
		final double kd = 0;
		final double kc = 8;
		int iterations = 20;
		double pixels = 512;
		double moleculeHeight = (1d / 10d);
		int step = 1;

		boolean overlap = false;
		boolean whiteNoise = true;
		boolean filter = true;
		boolean filterP = true;
		boolean average = false;
		boolean averageCurrent = false;

		Pid pid1 = new Pid();
		Filter filter1 = new Filter();
		filter1.initializeFilter();
		filter1.initializeFilterP();

		// pocet kroku smycky
		double numberOfSteps = iterations * pixels;

		// proud, ktery chceme udrzovat
		final double setpointCurrent = 0.08;

		double level = 1;
		double levelPrevious = level;
		double levelHeightened = (level + (1d / 3d));

		double averageOutputCounter = 0;
		double averageCurrentCounter = 0;
		double pidOutputFiltered = 0;
		double positionFiltered = 0;

		// inicializace writeru, ktery pise do souboru
		PrintWriter out = new PrintWriter("results3/hrot.txt");
		PrintWriter outSurface = new PrintWriter("results3/povrch.txt");
		PrintWriter outCurrent = new PrintWriter("results3/current.txt");
		PrintWriter outFilter = new PrintWriter("results3/filter.txt");
		PrintWriter outFilterP = new PrintWriter("results3/filterP.txt");

		// cyklus
		Double pidOutput = 0.0;

		// smycka meri kazdych 25 mikrosekund
		// v jednom pixelu hrot stravi pul milisekundy

		boolean moleculePresent = false;
		int counter = 0;

		// pozice v ose z na ktere zacina hrot
		double position = 10;

		// hlavni smycka
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

			inputCurrent = pid1.convertZToCurrent(
					(position - SetpointDistance), kc);

			// vysledny proud se ziska poslanim aktualniho proudu a pozadovane
			// hodnoty do pid regulatoru

			pidOutput = pid1.solve(kp, ki, kd, inputCurrent, setpointCurrent,
					kc);

			System.out.println(pidOutput);

			// zasumeni proudu, max do hodnoty 10 %
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

			// prevod proudu na vzdalenost hrotu od vzorku
			Double distance = pid1.convertCurrentToZ(pidOutput, kc);

			// prevod vzdalenosti na pozici v ose z
			position = SetpointDistance + distance;

			// prekryti v grafu
			if (overlap == true) {
				position -= level;
			}

			// hustota filtru
			int numberEntry = 12;
			filter1.setFilterDensity(numberEntry);
			// filtr na proud
			if (filter == true) {
				pidOutputFiltered = filter1.filter(numberEntry, pidOutput);
				outFilter.println(i + " " + pidOutputFiltered);
			}

			// filtr na position
			if (filterP == true) {
				positionFiltered = filter1.filterP(numberEntry, position);
				outFilterP.println(i + " " + positionFiltered);
			}

			// ruzne druhy vystupu
			if (average == false) {

				System.out.println(j + " " + position + " " + SetpointDistance);
				out.println(j + " " + (position));
				outSurface.println(j + " " + SetpointDistance);

			} else if (average == true) {
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
			}

			// funkce prumerovani proudu
			if (averageCurrent == true) {
				averageCurrentCounter += pidOutput;

				if ((counter) % (20) == 0) {
					double pidOutputCurrent = averageCurrentCounter / 20;

					// print to file
					outCurrent.println(i + " " + pidOutputCurrent);
					averageCurrentCounter = 0;
				}
			} else if (averageCurrent == false) {
				outCurrent.println(j + " " + (pidOutput));
			}

			// inputCurrent = pidOutput;
		}

		out.close();
		outSurface.close();
		outCurrent.close();
		outFilter.close();
		outFilterP.close();
	}
}
