package data;

public class Comment {
	public String comments = "";
	public Rating rating = Rating.None;
	public boolean isRead = false;
	
	public String toString() {
		return "Comment: \n" + comments + "\nRating: " + rating + "\nIs read: " + isRead;
	}
}
