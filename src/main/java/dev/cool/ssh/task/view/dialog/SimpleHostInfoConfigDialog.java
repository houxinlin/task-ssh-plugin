package dev.cool.ssh.task.view.dialog;

import com.intellij.openapi.project.Project;
import dev.cool.ssh.task.model.HostInfo;
import org.jetbrains.annotations.Nullable;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JLabel;
import java.awt.*;

public class SimpleHostInfoConfigDialog extends BaseSSHConfigDialog {

    public SimpleHostInfoConfigDialog(@Nullable Project project, HostInfo hostInfo) {
        super(project, hostInfo);
        setSize(400, 300);
        init();
    }

    public SimpleHostInfoConfigDialog(@Nullable Project project) {
        this(project, null);
    }

    @Override
    protected int getHostType() {
        return 1;
    }

    @Override
    protected void fillExtendFields(HostInfo hostInfo) {
    }

    @Override
    protected void updateExtendFields(HostInfo hostInfo) {
    }

    @Override
    protected JComponent createCenterPanel() {
        JPanel panel = (JPanel) super.createCenterPanel();
        GridBagConstraints gbc = ((GridBagLayout) panel.getLayout()).getConstraints(panel.getComponent(0));
        gbc.gridy = panel.getComponentCount();
        
        addTestConnectionLabel(panel, gbc);
        
        return panel;
    }
}
