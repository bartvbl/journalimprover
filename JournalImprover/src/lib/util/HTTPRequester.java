package lib.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;

public class HTTPRequester {
	public static String request(String address) throws IOException {
		URL url = new URL(address);
		URLConnection connection = url.openConnection();
		BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
		StringBuffer buffer = new StringBuffer();
		while(reader.ready()) {
			buffer.append(reader.readLine());
		}
		reader.close();
		return buffer.toString();
	}
}
