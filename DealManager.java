import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This class contains all the Lists and Maps of the csv file. It also performs
 * all of the object instantiation and algorithm for finding the optimal asset
 * allocations for the problem.
 * 
 * @author rdu
 *
 */
class DealManager {
	// The final result of how much we need to borrow
	private long totalBorrowed = 0;
	// List of all the borrow rates when we need to borrow
	private List<Asset> borrowRates = new ArrayList<>();

	/**
	 * Map that contains all Inventory assets, divided into asset classes: 
	 * Each Map <"key"|"value"> = ("c"|"c Map", "m"|"m Map", "s"|"s Map") 
	 * Each asset Map <"key"|"value"> = ("AAA"|"Asset", "AA"|"Asset", "A"|"Asset")
	 */
	private Map<String, Map<String, Asset>> invenAssets = new HashMap<>();

	// Map of all deals
	private Map<String, UniqueDeal> allDeals = new HashMap<>();
	// List of each credit rating requirement, and pointer to the respective deal
	private List<DealCreditPtr> dealCreditReqs = new ArrayList<>();

	String getTotalBorrowed() {
		return Long.toString(this.totalBorrowed);
	}

	/**
	 * Parses csv file and initialize the List of BorrowRates assets
	 * @param file Csv file that contains the borrow rates 
	 * @throws IOException On input error
	 */
	void initBorrowRates(File file) throws IOException {
		for (String line : FileScanner.toStringArray(file)) {
			String[] values = line.split(",");
			this.borrowRates.add(new Asset(values[0], values[1], values[2]));
		}
		Collections.sort(this.borrowRates);
	}

	/**
	 * Parses csv file and initialize the Map of inventory assets
	 * @param file Csv file that contains the inventory assets
	 * @throws IOException On input error
	 */
	void initInventory(File file) throws IOException {
		for (String line : FileScanner.toStringArray(file)) {
			String[] values = line.split(",");
			if (this.invenAssets.containsKey(values[2]) != true) {
				this.invenAssets.put(values[2], new HashMap<String, Asset>());
			}
			this.invenAssets.get(values[2])
					.put(values[3],
							new UniqueAsset(values[0], values[1], values[2],
									values[3]));
		}
	}

	/**
	 * Parses csv file and initialize the deal's total price requirements
	 * @param file Csv file that contains the deal's price requirements
	 * @throws IOException On input error
	 */
	void initDealPrices(File file) throws IOException {
		long CreditPrice;
		UniqueDeal unqDeal;

		for (String line : FileScanner.toStringArray(file)) {
			String[] values = line.split(",");
			if (this.allDeals.containsKey(values[0]) != true) {
				this.allDeals.put(values[0], new UniqueDeal(values[0]));
			}
			CreditPrice = Long.parseLong(values[1]);
			unqDeal = this.allDeals.get(values[0]);
			unqDeal.setPrice(CreditPrice);
			unqDeal.setRemainingAClassPrice(unqDeal.getRemainingAClassPrice()
					+ CreditPrice);
		}
	}

	/**
	 * Parses csv file and initialize the Map of deal's credit requirements
	 * @param file Csv file that contains the deal's credit requirements
	 * @throws IOException On input error
	 */
	void initDealCredits(File file) throws IOException {
		long reqCreditPrice;
		UniqueDeal unqDeal;

		for (String line : FileScanner.toStringArray(file)) {
			String[] values = line.split(",");
			if (this.allDeals.containsKey(values[0]) != true) {
				this.allDeals.put(values[0], new UniqueDeal(values[0]));
			}
			reqCreditPrice = Long.parseLong(values[2]);
			unqDeal = this.allDeals.get(values[0]);
			unqDeal.getCredit().put(values[1], reqCreditPrice);
		}
	}

	/**
	 * Parses csv file and initialize the Map of deal's asset class requirements
	 * @param file Csv file that contains the deal's asset class requirements
	 * @throws IOException On input error
	 */
	void initDealAClasses(File file) throws IOException {
		long reqClassPrice;
		UniqueDeal unqDeal;

		for (String line : FileScanner.toStringArray(file)) {
			String[] values = line.split(",");
			if (this.allDeals.containsKey(values[0]) != true) {
				this.allDeals.put(values[0], new UniqueDeal(values[0]));
			}
			reqClassPrice = Long.parseLong(values[2]);
			unqDeal = this.allDeals.get(values[0]);
			unqDeal.getaClass().put(values[1], reqClassPrice);
			unqDeal.setRemainingAClassPrice(unqDeal.getRemainingAClassPrice()
					- reqClassPrice);
		}
	}

