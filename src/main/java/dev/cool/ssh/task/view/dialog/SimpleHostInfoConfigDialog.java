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

public class SimpleHostInfoConfigDialog extends DialogWrapper {
    private JTextField hostField;
    private JTextField portField;
    private JTextField usernameField;
    private JPasswordField passwordField;
    private final Project project;
    private JLabel testLabel;

    public SimpleHostInfoConfigDialog(@Nullable Project project) {
        super(project);
        this.project = project;
        init();
        setSize(400, 200);
        setTitle("SSH Config");
    }

    public HostInfo buildHost() {
        HostInfo hostInfo = new HostInfo();
        hostInfo.setHost(hostField.getText());
        hostInfo.setPort(Integer.parseInt(portField.getText()));
        hostInfo.setUsername(usernameField.getText());
        hostInfo.setPassword(new String(passwordField.getPassword()));
        hostInfo.setHostType(1);
        return hostInfo;
    }

    private void testConnection() {
        if (testLabel.getIcon() == com.intellij.ui.AnimatedIcon.Default.INSTANCE) {
            return;
        }

        testLabel.setIcon(com.intellij.ui.AnimatedIcon.Default.INSTANCE);

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
        
        // 创建主机地址和端口的容器面板
        JPanel hostPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        hostField = new JTextField("localhost");
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

        // 添加测试连接标签
        gbc.gridy++;
        gbc.gridx = 0;
        gbc.gridwidth = 1;
        gbc.anchor = GridBagConstraints.WEST;
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

        return panel;
    }
}
