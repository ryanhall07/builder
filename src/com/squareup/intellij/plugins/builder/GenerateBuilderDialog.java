package com.squareup.intellij.plugins.builder;

import com.google.common.collect.Lists;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.openapi.ui.LabeledComponent;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiField;
import com.intellij.psi.PsiModifier;
import com.intellij.ui.ToolbarDecorator;
import java.util.List;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JTable;

/**
 * A {@link DialogWrapper} for generating a Builder class.
 */
public class GenerateBuilderDialog extends DialogWrapper {

  private final FieldTableModel fieldTableModel;
  private final LabeledComponent<JPanel> component;

  public GenerateBuilderDialog(PsiClass psiClass) {
    super(psiClass.getProject());
    setTitle("Select Fields for Builder");

    fieldTableModel = new FieldTableModel(getMemberFields(psiClass));
    JTable table = new JTable(fieldTableModel);
    ToolbarDecorator decorator = ToolbarDecorator.createDecorator(table);
    decorator.disableAddAction();
    JPanel panel = decorator.createPanel();
    component = LabeledComponent.create(panel, "Fields to include in Builder:");

    init();
  }

  @Override protected JComponent createCenterPanel() {
    return component;
  }

  public List<TableEntry> getEntries() {
    return fieldTableModel.getEntries();
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
