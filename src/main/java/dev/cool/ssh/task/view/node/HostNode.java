package dev.cool.ssh.task.view.node;

import dev.cool.ssh.task.model.HostInfo;

import javax.swing.*;

public class HostNode extends TaskNode {
    public HostNode(Object userObject) {
        super(userObject);
    }

    @Override
    public String toString() {
        return ((HostInfo) getUserObject()).getHost();
    }

    @Override
    public Icon getIcon() {
        return null;
    }
}
