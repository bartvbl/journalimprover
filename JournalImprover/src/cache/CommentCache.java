package cache;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;

import data.Comment;
import data.Idea;
import lib.util.IOUtils;
import nu.xom.Builder;
import nu.xom.Document;
import nu.xom.Element;
import nu.xom.Elements;
import nu.xom.ParsingException;

public class CommentCache {

	private static final File cacheFile = new File("cache/comments.xml");
	
	public static void write(HashMap<String, Comment> commentMap) {
		Element rootElement = new Element("comments");
		
		for(String paperTitle : commentMap.keySet()) {
			Element commentElement = CommentConverter.convertCommentToXML(paperTitle, commentMap.get(paperTitle));
			rootElement.appendChild(commentElement);
		}
		
		IOUtils.writeXMLDocument(rootElement, cacheFile);
	}

	public static HashMap<String, Comment> load() {
		if(!cacheFile.exists()) {
			return new HashMap<String, Comment>();
		}
		
		
		try {
			HashMap<String, Comment> commentMap = new HashMap<String, Comment>();
			String fileContents = IOUtils.readFileContents(cacheFile);
			Builder builder = new Builder();
			InputStream stream = new ByteArrayInputStream(fileContents.getBytes(StandardCharsets.UTF_8));
			Document document = builder.build(stream);
			
			Elements commentElements = document.getRootElement().getChildElements();
			
			for(int i = 0; i < commentElements.size(); i++) {
				Element element = commentElements.get(i);
				Comment comment = CommentConverter.convertXMLToComment(element);
				String title = CommentConverter.convertXMLToPaperTitle(element);
				commentMap.put(title, comment);
			}
			
			return commentMap;
		} catch (IOException | ParsingException e) {
			e.printStackTrace();
			throw new RuntimeException("Failed to load comments!", e);
		}
	}

}
