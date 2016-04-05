package querying.scienceDirect;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;

import data.Author;
import data.Date;
import data.Paper;
import interactivity.OnlineSearchHandler;
import lib.util.Config;
import lib.util.HTTPRequester;
import nu.xom.Builder;
import nu.xom.Document;
import nu.xom.Element;
import nu.xom.Elements;
import nu.xom.ParsingException;
import nu.xom.ValidityException;
import querying.DataSource;

public class ScienceDirectLoader {

	private static final String baseURL = "api.elsevier.com/content/search/scidir";
	private static final int papersPerRequest = 200;
	private static final int numRequests = 15;
	
	private static final HashMap<String, String> URIMap = new HashMap<String, String>();
	
	static {
		URIMap.put("dc", "http://purl.org/dc/elements/1.1/");
		URIMap.put("prism", "http://prismstandard.org/namespaces/basic/2.0/");
		URIMap.put("Atom", "http://www.w3.org/2005/Atom");
	}
	
	public static Paper[] query(String query, OnlineSearchHandler onlineSearchHandler) throws IOException, ValidityException, ParsingException {
		Paper[] foundPapers = new Paper[0];
		for(int i = 0; i < numRequests; i++) {
			onlineSearchHandler.printStatusMessage("ScienceDirect: Request " + (i+1) + " of " + numRequests);
			
			// Sequence number is 1-indexed
			String url = baseURL + "?" +
					"httpAccept=application/xml&" + 
					"apiKey=" + Config.get("Elsevier") + "&" +
					"query=" + query + "&" +
					"view=COMPLETE&" +
					"suppressNavLinks=true&" + 
					"start=" + (i * papersPerRequest) + "&" +
					"count=" + papersPerRequest;

			Element rootElement = requestDocument(url, onlineSearchHandler);
			
			Elements responseElements = rootElement.getChildElements();
			
			int entriesInResponse = countEntriesInResponse(responseElements);
			
			Paper[] result = new Paper[entriesInResponse];
			int paperIndex = 0;
			
			for(int paper = 0; paper < responseElements.size(); paper++) {
				onlineSearchHandler.setProgress((double) paperIndex / (double) entriesInResponse);
				Element entry = responseElements.get(paper);
				if(entry.getLocalName().equals("entry")) {
					result[paperIndex] = parsePaper(entry);
					paperIndex++;
				}
			}
			
			Paper[] newPaperArray = new Paper[foundPapers.length + result.length];
			System.arraycopy(foundPapers, 0, newPaperArray, 0, foundPapers.length);
			System.arraycopy(result, 0, newPaperArray, foundPapers.length, result.length);
			foundPapers = newPaperArray;
			
			onlineSearchHandler.printStatusMessage("ScienceDirect: Complete.");
			try {
				Thread.sleep(10000); // Being nice to the API
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		
		return foundPapers;
		
	}

	private static Paper parsePaper(Element entry) {
		try {
			// dc:identifier - unsupported
			// eid - unsupported
			// prism:url - unsupported
			String title = entry.getFirstChildElement("title", URIMap.get("dc")).getValue();
			// dc:creator - unsupported
			String abstractText = getEntryValue(entry, "description", "dc", "");
			// prism:publicationName - unsupported
			// prism:issn - unsupported
			String volume = getEntryValue(entry, "volume", "prism", "");
			// prism:issueIdentifier - unsupported
			Date publicationDate = parseDate(entry);
			// prism:coverDisplayDate - unsupported
			String pages = 
					hasChild(entry, "startingPage", "prism") ? 
							getEntryValue(entry, "startingPage", "prism", "") + 
							(hasChild(entry, "endingPage", "prism") ? " - " + getEntryValue(entry, "endingPage", "prism", "") : "") 
							: "";
			String DOI = getEntryValue(entry, "DOI", "prism", "");
			// openaccess - unsupported
			// openaccessArticle - unsupported
			// openArchiveArticle - unsupported
			// openacessUserLicense - unsupported
			// prism:aggregationType - unsupported
			String publisher = getEntryValue(entry, "copyright", "prism", "");
			// pii - unsupported
			Author[] authors = hasChild(entry, "authors", "Atom") ? parseAuthors(entry.getFirstChildElement("authors", URIMap.get("Atom"))) : new Author[0];
			
			Paper paper = new Paper(DataSource.ScienceDirect, title, "", DOI, authors, publicationDate, publisher, volume, pages, abstractText);
			
			return paper;
		} catch(Exception e) {
			System.out.println("Error occurred in parsing: ");
			System.out.println(entry.toXML());
			Elements children = entry.getChildElements();
			for(int i = 0; i < children.size(); i++) {
				System.out.println("Found entry: " + children.get(i).getLocalName() + ", " + children.get(i).getBaseURI() + ", " + children.get(i).getNamespacePrefix() + ", " + children.get(i).getNamespaceURI() + ", " + children.get(i).getQualifiedName());
			}
			throw new RuntimeException("Failed to parse XML document!", e);
		}
	}
	
	private static boolean hasChild(Element entry, String name, String URIName) {
		Element foundElement = entry.getFirstChildElement(name, URIMap.get(URIName));
		return foundElement != null;
	}

	private static String getEntryValue(Element entry, String name, String URIName, String defaultValue) {
		Element foundElement = entry.getFirstChildElement(name, URIMap.get(URIName));
		if(foundElement == null) {
			return defaultValue;
		}
		if(foundElement.getChildCount() == 0) {
			return defaultValue;
		}
		return entry.getFirstChildElement(name, URIMap.get(URIName)).getValue();
	}

	private static Date parseDate(Element entry) {
		String dateString = getEntryValue(entry, "coverDate", "prism", "");
		String[] dateParts = dateString.split("-");
		
		return new Date(
				Integer.parseInt(dateParts[0]), 
				Integer.parseInt(dateParts[1]), 
				Integer.parseInt(dateParts[2])
			);
	}

	private static Author[] parseAuthors(Element element) {
		Elements authorElements = element.getChildElements();
		Author[] authors = new Author[authorElements.size()];
		for(int i = 0; i < authors.length; i++) {
			Element authorElement = authorElements.get(i);
			String firstName = getEntryValue(authorElement, "given-name", "Atom", "");
			String lastName = getEntryValue(authorElement, "surname", "Atom", "");
			authors[i] = new Author(firstName, lastName, new String[0]);
		}
		return authors;
	}

	private static int countEntriesInResponse(Elements responseElements) {
		int entriesInResponse = 0;
		for(int i = 0; i < responseElements.size(); i++) {
			if(responseElements.get(i).getLocalName().equals("entry")) {
				entriesInResponse++;
			}
		}
		return entriesInResponse;
	}

	private static Element requestDocument(String url, OnlineSearchHandler onlineSearchHandler) throws IOException, ParsingException, ValidityException {
		String responseString = HTTPRequester.request(url);
		Builder builder = new Builder();
		InputStream stream = new ByteArrayInputStream(responseString.getBytes(StandardCharsets.UTF_8));
		Document document = builder.build(stream);
		Element rootElement = document.getRootElement();
		return rootElement;
	}

}
