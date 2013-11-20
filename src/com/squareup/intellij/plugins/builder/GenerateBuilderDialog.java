package com.squareup.intellij.plugins.builder;

import com.google.common.collect.Lists;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.openapi.ui.LabeledComponent;
import com.intellij.psi.PsiAnnotation;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiField;
import com.intellij.psi.PsiModifier;
import com.intellij.psi.PsiNameValuePair;
import com.intellij.ui.ToolbarDecorator;
import com.intellij.ui.components.JBCheckBox;
import com.intellij.ui.table.JBTable;
import java.awt.BorderLayout;
import java.util.List;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JTable;

/**
 * A {@link DialogWrapper} for generating a Builder class.
 */
public class GenerateBuilderDialog extends DialogWrapper {

  private final JBCheckBox gettersCheckBox;
  private final FieldTableModel fieldTableModel;
  private final LabeledComponent<JPanel> component;

  public GenerateBuilderDialog(PsiClass psiClass) {
    super(psiClass.getProject());
    setTitle("Select Fields for Builder");

    fieldTableModel = new FieldTableModel(getTableEntries(getMemberFields(psiClass)));
    JTable table = new JBTable(fieldTableModel);
    ToolbarDecorator decorator = ToolbarDecorator.createDecorator(table);
    decorator.disableAddAction();
    JPanel tableWithToolbar = decorator.createPanel();

    gettersCheckBox = new JBCheckBox("Create getters (nullables will return an Optional)");
    gettersCheckBox.setSelected(true);

    JPanel panel = new JPanel(new BorderLayout());
    panel.add(tableWithToolbar);
    panel.add(gettersCheckBox, BorderLayout.PAGE_END);
    component = LabeledComponent.create(panel, "Fields to include in Builder:");
    init();
  }

  @Override protected JComponent createCenterPanel() {
    return component;
  }

  public List<TableEntry> getEntries() {
    return fieldTableModel.getEntries();
  }

  public boolean shouldCreateGetters() {
    return gettersCheckBox.isSelected();
  }

  private List<TableEntry> getTableEntries(List<PsiField> fields) {
    List<TableEntry> tableEntries = Lists.newArrayList();
    for (PsiField field : fields) {
      TableEntry tableEntry = new TableEntry(field);
      tableEntries.add(tableEntry);

      // Attempt to infer nullable from javax.persistence annotations.
      for (PsiAnnotation annotation : field.getModifierList().getAnnotations()) {
        if (annotation.getQualifiedName().equals("javax.persistence.Column")) {
          tableEntry.setNullable(Boolean.TRUE);
          for (PsiNameValuePair pair : annotation.getParameterList().getAttributes()) {
            if (pair.getName().equals("nullable") &&
                !Boolean.valueOf(pair.getValue().getText())) {
              tableEntry.setNullable(Boolean.FALSE);
            }
          }
        } else if (annotation.getQualifiedName().equals("javax.persistence.ManyToOne")) {
          tableEntry.setNullable(Boolean.TRUE);
          for (PsiNameValuePair pair : annotation.getParameterList().getAttributes()) {
            if (pair.getName().equals("optional") &&
                !Boolean.valueOf(pair.getValue().getText())) {
              tableEntry.setNullable(Boolean.FALSE);
            }
          }
        }
      }
    }
    return tableEntries;
  }

  private List<PsiField> getMemberFields(PsiClass psiClass) {
    List<PsiField> memberFields = Lists.newArrayList();
    for (PsiField field : psiClass.getFields()) {
      if (field.hasModifierProperty(PsiModifier.STATIC)) {
        continue;
      }
      memberFields.add(field);
    }
    return memberFields;
  }
}
