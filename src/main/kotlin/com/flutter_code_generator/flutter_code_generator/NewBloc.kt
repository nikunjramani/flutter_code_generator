package com.flutter_code_generator.flutter_code_generator

import com.flutter_code_generator.flutter_code_generator.helper.BlocConfig
import com.flutter_code_generator.flutter_code_generator.helper.BlocTaoData
import com.google.common.base.CaseFormat
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.PlatformDataKeys
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.Messages
import com.intellij.ui.JBColor
import java.awt.Container
import java.awt.FlowLayout
import java.awt.GridLayout
import java.awt.event.ActionListener
import java.awt.event.KeyEvent
import java.awt.event.KeyListener
import java.io.*
import java.util.*
import javax.swing.*

class NewBloc : AnAction() {
    private var project: Project? = null
    private var psiPath: String? = null
    private lateinit var data: BlocTaoData;

    /**
     * Overall popup entity
     */
    private var jDialog: JDialog? = null
    private var nameTextField: JTextField? = null
    private var templateGroup: ButtonGroup? = null

    /**
     * Checkbox
     * Use folder：default true
     * Use prefix：default false
     */
    private val folderBox: JCheckBox? = null
    private val prefixBox: JCheckBox? = null

    override fun actionPerformed(event: AnActionEvent) {
        project = event.project
        psiPath = event.getData(PlatformDataKeys.PSI_ELEMENT).toString()
        psiPath = psiPath!!.substring(psiPath!!.indexOf(":") + 1)
        initData()
        initView()
    }

    private fun initData() {
        data = BlocTaoData.instance
        jDialog = JDialog(JFrame(), "Bloc Template Code Produce")
    }

    private fun initView() {
        //Set function button
        val container = jDialog!!.contentPane
        container.layout = BoxLayout(container, BoxLayout.Y_AXIS)

        //Set the main mode style
        //deal default value
        setMode(container)

        //Generate module name and OK cancel button
        setModuleAndConfirm(container)

        //Choose a pop-up style
        setJDialog()
    }

    /**
     * generate  file
     */
    private fun save() {
        if (nameTextField!!.text == null || nameTextField!!.text.trim { it <= ' ' }.isEmpty()) {
            Messages.showInfoMessage(project, "Please input the module name", "Info")
            return
        }
        dispose()
        //Create a file
        createFile()
        //Refresh project
        project!!.baseDir.refresh(false, true)
    }

    /**
     * Set the overall pop-up style
     */
    private fun setJDialog() {
        //The focus is on the current pop-up window,
        // and the focus will not shift even if you click on other areas
        jDialog!!.isModal = true
        //Set padding
        (jDialog!!.contentPane as JPanel).border = BorderFactory.createEmptyBorder(10, 10, 10, 10)
        jDialog!!.setSize(430, 350)
        jDialog!!.setLocationRelativeTo(null)
        jDialog!!.isVisible = true
    }

    /**
     * Main module
     */
    private fun setMode(container: Container) {
        //Two rows and two columns
        val template = JPanel()
        template.layout = GridLayout(1, 2)
        //Set the main module style：mode, function
        template.border = BorderFactory.createTitledBorder("Select Mode")
        //default: high setting
        val defaultBtn: JRadioButton = JRadioButton(BlocConfig.modeDefault, data.defaultMode === 0)
        defaultBtn.actionCommand = BlocConfig.modeDefault
        setPadding(defaultBtn, 5, 10)
        val highBtn: JRadioButton = JRadioButton(BlocConfig.modeHigh, data.defaultMode === 1)
        setPadding(highBtn, 5, 10)
        highBtn.actionCommand = BlocConfig.modeHigh

        template.add(defaultBtn)
        template.add(highBtn)
        templateGroup = ButtonGroup()
        templateGroup!!.add(defaultBtn)
        templateGroup!!.add(highBtn)

        container.add(template)
        setDivision(container)
    }

    /**
     * Generate file
     */
    private fun setCodeFile(container: Container) {
        //Select build file
        val file = JPanel()
        file.layout = GridLayout(2, 2)
        file.border = BorderFactory.createTitledBorder("Select Function")

        container.add(file)
        setDivision(container)
    }


