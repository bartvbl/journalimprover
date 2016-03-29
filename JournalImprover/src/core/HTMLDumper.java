package core;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.HashMap;

import lib.util.IOUtils;
import nu.xom.Attribute;
import nu.xom.Builder;
import nu.xom.Document;
import nu.xom.Element;
import nu.xom.Elements;
import nu.xom.ParsingException;
import nu.xom.ValidityException;

public class HTMLDumper {
	public static void main(String[] args) {
		Builder builder = new Builder();
		File[] inputFiles = new File("input/").listFiles();
		
		for(File inputFile : inputFiles) {
			try {
				System.out.println("Processing " + inputFile);
				convert(inputFile, builder);
			} catch (ParsingException | IOException e) {
				System.out.println("Building of " + inputFile + " failed.");
				e.printStackTrace();
			}
		}
	}

	private static void convert(File inputFile, Builder builder) throws ValidityException, ParsingException, IOException {
		String fileContents = IOUtils.readFileContents(inputFile);
		fileContents = preprocess(fileContents);
		InputStream stream = new ByteArrayInputStream(fileContents.getBytes(StandardCharsets.UTF_8));
		Document document = builder.build(stream);
		Element newRootElement = restructure(document.getRootElement());
		FileWriter writer = new FileWriter(new File("output/" + inputFile.getName()));
		writer.write(newRootElement.toXML());
		writer.close();
	}

	private static Element restructure(Element rootElement) {
		Element newRootElement = createDocumentRoot();
		Element bodyElement = newRootElement.getFirstChildElement("body");
		
		Elements entries = rootElement.getChildElements();
		for(int i = 0; i < entries.size(); i++) {
			Element entry = entries.get(i);
			if(entry.getLocalName().equals("table")) {
				processEntry(bodyElement, entry);
			}
		}
		return newRootElement;
	}

	private static Element createDocumentRoot() {
		Element newRootElement = new Element("html");
		
		Element headElement = new Element("head");
		newRootElement.appendChild(headElement);
		
		Element metaElement = new Element("meta");
		metaElement.addAttribute(new Attribute("name", "viewport"));
		metaElement.addAttribute(new Attribute("content", "width=device-width, initial-scale=1.0"));
		headElement.appendChild(metaElement);
		
		Element styleElement = new Element("style");
		styleElement.appendChild("");

		Element bodyElement = new Element("body");
		newRootElement.appendChild(bodyElement);
		
		
		
		return newRootElement;
	}

	private static void processEntry(Element newRootElement, Element entry) {
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
				dataElementContents += ", " + dataElement.getValue();
			}
			
			String key = keyElement.getValue().trim();
			entryMap.put(key, dataElementContents);
		}

		Element entryElement = new Element("div");
		
		Element titleElement = new Element("h3");
		titleElement.appendChild(entryMap.get("TI"));
		entryElement.appendChild(titleElement);
		
		Element metaDataElement = new Element("h5");
		metaDataElement.appendChild(entryMap.get("PD") + ", by " + entryMap.get("AU"));
		entryElement.appendChild(metaDataElement);
		
		Element abstractElement = new Element("p");
		abstractElement.appendChild(entryMap.get("AB"));
		entryElement.appendChild(abstractElement);
		
		entryElement.appendChild(new Element("hr"));
		
		newRootElement.appendChild(entryElement);
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

	
}
