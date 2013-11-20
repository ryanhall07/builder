package com.squareup.intellij.plugins.builder;

import com.intellij.psi.JavaPsiFacade;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementFactory;
import com.intellij.psi.PsiField;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.PsiModifier;
import com.intellij.psi.PsiParameter;
import com.intellij.psi.PsiStatement;
import com.intellij.psi.codeStyle.JavaCodeStyleManager;

/**
 * Generates the private constructor.
 */
public class ConstructorGenerator implements BuilderGenerator {

  @Override public void generate(PsiClass psiClass, GenerateBuilderDialog dialog) {
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

      PsiStatement assignStatement = elementFactory.createStatementFromText(
          fieldAssign.toString(),
          psiClass);
      PsiElement assignShortened = JavaCodeStyleManager.getInstance(psiClass.getProject())
          .shortenClassReferences(assignStatement);
      constructor.getBody().add(assignShortened);
    }

    psiClass.add(constructor);
  }

  @Override public void rollback(PsiClass psiClass, GenerateBuilderDialog dialog) {
    for (PsiMethod constructor : psiClass.getConstructors()) {
      PsiParameter[] parameters = constructor.getParameterList().getParameters();
      if (parameters.length == 1 && parameters[0].getName().equals("builder")) {
        constructor.delete();
        break;
      }
    }
  }
}
