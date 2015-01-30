/**
 * This is a basic asset object and contains "credit rating" and "asset class type"
 * @author rdu
 *
 */

class Asset implements Comparable<Asset> {
	private long price;
	// AssetClasses = "c", "s", "m"
	private String assetClass;
	// Credit = "AAA", "AA", "A"
	private String credit;
	
	Asset(String price, String assetClass, String credit) {
		this.price = Long.parseLong(price);
		this.assetClass = assetClass;
		this.credit = credit;
	}

	long getPrice() {
		return price;
	}

	void setPrice(long price) {
		this.price = price;
	}
	
	String getaClass() {
		return assetClass;
	}

	void setaClass(String aClass) {
		this.assetClass = aClass;
	}

	String getCredit() {
		return credit;
	}

	void setCredit(String credit) {
		this.credit = credit;
	}

	static String getLowestCreditRating() { 
		return "A"; 
	}
	
	@Override
	public int compareTo(Asset asset) {
		return (int)(this.price - asset.price);
	}

	@Override
	public String toString() {
		return "Asset [price=" + price + ", aClass=" + assetClass + ", credit="
				+ credit + "]";
	}
	
}
