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
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;

import backend.Backend;
import cache.IdeaCache;
import data.Idea;
import data.Paper;
import gui.PaperTrackerWindow;
import interactivity.paperBase.PaperBase;
import lib.events.Event;
import lib.events.EventDispatcher;
import lib.events.EventHandler;
import lib.events.EventType;
import lib.util.WorkerThread;

public class IdeaTracker implements EventHandler {

	private final PaperTrackerWindow window;
	private final EventDispatcher eventDispatcher;
	private final Backend backend;
	
	private final DefaultListModel<String> ideaListModel;
	private final DefaultTableModel relevantTableModel;
	
	public IdeaTracker(Backend backend, PaperTrackerWindow window, EventDispatcher mainDispatcher) {
		this.window = window;
		this.eventDispatcher = mainDispatcher;
		this.backend = backend;
		
		this.ideaListModel = new DefaultListModel<String>();
		window.ideaList.setModel(this.ideaListModel);
		window.ideaList.getSelectionModel().setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		window.ideaList.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
			public void valueChanged(ListSelectionEvent event) {
				int selectedIndex = window.ideaList.getSelectionModel().getLeadSelectionIndex();
				if(selectedIndex < 0 || selectedIndex >= backend.ideas.getIdeaCount()) {
					return;
				}
				updateIdeaButtons();
				refreshRelevantPaperList();
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
				if(selectedIndex < 0 || selectedIndex >= backend.ideas.getIdeaCount()) {
					return;
				}
				int answer = JOptionPane.showConfirmDialog(window, "Delete this idea?", "Delete", JOptionPane.YES_NO_OPTION);
				if(answer == JOptionPane.OK_OPTION) {
					backend.ideas.removeIdeaByIndex(selectedIndex);
					updateIdeaButtons();
					refreshIdeaList();
					refreshRelevantPaperList();
					backend.ideas.writeCache();
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
		
		this.relevantTableModel = new PaperTrackerTableModel();
		relevantTableModel.setColumnIdentifiers(new String[]{"Date", "Title"});
		window.relevantPaperTable.setDragEnabled(false);
		window.relevantPaperTable.setRowSorter(new TableRowSorter(relevantTableModel));
		
		window.relevantPaperTable.setModel(relevantTableModel);
		window.relevantPaperTable.getSelectionModel().setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		window.relevantPaperTable.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
			@Override
			public void valueChanged(ListSelectionEvent e) {
				int selectedIndex = window.relevantPaperTable.getSelectionModel().getLeadSelectionIndex();
				Idea selectedIdea = getSelectedIdea();
				
				if(selectedIdea == null) {
					window.removeRelevantPaperButton.setEnabled(false);
					return;
				}
				if(selectedIndex < 0 || selectedIndex >= selectedIdea.relevantPapers.size()) {
					window.removeRelevantPaperButton.setEnabled(false);
					return;
				}
				int modelIndex = window.relevantPaperTable.convertRowIndexToModel(selectedIndex);
				window.removeRelevantPaperButton.setEnabled(true);
				Paper selectedRelevantPaper = selectedIdea.relevantPapers.get(modelIndex);
				eventDispatcher.dispatchEvent(new Event<Paper>(EventType.PAPER_SELECTED, selectedRelevantPaper));
			}
		});
		
		window.notesIdeaBox.setWrapStyleWord(true);
		window.notesIdeaBox.addCaretListener(new CaretListener() {

			@Override
			public void caretUpdate(CaretEvent arg0) {
				Idea selectedIdea = getSelectedIdea();
				if(selectedIdea == null) {
					return;
				}
				selectedIdea.notes = window.notesIdeaBox.getText();
				backend.ideas.writeCache();
			}
			
		});
		
		window.removeRelevantPaperButton.setEnabled(false);
		window.removeRelevantPaperButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				int selectedIndex = window.relevantPaperTable.getSelectionModel().getLeadSelectionIndex();
				Idea selectedIdea = getSelectedIdea();
				
				if(selectedIdea == null) {
					return;
				}
				if(selectedIndex < 0 || selectedIndex >= selectedIdea.relevantPapers.size()) {
					return;
				}
				int modelIndex = window.relevantPaperTable.convertRowIndexToModel(selectedIndex);
				selectedIdea.relevantPapers.remove(modelIndex);
				refreshRelevantPaperList();
				backend.ideas.writeCache();
			}
		});
		
		eventDispatcher.addEventListener(this, EventType.ADD_RELEVANT_PAPER);
		window.addRelevantPaperButton.setEnabled(false);
		
		refreshIdeaList();
		refreshRelevantPaperList();
	}

	private void addNewIdea() {
		if(window.ideaNameField.getText().equals("")) {
			return;
		}
		backend.ideas.addIdea(new Idea(window.ideaNameField.getText()));
		window.ideaNameField.setText("");
		backend.ideas.writeCache();
		refreshIdeaList();
	}
	
	private void updateIdeaButtons() {
		Idea currentIdea = getSelectedIdea();
		boolean isIdeaSelected = currentIdea != null;
		
		window.addRelevantPaperButton.setEnabled(isIdeaSelected);
		window.deleteIdeaButton.setEnabled(isIdeaSelected);
	}
	
	private void refreshIdeaList() {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				int currentIdeaIndex = getSelectedIdeaIndex();
				
				ideaListModel.clear();
				for(Idea idea : backend.ideas.all()) {
					ideaListModel.addElement(idea.name);
				}
				
				window.ideaList.getSelectionModel().setLeadSelectionIndex(currentIdeaIndex);
			}
		});
	}
	
	private void refreshRelevantPaperList() {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				Idea currentIdea = getSelectedIdea();
				
				relevantTableModel.setRowCount(0);
				if(currentIdea != null) {
					window.notesIdeaBox.setText(currentIdea.notes);
					for(Paper paper : currentIdea.relevantPapers) {
						relevantTableModel.addRow(new String[]{paper.publicationDate.toPrettyString(), paper.title});
					}
				}
			}
		});
	}

	@Override
	public void handleEvent(Event<?> event) {
		if(event.eventType == EventType.ADD_RELEVANT_PAPER) {
			Paper paper = (Paper) event.getEventParameterObject();
			Idea selectedIdea = getSelectedIdea();
			if(!selectedIdea.relevantPapers.contains(paper)) {
				selectedIdea.relevantPapers.add(paper);
				backend.ideas.writeCache();
				refreshRelevantPaperList();
			}
		}
	}
	
	private int getSelectedIdeaIndex() {
		return window.ideaList.getSelectionModel().getLeadSelectionIndex();
	}
	
	private Idea getSelectedIdea() {
		int selectedIndex = getSelectedIdeaIndex();
		if(selectedIndex < 0 || selectedIndex >= backend.ideas.getIdeaCount()) {
			return null;
		}
		return backend.ideas.getIdeaByIndex(selectedIndex);
	}

	
}
