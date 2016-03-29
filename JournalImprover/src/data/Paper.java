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
	
	@Override
	public boolean equals(Object other) {
		if(!(other instanceof Paper)) {
			return false;
		}
		Paper otherPaper = (Paper) other;
		return otherPaper.title.equals(title);
	}
	
	@Override
	public int hashCode() {
		return title.hashCode();
	}
}
