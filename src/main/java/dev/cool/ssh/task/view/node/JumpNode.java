package dev.cool.ssh.task.view.node;

import dev.cool.ssh.task.common.Icons;
import dev.cool.ssh.task.model.HostInfo;
import dev.cool.ssh.task.model.JumpServerHostInfo;
import dev.cool.ssh.task.utils.JSONUtils;

import javax.swing.*;

public class JumpNode extends HostNode {
    private final HostInfo hostInfo;
    private final JumpServerHostInfo jumpServerHostInfo;

    public JumpNode(HostInfo hostInfo) {
        super(hostInfo);
        this.hostInfo = hostInfo;
        this.jumpServerHostInfo = JSONUtils.fromJSON(hostInfo.getHostExtJSON(), JumpServerHostInfo.class);
    }

    @Override
    public String toString() {
        return super.toString() + " id:" + jumpServerHostInfo.getIp() + " user:" + jumpServerHostInfo.getUserId();
    }

    public HostInfo getHostInfo() {
        return hostInfo;
    }

    @Override
    public Icon getIcon() {
        return Icons.JumpServer;
    }
}
