package lib.util;

public class StringUtil {

	public static String createCommaSeparatedList(String[] array) {
		if(array.length == 0) {
			return "";
		}
		String list = array[0];
		for(int i = 1; i < array.length; i++) {
			list += ", " + array[i];
		}
		return list;
	}

}
