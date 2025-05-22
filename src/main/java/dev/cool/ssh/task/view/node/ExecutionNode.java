package dev.cool.ssh.task.view.node;

import com.intellij.ui.treeStructure.Tree;
import com.jcraft.jsch.Channel;
import dev.cool.ssh.task.model.ExecuteInfo;

public abstract class ExecutionNode extends TreeStateNode {
    private final ExecuteInfo executeInfo;
    public int tryCount;
    private Channel channel;
    private boolean cancelled;

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

    public boolean isCancelled() {
        return cancelled;
    }

    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }

    public Channel getChannel() {
        return channel;
    }

    public void setChannel(Channel channel) {
        this.channel = channel;
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
