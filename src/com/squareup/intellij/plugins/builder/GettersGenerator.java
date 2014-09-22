package com.squareup.intellij.plugins.builder;

import com.google.common.collect.Maps;
import com.intellij.psi.JavaPsiFacade;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElementFactory;
import com.intellij.psi.PsiField;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.PsiType;
import java.util.Map;

/**
 * Generates the getter methods.
 *
 * <p>Nullable fields will return Optionals</p>
 */
public class GettersGenerator implements BuilderGenerator {

  @Override public void generate(PsiClass psiClass, GenerateBuilderDialog dialog) {
    if (!dialog.shouldCreateGetters()) {
      return;
    }
    PsiElementFactory elementFactory = JavaPsiFacade.getElementFactory(psiClass.getProject());
    for (TableEntry entry : dialog.getEntries()) {
      PsiField field = entry.getField();
      PsiMethod getter;
      StringBuilder getterStatement = new StringBuilder();
      if (entry.isNullable()) {
        StringBuilder optionalBuilder = new StringBuilder()
            .append("java.util.Optional")
            .append(field.getType().getCanonicalText())
            .append(">");
        PsiType optionalType = elementFactory.createTypeFromText(
            optionalBuilder.toString(),
            psiClass);
        getter = elementFactory.createMethod(getGetterMethodName(field), optionalType);
        getterStatement.append("return Optional.ofNullable(")
            .append(field.getName())
            .append(");\n");
      } else {
        getter = elementFactory.createMethod(getGetterMethodName(field), field.getType());
        getterStatement.append("return ").append(field.getName()).append(";\n");
      }
      getter.getBody().add(
          elementFactory.createStatementFromText(getterStatement.toString(), psiClass));
      psiClass.add(getter);
    }
  }

  @Override public void rollback(PsiClass psiClass, GenerateBuilderDialog dialog) {
    if (!dialog.shouldCreateGetters()) {
      return;
    }

    Map<String, PsiMethod> methods = Maps.newHashMap();
    for (PsiMethod method : psiClass.getMethods()) {
      methods.put(method.getName(), method);
    }

    for (TableEntry entry : dialog.getEntries()) {
      String getterName = getGetterMethodName(entry.getField());
      PsiMethod getterMethod = methods.get(getterName);
      if (getterMethod != null) {
        getterMethod.delete();
      }
    }
  }

  private String getGetterMethodName(PsiField psiField) {
    char[] chars = psiField.getName().toCharArray();
    chars[0] = Character.toUpperCase(chars[0]);
    return new StringBuilder("get").append(chars).toString();
  }
}
