package core;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

import nu.xom.Builder;
import nu.xom.Document;
import nu.xom.ParsingException;
import nu.xom.ValidityException;

public class Main {
	public static void main(String[] args) {
		Builder builder = new Builder();
		File[] inputFiles = new File("input/").listFiles();
		
		for(File inputFile : inputFiles) {
			try {
				convert(inputFile, builder);
			} catch (ParsingException | IOException e) {
				System.out.println("Building of " + inputFile + " failed.");
				e.printStackTrace();
			}
		}
	}

	private static void convert(File inputFile, Builder builder) throws ValidityException, ParsingException, IOException {
		String fileContents = readFileContents(inputFile);
		fileContents = preprocess(fileContents);
		InputStream stream = new ByteArrayInputStream(fileContents.getBytes(StandardCharsets.UTF_8));
		Document document = builder.build(stream);
		System.out.println(document.getChildCount());
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
