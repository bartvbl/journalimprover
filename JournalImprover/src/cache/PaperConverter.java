package cache;

import data.Author;
import data.Date;
import data.Paper;
import lib.util.StringUtil;
import nu.xom.Attribute;
import nu.xom.Element;
import nu.xom.Elements;
import querying.DataSource;

public class PaperConverter {
	public static Element convertPaperToXML(Paper paper) {
		Element paperElement = new Element("paper");
		
		Element abstractElement = new Element("abstract");
		Element authorsElement = new Element("authors");
		
		Attribute dateAttribute = new Attribute("publicationDate", paper.publicationDate.toString());
		Attribute titleAttribute = new Attribute("title", paper.title);
		Attribute subtitleAttribute = new Attribute("subtitle", paper.subtitle);
		Attribute volumeAttribute = new Attribute("volume", paper.volume);
		Attribute pageAttribute = new Attribute("page", paper.page);
		Attribute publisherAttribute = new Attribute("publisher", paper.publisher);
		
		Element originsElement = new Element("origins");
		for(DataSource origin : paper.origins) {
			Element originElement = new Element("origin");
			originElement.appendChild(origin.name());
		}
		
		paperElement.appendChild(originsElement);
		
		if(paper.DOI != null) {
			Attribute doiAttribute = new Attribute("doi", paper.DOI);
			paperElement.addAttribute(doiAttribute);
		}
		
		if(paper.PDFURL != null) {
			Attribute pdfAttribute = new Attribute("pdfurl", paper.PDFURL);
			paperElement.addAttribute(pdfAttribute);
		}
		
		abstractElement.appendChild(paper.abstractText);
		
		for(Author author : paper.authors) {
			Element authorElement = new Element("author");
			authorsElement.appendChild(authorElement);
			
			Attribute firstNameAttribute = new Attribute("firstName", author.firstName);
			Attribute lastNameAttribute = new Attribute("lastName", author.lastName);
			Attribute affiliations = new Attribute("affiliations", StringUtil.createCommaSeparatedList(author.affiliation));
			
			authorElement.addAttribute(firstNameAttribute);
			authorElement.addAttribute(lastNameAttribute);
			authorElement.addAttribute(affiliations);
		}
		
		paperElement.appendChild(abstractElement);
		paperElement.appendChild(authorsElement);
		
		paperElement.addAttribute(dateAttribute);
		paperElement.addAttribute(titleAttribute);
		paperElement.addAttribute(subtitleAttribute);
		paperElement.addAttribute(volumeAttribute);
		paperElement.addAttribute(pageAttribute);
		paperElement.addAttribute(publisherAttribute);
		
		return paperElement;
	}
	
	public static Paper convertXMLToPaper(Element paperElement) {
		Element abstractElement = paperElement.getFirstChildElement("abstract");
		Element authorsElement = paperElement.getFirstChildElement("authors");
		
		Date date = Date.fromString(paperElement.getAttributeValue("publicationDate"));
		String title = paperElement.getAttributeValue("title");
		String subtitle = paperElement.getAttributeValue("subtitle");
		String volume = paperElement.getAttributeValue("volume");
		String page = paperElement.getAttributeValue("page");
		String publisher = paperElement.getAttributeValue("publisher");
		String doi = paperElement.getAttributeValue("doi");
		
		String abstractText = abstractElement.getValue();
		Author[] authors = parseAuthors(authorsElement);
		
		Element originsElement = paperElement.getFirstChildElement("origins");
		Elements originElements = originsElement.getChildElements();
		DataSource[] sources = new DataSource[originElements.size()];
		for(int i = 0; i < originElements.size(); i++) {
			Element originElement = originElements.get(i);
			DataSource source = DataSource.valueOf(originElement.getValue());
			sources[i] = source;
		}
		
		Paper paper = new Paper(sources, title, subtitle, doi, authors, date, publisher, volume, page, abstractText);
		
		paper.PDFURL = paperElement.getAttributeValue("pdfurl");
		
		return paper;
	}

	private static Author[] parseAuthors(Element authorsElement) {
		Elements authorElements = authorsElement.getChildElements();
		Author[] authors = new Author[authorElements.size()];
		for(int i = 0; i < authors.length; i++) {
			Element authorElement = authorElements.get(i);
			String firstName = authorElement.getAttributeValue("firstName");
			String lastName = authorElement.getAttributeValue("lastName");
			String[] affiliations = authorElement.getAttributeValue("affiliations").split(", ");
			authors[i] = new Author(firstName, lastName, affiliations);
		}
		return authors;
	}
}
