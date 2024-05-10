package com.flutter_code_generator.flutter_code_generator.setting


import com.intellij.ui.components.JBLabel
import com.intellij.ui.components.JBTextField
import com.intellij.util.ui.FormBuilder
import javax.swing.JPanel


class SettingsComponent {
    var mainPanel: JPanel
    var blocName: JBTextField = JBTextField()
    var cubitName: JBTextField = JBTextField()
    var eventName: JBTextField = JBTextField()
    var viewName: JBTextField = JBTextField()
    var viewFileName: JBTextField = JBTextField()

    init {
        mainPanel = FormBuilder.createFormBuilder()
            .addLabeledComponent(JBLabel("Bloc Name: "), blocName)
            .addLabeledComponent(JBLabel("Cubit Name: "), cubitName)
            .addLabeledComponent(JBLabel("Event Name: "), eventName)
            .addLabeledComponent(JBLabel("View Name: "), viewName)
            .addLabeledComponent(JBLabel("View File Name: "), viewFileName)
            .addComponentFillVertically(JPanel(), 0)
            .panel
    }
}
