package dev.cool.ssh.task.exec.wrapper;

import dev.cool.ssh.task.model.ExecuteInfo;

import javax.swing.tree.DefaultMutableTreeNode;

public class ExecuteInfoWrapper extends ExecuteInfo {
    private final DefaultMutableTreeNode node;
    private ExecuteInfo executeInfo;

    public ExecuteInfoWrapper(DefaultMutableTreeNode node, ExecuteInfo executeInfo) {
        this.node = node;
        this.executeInfo = executeInfo;
    }

    public DefaultMutableTreeNode getNode() {
        return node;
    }

    @Override
    public int getExecuteType() {
        return executeInfo.getExecuteType();
    }

    @Override
    public int getSort() {
        return executeInfo.getSort();
    }

    @Override
    public String getExecuteExtJSON() {
        return executeInfo.getExecuteExtJSON();
    }

    @Override
    public String getExecuteName() {
        return executeInfo.getExecuteName();
    }
}