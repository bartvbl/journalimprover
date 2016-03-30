package cache;

import data.Idea;
import data.Paper;
import interactivity.PaperBase;
import nu.xom.Attribute;
import nu.xom.Element;
import nu.xom.Elements;

public class IdeaConverter {
	public static Element convertIdeaToXML(Idea idea) {
		Element ideaElement = new Element("idea");
		
		Attribute nameAttribute = new Attribute("name", idea.name);
		ideaElement.addAttribute(nameAttribute);
		
		for(Paper relevantPaper : idea.relevantPapers) {
			Element paperElement = new Element("paper");
			
			Attribute titleAttribute = new Attribute("title", relevantPaper.title);
			paperElement.addAttribute(titleAttribute);
			
			ideaElement.appendChild(paperElement);
		}
		return ideaElement;
	}

	public static Idea convertXMLToIdea(Element element, PaperBase paperBase) {
		String name = element.getAttributeValue("name");
		
		Idea idea = new Idea(name);
		Elements paperElements = element.getChildElements();
		
		for(int i = 0; i < paperElements.size(); i++) {
			String paperTitle = paperElements.get(i).getAttributeValue("title");
			Paper paper = paperBase.getPaperByTitle(paperTitle);
			idea.relevantPapers.add(paper);
		}
		
		return idea;
	}
}
