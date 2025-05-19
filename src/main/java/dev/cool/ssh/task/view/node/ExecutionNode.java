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
        String progress = doGetProgressText();
        StringBuilder sb = new StringBuilder();
        if (progress != null && !progress.isEmpty()) {
            sb.append(progress);
        }
        if (tryCount >= 1) {
            if (!sb.isEmpty()) sb.append(" ");
            sb.append("try...").append(tryCount);
        }
        return sb.toString();
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
