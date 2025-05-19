package dev.cool.ssh.task.view.dialog;

import com.intellij.icons.AllIcons;
import com.intellij.ide.util.treeView.NodeRenderer;
import com.intellij.openapi.util.NlsSafe;
import dev.cool.ssh.task.model.FileEntry;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;

public class FileEntryTreeCellRenderer extends NodeRenderer {

    public FileEntryTreeCellRenderer() {
    }

    @Override
    public void customizeCellRenderer(@NotNull JTree tree, @NlsSafe Object value, boolean selected, boolean expanded, boolean leaf, int row, boolean hasFocus) {
        super.customizeCellRenderer(tree, value, selected, expanded, leaf, row, hasFocus);
        if (value instanceof DefaultMutableTreeNode node) {
            FileEntry userObject = (FileEntry) node.getUserObject();
            if (userObject.isDirectory()) {
                setIcon(AllIcons.Actions.ProjectDirectory);
            } else {
                setIcon(AllIcons.Actions.AddFile);
            }
        }
    }
}