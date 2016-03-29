package interactivity;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;

import javax.swing.DefaultListModel;
import javax.swing.JOptionPane;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;

import data.Idea;
import data.Paper;
import gui.PaperTrackerWindow;
import lib.events.Event;
import lib.events.EventDispatcher;
import lib.events.EventHandler;
import lib.events.EventType;

public class IdeaTracker implements EventHandler {

	private final PaperTrackerWindow window;
	private final EventDispatcher eventDispatcher;
	
	private final DefaultListModel<String> ideaListModel;
	private final ArrayList<Idea> ideaList = new ArrayList<Idea>();
	
	private final DefaultTableModel relevantTableModel;
	
	private Idea currentSelectedIdea = null;
	private int selectedIdeaIndex = -1;

	public IdeaTracker(PaperTrackerWindow window, EventDispatcher mainDispatcher) {
		this.window = window;
		this.eventDispatcher = mainDispatcher;
		
		this.ideaListModel = new DefaultListModel<String>();
		window.ideaList.setModel(this.ideaListModel);
		window.ideaList.getSelectionModel().setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		window.ideaList.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
			public void valueChanged(ListSelectionEvent arg0) {
				int selectedIndex = window.ideaList.getSelectionModel().getLeadSelectionIndex();
				if(selectedIndex < 0 || selectedIndex >= ideaList.size()) {
					window.addRelevantPaperButton.setEnabled(false);
					window.deleteIdeaButton.setEnabled(false);
					selectedIdeaIndex = -1;
					currentSelectedIdea = null;
					return;
				}
				selectedIdeaIndex = selectedIndex;
				window.addRelevantPaperButton.setEnabled(true);
				window.deleteIdeaButton.setEnabled(true);
				currentSelectedIdea = ideaList.get(selectedIndex);				
			}
		});
		
		window.addIdeaButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				addNewIdea();
			}
		});
		
		window.deleteIdeaButton.setEnabled(false);
		window.deleteIdeaButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				int selectedIndex = window.ideaList.getSelectionModel().getLeadSelectionIndex();
				if(selectedIndex < 0 || selectedIndex >= ideaList.size()) {
					return;
				}
				int answer = JOptionPane.showConfirmDialog(window, "Delete this idea?", "Delete", JOptionPane.YES_NO_OPTION);
				if(answer == JOptionPane.OK_OPTION) {
					ideaList.remove(selectedIndex);
					currentSelectedIdea = null;
					refreshIdeas();				
				}				
			}
		});
		
		window.ideaNameField.addKeyListener(new KeyListener() {
			public void keyPressed(KeyEvent arg0) {}
			public void keyTyped(KeyEvent arg0) {}

			public void keyReleased(KeyEvent event) {
				if(event.getKeyChar() == '\n') {
					addNewIdea();
				}
			}
		});
		
		this.relevantTableModel = new DefaultTableModel(new String[]{"Date", "Title"}, 0);
		
		window.relevantPaperTable.setModel(relevantTableModel);
		window.relevantPaperTable.getSelectionModel().setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		window.relevantPaperTable.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
			@Override
			public void valueChanged(ListSelectionEvent e) {
				int selectedIndex = window.relevantPaperTable.getSelectionModel().getLeadSelectionIndex();
				if(selectedIndex < 0 || selectedIndex >= ideaList.size()) {
					return;
				}
				Paper selectedRelevantPaper = currentSelectedIdea.relevantPapers.get(selectedIndex);
				eventDispatcher.dispatchEvent(new Event<Paper>(EventType.PAPER_SELECTED, selectedRelevantPaper));
			}
		});
		
		eventDispatcher.addEventListener(this, EventType.ADD_RELEVANT_PAPER);
		window.addRelevantPaperButton.setEnabled(false);
		
		refreshIdeas();
	}

	private void addNewIdea() {
		if(window.ideaNameField.getText().equals("")) {
			return;
		}
		this.ideaList.add(new Idea(window.ideaNameField.getText()));
		window.ideaNameField.setText("");
		refreshIdeas();
	}
	
	private void refreshIdeas() {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				ideaListModel.clear();
				for(Idea idea : ideaList) {
					ideaListModel.addElement(idea.name);
				}
				relevantTableModel.setRowCount(0);
				if(currentSelectedIdea != null) {
					for(Paper paper : currentSelectedIdea.relevantPapers) {
						relevantTableModel.addRow(new String[]{paper.publicationDate, paper.title});
					}
					window.ideaList.getSelectionModel().setLeadSelectionIndex(selectedIdeaIndex);
				}
			}
		});
	}

	@Override
	public void handleEvent(Event<?> event) {
		if(event.eventType == EventType.ADD_RELEVANT_PAPER) {
			if(currentSelectedIdea != null) {
				Paper paper = (Paper) event.getEventParameterObject();
				currentSelectedIdea.relevantPapers.add(paper);
				refreshIdeas();
			}
		}
	}
}
