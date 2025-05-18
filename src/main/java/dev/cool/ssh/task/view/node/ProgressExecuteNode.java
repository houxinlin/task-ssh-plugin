package dev.cool.ssh.task.view.node;

import com.intellij.ui.treeStructure.Tree;
import dev.cool.ssh.task.exec.State;
import dev.cool.ssh.task.model.ExecuteInfo;

public class ProgressExecuteNode extends ExecutionNode {
    private int progress = 0;

    public ProgressExecuteNode(ExecuteInfo executeInfo, Tree tree) {
        super(executeInfo, tree);
    }

    public int getProgress() {
        return progress;
    }

    public void setProgress(int progress) {
        this.progress = progress;
        nodeChanged();
    }

    public String doGetProgressText() {
        if (getState() == State.WAITING) return null;
        return "(" + progress + "%)";
    }
}
