package interactivity;

import java.util.Arrays;

import backend.Backend;
import data.Author;
import data.Date;
import data.Paper;
import gui.PaperTrackerWindow;
import lib.events.Event;
import lib.events.EventDispatcher;
import lib.events.EventHandler;
import lib.events.EventType;
import querying.DataSource;

public class PaperDisplayer implements EventHandler {

	private final PaperTrackerWindow window;
	private final EventDispatcher eventDispatcher;
	private final Backend backend;
	private Paper currentSelectedPaper = new Paper(new DataSource[0], "", "", null, new Author[]{new Author("", "", new String[0])}, new Date(0, 0, 0), "", "", "", "");

	public PaperDisplayer(Backend backend, PaperTrackerWindow window, EventDispatcher mainDispatcher) {
		this.window = window;
		this.eventDispatcher = mainDispatcher;
		this.backend = backend;
		
		eventDispatcher.addEventListener(this, EventType.PAPER_SELECTED);
		
		window.paperAbstractField.setEditable(false);
		window.paperAbstractField.setLineWrap(true);
		window.paperAbstractField.setWrapStyleWord(true);
		
		updateWindow();
	}

	private void updateWindow() {
		window.paperAbstractField.setText(currentSelectedPaper.abstractText);
		window.paperTitleLabel.setText("<html>" + currentSelectedPaper.title.replaceAll("\n", "<br>"));
		window.paperDateLabel.setText("<html>" + currentSelectedPaper.publicationDate.toPrettyString() + " in " + Arrays.toString(currentSelectedPaper.origins.toArray(new DataSource[currentSelectedPaper.origins.size()])));
		window.paperAuthorLabel.setText("<html>" + currentSelectedPaper.createAuthorString().replaceAll("\n", "<br>"));
		
		// ensure scroll area is at the top of the abstract
		window.paperAbstractField.setCaretPosition(0);
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
