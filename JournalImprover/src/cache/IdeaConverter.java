package cache;

import data.Idea;
import data.Paper;
import interactivity.paperBase.PaperBase;
import nu.xom.Attribute;
import nu.xom.Element;
import nu.xom.Elements;

public class IdeaConverter {
	public static Element convertIdeaToXML(Idea idea) {
		Element ideaElement = new Element("idea");
		
		Attribute nameAttribute = new Attribute("name", idea.name);
		ideaElement.addAttribute(nameAttribute);
		
		Element notesElement = new Element("notes");
		notesElement.appendChild(idea.notes);
		ideaElement.appendChild(notesElement);
		
		Element papersElement = new Element("papers");
		for(Paper relevantPaper : idea.relevantPapers) {
			Element paperElement = new Element("paper");
			
			Attribute titleAttribute = new Attribute("title", relevantPaper.title);
			paperElement.addAttribute(titleAttribute);
			
			papersElement.appendChild(paperElement);
		}
		ideaElement.appendChild(papersElement);
		return ideaElement;
	}

	public static Idea convertXMLToIdea(Element element, PaperBase paperBase) {
		String name = element.getAttributeValue("name");
		
		Idea idea = new Idea(name);
		Elements paperElements = element.getFirstChildElement("papers").getChildElements();
		Element notesElement = element.getFirstChildElement("notes");
		idea.notes = notesElement.getValue();
		
		for(int i = 0; i < paperElements.size(); i++) {
			String paperTitle = paperElements.get(i).getAttributeValue("title");
			Paper paper = paperBase.getPaperByTitle(paperTitle);
			idea.relevantPapers.add(paper);
		}
		
		return idea;
	}
}
