package regulaceSTM;

public class Pid {
	// private double Input; // merena aktualni hodnota
	private double Output; // vysledek, vydana hodnota po regulaci
	// private double Setpoint; // setpoint je pozadovana, referencni hodnota
	private double errorSum;
	private double time;
	private double deltaTime;
	private double previousError;

	public double solve(double kp, double ki, double kd, double Input,
			double Setpoint, double kc) {
		// // inicializace
		deltaTime = 1;

		// Spocitat error
		double error = (Setpoint - Input);

		// Pricist error do error sum
		errorSum += (error * deltaTime);

		// zavest derivacni error
		double derError = (error - previousError) / deltaTime;

		// Spocitat vysledek PID
		Output = kp * error + ki * errorSum + kd * derError;

		// pripravit na dalsi kolo
		previousError = error;

		return Output;
	}

	public double convertZToCurrent(double z, double kc) {
		double current = 1 / (Math.exp(kc * z));
		return current;
	}

	public double convertCurrentToZ(double current, double kc) {
		// if (current < 0) {
		// current = current * (-1);
		// }
		double z = Math.log(1 / current) / (kc);
		return z;
	}
}
