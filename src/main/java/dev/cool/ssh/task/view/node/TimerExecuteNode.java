package dev.cool.ssh.task.view.node;

import com.intellij.ui.treeStructure.Tree;
import dev.cool.ssh.task.exec.State;
import dev.cool.ssh.task.model.ExecuteInfo;

public class TimerExecuteNode extends ExecutionNode {
    private long beginTime = 0;
    private long endTime = 0;

    public TimerExecuteNode(ExecuteInfo executeInfo, Tree tree) {
        super(executeInfo, tree);
    }

    public String doGetProgressText() {
        if (beginTime == 0) return null;
        long duration = endTime != 0 ? endTime - beginTime : System.currentTimeMillis() - beginTime;
        
        long totalSeconds = duration / 1000;
        long hours = totalSeconds / 3600;
        long minutes = (totalSeconds % 3600) / 60;
        long seconds = totalSeconds % 60;

        StringBuilder sb = new StringBuilder();
        if (hours > 0) sb.append(hours).append(" hr ");
        if (minutes > 0) sb.append(minutes).append(" min ");
        if (seconds > 0 || sb.isEmpty()) sb.append(seconds).append(" sec");

        return sb.toString().trim();
    }

    @Override
    public void setState(State state) {
        if (state == State.RUNNING) {
            beginTime = System.currentTimeMillis();
            endTime = 0;
        }
        if (state == State.FINISHED || state == State.FAILED) {
            endTime = System.currentTimeMillis();
        }
        super.setState(state);
    }

    public long getBeginTime() {
        return beginTime;
    }

    public void setBeginTime(long beginTime) {
        this.beginTime = beginTime;
    }

    public long getEndTime() {
        return endTime;
    }

    public void setEndTime(long endTime) {
        this.endTime = endTime;
    }
}
