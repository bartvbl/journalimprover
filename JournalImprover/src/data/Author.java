package data;

public class Author {
	public final String firstName;
	public final String lastName;
	public final String[] affiliation;

	public Author(String firstName, String lastName, String[] affiliation) {
		this.firstName = firstName;
		this.lastName = lastName;
		this.affiliation = affiliation;
	}
}
