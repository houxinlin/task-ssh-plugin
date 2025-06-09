package dev.cool.ssh.task.view.dialog;

import com.intellij.openapi.project.Project;
import dev.cool.ssh.task.model.ExecuteInfo;
import dev.cool.ssh.task.model.KillPortParameter;
import dev.cool.ssh.task.utils.JSONUtils;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;

public class KillPortParameterDialog extends ExecParameterDialogWrapper {
    private JTextField portField;
    private JCheckBox sudoCheckBox;

    public KillPortParameterDialog(@Nullable Project project, ExecuteInfo executeInfo) {
        super(project, executeInfo);
        setTitle("Kill Port");
        init();
    }

    @Override
    protected String buildExtJSON() {
        KillPortParameter parameter = new KillPortParameter();
        parameter.setValue(getPortValue());
        parameter.setSudo(sudoCheckBox.isSelected());
        return JSONUtils.toJSON(parameter);
    }

    @Override
    protected @Nullable JComponent createCenterPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // 添加前缀标签
        JLabel prefixLabel = new JLabel("kill -9 $(lsof -t -i:");
        gbc.gridx = 0;
        gbc.gridy = 0;
        panel.add(prefixLabel, gbc);

        // 添加输入框
        portField = new JTextField(20);
        gbc.gridx = 1;
        gbc.gridy = 0;
        panel.add(portField, gbc);

        // 添加后缀标签
        JLabel suffixLabel = new JLabel(")");
        gbc.gridx = 2;
        gbc.gridy = 0;
        panel.add(suffixLabel, gbc);

        // 添加sudo复选框
        sudoCheckBox = new JCheckBox("sudo");
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 3;
        panel.add(sudoCheckBox, gbc);

        if (getExecuteInfo() != null) {
            KillPortParameter parameter = JSONUtils.fromJSON(getExecuteInfo().getExecuteExtJSON(), KillPortParameter.class);
            if (parameter != null) {
                portField.setText(parameter.getValue());
                sudoCheckBox.setSelected(parameter.isSudo());
            }
        }
        return panel;
    }

    @Override
    protected void doOKAction() {
        super.doOKAction();
        ExecuteInfo executeInfo = getExecuteInfo();
        if (executeInfo != null) {
            executeInfo.setExecuteName("终止端口 " + getPortValue());
        }
    }

    public String getPortValue() {
        return portField.getText().trim();
    }
}
