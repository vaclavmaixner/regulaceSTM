package regulaceSTM;

public class Output {
	Integer oscillationCounter;
	String exceedSetpoint = "-";
	String failToReachSetpoint = "-";

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
