package regulaceSTM;

public class Output {
	Integer oscillationCounter;
	String oscillate = "-";
	String exceedSetpoint = "-";
	String failToReachSetpoint = "-";

	public String evaluateOutput(double pidOutput, int i, double Setpoint) {
		// evaluateOscillation(previousPidOutput, pidOutput);

		// if (oscillationCounter >= 3) {
		// oscillate = "s";
		// }

		if (pidOutput > Setpoint) {
			exceedSetpoint = "x";
		}

		double deviation = (pidOutput - Setpoint);
		if ((i > 100) && ((absDeviation(deviation))) > (Setpoint / 100)) {
			failToReachSetpoint = "f";
		}

		String prefix = oscillate + exceedSetpoint + failToReachSetpoint;
		return prefix;
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
