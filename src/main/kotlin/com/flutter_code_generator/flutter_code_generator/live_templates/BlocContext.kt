package com.flutter_code_generator.flutter_code_generator.live_templates

import com.intellij.codeInsight.template.TemplateContextType
import com.intellij.psi.PsiFile

class BlocContext protected constructor() : TemplateContextType("FLUTTER", "Flutter") {
    override fun isInContext(file: PsiFile, offset: Int): Boolean {
        return file.name.endsWith(".dart")
    }
}