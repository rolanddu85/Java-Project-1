import java.util.Map;

/**
 * This object is instantiated per credit rating requirement (some deals have
 * multiple credit requirements). The purpose is for sorting all the deals by
 * credit rating requirement, in order to satisfy the most restrictive credit
 * ratings first.
 * 
 * @author rdu
 *
 */
class DealCreditPtr implements Comparable<DealCreditPtr> {
	private long price;
	private String credit;
	private UniqueDeal unqDeal;

	/**
	 * The constructor
	 * @param price The required price of the deal
	 * @param credit The required credit rating of the deal
	 * @param deal The reference to the respective deal object
	 */
	DealCreditPtr(long price, String credit, UniqueDeal deal) {
		this.price = price;
		this.credit = credit;
		this.unqDeal = deal;
	}

	long getPrice() {
		return this.price;
	}

	void setPrice(long price) {
		this.price = price;
	}

	String getCredit() {
		return credit;
	}

	void setCredit(String credit) {
		this.credit = credit;
	}

	UniqueDeal getUnqDeal() {
		return unqDeal;
	}

	void setUnqDeal(UniqueDeal deal) {
		this.unqDeal = deal;
	}

	/**
	 * Calculate the correct amount to allocate/subtract
	 * @param asset The asset being used for determining class and credit
	 * @param classExists True if deal actually contains this class key
	 * @param isInventory True if the asset is from inventory, False if borrowing
	 * @return The amount to allocate/subtract from deals
	 */
	long calculateAllocateAmount(Asset asset, boolean classExists, boolean isInventory) {
		long allocateAmount;
		Map<String, Long> dealClassMap = this.unqDeal.getaClass();
		Map<String, Long> dealCreditMap = this.unqDeal.getCredit();
		
		// make sure to only allocate whichever is smaller between
		// required class, credit, or the asset amount
		if (classExists) {
			allocateAmount = Math.min(dealClassMap.get(asset.getaClass())
					+ this.unqDeal.getRemainingAClassPrice(),
					dealCreditMap.get(asset.getCredit()));
		} else {
			allocateAmount = Math.min(this.unqDeal.getRemainingAClassPrice(),
					dealCreditMap.get(asset.getCredit()));
		}
		
		if (isInventory) {
			allocateAmount = Math.min(allocateAmount, asset.getPrice());
		}

		return allocateAmount;
	}
	
	/**
	 * Actually perform the allocation of inventory assets for the deals and
	 * subtract from the deal requirements AND inventory AND dealptr afterwards
	 * @param asset The asset being used for determining class and credit
	 * @param classExists True if deal actually contains this class key
	 * @param allocateAmount The amount to allocate/subtract
	 * @param isInventory True if the asset is from inventory, False if borrowing
	 */
	void performAllocateAsset(Asset asset, boolean classExists, long allocateAmount, boolean isInventory) {
		long tempCreditValue;
		long tempClassValue;
		Map<String, Long> dealCreditMap = this.unqDeal.getCredit();
		Map<String, Long> dealClassMap = this.unqDeal.getaClass();
		
		//If the class key does not exist
		if (!classExists) {
			this.unqDeal.setRemainingAClassPrice(this.unqDeal
					.getRemainingAClassPrice() - allocateAmount);
		}
		//Else if the amount to allocate is larger than required class amount
		else if (allocateAmount > dealClassMap.get(asset.getaClass())) {
			tempClassValue = 0;
			dealClassMap.put(asset.getaClass(), tempClassValue);
			this.unqDeal.setRemainingAClassPrice(this.unqDeal
					.getRemainingAClassPrice()
					+ dealClassMap.get(asset.getaClass()) - allocateAmount);
		}
		//If the amount to allocate is less than required class amount
		else {
			tempClassValue = dealClassMap.get(asset.getaClass())
					- allocateAmount;
			dealClassMap.put(asset.getaClass(), tempClassValue);
		}
		
		this.price -= allocateAmount;
		tempCreditValue = dealCreditMap.get(asset.getCredit())
				- allocateAmount;
		dealCreditMap.put(asset.getCredit(), tempCreditValue);
		if (isInventory) {
			asset.setPrice(asset.getPrice() - allocateAmount);
		}
	}
	
	/**
	 * Allocate the correct amount of inventory assets for the deals and
	 * subtract from the deal requirements AND inventory AND dealptr afterwards
	 * @param unqAsset The asset we are using to satisfy the deal
	 */
	void allocateAsset(Asset unqAsset) {
		long allocateAmount;
		// Check if deal actually contains this class key
		boolean classExists = this.unqDeal.getaClass().containsKey(
				unqAsset.getaClass());

		allocateAmount = calculateAllocateAmount(unqAsset, classExists, true);
		//Dont bother doing the rest
		if (allocateAmount == 0) { return; }
		performAllocateAsset(unqAsset, classExists, allocateAmount, true);
	}

	/**
	 * Allocate correct amount of inventory assets for the deals Subtract deal
	 * and subtract from requirements AND dealptr afterwards
	 * @param borrow The asset we are using to satisfy the deal
	 */
	long borrowAsset(Asset borrow) {
		long allocateAmount;
		// Check if deal actually contains this class key
		boolean classExists = this.unqDeal.getaClass().containsKey(
				borrow.getaClass());

		allocateAmount = calculateAllocateAmount(borrow, classExists, false);
		//Dont bother doing the rest
		if (allocateAmount == 0) { return 0; }
		performAllocateAsset(borrow, classExists, allocateAmount, false);
		return allocateAmount;
	}

	/**
	 * This returns the value for sorting with compareTo()
	 * @param s Asset credit rating
	 * @return
	 */
	int creditSortValue(String s) {
		switch (s) {
		case "AAA":
			return 0;
		case "AA":
			return 1;
		case "A":
			return 2;
		default:
			System.out.println("Uknown credit value");
			return 99;
		}
	}

	@Override
	public int compareTo(DealCreditPtr dealptr) {
		if (creditSortValue(this.credit) == creditSortValue(dealptr.credit)) {
			return (int) (dealptr.price - this.price);
		}
		else {
			return creditSortValue(this.credit) - creditSortValue(dealptr.credit);
		}
	}

	@Override
	public String toString() {
		return "DealCreditPtr [price=" + price + ", credit=" + credit
				+ ", unqDeal=" + unqDeal + "]";
	}
}
