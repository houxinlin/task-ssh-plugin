package dev.cool.ssh.task.view.dialog;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.TextFieldWithBrowseButton;
import dev.cool.ssh.task.model.ExecuteInfo;
import dev.cool.ssh.task.model.ScriptParameter;
import dev.cool.ssh.task.utils.JSONUtils;
import dev.cool.ssh.task.utils.Utils;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ScriptPathChooseDialog extends ExecParameterDialogWrapper {
    private TextFieldWithBrowseButton pathField;
    private JCheckBox executeInScriptDirCheckBox;
    private Project project;

    public ScriptPathChooseDialog(@Nullable Project project, ExecuteInfo executeInfo) {
        super(project, executeInfo);
        this.project = project;
        setTitle("Select Script Path");
        setSize(750, 200);
        init();
    }

    public ScriptPathChooseDialog(@Nullable Project project) {
        this(project, null);

    }

    @Override
    protected String buildExtJSON() {
        ScriptParameter simpleParameter = new ScriptParameter();
        simpleParameter.setValue(getPath());
        return JSONUtils.toJSON(simpleParameter);
    }

    @Override
    protected @Nullable JComponent createCenterPanel() {
        JPanel panel = new JPanel(new BorderLayout(5, 5));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // 创建路径输入框和标签的容器
        JPanel pathPanel = new JPanel(new BorderLayout(5, 0));
        pathField = new TextFieldWithBrowseButton(e -> {
            String path = Utils.getPath(false, project, getHostInfo());
            if (path != null) {
                pathField.setText(path);
            }
        });
        JLabel pathLabel = new JLabel("Path: ");
        pathLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 5));
        pathPanel.add(pathLabel, BorderLayout.WEST);
        pathPanel.add(pathField, BorderLayout.CENTER);
        panel.add(pathPanel, BorderLayout.NORTH);

        // 创建复选框
        executeInScriptDirCheckBox = new JCheckBox("在脚本所在目录执行", true);
        executeInScriptDirCheckBox.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));
        panel.add(executeInScriptDirCheckBox, BorderLayout.CENTER);

        if (getExecuteInfo() != null) {
            ScriptParameter scriptParameter = JSONUtils.fromJSON(getExecuteInfo().getExecuteExtJSON(), ScriptParameter.class);
            pathField.setText(scriptParameter.getValue());
            executeInScriptDirCheckBox.setSelected(scriptParameter.isExecuteInScriptDir());
        }
        return panel;
    }

    @Override
    protected void doOKAction() {
        super.doOKAction();
    }

    public String getPath() {
        return pathField.getText().trim();
    }

    public boolean isExecuteInScriptDir() {
        return executeInScriptDirCheckBox.isSelected();
    }
}
