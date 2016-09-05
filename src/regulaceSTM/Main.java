package regulaceSTM;

public class Main {
	public static void main(String[] args) {
		// final double kp = 10; // konstanty proporcni, integracni, derivacni
		// final double ki = 10;
		// final double kd = 10;

		Pid pid1 = new Pid();
		for (int i = 0; i < 400; i++) {
			System.out.println(pid1.solve(0.2, 0.2, 0, 6));
		}
	}
}
