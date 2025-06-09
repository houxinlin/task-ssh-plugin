package dev.cool.ssh.task.view.dialog;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.TextFieldWithBrowseButton;
import dev.cool.ssh.task.model.CommandParameter;
import dev.cool.ssh.task.model.ExecuteInfo;
import dev.cool.ssh.task.utils.JSONUtils;
import dev.cool.ssh.task.utils.Utils;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;

public class CommandParameterDialog extends ExecParameterDialogWrapper {
    private JTextField commandField;
    private TextFieldWithBrowseButton directoryField;
    private final Project project;

    public CommandParameterDialog(@Nullable Project project, ExecuteInfo executeInfo) {
        super(project, executeInfo);
        this.project = project;
        setTitle("执行命令");
        init();
    }

    public CommandParameterDialog(@Nullable Project project) {
        this(project, null);
    }

    @Override
    protected String buildExtJSON() {
        CommandParameter parameter = new CommandParameter();
        parameter.setValue(commandField.getText().trim());
        parameter.setDirectory(directoryField.getText().trim());
        return JSONUtils.toJSON(parameter);
    }

    @Override
    protected @Nullable JComponent createCenterPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // 创建目录输入框
        JLabel dirLabel = new JLabel("执行目录:");
        gbc.gridx = 0;
        gbc.gridy = 0;
        panel.add(dirLabel, gbc);

        directoryField = new TextFieldWithBrowseButton(e -> {
            String path = Utils.getPath(true, project, getHostInfo());
            if (path != null) {
                directoryField.setText(path);
            }
        });
        gbc.gridx = 1;
        gbc.gridy = 0;
        panel.add(directoryField, gbc);

        // 创建命令输入框
        JLabel cmdLabel = new JLabel("bash:");
        gbc.gridx = 0;
        gbc.gridy = 1;
        panel.add(cmdLabel, gbc);

        commandField = new JTextField(40);
        gbc.gridx = 1;
        gbc.gridy = 1;
        panel.add(commandField, gbc);

        if (getExecuteInfo() != null) {
            CommandParameter parameter = JSONUtils.fromJSON(getExecuteInfo().getExecuteExtJSON(), CommandParameter.class);
            commandField.setText(parameter.getValue());
            directoryField.setText(parameter.getDirectory());
        }
        return panel;
    }

    public String getCommand() {
        return commandField.getText().trim();
    }

    public String getDirectory() {
        return directoryField.getText().trim();
    }
}
