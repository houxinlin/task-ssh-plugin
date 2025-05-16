package dev.cool.ssh.task.view.node;

import com.intellij.ui.treeStructure.Tree;
import dev.cool.ssh.task.model.ExecuteInfo;

public class ExecutionNode extends TreeStateNode {
    private final ExecuteInfo executeInfo;

    private long beginTime;

    public ExecutionNode(ExecuteInfo executeInfo, Tree tree) {
        super(executeInfo, tree);
        this.executeInfo = executeInfo;
    }

    public ExecuteInfo getExecuteInfo() {
        return executeInfo;
    }

    @Override
    public String toString() {
        return executeInfo.getExecuteName();
    }
}
