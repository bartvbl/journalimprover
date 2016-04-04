package data;

import java.util.ArrayList;

import lib.util.StringUtil;
import querying.DataSource;

public class Paper {

	public final String title;
	public Date publicationDate;
	public Author[] authors;
	public String abstractText;
	public final String subtitle;
	public String publisher;
	public String volume;
	public String page;
	public String PDFURL = null;
	public final String DOI;
	public final ArrayList<DataSource> origins;

	public Paper(DataSource origin, String title, String subtitle, String doi, Author[] authors, Date publicationDate, String publisher, String volume, String page, String abstractText) {
		this.title = title;
		this.subtitle = subtitle;
		this.publicationDate = publicationDate;
		this.publisher = publisher;
		this.DOI = doi;
		this.volume = volume;
		this.page = page;
		this.authors = authors;
		this.abstractText = abstractText;
		
		this.origins = new ArrayList<DataSource>();
		this.origins.add(origin);
	}

	public Paper(DataSource[] sources, String title, String subtitle, String doi, Author[] authors, Date publicationDate, String publisher, String volume, String page, String abstractText) {
		this.title = title;
		this.subtitle = subtitle;
		this.publicationDate = publicationDate;
		this.publisher = publisher;
		this.DOI = doi;
		this.volume = volume;
		this.page = page;
		this.authors = authors;
		this.abstractText = abstractText;
		
		this.origins = new ArrayList<DataSource>();
		for(DataSource origin : sources) {
			origins.add(origin);
		}
	}

	@Override
	public boolean equals(Object other) {
		if(!(other instanceof Paper)) {
			return false;
		}
		Paper otherPaper = (Paper) other;
		if(otherPaper.DOI != null && otherPaper.DOI.equals(DOI)) {
			return true;
		}
		if((otherPaper.DOI == null || this.DOI != null) && (otherPaper.title.equals(this.title))) {
			return true;
		}
		return false;
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

	public void update(Paper paper) {
		int newNumDefinedDateFields = 
				(paper.publicationDate.day != 0 ? 1 : 0) +
				(paper.publicationDate.month != 0 ? 1 : 0) +
				(paper.publicationDate.year != 0 ? 1 : 0);
		int currentNumDefinedDateFields = 
				(this.publicationDate.day != 0 ? 1 : 0) +
				(this.publicationDate.month != 0 ? 1 : 0) +
				(this.publicationDate.year != 0 ? 1 : 0);
		if(newNumDefinedDateFields > currentNumDefinedDateFields) {
			this.publicationDate = paper.publicationDate;
			this.origins.addAll(paper.origins);
		}
		
		if(this.authors.length == 0 && paper.authors.length > 0) {
			this.authors = paper.authors;
			this.origins.addAll(paper.origins);
		}
		
		if(this.abstractText.equals("") && !paper.abstractText.equals("")) {
			System.out.println("Found an abstract for " + paper.title + ".");
			this.abstractText = paper.abstractText;
			this.origins.addAll(paper.origins);
		}

		if(this.publisher.equals("") && !paper.publisher.equals("")) {
			this.publisher = paper.publisher;
			this.origins.addAll(paper.origins);
		}

		if(this.volume.equals("") && !paper.volume.equals("")) {
			this.volume = paper.volume;
			this.origins.addAll(paper.origins);
		}

		if(this.page.equals("") && !paper.page.equals("")) {
			this.page = paper.page;
			this.origins.addAll(paper.origins);
		}	

		
		if((this.PDFURL == null || this.PDFURL.equals("")) && (paper.PDFURL != null && !paper.PDFURL.equals(""))) {
			this.PDFURL = paper.PDFURL;
			this.origins.addAll(paper.origins);
		}
	}
}
