package data;

import lib.util.StringUtil;

public class Paper {

	public final String title;
	public final Date publicationDate;
	public final Author[] authors;
	public String abstractText;
	public final String subtitle;
	public final String publisher;
	public final String volume;
	public final String page;
	public String PDFURL = null;

	public Paper(String title, String subtitle, Author[] authors, Date publicationDate, String publisher, String volume, String page, String abstractText) {
		this.title = title;
		this.subtitle = subtitle;
		this.publicationDate = publicationDate;
		this.publisher = publisher;
		this.volume = volume;
		this.page = page;
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

	public boolean containsAuthor(String searchQuery) {
		for(Author author : authors) {
			if(author.firstName.contains(searchQuery) || author.lastName.contains(searchQuery)) {
				return true;
			}
		}
		return false;
	}

	public String createAuthorString() {
		String affiliationString = (authors[0].affiliation.length > 0 && !authors[0].affiliation[0].equals("") ? " (" + StringUtil.createCommaSeparatedList(authors[0].affiliation) + ")" : "");
		String authorString = authors[0].firstName + " " + authors[0].lastName + affiliationString + "\n";
		for(int i = 1; i < authors.length; i++) {
			affiliationString = (authors[i].affiliation.length > 0 && !authors[0].affiliation[0].equals("") ? " (" + StringUtil.createCommaSeparatedList(authors[i].affiliation) + ")" : "");
			authorString += authors[i].firstName + " " + authors[i].lastName + affiliationString + "\n";
		}
		return authorString;
	}

	public void setPDFURL(String pdfURL) {
		this.PDFURL  = pdfURL;
	}
}
