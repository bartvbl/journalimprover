package interactivity;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;

import javax.swing.JOptionPane;

import data.Paper;
import gui.PaperTrackerWindow;
import nu.xom.Attribute;
import nu.xom.Builder;
import nu.xom.Document;
import nu.xom.Element;
import nu.xom.Elements;
import nu.xom.ParsingException;
import nu.xom.ValidityException;

public class PaperLoader {

	public static Paper[] loadPapers(File inputFile, PaperTrackerWindow window) {
		String fileContents;
		try {
			Builder builder = new Builder();
			fileContents = readFileContents(inputFile);
			fileContents = preprocess(fileContents);
			InputStream stream = new ByteArrayInputStream(fileContents.getBytes(StandardCharsets.UTF_8));
			Document document = builder.build(stream);
			Paper[] papers = restructure(document.getRootElement());
			return papers;
		} catch (IOException | ParsingException e) {
			e.printStackTrace();
			JOptionPane.showMessageDialog(window, "Something went wrong while loading the paper file:\n" + e.getMessage());
			throw new RuntimeException("Paper loading failed!", e);
		}
	}

	private static Paper[] restructure(Element rootElement) {
		Elements entries = rootElement.getChildElements();
		ArrayList<Paper> paperList = new ArrayList<Paper>();
		for(int i = 0; i < entries.size(); i++) {
			Element entry = entries.get(i);
			if(entry.getLocalName().equals("table")) {
				Paper paper = processEntry(entry);
				paperList.add(paper);
			}
		}
		return paperList.toArray(new Paper[paperList.size()]);
	}

	private static Paper processEntry(Element entry) {
		HashMap<String, String> entryMap = new HashMap<String, String>();
		
		// single paper entry
		Elements tableRows = entry.getChildElements();
		for(int tableRowID = 0; tableRowID < tableRows.size(); tableRowID++) {
			Element tableRow = tableRows.get(tableRowID);
			Elements tableDatas = tableRow.getChildElements();
			
			Element keyElement = tableDatas.get(0);
			String dataElementContents = tableDatas.get(1).getValue();
			
			for(int i = 2; i < tableDatas.size(); i++) {
				Element dataElement = tableDatas.get(i);
				dataElementContents += "\n" + dataElement.getValue();
			}
			
			String key = keyElement.getValue().trim();
			entryMap.put(key, dataElementContents);
		}

		String title = entryMap.get("TI");
		String publicationDate = entryMap.get("PD");
		String authors = entryMap.get("AU");
		String abstractText = entryMap.get("AB");
		
		return new Paper(title, publicationDate, authors, abstractText);
	}

	private static String preprocess(String fileContents) {
		// HTML tags that are generated without a closing tag
		fileContents = fileContents.replaceAll("<br(\"[^\"]*\"|'[^']*'|[^'\">])*>", "<br></br>");
		fileContents = fileContents.replaceAll("<hr(\"[^\"]*\"|'[^']*'|[^'\">])*>", "<hr></hr>");
		
		// HTML entities
		fileContents = fileContents.replace("&plusmn;", "+-");
		fileContents = fileContents.replace("&nbsp;", "");
		fileContents = fileContents.replace("&rsquo;", "'");
		fileContents = fileContents.replace("&reg;", "(r)");
		fileContents = fileContents.replace("&ge;", "greater or equal to");
		fileContents = fileContents.replace("&middot;", ".");
		fileContents = fileContents.replace("&aacute;", "a´");
		fileContents = fileContents.replace("&atilde;", "a~");
		fileContents = fileContents.replace("&acirc;", "a^");
		fileContents = fileContents.replace("&Aacute;", "A´");
		fileContents = fileContents.replace("&Atilde;", "A~");
		fileContents = fileContents.replace("&ccedil;", "c,");
		fileContents = fileContents.replace("&Ccedil;", "C,");
		fileContents = fileContents.replace("&eacute;", "e´");
		fileContents = fileContents.replace("&ecirc;", "e^");
		fileContents = fileContents.replace("&Eacute;", "E´");
		fileContents = fileContents.replace("&oacute;", "o´");
		fileContents = fileContents.replace("&otilde;", "o~");
		fileContents = fileContents.replace("&iacute;", "i´");
		fileContents = fileContents.replace("&uacute;", "u´");
		fileContents = fileContents.replace("&ntilde;", "n~");
		fileContents = fileContents.replace("&ldquo;", "''");
		fileContents = fileContents.replace("&rdquo;", "''");
		fileContents = fileContents.replace("&lsquo;", "''");
		fileContents = fileContents.replace("&#55349;", "??");
		fileContents = fileContents.replace("&#56490;", "??");
		fileContents = fileContents.replace("&deg;", " degrees");
		return fileContents;
	}

	private static String readFileContents(File inputFile) throws IOException {
		BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(new FileInputStream(inputFile), "UTF8"));
		StringBuffer fileContents = new StringBuffer();
		while(bufferedReader.ready()) {
			fileContents.append(bufferedReader.readLine()).append("\n");
		}
		bufferedReader.close();
		return fileContents.toString();
	}

}
