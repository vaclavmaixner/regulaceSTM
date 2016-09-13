package regulaceSTM;

public class Filter {
	int entryNo = 12;
	double pidOutputCounter = 0;
	double pidOutputCounterP = 0;
	// int counter = 0;
	double result = 0;
	double resultP = 0;

	// predani velikosti poli
	public void setFilterDensity(int entryNumber) {
		entryNo = entryNumber;
	}

	// deklarace poli
	double[] catalog = new double[entryNo + 1];
	double[] catalogP = new double[entryNo + 1];

	public void initializeFilter() {
		for (int i = 0; i == 12; i++) {
			catalog[i] = 0.0;
		}
	}

	public void initializeFilterP() {
		for (int i = 0; i == 12; i++) {
			catalogP[i] = 0.0;
		}
	}

	public double filter(int entryNumber, double pidOutput) {
		// prvni je posunuti vsech clenu na nizsi index
		for (int i = 0; i < (entryNumber); i++) {
			catalog[i] = catalog[i + 1];
			// System.out.println(catalog[i]);
		}

		// priradi poslednimu policku novou hodnotu
		catalog[entryNumber] = pidOutput;

		for (int i = 0; i < entryNumber; i++) {
			pidOutputCounter += catalog[i];
		}

		result = pidOutputCounter / entryNumber;

		pidOutputCounter = 0;
		return result;
	}

	public double filterP(int entryNumber, double pidOutput) {
		// prvni je posunuti vsech clenu na nizsi index
		for (int i = 0; i < (entryNumber); i++) {
			catalogP[i] = catalogP[i + 1];
			// System.out.println(catalog[i]);
		}

		// priradi poslednimu policku novou hodnotu
		catalogP[entryNumber] = pidOutput;

		for (int i = 0; i < entryNumber; i++) {
			pidOutputCounterP += catalogP[i];
		}

		resultP = pidOutputCounterP / entryNumber;

		pidOutputCounterP = 0;
		return resultP;
	}
}
