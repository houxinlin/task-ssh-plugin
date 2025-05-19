package dev.cool.ssh.task.view.dialog;

import com.google.gson.Gson;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.TextFieldWithBrowseButton;
import dev.cool.ssh.task.model.HostInfo;
import dev.cool.ssh.task.model.JumpServerHostInfo;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;

public class JumpServerSSHConfigDialog extends BaseSSHConfigDialog {
    private TextFieldWithBrowseButton assetIpField;
    private JTextField userIdField;

    public JumpServerSSHConfigDialog(@Nullable Project project, HostInfo hostInfo) {
        super(project, hostInfo);
        assetIpField = new TextFieldWithBrowseButton();
        userIdField = new JTextField();

        setSize(400, 300);
        init();

    }

    public JumpServerSSHConfigDialog(@Nullable Project project) {
        this(project, null);
    }

    @Override
    protected int getHostType() {
        return 2;
    }

    @Override
    protected void fillExtendFields(HostInfo hostInfo) {
        JumpServerHostInfo jumpServerHostInfo = new JumpServerHostInfo();
        jumpServerHostInfo.setIp(assetIpField.getText());
        jumpServerHostInfo.setUserId(userIdField.getText());
        hostInfo.setHostExtJSON(new Gson().toJson(jumpServerHostInfo));
    }

    @Override
    protected void updateExtendFields(HostInfo hostInfo) {
        if (hostInfo.getHostExtJSON() != null) {
            JumpServerHostInfo ext = new Gson().fromJson(hostInfo.getHostExtJSON(), JumpServerHostInfo.class);
            if (ext != null) {
                assetIpField.setText(ext.getIp());
                userIdField.setText(ext.getUserId());
            }
        }
    }

    @Override
    protected JComponent createCenterPanel() {
        JPanel panel = (JPanel) super.createCenterPanel();
        GridBagConstraints gbc = ((GridBagLayout) panel.getLayout()).getConstraints(panel.getComponent(0));
        gbc.gridy = panel.getComponentCount();

        // 添加跳板机特有的字段
        panel.add(new JLabel("资产ip:"), gbc);
        gbc.gridy++;
        panel.add(new JLabel("用户id:"), gbc);

        gbc.gridx = 1;
        gbc.gridy = panel.getComponentCount() - 2;
        gbc.weightx = 1.0;

        panel.add(assetIpField, gbc);
        gbc.gridy++;
        panel.add(userIdField, gbc);

        // 默认值
        if (hostInfo == null) {
            hostField.setText("101.42.129.143");
            portField.setText("2222");
            usernameField.setText("houxinlin3219");
            passwordField.setText("Hxl495594@@");
        }

        assetIpField.addActionListener(e -> {
            JumpServerHostInfoChooseDialog jumpServerHostInfoChooseDialog =
                    new JumpServerHostInfoChooseDialog(project, buildHost());
            jumpServerHostInfoChooseDialog.show();
            String selectedHostName = jumpServerHostInfoChooseDialog.getSelectedHostName();
            if (selectedHostName != null) {
                assetIpField.setText(selectedHostName);
            }
        });

        // 添加测试连接标签
        addTestConnectionLabel(panel, gbc);

        return panel;
    }
}
