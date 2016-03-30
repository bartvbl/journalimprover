package cache;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.HashMap;
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

	public static void store(Collection<Paper> papers) {
		Element rootElement = new Element("paperCache");
		for(Paper paper : papers) {
			Element paperElement = PaperConverter.convertPaperToXML(paper);
			
			rootElement.appendChild(paperElement);
		}
		
		IOUtils.writeXMLDocument(rootElement, cacheFile);
	}

	

	public static HashMap<String, Paper> load() {
		if(!cacheFile.exists()) {
			return new HashMap<String, Paper>();
		}
		
		try {
			String documentContent = IOUtils.readFileContents(cacheFile);
			Builder builder = new Builder();
			InputStream stream = new ByteArrayInputStream(documentContent.getBytes(StandardCharsets.UTF_8));
			Document document = builder.build(stream);
			
			Element rootElement = document.getRootElement();
			Elements cachedPapers = rootElement.getChildElements();
			
			HashMap<String, Paper> paperCache = new HashMap<String, Paper>();
			
			for(int i = 0; i < cachedPapers.size(); i++) {
				Element paperElement = cachedPapers.get(i);
				
				Paper paper = PaperConverter.convertXMLToPaper(paperElement);
				
				paperCache.put(paper.title, paper);
			}
			
			return paperCache;
		} catch (IOException | ParsingException e) {
			e.printStackTrace();
			throw new RuntimeException("Failed to load cached papers :(", e);
		}
		
	}

}
