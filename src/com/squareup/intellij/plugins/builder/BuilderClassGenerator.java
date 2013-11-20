package com.squareup.intellij.plugins.builder;

import com.intellij.psi.JavaPsiFacade;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElementFactory;
import com.intellij.psi.PsiField;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.PsiParameter;
import com.intellij.psi.PsiStatement;

/**
 * Generates a static nested Builder class.
 */
public class BuilderClassGenerator implements BuilderGenerator {

  @Override public void generate(PsiClass psiClass, GenerateBuilderDialog dialog) {
    PsiElementFactory elementFactory = JavaPsiFacade.getElementFactory(psiClass.getProject());
    PsiClass builderClass = elementFactory.createClass("Builder");
    builderClass.getModifierList().add(elementFactory.createKeyword("static"));

    for (TableEntry entry : dialog.getEntries()) {
      PsiField field = entry.getField();
      PsiField builderField = elementFactory.createField(field.getName(), field.getType());
      builderClass.add(builderField);

      PsiMethod builderMethod = elementFactory.createMethod(
          field.getName(),
          elementFactory.createType(builderClass));
      PsiParameter parameter = elementFactory.createParameter(field.getName(), field.getType());
      builderMethod.getParameterList().add(parameter);

      StringBuilder assignBuilder = new StringBuilder()
          .append("this.").append(field.getName()).append(" = ").append(field.getName())
          .append(";\n");

      PsiStatement assignStatement = elementFactory.createStatementFromText(
          assignBuilder.toString(),
          builderClass);
      PsiStatement returnStatement = elementFactory.createStatementFromText(
          "return this;\n",
          builderClass);
      builderMethod.getBody().add(assignStatement);
      builderMethod.getBody().add(returnStatement);
      builderClass.add(builderMethod);
    }

    PsiMethod prototypeMethod = elementFactory.createMethod(
        "fromPrototype",
        elementFactory.createType(builderClass));
    prototypeMethod.getParameterList().add(
        elementFactory.createParameter("prototype", elementFactory.createType(psiClass)));

    for (TableEntry entry : dialog.getEntries()) {
      PsiField field = entry.getField();
      StringBuilder assignBuilder = new StringBuilder()
          .append(field.getName()).append(" = prototype.").append(field.getName()).append(";\n");
      PsiStatement assignStatement = elementFactory.createStatementFromText(
          assignBuilder.toString(),
          builderClass);
      prototypeMethod.getBody().add(assignStatement);
    }
    PsiStatement prototypeReturnStatement = elementFactory.createStatementFromText(
        "return this;\n",
        builderClass);
    prototypeMethod.getBody().add(prototypeReturnStatement);
    builderClass.add(prototypeMethod);

    PsiMethod buildMethod = elementFactory.createMethod(
        "build",
        elementFactory.createType(psiClass));
    StringBuilder returnBuilder = new StringBuilder()
        .append("return new ").append(psiClass.getName()).append("(this);\n");
    PsiStatement returnStatement = elementFactory.createStatementFromText(
        returnBuilder.toString(),
        builderClass);
    buildMethod.getBody().add(returnStatement);
    builderClass.add(buildMethod);

    psiClass.add(builderClass);
  }

  @Override public void rollback(PsiClass psiClass, GenerateBuilderDialog dialog) {
    PsiClass[] innerClasses = psiClass.getInnerClasses();
    for (PsiClass innerClass : innerClasses) {
      if (innerClass.getName().equals("Builder")) {
        innerClass.delete();
        break;
      }
    }
  }
}
