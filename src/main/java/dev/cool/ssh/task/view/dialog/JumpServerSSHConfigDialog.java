package dev.cool.ssh.task.view.dialog;

import com.google.gson.Gson;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.openapi.ui.TextFieldWithBrowseButton;
import com.intellij.util.ui.JBUI;
import dev.cool.ssh.task.model.HostInfo;
import dev.cool.ssh.task.model.JumpServerHostInfo;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;

public class JumpServerSSHConfigDialog extends DialogWrapper {
    private JTextField hostField;
    private JTextField usernameField;
    private JPasswordField passwordField;
    private TextFieldWithBrowseButton assetIpField;
    private JTextField userIdField;
    private final Project project;

    public JumpServerSSHConfigDialog(@Nullable Project project) {
        super(project);
        this.project = project;
        init();
        setSize(400, 300);
        setTitle("SSH Config");
    }

    public HostInfo buildHost() {
        HostInfo hostInfo = new HostInfo();
        hostInfo.setHost(hostField.getText());
        hostInfo.setUsername(usernameField.getText());
        hostInfo.setPassword(new String(passwordField.getPassword()));
        hostInfo.setHostType(2);
        JumpServerHostInfo jumpServerHostInfo = new JumpServerHostInfo();
        jumpServerHostInfo.setIp(assetIpField.getText());
        jumpServerHostInfo.setUserId(userIdField.getText());
        hostInfo.setHostExtJSON(new Gson().toJson(jumpServerHostInfo));
        return hostInfo;
    }

    @Override
    protected @Nullable JComponent createCenterPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = JBUI.insets(4, 8);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 0;
        gbc.gridy = 0;
        panel.add(new JLabel("Host:"), gbc);
        gbc.gridy++;
        panel.add(new JLabel("Username:"), gbc);
        gbc.gridy++;
        panel.add(new JLabel("Authentication type:"), gbc);
        gbc.gridy++;
        panel.add(new JLabel("Password:"), gbc);
        gbc.gridy++;
        panel.add(new JLabel("资产ip:"), gbc);
        gbc.gridy++;
        panel.add(new JLabel("用户id:"), gbc);

        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        hostField = new JTextField("101.42.129.143");
        panel.add(hostField, gbc);
        gbc.gridy++;
        usernameField = new JTextField("houxinlin3219");
        panel.add(usernameField, gbc);
        gbc.gridy++;
        JComboBox<String> authTypeCombo = new JComboBox<>(new String[]{"Password", "Public Key"});
        panel.add(authTypeCombo, gbc);
        gbc.gridy++;
        passwordField = new JPasswordField("Hxl495594@@");
        panel.add(passwordField, gbc);
        gbc.gridy++;
        assetIpField = new TextFieldWithBrowseButton();
        panel.add(assetIpField, gbc);
        gbc.gridy++;
        userIdField = new JTextField();
        panel.add(userIdField, gbc);

        assetIpField.addActionListener(e -> {
            JumpServerHostInfoChooseDialog jumpServerHostInfoChooseDialog = new JumpServerHostInfoChooseDialog(project, hostField.getText(), usernameField.getText(), new String(passwordField.getPassword()));
            jumpServerHostInfoChooseDialog.show();
            String selectedHostName = jumpServerHostInfoChooseDialog.getSelectedHostName();
            if (selectedHostName != null) {
                assetIpField.setText(selectedHostName);
            }
        });
        return panel;
    }
}
