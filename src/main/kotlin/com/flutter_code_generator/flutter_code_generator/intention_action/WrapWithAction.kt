package com.flutter_code_generator.flutter_code_generator.intention_action

import com.intellij.codeInsight.intention.IntentionAction
import com.intellij.codeInsight.intention.PsiElementBaseIntentionAction
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.command.WriteCommandAction
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.project.Project
import com.intellij.openapi.util.TextRange
import com.intellij.psi.PsiDocumentManager
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile
import com.intellij.psi.codeStyle.CodeStyleManager
import com.intellij.util.IncorrectOperationException
import com.flutter_code_generator.flutter_code_generator.intention_action.WrapHelper.Companion.callExpressionFinder
import com.flutter_code_generator.flutter_code_generator.intention_action.WrapHelper.Companion.isSelectionValid

abstract class WrapWithAction(private val snippetType: SnippetType) : PsiElementBaseIntentionAction(), IntentionAction {
    private var callExpressionElement: PsiElement? = null

    override fun getFamilyName(): String {
        return text
    }


    override fun isAvailable(project: Project, editor: Editor?, psiElement: PsiElement): Boolean {

        val currentFile = getCurrentFile(project, editor!!)
        if (currentFile != null && !currentFile.name.endsWith(".dart")) {
            return false
        }

        if (psiElement.toString() != "PsiElement(IDENTIFIER)") {
            return false
        }

        callExpressionElement = callExpressionFinder(psiElement)
        return callExpressionElement != null
    }


    @Throws(IncorrectOperationException::class)
    override fun invoke(project: Project, editor: Editor, element: PsiElement) {
        val runnable = Runnable { invokeSnippetAction(project, editor, snippetType) }
        WriteCommandAction.runWriteCommandAction(project, runnable)
    }

    private fun invokeSnippetAction(project: Project, editor: Editor, snippetType: SnippetType?) {
        val document = editor.document

        val element = callExpressionElement
        val elementSelectionRange = element!!.textRange
        val offsetStart = elementSelectionRange.startOffset
        val offsetEnd = elementSelectionRange.endOffset

        if (!isSelectionValid(offsetStart, offsetEnd)) {
            return
        }

        val selectedText = document.getText(TextRange.create(offsetStart, offsetEnd))
        val replaceWith = Snippets.getSnippet(snippetType, selectedText)

        // wrap the widget:
        WriteCommandAction.runWriteCommandAction(
            project
        ) {
            document.replaceString(offsetStart, offsetEnd, replaceWith)
        }

        // place cursors to specify types:
        val prefixSelection = Snippets.PREFIX_SELECTION
        val snippetArr =
            arrayOf<String?>(Snippets.BLOC_SNIPPET_KEY, Snippets.STATE_SNIPPET_KEY, Snippets.REPOSITORY_SNIPPET_KEY)

        val caretModel = editor.caretModel
        caretModel.removeSecondaryCarets()

        for (snippet in snippetArr) {
            if (!replaceWith.contains(snippet!!)) {
                continue
            }

            val caretOffset = offsetStart + replaceWith.indexOf(snippet)
            val visualPos = editor.offsetToVisualPosition(caretOffset)
            caretModel.addCaret(visualPos)

            // select snippet prefix keys:
            val currentCaret = caretModel.currentCaret
            currentCaret.setSelection(caretOffset, caretOffset + prefixSelection.length)
        }

        val initialCaret = caretModel.allCarets[0]
        if (!initialCaret.hasSelection()) {
            // initial position from where was triggered the intention action
            caretModel.removeCaret(initialCaret)
        }

        // reformat file:
        ApplicationManager.getApplication().runWriteAction {
            PsiDocumentManager.getInstance(project).commitDocument(document)
            val currentFile = getCurrentFile(project, editor)
            if (currentFile != null) {
                val unformattedText = document.text
                val unformattedLineCount = document.lineCount

                CodeStyleManager.getInstance(project).reformat(currentFile)

                val formattedLineCount = document.lineCount

                // file was incorrectly formatted, revert formatting
                if (formattedLineCount > unformattedLineCount + 3) {
                    document.setText(unformattedText)
                    PsiDocumentManager.getInstance(project).commitDocument(document)
                }
            }
        }
    }

    override fun startInWriteAction(): Boolean {
        return true
    }

    private fun getCurrentFile(project: Project, editor: Editor): PsiFile? {
        return PsiDocumentManager.getInstance(project).getPsiFile(editor.document)
    }
}
