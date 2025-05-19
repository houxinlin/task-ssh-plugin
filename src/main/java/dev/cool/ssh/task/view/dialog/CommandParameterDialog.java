package dev.cool.ssh.task.view.dialog;

import com.intellij.openapi.project.Project;
import dev.cool.ssh.task.model.ExecuteInfo;
import dev.cool.ssh.task.model.SimpleParameter;
import dev.cool.ssh.task.utils.JSONUtils;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;

public class CommandParameterDialog extends ExecParameterDialogWrapper {
    private JTextField commandField;

    public CommandParameterDialog(@Nullable Project project, ExecuteInfo executeInfo) {
        super(project, executeInfo);
        setTitle("执行命令");
        init();
    }

    public CommandParameterDialog(@Nullable Project project) {
        this(project, null);
    }

    @Override
    protected String buildExtJSON() {
        SimpleParameter simpleParameter = new SimpleParameter();
        simpleParameter.setValue(commandField.getText().trim());
        return JSONUtils.toJSON(simpleParameter);
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

        if (getExecuteInfo() != null) {
            SimpleParameter scriptParameter = JSONUtils.fromJSON(getExecuteInfo().getExecuteExtJSON(), SimpleParameter.class);
            commandField.setText(scriptParameter.getValue());
        }
        return panel;
    }

    public String getCommand() {
        return commandField.getText().trim();
    }
}
