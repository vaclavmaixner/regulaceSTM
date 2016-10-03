package regulaceSTM;

import java.awt.Color;
import java.awt.Container;
import java.awt.event.ActionEvent;

import javax.swing.BorderFactory;
import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class Gui extends JFrame {
	final int WIDTH = 500, HEIGHT = 300;
	public static double userInput;

	public void runGui() {
		initGui();
	}

	public void initGui() {
		JFrame frame = new JFrame();
		JPanel panel = new JPanel();
		frame.add(panel);
		placeComponents(panel);

		// JButton quitButton = new JButton("quit");
		// quitButton.addActionListener((ActionEvent event) -> {
		// System.out.println("Yeaaah.");
		// });

		// createLayout(quitButton, field);

		frame.setTitle("PID regulátor");
		frame.setSize(WIDTH, HEIGHT);
		frame.setLocationRelativeTo(null);
		frame.setDefaultCloseOperation(EXIT_ON_CLOSE);
		frame.setResizable(false);
		frame.setVisible(true);
	}

	private void createLayout(JComponent... arg) {
		Container pane = getContentPane();
		GroupLayout gl = new GroupLayout(pane);
		System.out.println(userInput);
		pane.setLayout(gl);

		gl.setAutoCreateContainerGaps(true);

		gl.setHorizontalGroup(gl.createSequentialGroup().addComponent(arg[0]));
		gl.setVerticalGroup(gl.createSequentialGroup().addComponent(arg[0]));

	}

	private static void placeComponents(JPanel panel) {
		final int WIDTH = 600, HEIGHT = 600, WINDOWWIDTH = 100, WINDOWHEIGHT = 25;
		final int LABELWIDTH = 160;
		panel.setLayout(null);

		JLabel leftWindow = new JLabel();
		leftWindow.setBounds(5, 5, 280, 185);
		leftWindow.setBorder(BorderFactory.createLineBorder(Color.gray));
		panel.add(leftWindow);

		JLabel kpLabel = new JLabel("Proporční konstanta");
		kpLabel.setBounds(10, 10, LABELWIDTH, WINDOWHEIGHT);
		panel.add(kpLabel);

		JTextField kpText = new JTextField("0.7");
		kpText.setBounds(180, 10, WINDOWWIDTH, WINDOWHEIGHT);
		panel.add(kpText);

		JLabel kiLabel = new JLabel("Integrační konstanta");
		kiLabel.setBounds(10, 40, LABELWIDTH, WINDOWHEIGHT);
		panel.add(kiLabel);

		JTextField kiText = new JTextField("0.7");
		kiText.setBounds(180, 40, WINDOWWIDTH, WINDOWHEIGHT);
		panel.add(kiText);

		JLabel kdLabel = new JLabel("Derivační konstanta");
		kdLabel.setBounds(10, 70, LABELWIDTH, WINDOWHEIGHT);
		panel.add(kdLabel);

		JTextField kdText = new JTextField("0.0");
		kdText.setBounds(180, 70, WINDOWWIDTH, WINDOWHEIGHT);
		panel.add(kdText);

		JLabel kcLabel = new JLabel("Proudová konstanta");
		kcLabel.setBounds(10, 100, LABELWIDTH, WINDOWHEIGHT);
		panel.add(kcLabel);

		JTextField kcText = new JTextField("1.0");
		kcText.setBounds(180, 100, WINDOWWIDTH, WINDOWHEIGHT);
		panel.add(kcText);

		JLabel setpointCurrentLabel = new JLabel("Požadovaný proud");
		setpointCurrentLabel.setBounds(10, 130, LABELWIDTH, WINDOWHEIGHT);
		panel.add(setpointCurrentLabel);

		JTextField setpointCurrentText = new JTextField("0.5");
		setpointCurrentText.setBounds(180, 130, WINDOWWIDTH, WINDOWHEIGHT);
		panel.add(setpointCurrentText);

		JLabel numberEntryLabel = new JLabel("Hustota filtru");
		numberEntryLabel.setBounds(10, 160, LABELWIDTH, WINDOWHEIGHT);
		panel.add(numberEntryLabel);

		JTextField numberEntryText = new JTextField("4");
		numberEntryText.setBounds(180, 160, WINDOWWIDTH, WINDOWHEIGHT);
		panel.add(numberEntryText);

		// cudliky
		JButton runButton = new JButton("run");
		runButton.setBounds(WIDTH - 100, HEIGHT - 45, 80, 25);
		panel.add(runButton);

		JCheckBox whiteNoiseBox = new JCheckBox("Zašumění proudu");
		whiteNoiseBox.setBounds(300, 10, 200, 20);
		panel.add(whiteNoiseBox);

		JCheckBox corrugationBBox = new JCheckBox("Korugace povrchu");
		corrugationBBox.setBounds(300, 40, 200, 20);
		panel.add(corrugationBBox);

		JCheckBox moleculeBox = new JCheckBox("Bzukající molekula");
		moleculeBox.setBounds(300, 70, 200, 20);
		panel.add(moleculeBox);

		JCheckBox showCurrentBox = new JCheckBox("Vypisovat proud");
		showCurrentBox.setBounds(300, 100, 200, 20);
		panel.add(showCurrentBox);

		JCheckBox filterBox = new JCheckBox("Filtr pozice hrotu");
		filterBox.setBounds(300, 130, 200, 20);
		panel.add(filterBox);

		JCheckBox filterPBox = new JCheckBox("Filtr proudu");
		filterPBox.setBounds(300, 160, 200, 20);
		panel.add(filterPBox);

		JCheckBox averageBox = new JCheckBox("Průměrování hrotu");
		averageBox.setBounds(300, 190, 200, 20);
		panel.add(averageBox);

		JCheckBox averageCurrentBox = new JCheckBox("Průměrování proudu");
		averageCurrentBox.setBounds(300, 220, 200, 20);
		panel.add(averageCurrentBox);

		JCheckBox averageFilteredBox = new JCheckBox("Reálný výstup");
		averageFilteredBox.setBounds(300, 250, 200, 20);
		panel.add(averageFilteredBox);

		// po stisku run sebere informace zadane do forem a nacpe je do
		// promennych
		runButton.addActionListener((ActionEvent event) -> {
			Main.kp = Double.parseDouble(kpText.getText());
			Main.ki = Double.parseDouble(kiText.getText());
			Main.kd = Double.parseDouble(kdText.getText());
			Main.kc = Double.parseDouble(kcText.getText());
			Main.setpointCurrent = Double.parseDouble(setpointCurrentText
					.getText());
			Main.numberEntry = (int) Double.parseDouble(numberEntryText
					.getText());

			if (whiteNoiseBox.isSelected()) {
				Main.whiteNoise = true;
			} else {
				Main.whiteNoise = false;
			}

			if (corrugationBBox.isSelected()) {
				Main.corrugationB = true;
			} else {
				Main.corrugationB = false;
			}

			if (moleculeBox.isSelected()) {
				Main.molecule = true;
			} else {
				Main.molecule = false;
			}

			if (showCurrentBox.isSelected()) {
				Main.showCurrent = true;
			} else {
				Main.showCurrent = false;
			}

			if (filterBox.isSelected()) {
				Main.filter = true;
			} else {
				Main.filter = false;
			}

			if (filterPBox.isSelected()) {
				Main.filterP = true;
			} else {
				Main.filterP = false;
			}

			if (averageBox.isSelected()) {
				Main.average = true;
			} else {
				Main.average = false;
			}

			if (averageCurrentBox.isSelected()) {
				Main.averageCurrent = true;
			} else {
				Main.averageCurrent = false;
			}

			if (averageFilteredBox.isSelected()) {
				Main.averageFiltered = true;
			} else {
				Main.averageFiltered = false;
			}

			// activeBox.addItemListener(new ItemListener() {
			//
			// @Override
			// public void itemStateChanged(ItemEvent event) {
			//
			// if (activeBox.isSelected()) {
			// System.out.println("zaskrtnuto");
			// } else {
			// System.out.println("nezaskrtnuto");
			// }
			// }
			// });

				try {
					Main.runMicroscope();
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			});

	}
}
