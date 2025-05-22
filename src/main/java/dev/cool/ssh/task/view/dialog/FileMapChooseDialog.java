package dev.cool.ssh.task.view.dialog;

import com.intellij.icons.AllIcons;
import com.intellij.openapi.fileChooser.FileChooserDescriptorFactory;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.TextFieldWithBrowseButton;
import com.intellij.util.ui.JBUI;
import dev.cool.ssh.task.model.ExecuteInfo;
import dev.cool.ssh.task.model.FileExecuteInfo;
import dev.cool.ssh.task.utils.JSONUtils;
import dev.cool.ssh.task.utils.Utils;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;
import java.io.File;

public class FileMapChooseDialog extends ExecParameterDialogWrapper {
    private TextFieldWithBrowseButton localPathField;
    private TextFieldWithBrowseButton remotePathField;

    private final Project project;


    public FileMapChooseDialog(@Nullable Project project) {
        this(project, null);

    }

    public FileMapChooseDialog(@Nullable Project project, ExecuteInfo executeInfo) {
        super(project, executeInfo);
        this.project = project;
        setSize(750, 200);
        init();
        setTitle("文件映射选择");
    }

    @Override
    protected String buildExtJSON() {
        FileExecuteInfo fileExecuteInfo = new FileExecuteInfo();
        fileExecuteInfo.setLocalPath(getLocalPath());
        fileExecuteInfo.setRemotePath(getRemotePath());
        return JSONUtils.toJSON(fileExecuteInfo);
    }

    @Override
    protected @Nullable JComponent createCenterPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = JBUI.insets(8, 8, 4, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // 本地路径标签
        gbc.gridx = 0;
        gbc.gridy = 0;
        panel.add(new JLabel("本地路径:"), gbc);

        // 本地路径输入框
        gbc.gridy = 1;
        localPathField = new TextFieldWithBrowseButton();
        localPathField.getTextField().setColumns(40);
        localPathField.addBrowseFolderListener("选择本地文件或文件夹", "", project,
                FileChooserDescriptorFactory.createSingleFileOrFolderDescriptor());
        panel.add(localPathField, gbc);

        // 箭头图标
        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.fill = GridBagConstraints.NONE;
        gbc.insets = JBUI.insets(8, 4, 4, 4);
        JLabel arrowLabel = new JLabel(AllIcons.Actions.Forward);
        panel.add(arrowLabel, gbc);

        // 远程路径标签
        gbc.gridx = 2;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = JBUI.insets(8, 8, 4, 8);
        panel.add(new JLabel("远程路径:"), gbc);

        // 远程路径输入框
        gbc.gridy = 1;
        remotePathField = new TextFieldWithBrowseButton(e -> {
            String path = Utils.getPath(true, project, getHostInfo());
            if (path != null) {
                remotePathField.setText(path);
            }
        });
        panel.add(remotePathField, gbc);
        localPathField.setPreferredSize(new Dimension(300, localPathField.getPreferredSize().height));
        remotePathField.setPreferredSize(new Dimension(300, remotePathField.getPreferredSize().height));

        panel.setPreferredSize(new Dimension(700, 120));

        if (getExecuteInfo() != null) {
            FileExecuteInfo fileExecuteInfo = JSONUtils.fromJSON(getExecuteInfo().getExecuteExtJSON(), FileExecuteInfo.class);
            localPathField.setText(fileExecuteInfo.getLocalPath());
            remotePathField.setText(fileExecuteInfo.getRemotePath());
        }
        return panel;
    }

    @Override
    protected void doOKAction() {
        super.doOKAction();
        if (getExecuteInfo() != null) {
            getExecuteInfo().setExecuteName("Upload " + new File(localPathField.getText()).getName());
        }
    }

    @Override
    public JComponent getPreferredFocusedComponent() {
        return localPathField;
    }

    @Override
    protected void init() {
        super.init();
        setSize(750, 200);
    }

    public String getLocalPath() {
        return localPathField.getText();
    }

    public String getRemotePath() {
        return remotePathField.getText();
    }
}
