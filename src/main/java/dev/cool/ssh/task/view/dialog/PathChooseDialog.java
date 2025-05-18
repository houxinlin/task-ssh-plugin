package dev.cool.ssh.task.view.dialog;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;

public class PathChooseDialog extends DialogWrapper {
    private JTextField pathField;
    private JCheckBox executeInScriptDirCheckBox;
    private String path;
    private boolean executeInScriptDir;

    public PathChooseDialog(@Nullable Project project) {
        super(project);
        setTitle("选择路径");
        init();
    }

    @Override
    protected @Nullable JComponent createCenterPanel() {
        JPanel panel = new JPanel(new BorderLayout(5, 5));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // 创建路径输入框和标签的容器
        JPanel pathPanel = new JPanel(new BorderLayout(5, 0));
        pathField = new JTextField(40);
        JLabel pathLabel = new JLabel("路径: ");
        pathLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 5));
        pathPanel.add(pathLabel, BorderLayout.WEST);
        pathPanel.add(pathField, BorderLayout.CENTER);
        panel.add(pathPanel, BorderLayout.NORTH);

        // 创建复选框
        executeInScriptDirCheckBox = new JCheckBox("在脚本所在目录执行", true);
        executeInScriptDirCheckBox.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));
        panel.add(executeInScriptDirCheckBox, BorderLayout.CENTER);

        return panel;
    }

    @Override
    protected void doOKAction() {
        path = pathField.getText().trim();
        executeInScriptDir = executeInScriptDirCheckBox.isSelected();
        super.doOKAction();
    }

    public String getPath() {
        return path;
    }

    public boolean isExecuteInScriptDir() {
        return executeInScriptDir;
    }
}
