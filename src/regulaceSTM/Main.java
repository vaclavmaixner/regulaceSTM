package regulaceSTM;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Random;

public class Main {
	// konstanty pid regulatoru
	public static double kp = 0.0;
	public static double ki = 0.0;
	public static double kd = 0;
	public static double kc = 1.0;
	// parametry povrchu
	public static double level = 1.5;
	public static double setpointDistance = 0;
	// parametry pro molekulu
	public static boolean moleculePresent = false;
	// rozhodnuti o filtrovani
	public static boolean filter = true;//
	public static boolean filterP = true;//
	public static boolean average = false;//
	public static boolean averageCurrent = false; //
	public static boolean overlap = false;
	public static boolean whiteNoise = true; //
	public static boolean corrugationB = true; //
	public static boolean molecule = true; //
	public static boolean cutOffStart = false;
	public static boolean showCurrent = false;//
	public static boolean averageFiltered = false;//
	// pocatecni parametry hrotu a vazby
	public static double position = 3.0;
	public static double setpointCurrent = 0.6;
	public static int numberEntry = 3;

	public static void main(String[] args) throws IOException {
		File dir = new File("results_PID");
		dir.mkdir();

		// zacatek gui
		Gui gui = new Gui();
		gui.runGui();

		// runMicroscope();
		System.out.println(numberEntry);
	}

	public static void runMicroscope() throws IOException {

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
		double levelPrevious = level;
		double levelHeightened = level + (1.0 / 3.0);
		// parametry pro molekulu
		double moleculeHeight = (1.0 / 10.0);
		// inicializace writeru, ktery pise do souboru
		PrintWriter outPosition = new PrintWriter("results_PID/hrot.txt");
		PrintWriter outSurface = new PrintWriter("results_PID/povrch.txt");
		PrintWriter outCurrent = new PrintWriter("results_PID/current.txt");
		PrintWriter outFilter = new PrintWriter("results_PID/filter.txt");
		PrintWriter outFilterP = new PrintWriter("results_PID/filterP.txt");
		PrintWriter outPositionFA = new PrintWriter("results_PID/hrotFA.txt");
		PrintWriter outCurrentFA = new PrintWriter("results_PID/proudFA.txt");
		// pocatecni parametry hrotu a vazby
		double inputCurrent = pid1.convertZToCurrent((position - level), kc);
		// inicializace promennych pro filtry
		double averageOutputCounter = 0;
		double averageCurrentCounter = 0;
		double pidOutputFiltered = 0;
		double positionFiltered = 0;
		double averageOutputCounterF = 0;
		double averageCurrentCounterF = 0;

		// hlavni smycka
		for (int i = 1; i <= (numberOfSteps); i++) {
			// counter pro average
			counter += 1;

			// schod
			if ((i >= Math.round((numberOfSteps / 100) * 40))
					&& (i < Math.round(numberOfSteps / 100) * 60)) {
				level = levelHeightened;
			} else {
				level = levelPrevious;
			}

			// korugace povrchu
			if (corrugationB) {
				setpointDistance = corrugation.corrugation(i, level, step);
			} else if (corrugationB == false) {
				setpointDistance = level;
			}
			// vyvyseni zpusobene molekulou
			double setpointDistanceWithMolecule = level + moleculeHeight;
			// ulozeni puvodni urovne povrchu
			double setpointDistanceWithoutMolecule = setpointDistance;

			// usek nahodneho vyskytu castice
			if (molecule) {
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

			filter1.setFilterDensity(numberEntry);

			if ((!cutOffStart) || (cutOffStart && i >= 100)) {
				// filtr na proud
				if (filter == true) {
					pidOutputFiltered = filter1.filter(numberEntry,
							inputCurrent);
					outFilter.println(i + " " + pidOutputFiltered);
				}

				// filtr na position
				if (filterP == true) {
					positionFiltered = filter1.filterP(numberEntry, position);
					outFilterP.println(i + " " + positionFiltered);
				}

				// ruzne druhy vystupu
				if (average == false && averageFiltered == false) {

					// System.out.println(i + " " + position + " "
					// + setpointDistance);
					outPositionFA.println(i + " " + (position));
					outSurface.println(i + " " + setpointDistance);

				} else if (average == true) {
					averageOutputCounter += position;
					outSurface.println(i + " " + setpointDistance);
					if ((counter) % (20) == 0) {
						double positionAverage = averageOutputCounter / 20;
						// konzolovej output
						// System.out.print(i + " "); // vytiskne poradnik
						// System.out.println((positionAverage) + " "
						// + setpointDistance);

						// print to file
						outPositionFA.println(i + " " + positionAverage);
						averageOutputCounter = 0;
					}
				}

				// funkce prumerovani proudu
				if (averageCurrent == true && showCurrent == true
						&& averageFiltered == false) {
					averageCurrentCounter += inputCurrent;

					if ((counter) % (20) == 0) {
						double pidOutputCurrent = averageCurrentCounter / 20;

						// print to file
						outCurrentFA.println(i + " " + pidOutputCurrent);
						averageCurrentCounter = 0;
					}
				} else if (averageCurrent == false && showCurrent == true
						&& averageFiltered == false) {
					outCurrentFA.println(i + " " + (inputCurrent));
				}

				// prumerovani filtrovanych dat
				if (averageFiltered) {
					averageOutputCounterF += positionFiltered;
					averageCurrentCounterF += pidOutputFiltered;

					if ((counter) % (20) == 0) {
						double positionAverageF = averageOutputCounterF / 20;
						double pidOutputCurrentF = averageCurrentCounterF / 20;

						// print to file
						// System.out.println(i + " " + (positionAverageF) + " "
						// + pidOutputCurrentF);
						outPositionFA.println(i + " " + positionAverageF);
						outSurface.println(i + " " + setpointDistance);
						outCurrentFA.println(i + " " + pidOutputCurrentF);
						averageOutputCounterF = 0;
						averageCurrentCounterF = 0;
					} else if (!averageFiltered) {
						outPositionFA.println(i + " " + (position));
						outSurface.println(i + " " + setpointDistance);
					}
				}
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
		outPositionFA.close();
		outCurrentFA.close();
	}
}