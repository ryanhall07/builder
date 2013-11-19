import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.LangDataKeys;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.editor.Editor;
import com.intellij.psi.JavaPsiFacade;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementFactory;
import com.intellij.psi.PsiField;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.PsiModifier;
import com.intellij.psi.PsiParameter;
import com.intellij.psi.PsiStatement;
import com.intellij.psi.codeStyle.JavaCodeStyleManager;
import com.intellij.psi.util.PsiTreeUtil;

/**
 * Created with IntelliJ IDEA. User: rhall Date: 11/18/13 Time: 1:58 PM To change this template use
 * File | Settings | File Templates.
 */
public class GenerateBuilder extends AnAction {
  public void actionPerformed(AnActionEvent e) {
    final PsiClass psiClass = getPsiClassFromContext(e);
    final GenerateBuilderDialog dialog = new GenerateBuilderDialog(psiClass);
    dialog.show();
    if (dialog.isOK()) {
      new WriteCommandAction.Simple(psiClass.getProject(), psiClass.getContainingFile()) {
        @Override protected void run() throws Throwable {
          // TODO(rhall): need to delete previous builder and ctor
          generateConstructor(psiClass, dialog);
          generateBuilder(psiClass, dialog);
        }
      }.execute();
    }
  }

  private void generateConstructor(PsiClass psiClass, GenerateBuilderDialog dialog) {
    PsiElementFactory elementFactory = JavaPsiFacade.getElementFactory(psiClass.getProject());
    PsiClass builderClass = elementFactory.createClass("Builder");
    PsiMethod constructor = elementFactory.createConstructor();
    constructor.getModifierList().setModifierProperty(PsiModifier.PRIVATE, true);
    constructor.getModifierList().setModifierProperty(PsiModifier.PUBLIC, false);
    constructor.getParameterList().add(
        elementFactory.createParameter("builder", elementFactory.createType(builderClass)));

    for (TableEntry entry : dialog.getEntries()) {
      PsiField field = entry.getField();
      StringBuilder fieldAssign = new StringBuilder()
          .append("this.").append(field.getName()).append(" = ");
      if (entry.isNullable()) {
        fieldAssign.append("builder.").append(field.getName());
      } else {
        fieldAssign.append("com.google.common.base.Preconditions.checkNotNull(builder.")
            .append(field.getName())
            .append(")");
      }
      fieldAssign.append(";\n");

      PsiStatement assignStatement = elementFactory.createStatementFromText(fieldAssign.toString(), psiClass);
      PsiElement assignShortened = JavaCodeStyleManager.getInstance(psiClass.getProject())
          .shortenClassReferences(assignStatement);
      constructor.getBody().add(assignShortened);
    }

    psiClass.add(constructor);
    return;
  }

  private void generateBuilder(PsiClass psiClass, GenerateBuilderDialog dialog) {
    PsiElementFactory elementFactory = JavaPsiFacade.getElementFactory(psiClass.getProject());
    PsiClass builderClass = elementFactory.createClass("Builder");
    builderClass.getModifierList().add(elementFactory.createKeyword("static"));
    for (TableEntry entry : dialog.getEntries()) {
      PsiField field = entry.getField();
      PsiField builderField = elementFactory.createField(field.getName(), field.getType());
      builderClass.add(builderField);

      PsiMethod builderMethod = elementFactory.createMethod(field.getName(), elementFactory.createType(builderClass));
      PsiParameter parameter = elementFactory.createParameter(field.getName(), field.getType());
      builderMethod.getParameterList().add(parameter);

      StringBuilder assignBuilder = new StringBuilder()
          .append("this.").append(field.getName()).append(" = ").append(field.getName()).append(
              ";\n");

      PsiStatement assignStatement = elementFactory.createStatementFromText(assignBuilder.toString(), builderClass);
      PsiStatement returnStatement = elementFactory.createStatementFromText("return this;\n", builderClass);
      builderMethod.getBody().add(assignStatement);
      builderMethod.getBody().add(returnStatement);
      builderClass.add(builderMethod);

    }
    PsiMethod buildMethod = elementFactory.createMethod("build", elementFactory.createType(psiClass));
    StringBuilder returnBuilder = new StringBuilder()
        .append("return new ").append(psiClass.getName()).append("(this);\n");
    PsiStatement returnStatement = elementFactory.createStatementFromText(returnBuilder.toString(), builderClass);
    buildMethod.getBody().add(returnStatement);
    builderClass.add(buildMethod);
    psiClass.add(builderClass);
  }

  @Override public void update(AnActionEvent e) {
    PsiClass psiClass = getPsiClassFromContext(e);
    e.getPresentation().setEnabled(psiClass != null);
  }

  private PsiClass getPsiClassFromContext(AnActionEvent e) {
    PsiFile psiFile = e.getData(LangDataKeys.PSI_FILE);
    Editor editor = e.getData(PlatformDataKeys.EDITOR);
    if (psiFile == null || editor == null) {
      return null;
    }
    int offset = editor.getCaretModel().getOffset();
    PsiElement elementAt = psiFile.findElementAt(offset);
    return PsiTreeUtil.getParentOfType(elementAt, PsiClass.class);
  }
}
