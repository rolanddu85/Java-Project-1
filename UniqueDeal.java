import java.util.HashMap;
import java.util.Map;

/**
 * 
 */

/**
 * This is a basic "deal" object with required total price, credit rating, 
 * and asset class
 * 
 * @author rdu
 *
 */
class UniqueDeal implements Comparable<UniqueDeal> {
	private String id;
	private long price = 0;
	private Map<String, Long> assetClass = new HashMap<>();
	private Map<String, Long> credit = new HashMap<>();
	private long remainingAClassPrice = 0;

	UniqueDeal(String id) {
		this.id = id;
	}

	String getId() {
		return id;
	}

	void setId(String id) {
		this.id = id;
	}

	long getPrice() {
		return price;
	}

	void setPrice(long price) {
		this.price = price;
	}

	Map<String, Long> getaClass() {
		return assetClass;
	}

	void setaClass(Map<String, Long> aClass) {
		this.assetClass = aClass;
	}

	Map<String, Long> getCredit() {
		return credit;
	}

	void setCredit(Map<String, Long> credit) {
		this.credit = credit;
	}

	long getRemainingAClassPrice() {
		return remainingAClassPrice;
	}

	void setRemainingAClassPrice(long remainingAClassPrice) {
		this.remainingAClassPrice = remainingAClassPrice;
	}

	@Override
	public int compareTo(UniqueDeal deal) {
		return this.id.compareTo(deal.id);
	}

	@Override
	public String toString() {
		return "UniqueDeal [id=" + id + ", price=" + price + ", aClass="
				+ assetClass + ", credit=" + credit + ", remainingAClassPrice="
				+ remainingAClassPrice + "]";
	}
}
