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
			Element paperElement = PaperConverter.convertPaperToXML(paper);
			
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
				
				Paper paper = PaperConverter.convertXMLToPaper(paperElement);
				
				paperCache.add(paper);
			}
			
			return paperCache;
		} catch (IOException | ParsingException e) {
			e.printStackTrace();
			throw new RuntimeException("Failed to load cached papers :(", e);
		}
		
	}
}
