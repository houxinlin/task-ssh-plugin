package dev.cool.ssh.task.view.dialog;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;

public class KillJarParameterDialog extends DialogWrapper {
    private JTextField jarNameField;
    private String jarName;

    public KillJarParameterDialog(@Nullable Project project) {
        super(project);
        setTitle("Kill Jar Process");
        init();
    }

    @Override
    protected @Nullable JComponent createCenterPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // 添加前缀标签
        JLabel prefixLabel = new JLabel("jps -l | grep \"");
        gbc.gridx = 0;
        gbc.gridy = 0;
        panel.add(prefixLabel, gbc);

        // 添加输入框
        jarNameField = new JTextField(20);
        gbc.gridx = 1;
        gbc.gridy = 0;
        panel.add(jarNameField, gbc);

        // 添加后缀标签
        JLabel suffixLabel = new JLabel("\" | awk '{print $1}' | xargs -r kill -9");
        gbc.gridx = 2;
        gbc.gridy = 0;
        panel.add(suffixLabel, gbc);

        return panel;
    }

    @Override
    protected void doOKAction() {
        jarName = jarNameField.getText().trim();
        super.doOKAction();
    }

    public String getJarName() {
        return jarName;
    }
}
