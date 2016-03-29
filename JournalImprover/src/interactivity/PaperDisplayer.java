package interactivity;

import data.Paper;
import gui.PaperTrackerWindow;
import lib.events.Event;
import lib.events.EventDispatcher;
import lib.events.EventHandler;
import lib.events.EventType;

public class PaperDisplayer implements EventHandler {

	private final PaperTrackerWindow window;
	private final EventDispatcher eventDispatcher;
	private Paper currentSelectedPaper = new Paper("", "", "", "");

	public PaperDisplayer(PaperTrackerWindow window, EventDispatcher mainDispatcher) {
		this.window = window;
		this.eventDispatcher = mainDispatcher;
		
		eventDispatcher.addEventListener(this, EventType.PAPER_SELECTED);
		
		window.paperAbstractField.setEditable(false);
		
		updateWindow();
	}

	private void updateWindow() {
		window.paperAbstractField.setText(currentSelectedPaper.abstractText);
		window.paperTitleLabel.setText(currentSelectedPaper.title);
		window.paperDateLabel.setText(currentSelectedPaper.publicationDate);
		window.paperAuthorLabel.setText(currentSelectedPaper.authors);
	}

	@Override
	public void handleEvent(Event<?> event) {
		if(event.eventType == EventType.PAPER_SELECTED) {
			Paper selectedPaper = (Paper) event.getEventParameterObject();
			this.currentSelectedPaper = selectedPaper;
			updateWindow();
		}
	}

}
