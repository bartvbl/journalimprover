package lib.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;

import nu.xom.Element;

public class IOUtils {
	public static String readFileContents(File inputFile) throws IOException {
		BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(new FileInputStream(inputFile), "UTF8"));
		StringBuffer fileContents = new StringBuffer();
		while(bufferedReader.ready()) {
			fileContents.append(bufferedReader.readLine()).append("\n");
		}
		bufferedReader.close();
		return fileContents.toString();
	}
	
	public static void writeXMLDocument(Element rootElement, File destination) {
		try {
			destination.getParentFile().mkdirs();
			destination.createNewFile();
			FileWriter writer = new FileWriter(destination);
			writer.write(rootElement.toXML());
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
