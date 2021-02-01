package java2;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Scanner;

/**
 * @author Yifei Cao
 *
 */
public class JavaReview3 {
	public static void main(String[] args) throws IOException {
		int inputValue;
		Scanner input = new Scanner(System.in);
		System.out.println("Enter month:");
		inputValue = input.nextInt();

		int numRows;
		int numCols;
		String connectionString = "jdbc:mysql://127.0.0.1:3306/practice?serverTimezone=UTC";
		String dbLogin = "javauser";
		String dbPassword = "1998610cyf";
		Connection conn = null;
		String sql;
		if (inputValue == 12) {
			sql = "SELECT month, day, year, hi, lo FROM temperatures "
					+ "WHERE month = 12 AND year = 2020 ORDER BY month, day, year;";
			numRows = 31;
			numCols = 6;
		} else {
			sql = "SELECT month, day, year, hi, lo FROM temperatures "
					+ "WHERE month = 11 AND year = 2020 ORDER BY month, day, year;";
			numRows = 30;
			numCols = 6;
		}

		String[][] dbResults = new String[numRows][numCols];
		;
		try {
			conn = DriverManager.getConnection(connectionString, dbLogin, dbPassword);
			if (conn != null) {
				try (Statement stmt = conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,
						ResultSet.CONCUR_UPDATABLE); ResultSet rs = stmt.executeQuery(sql)) {

					rs.last();
					numRows = rs.getRow();
					rs.first();

					for (int i = 0; i < numRows; i++) {
						dbResults[i][0] = rs.getString("month");
						dbResults[i][1] = rs.getString("day");
						dbResults[i][2] = rs.getString("year");
						dbResults[i][3] = rs.getString("hi");
						dbResults[i][4] = rs.getString("lo");
						dbResults[i][5] = String
								.valueOf(Integer.parseInt(dbResults[i][3]) - Integer.parseInt(dbResults[i][4]));
						rs.next();
					}

				} catch (SQLException ex) {
					System.out.println(ex.getMessage());
				}
			}
		} catch (Exception e) {
			System.out.println("Database connection failed.");
			e.printStackTrace();
		}
		createReport(dbResults);
		printReport(dbResults);
	}

	private static void printReport(String[][] data) {
		printLine(80);
		System.out.println("December 2020: Temperatures in Utah");
		printLine(80);
		System.out.println("Date\t\tHigh\tLow\tVariance");
		printLine(80);
		for (int i = 0; i < data.length; i++) {
			System.out.printf("%s/%s/%s\t%s\t%s\t%s%n", data[i][0], data[i][1], data[i][2], data[i][3], data[i][4],
					data[i][5]);
		}
		printLine(80);
		System.out.println();
		System.out.println();
		printLine(80);
		System.out.println("Graph");
		printLine(80);

	}

	private static void createReport(String[][] data) throws IOException {

		File report = new File("E:\\TemperaturesReportFromDB.txt");
		FileWriter txtwriter = new FileWriter(report, false);
		addHeader(txtwriter, report);
		addData(txtwriter, report, data);
		addSummary(txtwriter, report, data);
		addScale(txtwriter, report, scale());
		addGraph(txtwriter, report, data);
		txtwriter.close();
	}
