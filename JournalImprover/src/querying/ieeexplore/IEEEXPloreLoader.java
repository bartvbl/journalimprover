package querying.ieeexplore;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import data.Author;
import data.Date;
import data.Paper;
import interactivity.OnlineSearchHandler;
import lib.util.HTTPRequester;
import nu.xom.Builder;
import nu.xom.Document;
import nu.xom.Element;
import nu.xom.Elements;
import nu.xom.ParsingException;
import nu.xom.ValidityException;

public class IEEEXPloreLoader {

	private static final String baseURL = "http://ieeexplore.ieee.org/gateway/ipsSearch.jsp?";
	private static final int papersPerRequest = 1000;
	private static final int numRequests = 5;
	
	public static Paper[] query(String query, OnlineSearchHandler onlineSearchHandler) throws IOException, ValidityException, ParsingException {
		Paper[] foundPapers = new Paper[0];
		for(int i = 0; i < numRequests; i++) {
			// Sequence number is 1-indexed
			String url = baseURL + "queryText=" + query + "&hc=" + papersPerRequest + "&rs=" + ((i * papersPerRequest) + 1);
			Element rootElement = requestDocument(url, onlineSearchHandler);
			Elements responseElements = rootElement.getChildElements();
			
			int entriesInResponse = countEntriesInResponse(responseElements);
			
			Paper[] result = new Paper[entriesInResponse];
			int paperIndex = 0;
			
			for(int paper = 0; paper < responseElements.size(); paper++) {
				onlineSearchHandler.setProgress((double) paperIndex / (double) entriesInResponse);
				Element entry = responseElements.get(paper);
				if(entry.getLocalName().equals("document")) {
					result[paperIndex] = parsePaper(entry);
					paperIndex++;
				}
			}
			
			Paper[] newPaperArray = new Paper[foundPapers.length + result.length];
			System.arraycopy(foundPapers, 0, newPaperArray, 0, foundPapers.length);
			System.arraycopy(result, 0, newPaperArray, foundPapers.length, result.length);
			foundPapers = newPaperArray;
			
			onlineSearchHandler.printStatusMessage("IEEEXPlore: Complete.");
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
		int rank = Integer.parseInt(entry.getFirstChildElement("rank").getValue());
		String title = entry.getFirstChildElement("title") != null ? entry.getFirstChildElement("title").getValue() : "";
		Author[] authors = parseAuthors(entry.getFirstChildElement("authors").getValue());
		// "controlledterms" - unsupported
		// "thesaurusterms" - unsupported
		String pubtitle = entry.getFirstChildElement("pubtitle").getValue();
		int publicationNumber = Integer.parseInt(entry.getFirstChildElement("punumber").getValue());
		String publicationType = entry.getFirstChildElement("pubtype").getValue();
		String publisher = entry.getFirstChildElement("publisher").getValue();
		String volume = entry.getFirstChildElement("volume") != null ? entry.getFirstChildElement("volume").getValue() : "";
		Date publicationYear = new Date(Integer.parseInt(entry.getFirstChildElement("py").getValue()), 0, 0);
		String pages = 
				(entry.getFirstChildElement("spage") != null ? entry.getFirstChildElement("spage").getValue() : "") + 
				(entry.getFirstChildElement("epage") != null ? " - " + entry.getFirstChildElement("epage").getValue() : "");
		String abstractText = entry.getFirstChildElement("abstract") != null ? entry.getFirstChildElement("abstract").getValue() : "";
		String isbn = entry.getFirstChildElement("isbn") != null ? entry.getFirstChildElement("isbn").getValue() : "";
		// htmlFlag - unsupported
		int arnumber = Integer.parseInt(entry.getFirstChildElement("arnumber").getValue());
		String DOI = entry.getFirstChildElement("doi") != null ? entry.getFirstChildElement("doi").getValue() : "";
		int publicationID = Integer.parseInt(entry.getFirstChildElement("publicationId").getValue());
		int partnum = entry.getFirstChildElement("partnum") != null ? Integer.parseInt(entry.getFirstChildElement("partnum").getValue()) : -1;
		String mdurl = entry.getFirstChildElement("mdurl").getValue();
		String pdfURL = entry.getFirstChildElement("pdf").getValue();
		
		if(title.equals("")) {
			title = pubtitle;
		}
		
		Paper paper = new Paper(title, "", authors, publicationYear, publisher, volume, pages, abstractText);
		
		return paper;
		} catch(Exception e) {
			System.out.println("Error occurred in parsing: ");
			System.out.println(entry.toXML());
			throw new RuntimeException("Failed to parse XML document!", e);
		}
	}

	private static Author[] parseAuthors(String value) {
		String[] authorParts = value.split("; ");
		Author[] authors = new Author[authorParts.length];
		for(int i = 0; i < authors.length; i++) {
			authors[i] = new Author(authorParts[i], "", new String[0]);
		}
		return authors;
	}

	private static int countEntriesInResponse(Elements responseElements) {
		int entriesInResponse = 0;
		for(int i = 0; i < responseElements.size(); i++) {
			if(responseElements.get(i).getLocalName().equals("document")) {
				entriesInResponse++;
			}
		}
		return entriesInResponse;
	}

	private static Element requestDocument(String url, OnlineSearchHandler onlineSearchHandler) throws IOException, ParsingException, ValidityException {
		String responseString = HTTPRequester.request(url);
		onlineSearchHandler.printStatusMessage("IEEEXPlore: XML Response received.");
		Builder builder = new Builder();
		InputStream stream = new ByteArrayInputStream(responseString.getBytes(StandardCharsets.UTF_8));
		Document document = builder.build(stream);
		Element rootElement = document.getRootElement();
		onlineSearchHandler.printStatusMessage("IEEEXPlore: XML Response parsed.");
		return rootElement;
	}

}
