import com.google.common.base.CaseFormat;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.intellij.ui.JBColor;
import helper.BlocConfig;
import helper.BlocTaoData;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.*;


public class NewBloc extends AnAction {
    private Project project;
    private String psiPath;
    private BlocTaoData data;

    /**
     * Overall popup entity
     */
    private JDialog jDialog;
    private JTextField nameTextField;
    private ButtonGroup templateGroup;
    /**
     * Checkbox
     * Use folder：default true
     * Use prefix：default false
     */
    private JCheckBox folderBox, prefixBox;

    @Override
    public void actionPerformed(AnActionEvent event) {
        project = event.getProject();
        psiPath = event.getData(PlatformDataKeys.PSI_ELEMENT).toString();
        psiPath = psiPath.substring(psiPath.indexOf(":") + 1);
        initData();
        initView();
    }

    private void initData() {
        data = BlocTaoData.getInstance();
        jDialog = new JDialog(new JFrame(), "Bloc Template Code Produce");
    }

    private void initView() {
        //Set function button
        Container container = jDialog.getContentPane();
        container.setLayout(new BoxLayout(container, BoxLayout.Y_AXIS));

        //Set the main mode style
        //deal default value
        setMode(container);

        //Generate module name and OK cancel button
        setModuleAndConfirm(container);

        //Choose a pop-up style
        setJDialog();
    }

    /**
     * generate  file
     */
    private void save() {
        if (nameTextField.getText() == null || "".equals(nameTextField.getText().trim())) {
            Messages.showInfoMessage(project, "Please input the module name", "Info");
            return;
        }
        dispose();
        //Create a file
        createFile();
        //Refresh project
        project.getBaseDir().refresh(false, true);
    }

    /**
     * Set the overall pop-up style
     */
    private void setJDialog() {
        //The focus is on the current pop-up window,
        // and the focus will not shift even if you click on other areas
        jDialog.setModal(true);
        //Set padding
        ((JPanel) jDialog.getContentPane()).setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        jDialog.setSize(430, 350);
        jDialog.setLocationRelativeTo(null);
        jDialog.setVisible(true);
    }

    /**
     * Main module
     */
    private void setMode(Container container) {
        //Two rows and two columns
        JPanel template = new JPanel();
        template.setLayout(new GridLayout(1, 2));
        //Set the main module style：mode, function
        template.setBorder(BorderFactory.createTitledBorder("Select Mode"));
        //default: high setting
        JRadioButton defaultBtn = new JRadioButton(BlocConfig.modeDefault, data.defaultMode == 0);
        defaultBtn.setActionCommand(BlocConfig.modeDefault);
        setPadding(defaultBtn, 5, 10);
        JRadioButton highBtn = new JRadioButton(BlocConfig.modeHigh, data.defaultMode == 1);
        setPadding(highBtn, 5, 10);
        highBtn.setActionCommand(BlocConfig.modeHigh);

        template.add(defaultBtn);
        template.add(highBtn);
        templateGroup = new ButtonGroup();
        templateGroup.add(defaultBtn);
        templateGroup.add(highBtn);

        container.add(template);
        setDivision(container);
    }

    /**
     * Generate file
     */
    private void setCodeFile(Container container) {
        //Select build file
        JPanel file = new JPanel();
        file.setLayout(new GridLayout(2, 2));
        file.setBorder(BorderFactory.createTitledBorder("Select Function"));

        container.add(file);
        setDivision(container);
    }


    /**
     * Generate file name and button
     */
    private void setModuleAndConfirm(Container container) {
        JPanel nameField = new JPanel();
        nameField.setLayout(new FlowLayout());
        nameField.setBorder(BorderFactory.createTitledBorder("Module Name"));
        nameTextField = new JTextField(28);
        nameTextField.addKeyListener(keyListener);
        nameField.add(nameTextField);
        container.add(nameField);

        JPanel menu = new JPanel();
        menu.setLayout(new FlowLayout());

        //Set bottom spacing
        setDivision(container);

        //OK cancel button
        JButton cancel = new JButton("Cancel");
        cancel.setForeground(JBColor.RED);
        cancel.addActionListener(actionListener);

        JButton ok = new JButton("OK");
        ok.setForeground(JBColor.GREEN);
        ok.addActionListener(actionListener);
        menu.add(cancel);
        menu.add(ok);
        container.add(menu);
    }

