package dev.cool.ssh.task.view.node;

import com.intellij.ui.treeStructure.Tree;
import dev.cool.ssh.task.model.ExecuteInfo;

public abstract class ExecutionNode extends TreeStateNode {
    private final ExecuteInfo executeInfo;
    public int tryCount;

    public ExecutionNode(ExecuteInfo executeInfo, Tree tree) {
        super(executeInfo, tree);
        this.executeInfo = executeInfo;
    }

    public abstract String doGetProgressText();

    public String getDurationText() {
        String s = doGetProgressText();
        return s + (tryCount >= 1 ? "try..." + tryCount : "");
    }

    public ExecuteInfo getExecuteInfo() {
        return executeInfo;
    }

    public int getTryCount() {
        return tryCount;
    }

    public void setTryCount(int tryCount) {
        this.tryCount = tryCount;
    }

    @Override
    public String toString() {
        return executeInfo.getExecuteName();
    }
}
