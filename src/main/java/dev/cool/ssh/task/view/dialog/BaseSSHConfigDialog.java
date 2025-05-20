package dev.cool.ssh.task.view.dialog;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.ui.AnimatedIcon;
import com.intellij.util.ui.JBUI;
import dev.cool.ssh.task.model.ConnectionTestCallback;
import dev.cool.ssh.task.model.HostInfo;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;

public abstract class BaseSSHConfigDialog extends DialogWrapper {
    protected JTextField hostField;
    protected JTextField portField;
    protected JTextField usernameField;
    protected JPasswordField passwordField;
    protected final Project project;
    protected JLabel testLabel;
    protected HostInfo hostInfo;

    public BaseSSHConfigDialog(@Nullable Project project, HostInfo hostInfo) {
        super(project);
        this.project = project;
        this.hostInfo = hostInfo;
        setTitle("SSH Config");
    }

    public BaseSSHConfigDialog(@Nullable Project project) {
        this(project, null);
    }

    protected abstract int getHostType();

    protected abstract void fillExtendFields(HostInfo hostInfo);

    protected abstract void updateExtendFields(HostInfo hostInfo);

    public HostInfo buildHost() {
        HostInfo hostInfo = new HostInfo();
        hostInfo.setHost(hostField.getText());
        hostInfo.setPort(Integer.parseInt(portField.getText()));
        hostInfo.setUsername(usernameField.getText());
        hostInfo.setPassword(new String(passwordField.getPassword()));
        hostInfo.setHostType(getHostType());
        fillExtendFields(hostInfo);
        return hostInfo;
    }

    private void testConnection() {
        if (testLabel.getIcon() == AnimatedIcon.Default.INSTANCE) {
            return;
        }

        testLabel.setIcon(AnimatedIcon.Default.INSTANCE);

        HostInfo hostInfo = buildHost();
        hostInfo.testConnection(project, new ConnectionTestCallback() {
            @Override
            public void onSuccess() {
                testLabel.setIcon(null);
            }

            @Override
            public void onError(String errorMessage) {
                testLabel.setIcon(null);
            }
        });
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

        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.weightx = 1.0;

        // 主机和端口
        JPanel hostPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        hostField = new JTextField();
        hostField.setPreferredSize(new Dimension(280, hostField.getPreferredSize().height));
        portField = new JTextField("22");
        portField.setPreferredSize(new Dimension(60, hostField.getPreferredSize().height));
        hostPanel.add(hostField);
        hostPanel.add(new JLabel(":"));
        hostPanel.add(portField);
        panel.add(hostPanel, gbc);

        gbc.gridy++;
        usernameField = new JTextField();
        panel.add(usernameField, gbc);
        gbc.gridy++;
        JComboBox<String> authTypeCombo = new JComboBox<>(new String[]{"Password", "Public Key"});
        panel.add(authTypeCombo, gbc);
        gbc.gridy++;
        passwordField = new JPasswordField();
        panel.add(passwordField, gbc);

        // 填充数据
        if (hostInfo != null) {
            hostField.setText(hostInfo.getHost());
            portField.setText(String.valueOf(hostInfo.getPort()));
            usernameField.setText(hostInfo.getUsername());
            passwordField.setText(hostInfo.getPassword());
            updateExtendFields(hostInfo);
        }

        return panel;
    }

    protected void addTestConnectionLabel(JPanel panel, GridBagConstraints gbc) {
        gbc.gridy++;
        gbc.gridx = 0;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.insets = JBUI.insets(8, 0);
        testLabel = new JLabel("测试连接");
        testLabel.setFont(testLabel.getFont().deriveFont(11f));
        testLabel.setForeground(new Color(0, 120, 215));
        testLabel.setCursor(new Cursor(Cursor.HAND_CURSOR));
        testLabel.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                testConnection();
            }

            @Override
            public void mouseEntered(java.awt.event.MouseEvent e) {
                if (testLabel.getIcon() != AnimatedIcon.Default.INSTANCE) {
                    testLabel.setForeground(new Color(0, 102, 204));
                }
            }

            @Override
            public void mouseExited(java.awt.event.MouseEvent e) {
                if (testLabel.getIcon() != AnimatedIcon.Default.INSTANCE) {
                    testLabel.setForeground(new Color(0, 120, 215));
                }
            }
        });
        panel.add(testLabel, gbc);
    }

    @Override
    protected void doOKAction() {
        if (hostInfo != null) {
            hostInfo.setHost(hostField.getText());
            hostInfo.setPort(Integer.parseInt(portField.getText()));
            hostInfo.setUsername(usernameField.getText());
            hostInfo.setPassword(new String(passwordField.getPassword()));
            hostInfo.setHostType(getHostType());
            fillExtendFields(hostInfo);
        }
        super.doOKAction();
    }
} 