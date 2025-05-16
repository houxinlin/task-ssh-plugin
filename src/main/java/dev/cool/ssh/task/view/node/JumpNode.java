package dev.cool.ssh.task.view.node;

import dev.cool.ssh.task.model.HostInfo;

import javax.swing.tree.DefaultMutableTreeNode;

public class JumpNode extends DefaultMutableTreeNode {
    private HostInfo hostInfo;

    public JumpNode(HostInfo hostInfo) {
        super(hostInfo);
        this.hostInfo = hostInfo;
    }

    @Override
    public String toString() {
        return ((HostInfo) getUserObject()).getHost();
    }

    public HostInfo getHostInfo() {
        return hostInfo;
    }
}
