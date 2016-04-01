package lib.util;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;

public class Config {
	private static final File configLocation = new File("cgf/apikeys.cfg");
	private static final Config config = Config.load(configLocation);
	
	private final HashMap<String, String> configMap;

	private Config(HashMap<String, String> configMap) {
		this.configMap = configMap;
	}
	
	public static String get(String key) {
		return config.configMap.get(key);
	}
	
	private static Config load(File src) {
		try {
			String fileContents = IOUtils.readFileContents(src);
			String[] entries = fileContents.split("\n");
			HashMap<String, String> configMap = new HashMap<String, String>();
			
			for(String entry : entries) {
				String[] parts = entry.split(": ");
				configMap.put(parts[0], parts[1]);
			}
			return new Config(configMap);
		} catch (IOException e) {
			e.printStackTrace();
			throw new RuntimeException("Failed to load config!");
		}
	}

	
}
