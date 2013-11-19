package com.squareup.intellij.plugins.builder;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.intellij.util.ui.EditableModel;
import java.util.List;
import javax.swing.table.AbstractTableModel;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * An {@link AbstractTableModel} for the {@link GenerateBuilderDialog}.
 */
public class FieldTableModel extends AbstractTableModel implements EditableModel {

  private static final List<String> COLUMN_NAMES = ImmutableList.of("Field", "Nullable");

  private final List<TableEntry> entries;

  public FieldTableModel(List<TableEntry> tableEntries) {

    entries = Lists.newArrayList(checkNotNull(tableEntries));
  }

  @Override public int getRowCount() {
    return entries.size();
  }

  @Override public int getColumnCount() {
    return COLUMN_NAMES.size();
  }

  @Override public Object getValueAt(int row, int col) {
    TableEntry entry = entries.get(row);
    return col == TableEntry.FIELD_INDEX ? entry.getField().getName() : entry.isNullable();
  }

  @Override public Class<?> getColumnClass(int i) {
    return getValueAt(TableEntry.FIELD_INDEX, i).getClass();
  }

  @Override public String getColumnName(int i) {
    return COLUMN_NAMES.get(i);
  }

  @Override public boolean isCellEditable(int row, int col) {
    return col == TableEntry.NULLABLE_INDEX;
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
