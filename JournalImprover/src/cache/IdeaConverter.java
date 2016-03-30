package cache;

import data.Idea;
import data.Paper;
import nu.xom.Attribute;
import nu.xom.Element;
import nu.xom.Elements;

public class IdeaConverter {
	public static Element convertIdeaToXML(Idea idea) {
		Element ideaElement = new Element("idea");
		
		Attribute nameAttribute = new Attribute("name", idea.name);
		ideaElement.addAttribute(nameAttribute);
		
		for(Paper relevantPaper : idea.relevantPapers) {
			Element paperElement = PaperConverter.convertPaperToXML(relevantPaper);
			ideaElement.appendChild(paperElement);
		}
		return ideaElement;
	}

	public static Idea convertXMLToIdea(Element element) {
		String name = element.getAttributeValue("name");
		
		Idea idea = new Idea(name);
		Elements paperElements = element.getChildElements();
		
		for(int i = 0; i < paperElements.size(); i++) {
			Paper paper = PaperConverter.convertXMLToPaper(paperElements.get(i));
			idea.relevantPapers.add(paper);
		}
		
		return idea;
	}
}
