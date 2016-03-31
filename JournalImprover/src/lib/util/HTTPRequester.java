package lib.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;

public class HTTPRequester {
	public static String request(String address) throws IOException {
		System.setProperty("http.keepAlive", "false");
		URL url = new URL(address);
		URLConnection connection = url.openConnection();
		connection.setRequestProperty("Connection", "close");
		BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
		StringBuffer buffer = new StringBuffer();
		String line;
		while((line = reader.readLine()) != null) {
			buffer.append(line);
		}
		reader.close();
		return buffer.toString();
	}
}
