package cache;

import data.Comment;
import data.Rating;
import nu.xom.Attribute;
import nu.xom.Element;

public class CommentConverter {

	public static Element convertCommentToXML(String paperTitle, Comment comment) {
		Element commentElement = new Element("comment");
		
		Attribute titleAttribute = new Attribute("paperTitle", paperTitle);
		Attribute ratingAttribute = new Attribute("rating", ""+comment.rating.index);
		Attribute isReadAttribute = new Attribute("isRead", ""+comment.isRead);
		Attribute isSeenAttribute = new Attribute("isSeen", ""+comment.isSeen);
		
		Element commentTextElement = new Element("commentText");
		commentTextElement.appendChild(comment.comments);
		
		commentElement.addAttribute(titleAttribute);
		commentElement.addAttribute(ratingAttribute);
		commentElement.addAttribute(isReadAttribute);
		commentElement.addAttribute(isSeenAttribute);
		commentElement.appendChild(commentTextElement);
		
		return commentElement;
	}

	public static Comment convertXMLToComment(Element element) {
		String ratingString = element.getAttributeValue("rating");
		String isReadString = element.getAttributeValue("isRead");
		String isSeenString = element.getAttributeValue("isSeen");
		
		Rating rating = Rating.fromIndex(Integer.parseInt(ratingString));
		boolean isRead = Boolean.parseBoolean(isReadString);
		boolean isSeen = Boolean.parseBoolean(isSeenString);
		String commentString = element.getFirstChildElement("commentText").getValue();
		
		Comment comment = new Comment();
		
		comment.comments = commentString;
		comment.isRead = isRead;
		comment.isSeen = isSeen;
		comment.rating = rating;
		
		return comment;
	}

	public static String convertXMLToPaperTitle(Element element) {
		return element.getAttributeValue("paperTitle");
	}

}
