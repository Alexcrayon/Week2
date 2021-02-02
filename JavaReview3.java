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
		// choose data depend on user input
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

	// print the full report in console
	private static void printReport(String[][] data) {
		// print header
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
		// print summary
		if (Integer.parseInt(data[0][0]) == 12) {
			System.out.println("December Highest Temperature: " + data[highestTemp(data)][0] + "/"
					+ data[highestTemp(data)][1] + ": " + data[highestTemp(data)][3] + " Average Hi: " + avergHi(data));
			System.out.println("December Lowest Temperature: " + data[LowestTemp(data)][0] + "/"
					+ data[LowestTemp(data)][1] + ": " + data[LowestTemp(data)][4] + " Average Lo: " + avergLo(data));
		} else {
			System.out.println("November Highest Temperature: " + data[highestTemp(data)][0] + "/"
					+ data[highestTemp(data)][1] + ": " + data[highestTemp(data)][3] + " Average Hi: " + avergHi(data));
			System.out.println("November Lowest Temperature: " + data[LowestTemp(data)][0] + "/"
					+ data[LowestTemp(data)][1] + ": " + data[LowestTemp(data)][4] + " Average Lo: " + avergLo(data));
		}
		// graph part
		printLine(80);
		System.out.println("Graph");
		printLine(80);

		// Top scale
		System.out.print("\t ");
		System.out.printf("%3s%4s%4s%4s%4s%4s%4s%4s%4s%4s%4s%n", scale()[0][0], scale()[0][1], scale()[0][2],
				scale()[0][3], scale()[0][4], scale()[0][5], scale()[0][6], scale()[0][7], scale()[0][8], scale()[0][9],
				scale()[0][10]);
		System.out.print("\t   ");
		for (int i = 0; i < 11; i++) {
			System.out.print("|" + "   ");
		}
		System.out.println();
		printLine(80);

		// graph
		for (int i = 1; i <= data.length; i++) {
			System.out.print(i + "\tHi ");
			for (int j = 0; j < Integer.parseInt(data[i - 1][3]); j++) {
				System.out.print("+");
			}
			System.out.print("\n");
			System.out.print("\tLo ");
			for (int j = 0; j < Integer.parseInt(data[i - 1][4]); j++) {
				System.out.print("-");
			}
			System.out.print("\n");
		}

		// bottom scale
		printLine(80);
		System.out.print("\t   ");
		for (int i = 0; i < 11; i++) {
			System.out.print("|" + "   ");
		}
		System.out.println();
		System.out.print("\t ");
		System.out.printf("%3s%4s%4s%4s%4s%4s%4s%4s%4s%4s%4s%n", scale()[0][0], scale()[0][1], scale()[0][2],
				scale()[0][3], scale()[0][4], scale()[0][5], scale()[0][6], scale()[0][7], scale()[0][8], scale()[0][9],
				scale()[0][10]);
		printLine(80);

	}

	// overwrite file with the report
	private static void createReport(String[][] data) throws IOException {
		File report = new File("E:\\TemperaturesReportFromDB.txt");
		FileWriter txtwriter = new FileWriter(report, false);
		addHeader(txtwriter, report, data);
		addData(txtwriter, report, data);
		addSummary(txtwriter, report, data);
		addScale(txtwriter, report, scale());
		addGraph(txtwriter, report, data);
		addBottomScale(txtwriter, report, bottomScale());
		txtwriter.close();
	}

	// draw a line
	private static void printLine(int dashes) {
		for (int i = 1; i <= dashes; i++) {
			System.out.print("-");
		}
		System.out.print("\n");
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

	// create a string consist of "-"
	private static String Line(int num) {
		String Line = "";
		for (int i = 0; i <= num; i++) {
			Line = Line + "-";
		}
		Line = Line + "\n";
		return Line;
	}

	private static void addHeader(FileWriter txtWriter, File fileName, String[][] data) throws IOException {

		txtWriter.write(Line(80));
		txtWriter.write(Line(10));
		if (Integer.parseInt(data[0][0]) == 12) {
			txtWriter.write("December 2020: Temperatures in Utah\r\n");
		} else {
			txtWriter.write("November 2020: Temperatures in Utah\r\n");
		}
		txtWriter.write(Line(80));
		txtWriter.write(Line(10));
		txtWriter.write(String.format("%s\t\t%s%20s%20s%n", "Data", "High", "Low", "Variance"));
		txtWriter.write(Line(80));
		txtWriter.write(Line(10));

	}

	private static void addSummary(FileWriter txtWriter, File fileName, String[][] data) throws IOException {
		txtWriter.write(Line(80));
		txtWriter.write(Line(10));
		if (Integer.parseInt(data[0][0]) == 12) {
			txtWriter.write(
					"December Highest Temperature: " + data[highestTemp(data)][0] + "/" + data[highestTemp(data)][1]
							+ ": " + data[highestTemp(data)][3] + " Average Hi: " + avergHi(data) + "\n");
			txtWriter
					.write("December Lowest Temperature: " + data[LowestTemp(data)][0] + "/" + data[LowestTemp(data)][1]
							+ ": " + data[LowestTemp(data)][4] + " Average Lo: " + avergLo(data) + "\n");
		} else {
			txtWriter.write(
					"November Highest Temperature: " + data[highestTemp(data)][0] + "/" + data[highestTemp(data)][1]
							+ ": " + data[highestTemp(data)][3] + " Average Hi: " + avergHi(data) + "\n");
			txtWriter
					.write("November Lowest Temperature: " + data[LowestTemp(data)][0] + "/" + data[LowestTemp(data)][1]
							+ ": " + data[LowestTemp(data)][4] + " Average Lo: " + avergLo(data) + "\n");
		}
		txtWriter.write(Line(80));
		txtWriter.write(Line(10));
	}

	// find the date of the highest temperature
	private static int highestTemp(String[][] data) {
		int result = Integer.parseInt(data[0][3]);
		int position = 0;
		int i = 1;
		while (i < data.length) {
			if (Integer.parseInt(data[i][3]) > result) {
				result = Integer.parseInt(data[i][3]);
				position = i;
			}
			i++;
		}
		return position;
	}

	// find the date of the lowest temperature
	private static int LowestTemp(String[][] data) {
		int result = Integer.parseInt(data[0][4]);
		int position = 0;
		int i = 1;
		while (i < data.length) {
			if (Integer.parseInt(data[i][4]) < result) {
				result = Integer.parseInt(data[i][4]);
				position = i;
			}
			i++;
		}
		return position;
	}

	// calculate the average high
	private static double avergHi(String[][] data) {
		double sum = 0;
		for (int i = 0; i < data.length; i++) {
			sum += Integer.parseInt(data[i][3]);
		}
		double result = (double) Math.round((sum / data.length) * 10) / 10;
		return result;
	}

	// calculate the average low
	private static double avergLo(String[][] data) {
		int sum = 0;
		for (int i = 0; i < data.length; i++) {
			sum += Integer.parseInt(data[i][4]);
		}
		double result = (double) Math.round((sum / data.length) * 10) / 10;
		return result;
	}

	// draw graph in file
	private static void addGraph(FileWriter txtWriter, File fileName, String[][] data) throws IOException {
		for (int i = 1; i <= data.length; i++) {
			txtWriter.write(graphTemp(i, data) + "\n");
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

	// create a 2d string array for top scale
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

	// create a 2d string array for bottom scale
	private static String[][] bottomScale() {
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

	private static void addBottomScale(FileWriter txtWriter, File fileName, String[][] scale) throws IOException {
		txtWriter.write(Line(80));
		for (int i = 0; i < scale.length; i++) {
			if (i == 0) {
				txtWriter.write(String.format("%11s%11s%12s%12s%13s%13s%13s%13s%13s%13s%13s%n", scale[i][0],
						scale[i][1], scale[i][2], scale[i][3], scale[i][4], scale[i][5], scale[i][6], scale[i][7],
						scale[i][8], scale[i][9], scale[i][10]));
			} else {
				txtWriter.write(String.format("%10s%10s%10s%11s%11s%12s%12s%12s%11s%12s%12s%n", scale[i][0],
						scale[i][1], scale[i][2], scale[i][3], scale[i][4], scale[i][5], scale[i][6], scale[i][7],
						scale[i][8], scale[i][9], scale[i][10]));
			}
		}
		txtWriter.write(Line(80));
	}

	// return one day of the temperature graph
	private static String graphTemp(int date, String[][] data) {
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
