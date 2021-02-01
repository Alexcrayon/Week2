package java2;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * @author Yifei Cao
 *
 */
public class JavaReview2 {

	public static void main(String[] args) throws IOException {
		File fileName = new File("E:\\SLCDecember2020Temperatures.csv");
		int rows = 31;
		int cols = 4;
		int[][] file = new int[rows][cols];
		if (fileName.exists()) {
			BufferedReader br = null;
			String line = "";
			String cvsSplitBy = ",";
			br = new BufferedReader(new FileReader(fileName));
			for (int i = 0; i < rows; i++) {
				line = br.readLine();
				String[] stringValues = line.split(cvsSplitBy);
				for (int j = 0; j < 3; j++) {
					file[i][j] = Integer.parseInt(stringValues[j]);
				}

			}
			br.close();
		}

		for (int i = 0; i < 31; i++) {
			file[i][3] = file[i][1] - file[i][2];
		}
		File report = new File("E:\\TemperaturesReport.txt");

		addHeader(report);
		addData(report, file);
		addSummary(report);
		addScale(report);
		addGraph(report, file);
		addScale2(report);

		System.out.println(new String(Files.readAllBytes(Paths.get("E:\\TemperaturesReport.txt"))));

	}

	private static void addData(File fileName, int[][] data) throws IOException {
		FileWriter txtWriter = new FileWriter(fileName, true);
		for (int i = 0; i < data.length; i++) {
			for (int j = 0; j < data[0].length; j++) {

				txtWriter.write(String.format("%-10d", data[i][j]));
			}
			txtWriter.write("\n");
		}

		txtWriter.close();
	}

	private static String Line(int num) {
		String Line = "";
		for (int i = 0; i <= num; i++) {
			Line = Line + "-";
		}
		Line = Line + "\n";
		return Line;
	}

	private static void addHeader(File fileName) throws IOException {
		FileWriter txtWriter = new FileWriter(fileName, true);
		txtWriter.write(Line(80));
		txtWriter.write(Line(10));
		txtWriter.write("December 2020: Temperatures in Utah\r\n");
		txtWriter.write(Line(80));
		txtWriter.write(Line(10));
		txtWriter.write("Day     High    Low    Variance\r\n");
		txtWriter.write(Line(80));
		txtWriter.write(Line(10));
		txtWriter.close();
	}

	private static void addSummary(File fileName) throws IOException {
		FileWriter txtWriter = new FileWriter(fileName, true);
		txtWriter.write(Line(80));
		txtWriter.write(Line(10));
		txtWriter.write("December Highest Temperature: 12/21: 53 Average Hi: 37.9\r\n");
		txtWriter.write("December Lowest Temperature:  12/13: 16 Average Lo: 22.0\r\n");
		txtWriter.write(Line(80));
		txtWriter.write(Line(10));
		txtWriter.close();
	}

	private static void addGraph(File fileName, int[][] data) throws IOException {
		FileWriter txtWriter = new FileWriter(fileName, true);

		for (int i = 1; i <= 31; i++) {
			txtWriter.write(dailyTemp(i, data) + "\n");
		}
		txtWriter.close();
	}

	public static String[][] printArray(String[][] array) {
		String[][] output = new String[array.length][array[0].length];
		for (int i = 0; i < array.length; i++) {
			for (int j = 0; j <= array[0].length - 1; j++) {

				output[i][j] = String.format("-%10s", array[i][j]);

			}
		}

		return output;
	}

	private static String[][] scale() {
		String[][] scale = new String[2][11];
		scale[0][0] = "1";
		int num = 5;
		for (int i = 1; i < 11; i++) {
			scale[0][i] = String.valueOf(num);
			num += 5;
		}
		for (int i = 0; i < 11; i++) {
			scale[1][i] = "|";
		}
		return scale;

	}

	private static void addScale(File fileName) throws IOException {

		FileWriter txtWriter = new FileWriter(fileName, true);

		txtWriter.write("Graph\n");
		txtWriter.write(Line(80));
		txtWriter.write("\t" + "    "
				+ "  1        5        10        15         20         25        30         35          40        45        50\n");
		txtWriter.write("\t" + "    "
				+ "  |         |           |           |           |            |           |           |            |            |            |\n");
		txtWriter.write(Line(80));
		txtWriter.close();
	}

	private static void addScale2(File fileName) throws IOException {
		FileWriter txtWriter = new FileWriter(fileName, true);
		txtWriter.write("\t" + "    "
				+ "  |         |           |           |           |            |           |           |            |            |            |\n");
		txtWriter.write("\t" + "    "
				+ "  1        5        10        15         20         25        30         35          40        45        50\n");
		txtWriter.close();

	}

	private static String dailyTemp(int date, int[][] data) {
		String temperture = "";
		temperture = temperture + date + "\t";
		temperture = temperture + "Hi  ";
		for (int i = 0; i < data[date - 1][1]; i++) {
			temperture = temperture + "+";
		}
		temperture = temperture + "\n\t" + "Lo  ";
		for (int i = 0; i < data[date - 1][2]; i++) {
			temperture = temperture + "+";
		}

		return temperture;
	}
}
