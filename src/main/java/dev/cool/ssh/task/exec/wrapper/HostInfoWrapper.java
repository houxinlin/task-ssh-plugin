package dev.cool.ssh.task.exec.wrapper;

import dev.cool.ssh.task.model.HostInfo;

import javax.swing.tree.DefaultMutableTreeNode;
import java.util.List;

public class HostInfoWrapper {
    private DefaultMutableTreeNode defaultMutableTreeNode;
    private List<ExecuteInfoWrapper> executeInfos;
    private final HostInfo hostInfo;

    public HostInfoWrapper(DefaultMutableTreeNode defaultMutableTreeNode, List<ExecuteInfoWrapper> executeInfos) {
        this.defaultMutableTreeNode = defaultMutableTreeNode;
        this.executeInfos = executeInfos;
        this.hostInfo = ((HostInfo) defaultMutableTreeNode.getUserObject());
    }


    public int getSort() {
        return hostInfo.getSort();
    }


    public String getHost() {
        return hostInfo.getHost();
    }


    public int getHostType() {
        return hostInfo.getHostType();
    }


    public String getHostExtJSON() {
        return hostInfo.getHostExtJSON();
    }


    public int getPort() {
        return hostInfo.getPort();
    }


    public String getUsername() {
        return hostInfo.getUsername();
    }


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

    public HostInfo getHostInfo() {
        return hostInfo;
    }
}
