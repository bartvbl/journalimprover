package cache;

import data.Paper;
import nu.xom.Element;

public class PaperConverter {
	public static Element convertPaperToXML(Paper paper) {
		Element paperElement = new Element("paper");
		
		Element titleElement = new Element("title");
		Element abstractElement = new Element("abstract");
		Element dateElement = new Element("date");
		Element authorElement = new Element("author");
		
		titleElement.appendChild(paper.title);
		abstractElement.appendChild(paper.abstractText);
		dateElement.appendChild(paper.publicationDate);
		authorElement.appendChild(paper.authors);
		
		paperElement.appendChild(titleElement);
		paperElement.appendChild(abstractElement);
		paperElement.appendChild(dateElement);
		paperElement.appendChild(authorElement);
		return paperElement;
	}
	
	public static Paper convertXMLToPaper(Element paperElement) {
		Element titleElement = paperElement.getFirstChildElement("title");
		Element abstractElement = paperElement.getFirstChildElement("abstract");
		Element dateElement = paperElement.getFirstChildElement("date");
		Element authorElement = paperElement.getFirstChildElement("author");
		
		String title = titleElement.getValue();
		String abstractText = abstractElement.getValue();
		String date = dateElement.getValue();
		String author = authorElement.getValue();
		
		Paper paper = new Paper(title, date, author, abstractText);
		return paper;
	}
}
