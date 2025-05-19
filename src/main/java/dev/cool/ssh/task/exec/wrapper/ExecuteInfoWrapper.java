package dev.cool.ssh.task.exec.wrapper;

import dev.cool.ssh.task.model.ExecuteInfo;
import dev.cool.ssh.task.view.node.ExecutionNode;

public class ExecuteInfoWrapper {
    private final ExecutionNode node;
    private final ExecuteInfo executeInfo;

    public ExecuteInfoWrapper(ExecutionNode node, ExecuteInfo executeInfo) {
        this.node = node;
        this.executeInfo = executeInfo;
    }

    public ExecuteInfo getExecuteInfo() {
        return executeInfo;
    }

    public ExecutionNode getNode() {
        return node;
    }


    public int getExecuteType() {
        return executeInfo.getExecuteType();
    }


    public int getSort() {
        return executeInfo.getSort();
    }


    public String getExecuteExtJSON() {
        return executeInfo.getExecuteExtJSON();
    }


    public String getExecuteName() {
        return executeInfo.getExecuteName();
    }
}