//    	addScale2(report);
//    	//printArray(scale());
//    	System.out.println(new String(Files.readAllBytes(Paths.get("E:\\TemperaturesReport.txt"))));

	private static void printLine(int dashes) {
		for (int i = 1; i <= dashes; i++) {
			System.out.print("-");
		}
		System.out.print("\n");
	}

	private static void printFile(File fileName) throws IOException {

		if (fileName.exists()) {
			BufferedReader br = null;
			String line = "";
			String cvsSplitBy = " ";
			br = new BufferedReader(new FileReader(fileName));
			while ((line = br.readLine()) != null) {
				String[] stringValues = line.split(cvsSplitBy);
				System.out.println(stringValues[0] + " " + stringValues[1] + " " + stringValues[2]);
			}
			br.close();
		}
	}

	private static void addData(FileWriter txtWriter, File fileName, String[][] data) throws IOException {
		for (int i = 0; i < data.length; i++) {
			if (i <= 8) {
				txtWriter.write(String.format("%s/%s/%s  %20s%20s%20s%n", data[i][0], data[i][1], data[i][2],
						data[i][3], data[i][4], data[i][5]));
			} else {
				txtWriter.write(String.format("%s/%s/%s%20s%20s%20s%n", data[i][0], data[i][1], data[i][2], data[i][3],
						data[i][4], data[i][5]));
			}
		}

	}

	private static String Line(int num) {
		String Line = "";
		for (int i = 0; i <= num; i++) {
			Line = Line + "-";
		}
		Line = Line + "\n";
		return Line;
	}

	private static void addHeader(FileWriter txtWriter, File fileName) throws IOException {

		txtWriter.write(Line(80));
		txtWriter.write(Line(10));
		txtWriter.write("December 2020: Temperatures in Utah\r\n");
		txtWriter.write(Line(80));
		txtWriter.write(Line(10));
		txtWriter.write(String.format("%s\t\t%s%20s%20s%n", "Data", "High", "Low", "Variance"));
		txtWriter.write(Line(80));
		txtWriter.write(Line(10));

	}

	private static void addSummary(FileWriter txtWriter, File fileName, String[][] data) throws IOException {
		txtWriter.write(Line(80));
		txtWriter.write(Line(10));
		txtWriter.write("December Highest Temperature: 12/21: 53 Average Hi: 37.9\r\n");
		txtWriter.write("December Lowest Temperature:  12/13: 16 Average Lo: 22.0\r\n");
		txtWriter.write(Line(80));
		txtWriter.write(Line(10));
	}

	private static int highestTemp(String[][] data) {
		int result = Integer.parseInt(data[1][3]);
		for (int i = 1; i < data.length; i++) {
			if (Integer.parseInt(data[i][3]) > result) {
				result = Integer.parseInt(data[i][3]);
			}
		}
		return result;
	}

	private static int LowestTemp(String[][] data) {
		int result = Integer.parseInt(data[1][4]);
		for (int i = 1; i < data.length; i++) {
			if (Integer.parseInt(data[i][4]) < result) {
				result = Integer.parseInt(data[i][4]);
			}
		}
		return result;
	}

	private static double avergHi(String[][] data) {
		int sum = 0;
		for (int i = 0; i < data.length; i++) {
			sum += Integer.parseInt(data[i][3]);
		}
		double result = (double) ((sum / 31));
		return result;
	}

	private static double avergLo(String[][] data) {
		int sum = 0;
		for (int i = 0; i < data.length; i++) {
			sum += Integer.parseInt(data[i][4]);
		}
		double result = (double) (sum / 31);
		return result;
	}

	private static void addGraph(FileWriter txtWriter, File fileName, String[][] data) throws IOException {

		for (int i = 1; i <= data.length; i++) {
			txtWriter.write(dailyTemp(i, data) + "\n");
		}
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

	private static String[][] scale2() {
		String[][] scale = new String[2][11];
		scale[1][0] = "1";
		int num = 5;
		for (int i = 1; i < 11; i++) {
			scale[1][i] = String.valueOf(num);
			num += 5;
		}
		for (int i = 0; i < 11; i++) {
			scale[0][i] = "|";
		}
		return scale;
	}

	private static void addScale(FileWriter txtWriter, File fileName, String[][] scale) throws IOException {

		txtWriter.write("Graph\n");
		txtWriter.write(Line(80));
		for (int i = 0; i < scale.length; i++) {
			if (i == 0) {
				txtWriter.write(String.format("%10s%10s%10s%11s%11s%12s%12s%12s%11s%12s%12s%n", scale[i][0],
						scale[i][1], scale[i][2], scale[i][3], scale[i][4], scale[i][5], scale[i][6], scale[i][7],
						scale[i][8], scale[i][9], scale[i][10]));
			} else {
				txtWriter.write(String.format("%11s%11s%12s%12s%13s%13s%13s%13s%13s%13s%13s%n", scale[i][0],
						scale[i][1], scale[i][2], scale[i][3], scale[i][4], scale[i][5], scale[i][6], scale[i][7],
						scale[i][8], scale[i][9], scale[i][10]));
			}
		}
		txtWriter.write(Line(80));
	}

	private static String dailyTemp(int date, String[][] data) {
		String temperture = "";
		if (date < 10) {
			temperture = temperture + date + "   ";
			temperture = temperture + "Hi  ";
			for (int i = 0; i < Integer.parseInt(data[date - 1][3]); i++) {
				temperture = temperture + "+";
			}
			temperture = temperture + "\n     " + "Lo  ";
			for (int i = 0; i < Integer.parseInt(data[date - 1][4]); i++) {
				temperture = temperture + "+";
			}
		} else {
			temperture = temperture + date + "  ";
			temperture = temperture + "Hi  ";
			for (int i = 0; i < Integer.parseInt(data[date - 1][3]); i++) {
				temperture = temperture + "+";
			}
			temperture = temperture + "\n     " + "Lo  ";
			for (int i = 0; i < Integer.parseInt(data[date - 1][4]); i++) {
				temperture = temperture + "+";
			}
		}
		return temperture;
	}
}
