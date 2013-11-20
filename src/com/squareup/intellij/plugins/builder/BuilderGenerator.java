package com.squareup.intellij.plugins.builder;

import com.intellij.psi.PsiClass;

/**
 * Generates a piece of the Builder.
 */
public interface BuilderGenerator {

  /**
   * Generates a piece of the builder and attaches it to the provided {@link PsiClass}.
   *
   * @param psiClass The class to update.
   * @param dialog The dialog with options.
   */
  void generate(PsiClass psiClass, GenerateBuilderDialog dialog);

  /**
   * Rollback a piece of the builder and deletes it from the provided {@link PsiClass}.
   *
   * @param psiClass The class to update.
   * @param dialog The dialog with options.
   */
  void rollback(PsiClass psiClass, GenerateBuilderDialog dialog);
}
