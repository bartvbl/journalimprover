package cache;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.HashSet;

import data.Paper;
import lib.util.IOUtils;
import nu.xom.Builder;
import nu.xom.Document;
import nu.xom.Element;
import nu.xom.Elements;
import nu.xom.ParsingException;

public class PaperBaseCache {
	private static final File cacheFile = new File("cache/papers.xml");

	public static void store(HashSet<Paper> paperCollection) {
		Element rootElement = new Element("paperCache");
		for(Paper paper : paperCollection) {
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
			
			rootElement.appendChild(paperElement);
		}
		
		IOUtils.writeXMLDocument(rootElement, cacheFile);
	}

	public static HashSet<Paper> load() {
		if(!cacheFile.exists()) {
			return new HashSet<Paper>();
		}
		
		try {
			String documentContent = IOUtils.readFileContents(cacheFile);
			Builder builder = new Builder();
			InputStream stream = new ByteArrayInputStream(documentContent.getBytes(StandardCharsets.UTF_8));
			Document document = builder.build(stream);
			
			Element rootElement = document.getRootElement();
			Elements cachedPapers = rootElement.getChildElements();
			
			HashSet<Paper> paperCache = new HashSet<Paper>();
			
			for(int i = 0; i < cachedPapers.size(); i++) {
				Element paperElement = cachedPapers.get(i);
				
				Element titleElement = paperElement.getFirstChildElement("title");
				Element abstractElement = paperElement.getFirstChildElement("abstract");
				Element dateElement = paperElement.getFirstChildElement("date");
				Element authorElement = paperElement.getFirstChildElement("author");
				
				String title = titleElement.getValue();
				String abstractText = abstractElement.getValue();
				String date = dateElement.getValue();
				String author = authorElement.getValue();
				
				Paper paper = new Paper(title, date, author, abstractText);
				
				paperCache.add(paper);
			}
			
			return paperCache;
		} catch (IOException | ParsingException e) {
			e.printStackTrace();
			throw new RuntimeException("Failed to load cached papers :(", e);
		}
		
	}

}
