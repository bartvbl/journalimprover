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
import lib.events.EventDispatcher;

public class IdeaTracker implements ActionListener, KeyListener, ListSelectionListener {

	private final PaperTrackerWindow window;
	private final EventDispatcher eventDispatcher;
	
	private final DefaultListModel<String> ideaListModel;
	private final ArrayList<Idea> ideaList = new ArrayList<Idea>();
	
	private final DefaultTableModel relevantTableModel;
	
	private Idea currentSelectedIdea = null;

	public IdeaTracker(PaperTrackerWindow window, EventDispatcher mainDispatcher) {
		this.window = window;
		this.eventDispatcher = mainDispatcher;
		
		this.ideaListModel = new DefaultListModel<String>();
		window.ideaList.setModel(this.ideaListModel);
		window.ideaList.getSelectionModel().addListSelectionListener(this);
		window.ideaList.getSelectionModel().setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		
		window.addIdeaButton.addActionListener(this);
		window.deleteIdeaButton.addActionListener(this);
		window.ideaNameField.addKeyListener(this);
		
		this.relevantTableModel = new DefaultTableModel(new String[]{"Date", "Title"}, 0);
		window.relevantPaperTable.setModel(relevantTableModel);
		
		refreshIdeas();
	}

	@Override
	public void actionPerformed(ActionEvent event) {
		if(event == null || event.getSource() == window.addIdeaButton){			
			if(window.ideaNameField.getText().equals("")) {
				return;
			}
			this.ideaList.add(new Idea(window.ideaNameField.getText()));
			window.ideaNameField.setText("");
			refreshIdeas();
		} else if(event.getSource() == window.deleteIdeaButton) {
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
				}				
			}
		});
	}

	@Override
	public void keyPressed(KeyEvent arg0) {}
	@Override
	public void keyTyped(KeyEvent arg0) {}

	@Override
	public void keyReleased(KeyEvent event) {
		if(event.getKeyChar() == '\n') {
			actionPerformed(null);
		}
	}

	@Override
	public void valueChanged(ListSelectionEvent arg0) {
		int selectedIndex = window.ideaList.getSelectionModel().getLeadSelectionIndex();
		if(selectedIndex < 0 || selectedIndex >= ideaList.size()) {
			return;
		}
		currentSelectedIdea = ideaList.get(selectedIndex);
	}
}