    /**
     * Generate file name and button
     */
    private fun setModuleAndConfirm(container: Container) {
        val nameField = JPanel()
        nameField.layout = FlowLayout()
        nameField.border = BorderFactory.createTitledBorder("Module Name")
        nameTextField = JTextField(28)
        nameTextField!!.addKeyListener(keyListener)
        nameField.add(nameTextField)
        container.add(nameField)

        val menu = JPanel()
        menu.layout = FlowLayout()

        //Set bottom spacing
        setDivision(container)

        //OK cancel button
        val cancel = JButton("Cancel")
        cancel.foreground = JBColor.RED
        cancel.addActionListener(actionListener)

        val ok = JButton("OK")
        ok.foreground = JBColor.GREEN
        ok.addActionListener(actionListener)
        menu.add(cancel)
        menu.add(ok)
        container.add(menu)
    }

    private fun createFile() {
        val type = templateGroup!!.selection.actionCommand
        //deal default value
        if (BlocConfig.modeDefault.equals(type)) {
            data.defaultMode = 0
        } else if (BlocConfig.modeHigh.equals(type)) {
            data.defaultMode = 1
        }


        val name = upperCase(nameTextField!!.text)
        val prefix = CaseFormat.LOWER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, name)
        val folder = "/$prefix"
        val prefixName = prefix + "_"

