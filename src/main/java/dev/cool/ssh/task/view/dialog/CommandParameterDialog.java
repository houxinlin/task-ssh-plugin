package dev.cool.ssh.task.view.dialog;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;

public class CommandParameterDialog extends DialogWrapper {
    private JTextField commandField;
    private String command;

    public CommandParameterDialog(@Nullable Project project) {
        super(project);
        setTitle("执行命令");
        init();
    }

    @Override
    protected @Nullable JComponent createCenterPanel() {
        JPanel panel = new JPanel(new BorderLayout(2, 5));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // 创建命令输入框
        commandField = new JTextField(40);
        panel.add(commandField, BorderLayout.CENTER);

        // 创建提示符标签
        JLabel promptLabel = new JLabel("[base]$ ");
        promptLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 2));
        panel.add(promptLabel, BorderLayout.WEST);

        return panel;
    }

    @Override
    protected void doOKAction() {
        command = commandField.getText().trim();
        super.doOKAction();
    }

    public String getCommand() {
        return command;
    }
}
