package querying.springer;

import java.io.IOException;
import java.util.HashSet;

import javax.swing.plaf.synth.SynthSeparatorUI;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import lib.util.Config;
import lib.util.HTTPRequester;
import querying.DataSource;
import data.Author;
import data.Date;
import data.Paper;
import interactivity.OnlineSearchHandler;

public class SpringerLoader {
	private static final String baseURL = "http://api.springer.com/meta/v1/json";
	private static final int papersPerRequest = 100;
	private static final int numRequests = 30;

	public static Paper[] query(String query, OnlineSearchHandler onlineSearchHandler) throws IOException {
		Paper[] foundPapers = new Paper[0];
		for(int i = 0; i < numRequests; i++) {
			onlineSearchHandler.printStatusMessage("Springer: Request " + (i+1) + " of " + numRequests);
			Paper[] result = query(query, i * papersPerRequest, papersPerRequest);
			Paper[] newPaperArray = new Paper[foundPapers.length + result.length];
			System.arraycopy(foundPapers, 0, newPaperArray, 0, foundPapers.length);
			System.arraycopy(result, 0, newPaperArray, foundPapers.length, result.length);
			foundPapers = newPaperArray;
			onlineSearchHandler.printStatusMessage("Springer: Complete.");
			try {
				Thread.sleep(10000); // Being nice to the API
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		return foundPapers;
	}
	
	private static Paper[] query(String query, int start, int count) throws IOException {
		HashSet<String> entryMap = new HashSet<String>();
		String url = baseURL + "?" +
				"q=" + query + "&" + 
				"s=" + (start + 1) + "&" +
				"p=" + (count) + "&" +
				"api_key=" + Config.get("Springer");
		String result = HTTPRequester.request(url);
		JSONObject response = new JSONObject(result);

		JSONArray paperList = response.getJSONArray("records");
		
		int paperCount = paperList.length();
		
		Paper[] papers = new Paper[paperCount];
		
		for(int i = 0; i < paperCount; i++) {
			JSONObject entry = paperList.getJSONObject(i);
			papers[i] = parsePaper(entry);
			entryMap.addAll(entry.keySet());
		}
		System.out.println("Entries: " + entryMap);
		
		return papers;
	}

	private static Paper parsePaper(JSONObject entry) {
		try {
			// identifier - unsupported
			String PDFURL = getPDFURL(entry);
			String title = entry.getString("title");
			Author[] authors = parseAuthors(entry.getJSONArray("creators"));
			String publisher = entry.getString("publicationName");
			String ISSN = getString(entry, "issn", "");
			// openaccess - unsupported
			// journalid - unsupported
			String DOI = entry.getString("doi");
			// publisher - unsupported
			Date publicationDate = parseDate(entry.getString("publicationDate"));
			String volume = entry.getString("volume");
			// number - unsupported
			// issuetype - unsupported
			// topologicalCollection - unsupported
			String page = entry.getString("startingPage");
			// copyright - unsupported
			// genre - unsupported
			String abstractText = entry.getString("abstract");
			
			Paper paper = new Paper(DataSource.Springer, title, "", DOI, authors, publicationDate, publisher, volume, page, abstractText);
			return paper;
		} catch(JSONException e) {
			e.printStackTrace();
			System.err.println("Entry in question: " + entry.toString(4));
			throw new RuntimeException("Failed to parse paper", e);
		}
		
	}

	private static String getString(JSONObject entry, String entryString, String defaultString) {
		if(entry.has(entryString)) {
			return entry.getString(entryString);
		} else {
			return defaultString;
		}
	}

	private static String getPDFURL(JSONObject entry) {
		if(!entry.has("url")) {
			return "";
		}
		JSONArray urlTypes = entry.getJSONArray("url");
		for(int i = 0; i < urlTypes.length(); i++) {
			JSONObject urlEntry = urlTypes.getJSONObject(i);
			if(urlEntry.getString("format").equals("pdf")) {
				return urlEntry.getString("value");
			}
		}
		return "";
	}

	private static Author[] parseAuthors(JSONArray authorList) {
		Author[] authors = new Author[authorList.length()];
		for(int authorID = 0; authorID < authorList.length(); authorID++) {
			JSONObject entry = authorList.getJSONObject(authorID);
			String authorFullName = entry.getString("creator");
			String[] nameParts = authorFullName.split(", ");
			String firstName = nameParts.length > 1 ? nameParts[1] : "";
			String lastName = nameParts[0];
			String[] affiliations = new String[0];
			
			authors[authorID] = new Author(firstName, lastName, affiliations);
		}
		return authors;
	}

	private static String[] parseAffiliationArray(JSONArray entry) {
		String[] strings = new String[entry.length()];
		for(int i = 0; i < entry.length(); i++) {
			strings[i] = entry.getJSONObject(i).getString("name");
		}
		return strings;
	}
	
	private static String[] parseStringArray(JSONArray entry) {
		String[] strings = new String[entry.length()];
		for(int i = 0; i < entry.length(); i++) {
			strings[i] = entry.getString(i);
		}
		return strings;
	}

	private static Date parseDate(String dateString) {
		String[] dateParts = dateString.split("-");
		int year = Integer.parseInt(dateParts[0]);
		int month = Integer.parseInt(dateParts[1]);
		int day = Integer.parseInt(dateParts[2]);
		return new Date(year, month, day);
	}

}
