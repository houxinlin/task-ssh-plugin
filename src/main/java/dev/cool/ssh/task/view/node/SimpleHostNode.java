package dev.cool.ssh.task.view.node;

import dev.cool.ssh.task.common.Icons;
import dev.cool.ssh.task.model.HostInfo;

import javax.swing.*;

public class SimpleHostNode extends HostNode {
    private final HostInfo hostInfo;

    public SimpleHostNode(HostInfo hostInfo) {
        super(hostInfo);
        this.hostInfo = hostInfo;
    }

    public HostInfo getHostInfo() {
        return hostInfo;
    }

    @Override
    public Icon getIcon() {
        return Icons.Host;
    }
}
