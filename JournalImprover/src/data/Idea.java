package data;

import java.util.ArrayList;

public class Idea {

	public final String name;
	public final ArrayList<Paper> relevantPapers = new ArrayList<Paper>();

	public Idea(String name) {
		this.name = name;
	}
	
	public boolean equals(Object other) {
		if(!(other instanceof Idea)) {
			return false;
		}
		Idea otherIdea = (Idea) other;
		return name.equals(otherIdea.name);
	}
	
	public int hashCode() {
		return name.hashCode();
	}

}
