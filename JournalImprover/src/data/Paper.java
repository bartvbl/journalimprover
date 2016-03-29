package data;

public class Paper {

	public final String title;
	public final String publicationDate;
	public final String authors;
	public final String abstractText;

	public Paper(String title, String publicationDate, String authors, String abstractText) {
		this.title = title;
		this.publicationDate = publicationDate;
		this.authors = authors;
		this.abstractText = abstractText;
	}

}
