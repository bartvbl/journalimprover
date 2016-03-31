package data;

public class Date {
	public final int year;
	public final int month;
	public final int day;

	public Date(int year, int month, int day) {
		this.year = year;
		this.month = month;
		this.day = day;
	}
	
	public String toString() {
		return year + "-" + month + "-" + day;
	}

	public static Date fromString(String value) {
		String[] parts = value.split("-");
		int year = Integer.parseInt(parts[0]);
		int month = Integer.parseInt(parts[1]);
		int day = Integer.parseInt(parts[2]);
		return new Date(year, month, day);
	}

	public String toPrettyString() {
		return toString();
	}
}
