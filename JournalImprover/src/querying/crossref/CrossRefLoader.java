package querying.crossref;

import java.io.IOException;

import org.json.JSONArray;
import org.json.JSONObject;

import lib.util.HTTPRequester;
import data.Paper;

public class CrossRefLoader {
	private static final String baseURL = "http://api.crossref.org/";

	public static Paper[] query(String query) throws IOException {
		String result = HTTPRequester.request(baseURL + "works");
		System.out.println("Downloaded message:\n\n" + result + "\n\n");
		JSONObject response = new JSONObject(result);
		print(response, 0);
		return null;
	}

	private static void print(Object item, int depth) {
		if(item instanceof JSONObject) {
			JSONObject object = (JSONObject) item;
			for(String key : object.keySet()) {
				for(int i = 0; i < depth; i++) {
					System.out.print('\t');
				}
				System.out.print(key + "\n");
				print(object.get(key), depth + 1);
			}
		} else if(item instanceof String) {
			for(int i = 0; i < depth; i++) {
				System.out.print('\t');
			}
			System.out.print(item + "\n");
		} else if(item instanceof JSONObject) {
			print((JSONObject)item, depth + 1);
		} else if(item instanceof JSONArray) {
			JSONArray array = (JSONArray) item;
			for(Object element : array) {
				print(element, depth + 1);
			}
		} else {
			for(int i = 0; i < depth; i++) {
				System.out.print('\t');
			}
			System.out.print("[unknown type] " + item + "\n");
		}

	}

}
