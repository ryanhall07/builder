import com.intellij.psi.PsiField;

/**
* Created with IntelliJ IDEA. User: rhall Date: 11/18/13 Time: 8:17 PM To change this template use
* File | Settings | File Templates.
*/
public class TableEntry {
  private PsiField field;
  private Boolean nullable;

  TableEntry(PsiField field) {
    this.field = field;
    nullable = false;
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
