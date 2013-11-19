import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.intellij.psi.PsiField;
import com.intellij.util.ui.EditableModel;
import java.util.List;
import javax.swing.table.AbstractTableModel;

/**
* Created with IntelliJ IDEA. User: rhall Date: 11/18/13 Time: 8:16 PM To change this template use
* File | Settings | File Templates.
*/
public class FieldTableModel extends AbstractTableModel implements EditableModel {

  private final List<String> columnNames;
  private final List<TableEntry> entries;

  public FieldTableModel(PsiField... fields) {
    columnNames = ImmutableList.of("Field", "Nullable");
    entries = Lists.newArrayList();
    for (PsiField field : fields) {
      entries.add(new TableEntry(field));
    }
  }

  @Override public int getRowCount() {
    return entries.size();
  }

  @Override public int getColumnCount() {
    return columnNames.size();
  }

  @Override public Object getValueAt(int row, int col) {
    TableEntry entry = entries.get(row);
    return col == 0 ? entry.getField().getName() : entry.isNullable();
  }

  @Override public Class<?> getColumnClass(int i) {
    return getValueAt(0, i).getClass();
  }

  @Override public String getColumnName(int i) {
    return columnNames.get(i);
  }

  @Override public boolean isCellEditable(int row, int col) {
    return col == 1;
  }

  @Override public void setValueAt(Object o, int row, int col) {
    TableEntry entry = entries.get(row);
    entry.setNullable((Boolean) o);
  }

  @Override public void addRow() {
  }

  @Override public void removeRow(int index) {
    entries.remove(index);
  }

  @Override public void exchangeRows(int oldIndex, int newIndex) {
    TableEntry oldEntry = entries.get(oldIndex);
    TableEntry newEntry = entries.get(newIndex);
    entries.remove(oldIndex);
    entries.add(oldIndex, newEntry);
    entries.remove(newIndex);
    entries.add(newIndex, oldEntry);
  }

  @Override public boolean canExchangeRows(int oldIndex, int newIndex) {
    return true;
  }

  public List<TableEntry> getEntries() {
    return ImmutableList.copyOf(entries);
  }
}
