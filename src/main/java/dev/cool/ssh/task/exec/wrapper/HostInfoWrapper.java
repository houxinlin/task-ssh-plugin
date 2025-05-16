package dev.cool.ssh.task.exec.wrapper;

import dev.cool.ssh.task.model.HostInfo;
import dev.cool.ssh.task.view.node.JumpNode;

import javax.swing.tree.DefaultMutableTreeNode;
import java.util.List;

public class HostInfoWrapper extends HostInfo {
    private DefaultMutableTreeNode defaultMutableTreeNode;
    private List<ExecuteInfoWrapper> executeInfos;
    private HostInfo hostInfo;

    public HostInfoWrapper(DefaultMutableTreeNode defaultMutableTreeNode, List<ExecuteInfoWrapper> executeInfos) {
        this.defaultMutableTreeNode = defaultMutableTreeNode;
        this.executeInfos = executeInfos;
        this.hostInfo = ((JumpNode) defaultMutableTreeNode).getHostInfo();
    }

    @Override
    public int getSort() {
        return hostInfo.getSort();
    }

    @Override
    public String getHost() {
        return hostInfo.getHost();
    }

    @Override
    public int getHostType() {
        return hostInfo.getHostType();
    }

    @Override
    public String getHostExtJSON() {
        return hostInfo.getHostExtJSON();
    }

    @Override
    public int getPort() {
        return hostInfo.getPort();
    }

    @Override
    public String getUsername() {
        return hostInfo.getUsername();
    }

    @Override
    public String getPassword() {
        return hostInfo.getPassword();
    }

    public DefaultMutableTreeNode getDefaultMutableTreeNode() {
        return defaultMutableTreeNode;
    }

    public void setDefaultMutableTreeNode(DefaultMutableTreeNode defaultMutableTreeNode) {
        this.defaultMutableTreeNode = defaultMutableTreeNode;
    }

    public List<ExecuteInfoWrapper> getExecuteInfos() {
        return executeInfos;
    }

    public void setExecuteInfos(List<ExecuteInfoWrapper> executeInfos) {
        this.executeInfos = executeInfos;
    }
}
