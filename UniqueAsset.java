/**
 * This represents a unique inventory asset that we have in our possession
 * 
 * @author rdu
 *
 */
class UniqueAsset extends Asset {
	private String id;

	UniqueAsset(String id, String price, String aClass, String credit) {
		super(price, aClass, credit);
		this.id = id;
	}

	String getId() {
		return id;
	}

	void setId(String id) {
		this.id = id;
	}

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

	int aClassSortValue(String s) {
		switch (s) {
		case "c":
			return 0;
		case "m":
			return 1;
		case "s":
			return 2;
		default:
			System.out.println("Uknown asset class value");
			return 99;
		}
	}

	@Override
	public int compareTo(Asset asset) {
		int value = creditSortValue(getCredit()) - creditSortValue(asset.getCredit());
		if (value == 0) {
			return aClassSortValue(getaClass()) - aClassSortValue(asset.getaClass());
		} else {
			return value;
		}
	}

	@Override
	public String toString() {
		return "UniqueAsset [id=" + id + ", current=$" + getPrice()
				+ ", aClass=" + getaClass() + ", credit=" + getCredit() + "]";
	}
}
