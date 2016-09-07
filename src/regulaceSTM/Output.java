package regulaceSTM;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Output {
	Integer oscillationCounter;
	String exceedSetpoint = "-";
	String failToReachSetpoint = "-";

	// System.out.println(oscillation + evaluateResult);
	// // prejmenovani souboru podle toho, jestli osciluje
	// // "results/test.txt"
	//
	// // File (or directory) with old name
	// File file = new File("results2/" + nameOfFile);
	//
	// // File (or directory) with new name
	// File file2 = new File("results2/" + oscillation + evaluateResult
	// + nameOfFile);
	//
	// // if (file2.exists())
	// // throw new java.io.IOException("file exists");
	//
	// // Rename file (or directory)
	// boolean success = file.renameTo(file2);
	//
	// if (!success) {
	// // File was not successfully renamed
	// }

	public static void evaluateResults(String nameOfFile) {
		List<Double> list = new ArrayList<>();
		File file = new File("results2/" + nameOfFile);
		BufferedReader reader = null;

		try {
			reader = new BufferedReader(new FileReader(file));
			String text = null;

			while ((text = reader.readLine()) != null) {
				list.add(Double.parseDouble(text));
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (reader != null) {
					reader.close();
				}
			} catch (IOException e) {
			}
		}

		// print out the list
		System.out.println("Succes.");
	}

	public String evaluateOutput(double pidOutput, int i, double Setpoint) {
		double deviation = (pidOutput - Setpoint);
		if (pidOutput > Setpoint) {
			exceedSetpoint = "x";
		}

		if ((i > 100) && ((absDeviation(deviation))) > (Setpoint / 100)) {
			failToReachSetpoint = "f";
		}

		String prefix = exceedSetpoint + failToReachSetpoint;
		return prefix;
	}

	public String evaluateOscillation(double previousPidOutput, double pidOutput) {
		String oscillationString = "-";
		if (absDeviation(previousPidOutput) > absDeviation(pidOutput)) {
			oscillationString = "s";
		}
		return oscillationString;
	}

	// absolutni hodnota z devation
	private double absDeviation(double deviation) {
		if (deviation < 0) {
			deviation = deviation * (-1);
		}
		return deviation;
	}

	// public void evaluateOscillation(double previousPidOutput, double
	// pidOutput) {
	// if (previousPidOutput > pidOutput) {
	// oscillationCounter += 1;
	// }
	// }
}
