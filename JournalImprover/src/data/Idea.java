package data;

import java.util.ArrayList;

public class Idea {

	public final String name;
	public final ArrayList<Paper> relevantPapers = new ArrayList<Paper>();

	public Idea(String name) {
		this.name = name;
	}

}
