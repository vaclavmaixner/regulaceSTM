package regulaceSTM;

public class Surface {
	public double corrugation(int j, double level, int step) {
		return ((1.0 / 30.0) * Math.sin((j * (60.0 / 3.14)) / (step * 512)) + level);
	}
}
