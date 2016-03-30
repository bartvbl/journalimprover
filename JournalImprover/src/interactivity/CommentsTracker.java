package interactivity;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.HashMap;

import javax.swing.DefaultComboBoxModel;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;

import data.Comment;
import data.Paper;
import data.Rating;
import gui.PaperTrackerWindow;
import lib.events.Event;
import lib.events.EventDispatcher;
import lib.events.EventHandler;
import lib.events.EventType;

public class CommentsTracker implements EventHandler {

	private final PaperBase paperBase;
	private final PaperTrackerWindow window;
	private final EventDispatcher eventDispatcher;
	
	private final HashMap<String, Comment> commentMap = new HashMap<String, Comment>();
	private final DefaultComboBoxModel<String> ratingBoxModel;
	
	private Paper currentSelectedPaper = null;
	private boolean isSwitchingPaper = false;

	public CommentsTracker(PaperBase paperBase, PaperTrackerWindow window, EventDispatcher mainDispatcher) {
		this.paperBase = paperBase;
		this.window = window;
		this.eventDispatcher = mainDispatcher;
		
		eventDispatcher.addEventListener(this, EventType.PAPER_SELECTED);
		
		this.ratingBoxModel = new DefaultComboBoxModel<String>();
		
		for(Rating rating : Rating.values()) {
			ratingBoxModel.addElement(rating.displayName);
		}
		
		window.ratingComboBox.setModel(ratingBoxModel);
		
		window.paperCommentsField.addCaretListener(new CaretListener() {
			@Override
			public void caretUpdate(CaretEvent arg0) {
				updateCurrentComment();
			}
		});
		
		window.ratingComboBox.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent arg0) {
				updateCurrentComment();
			}
		});
		
		window.paperReadCheckbox.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				updateCurrentComment();
			}
		});
	}
	
	protected void updateCurrentComment() {
		if(isSwitchingPaper) {
			return;
		}
		Comment comment = commentMap.get(this.currentSelectedPaper.title);
		if(comment != null) {
			comment.comments = window.paperCommentsField.getText();
			comment.rating = Rating.fromIndex(window.ratingComboBox.getSelectedIndex());
			comment.isRead = window.paperReadCheckbox.isSelected();
		}		
	}

	@Override
	public void handleEvent(Event<?> event) {
		if(event.eventType == EventType.PAPER_SELECTED) {
			this.isSwitchingPaper = true;
			Paper selectedPaper = (Paper) event.getEventParameterObject();
			this.currentSelectedPaper = selectedPaper;
			
			Comment comment = commentMap.get(selectedPaper.title);
			if(comment != null) {
				window.paperCommentsField.setText(comment.comments);
				window.ratingComboBox.setSelectedIndex(comment.rating.index);
				window.paperReadCheckbox.setSelected(comment.isRead);
			} else {
				window.paperCommentsField.setText("");
				window.ratingComboBox.setSelectedIndex(0);
				window.paperReadCheckbox.setSelected(false);
				
				Comment newComment = new Comment();
				commentMap.put(selectedPaper.title, newComment);
			}
			this.isSwitchingPaper  = false;
		}
	}

}