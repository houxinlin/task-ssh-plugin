package dev.cool.ssh.task.view.dialog;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.openapi.util.Disposer;
import com.intellij.ui.components.JBScrollPane;
import com.intellij.ui.treeStructure.SimpleTree;
import dev.cool.ssh.task.model.FileEntry;
import dev.cool.ssh.task.model.HostInfo;
import dev.cool.ssh.task.ssh.ConnectionListener;
import dev.cool.ssh.task.ssh.FileSystemManager;
import dev.cool.ssh.task.ssh.JumpServerFileManager;
import dev.cool.ssh.task.ssh.SimpleFileSystemManager;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import java.util.List;

public class LinuxDirectoryChooseDialog extends DialogWrapper implements ConnectionListener {
    private final FileSystemManager fileSystemManager;
    private SimpleTree directoryTree;
    private DefaultTreeModel treeModel;
    private DefaultMutableTreeNode rootNode;

    public LinuxDirectoryChooseDialog(@Nullable Project project, HostInfo hostInfo) {
        super(project);
        fileSystemManager = hostInfo.getHostType() == 2 ?
                new JumpServerFileManager(hostInfo) : new SimpleFileSystemManager(hostInfo);
        setSize(400, 600);
        setTitle("Choose Linux Directory");
        initTree();
        init();
        Disposer.register(getDisposable(), fileSystemManager);
        fileSystemManager.connectFileSystem(this);
    }

    private void initTree() {
        rootNode = new DefaultMutableTreeNode(new FileEntry("Loading...", true, "", ""));
        treeModel = new DefaultTreeModel(rootNode);
        directoryTree = new SimpleTree(treeModel);
        directoryTree.setCellRenderer(new FileEntryTreeCellRenderer());

        directoryTree.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                if (evt.getClickCount() == 2) {
                    DefaultMutableTreeNode node = (DefaultMutableTreeNode)
                            directoryTree.getLastSelectedPathComponent();
                    if (node == null) return;

                    FileEntry fileEntry = (FileEntry) node.getUserObject();
                    if (fileEntry.isDirectory()) {
                        loadDirectory(node, getFullPath(node));
                    }
                }
            }
        });
    }

    private String getFullPath(DefaultMutableTreeNode node) {
        StringBuilder path = new StringBuilder();
        Object[] nodes = node.getPath();
        for (Object obj : nodes) {
            DefaultMutableTreeNode treeNode = (DefaultMutableTreeNode) obj;
            FileEntry entry = (FileEntry) treeNode.getUserObject();
            if (entry.getFileName().equals("/")) {
                path.append("/");
            } else if (!entry.getFileName().equals("Loading...")) {
                if (!path.toString().endsWith("/")) {
                    path.append("/");
                }
                path.append(entry.getFileName());
            }
        }
        return path.toString();
    }

    private void loadDirectory(DefaultMutableTreeNode parentNode, String path) {
        parentNode.removeAllChildren();
        DefaultMutableTreeNode loadingNode = new DefaultMutableTreeNode(
                new FileEntry("Loading...", true, "", ""));
        treeModel.insertNodeInto(loadingNode, parentNode, 0);
        treeModel.reload(parentNode);

        com.intellij.openapi.application.ApplicationManager.getApplication().executeOnPooledThread(() -> {
            List<FileEntry> files = fileSystemManager.listFile(path);
            if (files != null) {
                SwingUtilities.invokeLater(() -> {
                    parentNode.removeAllChildren();
                    for (FileEntry file : files) {
                        if (!file.getFileName().equals(".") && !file.getFileName().equals("..")) {
                            DefaultMutableTreeNode childNode = new DefaultMutableTreeNode(file);
                            treeModel.insertNodeInto(childNode, parentNode, parentNode.getChildCount());
                        }
                    }
                    treeModel.nodeStructureChanged(parentNode);
                });
            }
        });
    }

    @Override
    protected @Nullable JComponent createCenterPanel() {
        return new JBScrollPane(directoryTree);
    }

    @Override
    public void connectionSuccess() {
        SwingUtilities.invokeLater(() -> {
            rootNode.setUserObject(new FileEntry("/", true, "", ""));
            loadDirectory(rootNode, "/");
        });
    }

    @Override
    public void connectionFailed() {
        SwingUtilities.invokeLater(() -> {
            rootNode.setUserObject(new FileEntry("Connection Failed", true, "", ""));
            treeModel.reload();
        });
    }
}
