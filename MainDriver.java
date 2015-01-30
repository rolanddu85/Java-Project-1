import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;

/**
 * This is my attempt at an optimization question that I thought was interesting.
 * This is the main driver class that parses folders, instantiates objects, and
 * writes the result into another file.
 * 
 * @author rdu
 */
public class MainDriver {

	/**
	 * This is the main method that runs the overall task, 
	 * the folder path for files to look for is hard-coded to a 
	 * specific folder on the user's desktop for windows.
	 * @param args Unused
	 * @throws IOException On input errors 
	 */
	public static void main(String[] args) throws IOException {
		//The folder that contains all the csv files to parse
		File folderPath = new File(System.getProperty("user.home")
				+ "/Desktop/AssetsAndDealsSample");
		
		File[] listOfFiles = folderPath.listFiles();
		DealManager dealManager = new DealManager();
		try {
			for (File file : listOfFiles) {
				switch (file.getName()) {
				case "Inventory.txt":
					dealManager.initInventory(file);
					break;
				case "BorrowCosts.txt":
					dealManager.initBorrowRates(file);
					break;
				case "Deals.txt":
					dealManager.initDealPrices(file);
					break;
				case "AssetClassRequirements.txt":
					dealManager.initDealAClasses(file);
					break;
				case "CreditRequirements.txt":
					dealManager.initDealCredits(file);
					break;
				default:
					System.out.println("Ignored file -> " + file.getPath());
					break;
				}
			}
			// Initialize the List of pointers to deals for each possible credit
			// requirement
			dealManager.initDealCreditReqs();
			// Perform all the deal processing
			dealManager.processDeals();
			// Create file with results
			writeResultsFile("The total amount borrowed: "
					+ dealManager.getTotalBorrowed());

		} catch (NullPointerException e) {
			e.printStackTrace();
			writeResultsFile("Error - Could not read files");
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			writeResultsFile("Error - Could not read files");
		} catch (IOException e) {
			e.printStackTrace();
			writeResultsFile("Error - Could not read files");
		}
	}

	/**
	 * This write the results to a file and creates it if it doesnt already exist
	 * @param s The string to write to file
	 * @throws IOException
	 */
	static void writeResultsFile(String s) throws IOException {
		//The folder to write the results
		File folder = new File(System.getProperty("user.home")
				+ "/Desktop/AssetsAndDealsSample");
		//The file to write the results
		File results = new File(System.getProperty("user.home")
				+ "/Desktop/AssetsAndDealsSample", "Results.txt");
		
		if (!folder.exists()) {
			folder.mkdir();
		}
		if (!results.exists()) {
			results.createNewFile();
		}
		
		BufferedWriter bw = new BufferedWriter(new FileWriter(results));
		bw.write(s);
		bw.close();
	}
}
