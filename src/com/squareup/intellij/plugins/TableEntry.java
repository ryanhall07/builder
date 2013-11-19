package com.squareup.intellij.plugins;

import com.intellij.psi.PsiField;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * An entry in a {@link FieldTableModel}.
 */
public class TableEntry {

  public static final int FIELD_INDEX = 0;
  public static final int NULLABLE_INDEX = 1;

  private PsiField field;
  private Boolean nullable;

  public TableEntry(PsiField field) {
    this.field = checkNotNull(field);
    nullable = Boolean.FALSE;
  }

  public PsiField getField() {
    return field;
  }

  public Boolean isNullable() {
    return nullable;
  }

  public void setNullable(Boolean nullable) {
    this.nullable = nullable;
  }
}
