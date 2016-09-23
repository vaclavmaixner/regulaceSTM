package regulaceSTM;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Random;

public class Main {
	public static void main(String[] args) throws IOException {
		// konstanty pid regulatoru
		final double kp = 0.8;
		final double ki = 0.8;
		final double kd = 0;
		final double kc = 1;
		// parametry smycky
		int iterations = 20;
		double pixels = 512;
		double numberOfSteps = iterations * pixels;
		int step = 1;
		int counter = 0;
		// inicializace pid a filter
		Pid pid1 = new Pid();
		Filter filter1 = new Filter();
		filter1.initializeFilter();
		filter1.initializeFilterP();
		Surface corrugation = new Surface();
		// parametry povrchu
		double level = 2.0;
		double levelPrevious = level;
		double levelHeightened = level + (1.0 / 3.0);
		// parametry pro molekulu
		boolean moleculePresent = false;
		double moleculeHeight = (1.0 / 10.0);
		// inicializace writeru, ktery pise do souboru
		PrintWriter outPosition = new PrintWriter("results3/hrot.txt");
		PrintWriter outSurface = new PrintWriter("results3/povrch.txt");
		PrintWriter outCurrent = new PrintWriter("results3/current.txt");
		PrintWriter outFilter = new PrintWriter("results3/filter.txt");
		PrintWriter outFilterP = new PrintWriter("results3/filterP.txt");
		// pocatecni parametry hrotu a vazby
		double position = 2.1;
		final double setpointCurrent = 0.5;
		double inputCurrent = pid1.convertZToCurrent((position - level), kc);
		// inicializace promennych pro filtry
		double averageOutputCounter = 0;
		double averageCurrentCounter = 0;
		double pidOutputFiltered = 0;
		double positionFiltered = 0;

		// rozhodnuti o filtrovani
		// dodelat overlap
		boolean whiteNoise = false;
		boolean filter = false;
		boolean filterP = false;
		boolean average = false;
		boolean averageCurrent = false;
		boolean overlap = false;

		// hlavni smycka
		for (int i = 1; i <= (numberOfSteps); i++) {

			// schod
			if ((i >= Math.round((numberOfSteps / 100) * 40))
					&& (i < Math.round(numberOfSteps / 100) * 60)) {
				level = levelHeightened;
			} else {
				level = levelPrevious;
			}

			// korugace povrchu
			double setpointDistance = corrugation.corrugation(i, level, step);
			// vyvyseni zpusobene molekulou
			double setpointDistanceWithMolecule = level + moleculeHeight;
			// ulozeni puvodni urovne povrchu
			double setpointDistanceWithoutMolecule = setpointDistance;

			// usek nahodneho vyskytu castice
			if (i >= Math.round((numberOfSteps / 100) * 35)
					&& i < Math.round((numberOfSteps / 100) * 40)) {

				if (moleculePresent == false) {
					Random randomGenerator = new Random();
					int randomInt = randomGenerator.nextInt(iterations * 4);

					if (randomInt == 0) {
						moleculePresent = true;
						setpointDistance = setpointDistanceWithMolecule;
					}
				} else if (moleculePresent == true) {
					setpointDistance = setpointDistanceWithMolecule;

					Random randomGenerator = new Random();
					int randomInt = randomGenerator.nextInt(iterations * 4);

					if (randomInt == 0) {
						moleculePresent = false;
						setpointDistance = setpointDistanceWithoutMolecule;
					}
				}

			}

			// spocteni rozdilu vzdalenosti
			double zDistance = position - setpointDistance;
			// spocteni proudu odpovidajiciho rozdilu vzdalenosti
			inputCurrent = pid1.convertZToCurrent(zDistance, kc);

			// zasumeni proudu, max do hodnoty 10 %
			if (whiteNoise == true) {
				Random randomGenerator = new Random();
				int whiteNoiseFrequency = randomGenerator.nextInt(3);
				double whiteNoisevalue = randomGenerator.nextInt(10);

				if (whiteNoiseFrequency == 1) {
					inputCurrent = inputCurrent * (1 + (whiteNoisevalue / 100));
				} else if (whiteNoiseFrequency == 2) {
					inputCurrent = inputCurrent * (1 - (whiteNoisevalue / 100));
				}
			}

			// zmena proudu se ziska poslanim do PID regulatoru
			double pidOutput = pid1.solve(kp, ki, kd, inputCurrent,
					setpointCurrent);

			// zmena pozice v reakci na zpetnou vazbu
			position = pidOutput * (-1);

			// prekryti v grafu
			if (overlap) {
				position -= (pid1.convertCurrentToZ(setpointCurrent, kc));
			}

			// hustota filtru
			int numberEntry = 4;
			filter1.setFilterDensity(numberEntry);

			// filtr na proud
			if (filter == true) {
				pidOutputFiltered = filter1.filter(numberEntry, inputCurrent);
				outFilter.println(i + " " + pidOutputFiltered);
			}

			// filtr na position
			if (filterP == true) {
				positionFiltered = filter1.filterP(numberEntry, position);
				outFilterP.println(i + " " + positionFiltered);
			}

			// ruzne druhy vystupu
			if (average == false) {

				System.out.println(i + " " + position + " " + setpointDistance);
				outPosition.println(i + " " + (position));
				outSurface.println(i + " " + setpointDistance);

			} else if (average == true) {
				averageOutputCounter += position;
				outSurface.println(i + " " + setpointDistance);
				if ((counter) % (20) == 0) {
					double positionAverage = averageOutputCounter / 20;
					// konzolovej output
					System.out.print(i + " "); // vytiskne poradnik
					System.out.println((positionAverage) + " "
							+ setpointDistance);

					// print to file
					outPosition.println(i + " " + positionAverage);
					averageOutputCounter = 0;
				}
			}

			// funkce prumerovani proudu
			if (averageCurrent == true) {
				averageCurrentCounter += inputCurrent;

				if ((counter) % (20) == 0) {
					double pidOutputCurrent = averageCurrentCounter / 20;

					// print to file
					outCurrent.println(i + " " + pidOutputCurrent);
					averageCurrentCounter = 0;
				}
			} else if (averageCurrent == false) {
				outCurrent.println(i + " " + (inputCurrent));
			}

			// outPosition.println(i + " " + (position));
			// outSurface.println(i + " " + setpointDistance);
			//
			// outCurrent.println(i + " " + (inputCurrent));
		}

		outPosition.close();
		outSurface.close();
		outCurrent.close();
		outFilter.close();
		outFilterP.close();
	}
}