        when (type) {
            BlocConfig.modeDefault -> generateDefault(folder, prefixName)
            BlocConfig.modeHigh -> generateHigh(folder, prefixName)
        }
    }

    private fun generateDefault(folder: String, prefixName: String) {
        val path = psiPath + folder
        val cubitPath = "$path/cubit"
        val viewPath = "$path/view"
        generateFile("cubit/view/page.dart", viewPath, (prefixName + data.viewName?.toLowerCase()).toString() + ".dart")
        generateFile(
            "cubit/view/view.dart",
            viewPath,
            (prefixName + data.viewFileName?.toLowerCase()).toString() + ".dart"
        )
        generateFile("cubit/cubit/state.dart", cubitPath, prefixName + "state" + ".dart")
        generateFile(
            "cubit/cubit/cubit.dart",
            cubitPath,
            (prefixName + data.cubitName?.toLowerCase()).toString() + ".dart"
        )
    }

    private fun generateHigh(folder: String, prefixName: String) {
        val path = psiPath + folder
        val blocPath = "$path/bloc"
        val viewPath = "$path/view"
        generateFile(
            "bloc/view/view.dart",
            viewPath,
            (prefixName + data.viewFileName?.toLowerCase()).toString() + ".dart"
        )
        generateFile("bloc/view/page.dart", viewPath, (prefixName + data.viewName?.toLowerCase()).toString() + ".dart")
        generateFile("bloc/bloc/bloc.dart", blocPath, (prefixName + data.blocName?.toLowerCase()).toString() + ".dart")
        generateFile("bloc/bloc/event.dart", blocPath, (prefixName + data.eventName?.toLowerCase()).toString() + ".dart")
        generateFile("bloc/bloc/state.dart", blocPath, prefixName + "state" + ".dart")
    }


    private fun generateFile(inputFileName: String, filePath: String, outFileName: String) {
        //content deal
        val content = dealContent(inputFileName, outFileName)

        //Write file
        try {
            val folder = File(filePath)
            // if file doesnt exists, then create it
            if (!folder.exists()) {
                folder.mkdirs()
            }
            val file = File("$filePath/$outFileName")
            if (!file.exists()) {
                file.createNewFile()
            }

            val fw = FileWriter(file.absoluteFile)
            val bw = BufferedWriter(fw)
            bw.write(content)
            bw.close()
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    //content need deal
    private fun dealContent(inputFileName: String, outFileName: String): String {
        //name baseFolder
        val baseFolder = "/templates/"
        val type = templateGroup!!.selection.actionCommand

        //read file
        var content = ""
        try {
            val `in` = this.javaClass.getResourceAsStream(baseFolder + inputFileName)
            content = String(readStream(`in`))
        } catch (e: Exception) {
        }

        var prefixName = ""
        //Adding a prefix requires modifying the imported class name
        prefixName = CaseFormat.LOWER_CAMEL.to(
            CaseFormat.LOWER_UNDERSCORE, upperCase(
                nameTextField!!.text
            )
        ) + "_"
        //replace Cubit
        if (type == BlocConfig.modeDefault) {
            content = content.replace("\\\$nameCubit".toRegex(), "\\\$name" + data.cubitName)
            content = content.replace("view.dart", prefixName + "view" + ".dart")
            content = content.replace("cubit.dart", prefixName + "cubit" + ".dart")
            content = content.replace("state.dart", prefixName + "state" + ".dart")
        }


        //replace Bloc
        if (type == BlocConfig.modeHigh) {
            content = content.replace("\\\$nameBloc".toRegex(), "\\\$name" + data.blocName)
            content = content.replace("\\\$nameEvent".toRegex(), "\\\$name" + data.eventName)
            content = content.replace("InitEvent".toRegex(), "Init" + data.eventName)
            content = content.replace(
                "event.dart".toRegex(),
                (prefixName + data.eventName?.toLowerCase()).toString() + ".dart"
            )
            content = content.replace("event\\)".toRegex(), data.eventName?.toLowerCase() + "\\)")
            content = content.replace("event is".toRegex(), data.eventName?.toLowerCase() + " is")
            content = content.replace("state.dart", prefixName + "state" + ".dart")
            content = content.replace("blocs.dart", prefixName + "bloc" + ".dart")
            content = content.replace("view.dart", prefixName + "view" + ".dart")
        }
        //replace Event
        if (outFileName.contains(data.eventName!!.toLowerCase())) {
            content = content.replace("Event".toRegex(), data.eventName!!)
        }
        //replace view
        if (outFileName.contains(data.viewFileName!!.toLowerCase())) {
            content = content.replace(
                "'bloc.dart'",
                ("'" + prefixName + data.blocName?.toLowerCase()).toString() + ".dart" + "'"
            )
            content = content.replace(
                "'cubit.dart'",
                ("'" + prefixName + data.cubitName?.toLowerCase()).toString() + ".dart" + "'"
            )
            content = content.replace("final bloc", "final " + data.blocName?.toLowerCase())
            content = content.replace("final cubit", "final " + data.cubitName?.toLowerCase())
            content = content.replace("=> bloc", "=> " + data.blocName?.toLowerCase())
            content = content.replace("=> cubit", "=> " + data.cubitName?.toLowerCase())
            content = content.replace("nameCubit".toRegex(), "name" + data.cubitName)
            content = content.replace("nameBloc".toRegex(), "name" + data.blocName)
            content = content.replace(
                "'event.dart'".toRegex(),
                ("'" + prefixName + data.eventName?.toLowerCase()).toString() + ".dart'"
            )
            content = content.replace("Event".toRegex(), data.eventName!!)
            content = content.replace("'state.dart'", "'" + prefixName + "state.dart'")
            content = content.replace("Page", data.viewName!!)
        }

        content = content.replace("\\\$name".toRegex(), upperCase(nameTextField!!.text))

        return content
    }

    @Throws(Exception::class)
    private fun readStream(inStream: InputStream): ByteArray {
        val outSteam = ByteArrayOutputStream()
        try {
            val buffer = ByteArray(1024)
            var len = -1
            while ((inStream.read(buffer).also { len = it }) != -1) {
                outSteam.write(buffer, 0, len)
                println(String(buffer))
            }
        } catch (e: IOException) {
        } finally {
            outSteam.close()
            inStream.close()
        }
        return outSteam.toByteArray()
    }


    private val keyListener: KeyListener = object : KeyListener {
        override fun keyTyped(e: KeyEvent) {
        }

        override fun keyPressed(e: KeyEvent) {
            if (e.keyCode == KeyEvent.VK_ENTER) save()
            if (e.keyCode == KeyEvent.VK_ESCAPE) dispose()
        }

        override fun keyReleased(e: KeyEvent) {
        }
    }

    private val actionListener = ActionListener { e ->
        if (e.actionCommand == "Cancel") {
            dispose()
        } else {
            save()
        }
    }

    private fun setPadding(btn: JRadioButton, top: Int, bottom: Int) {
        btn.border = BorderFactory.createEmptyBorder(top, 10, bottom, 0)
    }

    private fun setMargin(btn: JCheckBox, top: Int, bottom: Int) {
        btn.border = BorderFactory.createEmptyBorder(top, 10, bottom, 0)
    }

    private fun setDivision(container: Container) {
        //Separate the spacing between modules
        val margin = JPanel()
        container.add(margin)
    }

    private fun dispose() {
        jDialog!!.dispose()
    }

    private fun upperCase(str: String): String {
        return str.substring(0, 1).uppercase(Locale.getDefault()) + str.substring(1)
    }
}