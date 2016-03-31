package interactivity;

import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;

public class PaperTrackerTableModel extends DefaultTableModel implements TableModel {

	@Override
	public boolean isCellEditable(int arg0, int arg1) {
		return false;
	}
}
