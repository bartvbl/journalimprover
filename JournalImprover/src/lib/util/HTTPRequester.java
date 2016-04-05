package lib.util;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLConnection;

import nu.xom.Element;

public class HTTPRequester {
	public static String request(String address) throws IOException {
		try {
		System.setProperty("http.keepAlive", "false");
		URL url = new URL("http://" + address);
		URI uri = new URI(url.getProtocol(), url.getUserInfo(), url.getHost(), url.getPort(), url.getPath(), url.getQuery(), url.getRef());
		url = uri.toURL();
		HttpURLConnection connection = (HttpURLConnection) url.openConnection();
		connection.setRequestProperty("Connection", "close");
		return readConnectionResponse(connection);
		} catch (URISyntaxException e) {
			throw new IOException("URI conversion failed. ", e);
		}
	}

	private static String readConnectionResponse(HttpURLConnection connection) throws IOException {
		int responseCode = connection.getResponseCode();
		if(responseCode == 400) {
			System.out.println("HTTP Error: " + connection.getResponseMessage());
		}
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
