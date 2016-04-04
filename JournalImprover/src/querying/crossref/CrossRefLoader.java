package querying.crossref;

import java.io.IOException;
import java.util.HashSet;

import javax.swing.plaf.synth.SynthSeparatorUI;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import lib.util.HTTPRequester;
import data.Author;
import data.Date;
import data.Paper;
import interactivity.OnlineSearchHandler;

public class CrossRefLoader {
	private static final String baseURL = "http://api.crossref.org/";
	private static final int papersPerRequest = 1000;
	private static final int numRequests = 5;

	public static Paper[] query(String query, OnlineSearchHandler onlineSearchHandler) throws IOException {
		Paper[] foundPapers = new Paper[0];
		for(int i = 0; i < numRequests; i++) {
			onlineSearchHandler.printStatusMessage("CrossRef: Request " + i + " of " + numRequests);
			Paper[] result = query(query, i * papersPerRequest, papersPerRequest);
			Paper[] newPaperArray = new Paper[foundPapers.length + result.length];
			System.arraycopy(foundPapers, 0, newPaperArray, 0, foundPapers.length);
			System.arraycopy(result, 0, newPaperArray, foundPapers.length, result.length);
			foundPapers = newPaperArray;
			onlineSearchHandler.printStatusMessage("CrossRef: Complete.");
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
		String result = HTTPRequester.request(baseURL + "works?rows=" + count + "&offset=" + start + "&query=" + query.replace(' ', '+'));
		JSONObject response = new JSONObject(result);

		if(!response.get("status").equals("ok")) {
			throw new RuntimeException("Unknown error occurred on the server.");
		}
		if(!response.get("message-version").equals("1.0.0")) {
			throw new RuntimeException("Unsupported reponse version!");
		}
		
		JSONObject messageObject = response.getJSONObject("message");
		JSONArray paperList = messageObject.getJSONArray("items");
		
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
			Date indexed = parseDate(entry.getJSONObject("indexed"));
			int referenceCount = entry.getInt("reference-count");
			String publisher = entry.getString("publisher");
			String issue = entry.has("issue") ? entry.getString("issue") : "";
			String DOI = entry.getString("DOI");
			String type = entry.getString("type");
			Date created = parseDate(entry.getJSONObject("created"));
			String page = entry.has("page") ? entry.getString("page") : "";
			String source = entry.getString("source");
			String title = entry.has("title") && entry.getJSONArray("title").length() > 0 ? entry.getJSONArray("title").getString(0) : "";
			String prefix;
			String container_title = entry.has("container-title") && entry.getJSONArray("container-title").length() > 0 ? entry.getJSONArray("container-title").getString(0) : "";
			String volume = entry.has("volume") ? entry.getString("volume") : "unknown";
			Author[] authors = entry.has("author") ? parseAuthors(entry.getJSONArray("author")) : new Author[]{new Author("[unknown author]", "", new String[0])};
			String member;
			Date deposited = parseDate(entry.getJSONObject("deposited"));
			double score = entry.getDouble("score");
			String subtitle = entry.getJSONArray("subtitle").length() > 0 ? entry.getJSONArray("subtitle").getString(0) : "";
			Date issued = parseDate(entry.getJSONObject("issued"));
			String alternative_id = entry.has("alternative-id") ? entry.getJSONArray("alternative-id").getString(0) : "";
			String URL = entry.getString("URL");
			String[] ISSN = entry.has("ISSN") ? parseStringArray(entry.getJSONArray("ISSN")) : new String[0];
			
			if(title.equals("")) { // measure against empty titles
				title = container_title + ", page(s) " + page;
			}
			
			Paper paper = new Paper(title, subtitle, DOI, authors, created, publisher, volume, page, "");
			return paper;
		} catch(JSONException e) {
			e.printStackTrace();
			System.err.println("Entry in question: " + entry.toString(4));
			throw new RuntimeException("Failed to parse paper", e);
		}
		
	}

	private static Author[] parseAuthors(JSONArray authorList) {
		Author[] authors = new Author[authorList.length()];
		for(int authorID = 0; authorID < authorList.length(); authorID++) {
			JSONObject entry = authorList.getJSONObject(authorID);
			String firstName = entry.has("given") ? entry.getString("given") : "";
			String lastName = entry.has("family") ? entry.getString("family") : "";
			String[] affiliations = entry.has("affiliation") && entry.getJSONArray("affiliation").length() > 1 ? parseAffiliationArray(entry.getJSONArray("affiliation")) : new String[0];
			
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

	private static Date parseDate(JSONObject dateObject) {
		JSONArray dateParts = dateObject.getJSONArray("date-parts").getJSONArray(0);
		if(dateParts.get(0).toString().equals("null")) {
			return new Date(0, 0, 0);
		}
		int year = dateParts.length() > 0 ? dateParts.getInt(0) : 0;
		int month = dateParts.length() > 1 ? dateParts.getInt(1) : 0;
		int day = dateParts.length() > 2 ? dateParts.getInt(2) : 0;
		return new Date(year, month, day);
	}

}
