package regulaceSTM;

public class Pid {
	private double Input; // merena aktualni hodnota
	private double Output; // vysledek, vydana hodnota po regulaci
	private double Setpoint; // setpoint je pozadovana, referencni hodnota
	private double errorSum;
	private double time;
	private double deltaTime;
	private double previousError;

	public double solve(double kp, double ki, double kd, double Input,
			double Setpoint) {
		// inicializace
		deltaTime = 0.1;
		// Setpoint = 8;

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

		// time meri ubehnuty cas
		time += deltaTime;
		// System.out.println("The time is " + time);

		return Output;
	}
}