	/**
	 * Initialize the List of pointers to deals for each possible credit
	 * requirement and set all remaining wildcard credit ratings as lowest
	 * possible rating.
	 */
	void initDealCreditReqs() {
		for (UniqueDeal unqDeal : allDeals.values()) {
			// sum of required credit ratings
			long reqSum = 0;
			for (String credit : unqDeal.getCredit().keySet()) {
				reqSum += unqDeal.getCredit().get(credit);
			}
			// Check if the requirement for lowest rating already exists
			if (reqSum > unqDeal.getPrice()) {
				if (unqDeal.getCredit().containsKey(
						Asset.getLowestCreditRating()) != true) {
					unqDeal.getCredit().put(Asset.getLowestCreditRating(),
							unqDeal.getPrice() - reqSum);
				} else {
					unqDeal.getCredit().put(Asset.getLowestCreditRating(),
							unqDeal.getPrice() - reqSum);
				}
			}

			for (String credit : unqDeal.getCredit().keySet()) {
				this.dealCreditReqs.add(new DealCreditPtr(unqDeal.getCredit()
						.get(credit), credit, unqDeal));
			}

		}
		Collections.sort(dealCreditReqs);
	}

	/**
	 * This is the greedy algorithm that handles allocating inventory assets
	 * & borrowing to satisfy the deal requirements.
	 */
	void processDeals() {
		Asset unqAsset;
		UniqueDeal unqDeal;

		// (0)Iterate through each DealCreditPtr to process most restrictive
		// credit rates first
		for (DealCreditPtr dealPtr : this.dealCreditReqs) {
			// (1)Iterate through borrow costs until a credit rating match is
			// found (lowest to highest)
			unqDeal = dealPtr.getUnqDeal();
			for (Asset borrowRate : borrowRates) {
				// (2)Check if deal requires that class OR has an wildcard
				// requirements
				// AND
				// (3)Check if inventory contains that credit/class
				if ((unqDeal.getaClass().containsKey(borrowRate.getaClass()) || unqDeal
						.getRemainingAClassPrice() > 0)
						&& invenAssets.get(borrowRate.getaClass()).containsKey(
								dealPtr.getCredit())) {
					// If check passes ==> subtract deal requirements AND
					// inventory AND dealptr
					unqAsset = invenAssets.get(borrowRate.getaClass()).get(
							dealPtr.getCredit());
					dealPtr.allocateAsset(unqAsset);
				}
			}
			// Check if any dealptr credit price is remaining
			// If remaining > 0 == Repeat (1) AND (2), but skip inventory check
			// and just borrow instead
			for (Asset borrowRate : borrowRates) {
				// (2)Check if deal requires that class OR has an wildcard
				// requirements
				if ((unqDeal.getaClass().containsKey(borrowRate.getaClass())
						|| unqDeal.getRemainingAClassPrice() > 0)
						&& dealPtr.getCredit().equals(borrowRate.getCredit())) {
					// If check passes ==> subtract deal requirements AND dealptr
					totalBorrowed += dealPtr.borrowAsset(borrowRate);
				}
			}
		}
	}

	/**
	 * This prints all Maps and Lists for debugging purposes
	 */
	void printAllData() {
		// Print out all BorrowRates
		System.out.println("**START BorrowRates**");
		for (Asset asset : this.borrowRates) {
			System.out.println(asset.toString());
		}
		System.out.println("**STOP BorrowRates**");

		// Print out all Inventory
		System.out.println("**START Inventory**");
		List<Asset> allAssets = new ArrayList<>();
		for (Map<String, Asset> classEntry : this.invenAssets.values()) {
			allAssets.addAll(classEntry.values());
		}
		Collections.sort(allAssets);
		for (Asset asset : allAssets) {
			System.out.println(asset.toString());
		}
		System.out.println("**STOP Inventory**");

		// Print out all Deals
		System.out.println("**START Deals**");
		List<UniqueDeal> allUnqDeals = new ArrayList<>();
		allUnqDeals.addAll(this.allDeals.values());
		Collections.sort(allUnqDeals);
		for (UniqueDeal deals : allUnqDeals) {
			System.out.println(deals.toString());
		}
		System.out.println("**STOP Deals**");

		// Print out all DealPtrs
		System.out.println("**START DealPtrs**");
		for (DealCreditPtr dealPtrs : this.dealCreditReqs) {
			System.out.println(dealPtrs.toString());
		}
		System.out.println("**STOP DealPtrs**");

		System.out.println("Total amount borrowed: " + totalBorrowed);
	}

}
