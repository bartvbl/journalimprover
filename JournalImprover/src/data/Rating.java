package data;

public enum Rating {
	None		(0, "(none specified)"), 
	Garbage		(1, "Garbage"), 
	Bad			(2, "Bad"),
	Mediocre	(3, "Mediocre"), 
	Good		(4, "Good"), 
	Fantastic	(5, "Very relevant");
	
	public final int index;
	public final String displayName;

	private Rating(int index, String displayName) {
		this.index = index;
		this.displayName = displayName;
	}

	public static Rating fromIndex(int selectedIndex) {
		for(Rating rating : Rating.values()) {
			if(rating.index == selectedIndex) {
				return rating;
			}
		}
		throw new RuntimeException("Invalid index!");
	}
}
