package cache;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

import data.Idea;
import data.Paper;
import interactivity.PaperBase;
import lib.util.IOUtils;
import nu.xom.Attribute;
import nu.xom.Builder;
import nu.xom.Document;
import nu.xom.Element;
import nu.xom.Elements;
import nu.xom.ParsingException;

public class IdeaCache {
	private static final File cacheFile = new File("cache/ideas.xml");

	public static void store(ArrayList<Idea> ideaList) {
		Element rootElement = new Element("ideas");
		
		for(Idea idea : ideaList) {
			Element ideaElement = IdeaConverter.convertIdeaToXML(idea);
			rootElement.appendChild(ideaElement);
		}
		
		IOUtils.writeXMLDocument(rootElement, cacheFile);
	}

	public static ArrayList<Idea> load(PaperBase paperBase) {
		
		if(!cacheFile.exists()) {
			return new ArrayList<Idea>();
		}
		
		try {
			ArrayList<Idea> ideas = new ArrayList<Idea>();
			String fileContents = IOUtils.readFileContents(cacheFile);
			Builder builder = new Builder();
			InputStream stream = new ByteArrayInputStream(fileContents.getBytes(StandardCharsets.UTF_8));
			Document document = builder.build(stream);
			
			Elements ideaElements = document.getRootElement().getChildElements();
			
			for(int i = 0; i < ideaElements.size(); i++) {
				Idea idea = IdeaConverter.convertXMLToIdea(ideaElements.get(i), paperBase);
				ideas.add(idea);
			}
			
			return ideas;
		} catch (IOException | ParsingException e) {
			e.printStackTrace();
			throw new RuntimeException("Failed to load ideas!", e);
		}
	}

}
