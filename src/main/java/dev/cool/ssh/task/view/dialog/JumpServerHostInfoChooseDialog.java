package dev.cool.ssh.task.view.dialog;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.openapi.util.Disposer;
import com.intellij.ui.components.JBScrollPane;
import dev.cool.ssh.task.model.HostInfo;
import dev.cool.ssh.task.ssh.JumpServer;
import dev.cool.ssh.task.ssh.JumpServerHostFinder;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class JumpServerHostInfoChooseDialog extends DialogWrapper implements JumpServerHostFinder {
    private final JTable table;
    private String selectedHostName;

    public JumpServerHostInfoChooseDialog(@Nullable Project project,
                                          HostInfo hostInfo) {
        super(project);
        setTitle("选择跳板机主机");
        String[] columnNames = {"id", "主机名", "ip", "备注"};

        DefaultTableModel model = new DefaultTableModel(null, columnNames) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        table = new JTable(model);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setRowSelectionAllowed(true);
        table.setFillsViewportHeight(true);
        table.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int selectedRow = table.getSelectedRow();
                if (selectedRow != -1) {
                    selectedHostName = table.getValueAt(selectedRow, 0).toString();
                }
                close(OK_EXIT_CODE);
            }
        });
        table.setRowHeight(32);
        init();
        JumpServer jumpServer = new JumpServer(hostInfo);
        jumpServer.beginListHost(this);
        Disposer.register(getDisposable(), jumpServer);
    }

    @Override
    public void findJumpServerHost(String id, String host, String ip, String remark) {
        SwingUtilities.invokeLater(() -> {
            ((DefaultTableModel) table.getModel()).addRow(new Object[]{id, host, ip, remark});
            ((DefaultTableModel) table.getModel()).fireTableDataChanged();
        });
    }

    @Override
    protected @Nullable JComponent createCenterPanel() {
        return new JBScrollPane(table);
    }

    @Override
    protected JComponent createSouthPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton cancelButton = new JButton("取消");
        JButton okButton = new JButton("确定");
        cancelButton.addActionListener(e -> doCancelAction());
        okButton.addActionListener(e -> {
            int selectedRow = table.getSelectedRow();
            if (selectedRow != -1) {
                selectedHostName = table.getValueAt(selectedRow, 0).toString();
            }
            close(OK_EXIT_CODE);
        });
        panel.add(cancelButton);
        panel.add(okButton);
        return panel;
    }

    public String getSelectedHostName() {
        return selectedHostName;
    }
}
