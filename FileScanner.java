import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

/**
 * This utility class parses text files (the csv files)
 * 
 * @author rdu
 * 
 */

class FileScanner {

	/**
	 * This prints the contents of the file
	 * 
	 * @param file The desired file
	 * @throws FileNotFoundException On invalid file
	 */
	static void printFile(File file) throws FileNotFoundException {
		Scanner sc = new Scanner(file);
		while (sc.hasNextLine()) {
			System.out.println(sc.nextLine());
		}
		sc.close();
	}

	/**
	 * This reads and returns a String array
	 * 
	 * @param file The desired file
	 * @return An array of Strings
	 * @throws IOException On invalid input
	 */
	static String[] toStringArray(File file) throws IOException {
		ArrayList<String> lines = new ArrayList<String>();
		BufferedReader br = new BufferedReader(new FileReader(file));
		while (br.ready()) {
			lines.add(br.readLine());
		}
		br.close();
		return lines.toArray(new String[lines.size()]);
	}

}
