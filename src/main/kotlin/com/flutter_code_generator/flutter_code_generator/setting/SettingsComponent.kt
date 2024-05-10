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
            .addLabeledComponent(JBLabel("Bloc name: "), blocName)
            .addLabeledComponent(JBLabel("Cubit name: "), cubitName)
            .addLabeledComponent(JBLabel("Event name: "), eventName)
            .addLabeledComponent(JBLabel("View name: "), viewName)
            .addLabeledComponent(JBLabel("View file name: "), viewFileName)
            .addComponentFillVertically(JPanel(), 0)
            .panel
    }
}
