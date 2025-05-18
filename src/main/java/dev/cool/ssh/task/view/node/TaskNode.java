package dev.cool.ssh.task.view.node;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;

public abstract class TaskNode extends DefaultMutableTreeNode {

    public TaskNode() {
    }

    public TaskNode(Object userObject) {
        super(userObject);
    }

    public TaskNode(Object userObject, boolean allowsChildren) {
        super(userObject, allowsChildren);
    }

    public abstract Icon getIcon();
}
