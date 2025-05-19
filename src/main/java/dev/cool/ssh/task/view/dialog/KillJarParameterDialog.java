package dev.cool.ssh.task.view.dialog;

import com.intellij.openapi.project.Project;
import dev.cool.ssh.task.model.ExecuteInfo;
import dev.cool.ssh.task.model.ScriptParameter;
import dev.cool.ssh.task.model.SimpleParameter;
import dev.cool.ssh.task.utils.JSONUtils;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;

public class KillJarParameterDialog extends ExecParameterDialogWrapper {
    private JTextField jarNameField;
    private String jarName;
    private ExecuteInfo executeInfo;

    public KillJarParameterDialog(@Nullable Project project, ExecuteInfo executeInfo) {
        super(project, executeInfo);
        this.executeInfo = executeInfo;
        setTitle("Kill Jar Process");
        init();
    }

    public KillJarParameterDialog(@Nullable Project project) {
        this(project, null);
    }

    @Override
    protected String buildExtJSON() {
        SimpleParameter simpleParameter = new SimpleParameter();
        simpleParameter.setValue(jarName);
        return JSONUtils.toJSON(simpleParameter);
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

        if (getExecuteInfo() != null) {
            SimpleParameter simpleParameter = JSONUtils.fromJSON(getExecuteInfo().getExecuteExtJSON(), SimpleParameter.class);
            jarNameField.setText(simpleParameter.getValue());
        }
        return panel;
    }

    @Override
    protected void doOKAction() {
        super.doOKAction();

    }

    public String getJarName() {
        return jarNameField.getText().trim();
    }
}