    private void createFile() {
        String type = templateGroup.getSelection().getActionCommand();
        //deal default value
        if (BlocConfig.modeDefault.equals(type)) {
            data.defaultMode = 0;
        } else if (BlocConfig.modeHigh.equals(type)) {
            data.defaultMode = 1;
        }


        String name = upperCase(nameTextField.getText());
        String prefix = CaseFormat.LOWER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, name);
        String folder =  "/" + prefix;
        String prefixName =  prefix + "_";

        switch (type) {
            case BlocConfig.modeDefault:
                generateDefault(folder, prefixName);
                break;
            case BlocConfig.modeHigh:
                generateHigh(folder, prefixName);
                break;
        }
    }

    private void generateDefault(String folder, String prefixName) {
        String path = psiPath + folder;
        String cubitPath = path+"/cubit";
        String viewPath = path+"/view";
        generateFile("cubit/view/page.dart", viewPath, prefixName + data.viewName.toLowerCase() + ".dart");
        generateFile("cubit/view/view.dart", viewPath, prefixName + data.viewFileName.toLowerCase() + ".dart");
        generateFile("cubit/cubit/state.dart", cubitPath, prefixName + "state" + ".dart");
        generateFile("cubit/cubit/cubit.dart", cubitPath, prefixName + data.cubitName.toLowerCase() + ".dart");
    }

    private void generateHigh(String folder, String prefixName) {
        String path = psiPath + folder;
        String blocPath = path+"/bloc";
        String viewPath = path+"/view";
        generateFile("bloc/view/view.dart", viewPath, prefixName + data.viewFileName.toLowerCase() + ".dart");
        generateFile("bloc/view/page.dart", viewPath, prefixName + data.viewName.toLowerCase() + ".dart");
        generateFile("bloc/bloc/bloc.dart", blocPath, prefixName + data.blocName.toLowerCase() + ".dart");
        generateFile("bloc/bloc/event.dart", blocPath, prefixName + data.eventName.toLowerCase() + ".dart");
        generateFile("bloc/bloc/state.dart", blocPath, prefixName + "state" + ".dart");
    }


    private void generateFile(String inputFileName, String filePath, String outFileName) {
        //content deal
        String content = dealContent(inputFileName, outFileName);

        //Write file
        try {
            File folder = new File(filePath);
            // if file doesnt exists, then create it
            if (!folder.exists()) {
                folder.mkdirs();
            }
            File file = new File(filePath + "/" + outFileName);
            if (!file.exists()) {
                file.createNewFile();
            }

            FileWriter fw = new FileWriter(file.getAbsoluteFile());
            BufferedWriter bw = new BufferedWriter(fw);
            bw.write(content);
            bw.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //content need deal
    private String dealContent(String inputFileName, String outFileName) {
        //name baseFolder
        String baseFolder = "/templates/";
        String type = templateGroup.getSelection().getActionCommand();

        //read file
        String content = "";
        try {
            InputStream in = this.getClass().getResourceAsStream(baseFolder + inputFileName);
            content = new String(readStream(in));
        } catch (Exception e) {
        }

        String prefixName = "";
        //Adding a prefix requires modifying the imported class name
            prefixName = CaseFormat.LOWER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, upperCase(nameTextField.getText())) + "_";
        //replace Cubit
        if (type.equals(BlocConfig.modeDefault)) {
            content = content.replaceAll("\\$nameCubit", "\\$name" + data.cubitName);
            content = content.replace("view.dart", prefixName + "view" + ".dart");
            content = content.replace("cubit.dart", prefixName + "cubit" + ".dart");
            content = content.replace("state.dart", prefixName + "state" + ".dart");
        }


        //replace Bloc
        if (type.equals(BlocConfig.modeHigh)) {
            content = content.replaceAll("\\$nameBloc", "\\$name" + data.blocName);
            content = content.replaceAll("\\$nameEvent", "\\$name" + data.eventName);
            content = content.replaceAll("InitEvent", "Init" + data.eventName);
            content = content.replaceAll("event.dart", prefixName + data.eventName.toLowerCase() + ".dart");
            content = content.replaceAll("event\\)", data.eventName.toLowerCase() + "\\)");
            content = content.replaceAll("event is", data.eventName.toLowerCase() + " is");
            content = content.replace("state.dart", prefixName + "state" + ".dart");
            content = content.replace("blocs.dart", prefixName + "bloc" + ".dart");
            content = content.replace("view.dart", prefixName + "view" + ".dart");

        }
        //replace Event
        if (outFileName.contains(data.eventName.toLowerCase())) {
            content = content.replaceAll("Event", data.eventName);
        }
        //replace view
        if (outFileName.contains(data.viewFileName.toLowerCase())) {
            content = content.replace("'bloc.dart'", "'" + prefixName + data.blocName.toLowerCase() + ".dart" + "'");
            content = content.replace("'cubit.dart'", "'" + prefixName + data.cubitName.toLowerCase() + ".dart" + "'");
            content = content.replace("final bloc", "final " + data.blocName.toLowerCase());
            content = content.replace("final cubit", "final " + data.cubitName.toLowerCase());
            content = content.replace("=> bloc", "=> " + data.blocName.toLowerCase());
            content = content.replace("=> cubit", "=> " + data.cubitName.toLowerCase());
            content = content.replaceAll("nameCubit", "name" + data.cubitName);
            content = content.replaceAll("nameBloc", "name" + data.blocName);
            content = content.replaceAll("'event.dart'", "'" + prefixName + data.eventName.toLowerCase() + ".dart'");
            content = content.replaceAll("Event", data.eventName);
            content = content.replace("'state.dart'", "'" + prefixName + "state.dart'");
            content = content.replace("Page", data.viewName);
        }

        content = content.replaceAll("\\$name", upperCase(nameTextField.getText()));

        return content;
    }

    private byte[] readStream(InputStream inStream) throws Exception {
        ByteArrayOutputStream outSteam = new ByteArrayOutputStream();
        try {
            byte[] buffer = new byte[1024];
            int len = -1;
            while ((len = inStream.read(buffer)) != -1) {
                outSteam.write(buffer, 0, len);
                System.out.println(new String(buffer));
            }

        } catch (IOException e) {
        } finally {
            outSteam.close();
            inStream.close();
        }
        return outSteam.toByteArray();
    }


    private final KeyListener keyListener = new KeyListener() {
        @Override
        public void keyTyped(KeyEvent e) {
        }

        @Override
        public void keyPressed(KeyEvent e) {
            if (e.getKeyCode() == KeyEvent.VK_ENTER) save();
            if (e.getKeyCode() == KeyEvent.VK_ESCAPE) dispose();
        }

        @Override
        public void keyReleased(KeyEvent e) {
        }
    };

    private final ActionListener actionListener = new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
            if (e.getActionCommand().equals("Cancel")) {
                dispose();
            } else {
                save();
            }
        }
    };

    private void setPadding(JRadioButton btn, int top, int bottom) {
        btn.setBorder(BorderFactory.createEmptyBorder(top, 10, bottom, 0));
    }

    private void setMargin(JCheckBox btn, int top, int bottom) {
        btn.setBorder(BorderFactory.createEmptyBorder(top, 10, bottom, 0));
    }

    private void setDivision(Container container) {
        //Separate the spacing between modules
        JPanel margin = new JPanel();
        container.add(margin);
    }

    private void dispose() {
        jDialog.dispose();
    }

    private String upperCase(String str) {
        return str.substring(0, 1).toUpperCase() + str.substring(1);
    }
}