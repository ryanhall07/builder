package com.squareup.intellij.plugins.builder;

import com.google.common.collect.ImmutableList;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.LangDataKeys;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.editor.Editor;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.util.PsiTreeUtil;
import java.util.List;

/** {@link AnAction} that generates a Builder class. */
public class GenerateBuilder extends AnAction {

  private final List<BuilderGenerator> generators = ImmutableList.of(
      new BuilderClassGenerator(),
      new ConstructorGenerator(),
      new GettersGenerator()
  );

  @Override
  public void update(AnActionEvent e) {
    PsiClass psiClass = getPsiClassFromContext(e);
    e.getPresentation().setEnabled(psiClass != null);
  }

  @Override
  public void actionPerformed(AnActionEvent e) {
    final PsiClass psiClass = getPsiClassFromContext(e);
    final GenerateBuilderDialog dialog = new GenerateBuilderDialog(psiClass);
    dialog.show();
    if (dialog.isOK()) {
      new WriteCommandAction.Simple(psiClass.getProject(), psiClass.getContainingFile()) {
        @Override protected void run() throws Throwable {
          for (BuilderGenerator generator : generators) {
            generator.rollback(psiClass, dialog);
          }
          for (BuilderGenerator generator : generators) {
            generator.generate(psiClass, dialog);
          }
        }
      }.execute();
    }
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
