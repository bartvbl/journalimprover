package lib.util;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

import nu.xom.Element;

public class HTTPRequester {
	public static String request(String address) throws IOException {
		System.setProperty("http.keepAlive", "false");
		URL url = new URL(address);
		URLConnection connection = url.openConnection();
		connection.setRequestProperty("Connection", "close");
		return readConnectionResponse(connection);
	}

	private static String readConnectionResponse(URLConnection connection) throws IOException {
		BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
		StringBuffer buffer = new StringBuffer();
		String line;
		while((line = reader.readLine()) != null) {
			buffer.append(line);
		}
		reader.close();
		return buffer.toString();
	}

	public static String post(String address, String requestBody) throws IOException {
		URL url = new URL(address);
		HttpURLConnection connection = (HttpURLConnection) url.openConnection();
		
		connection.setRequestMethod("POST");
		connection.setDoOutput(true);
		
		connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded"); 
		connection.setRequestProperty("charset", "utf-8");
		connection.setRequestProperty("Content-Length", "" + requestBody.length());
		connection.setRequestProperty("Connection", "close");
		
		DataOutputStream postStream = new DataOutputStream(connection.getOutputStream());
		postStream.writeUTF(requestBody);
		
		return readConnectionResponse(connection);
	}
}
