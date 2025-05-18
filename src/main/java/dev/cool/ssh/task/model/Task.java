package dev.cool.ssh.task.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Task implements Serializable {
    @java.io.Serial
    private static final long serialVersionUID = 1L;
    private String taskName = "New Task";
    private List<HostInfo> hostInfos;

    public String getTaskName() {
        return taskName;
    }

    public void setTaskName(String taskName) {
        this.taskName = taskName;
    }

    public List<HostInfo> getHosts() {
        if (hostInfos == null) hostInfos = new ArrayList<>();
        return hostInfos;
    }

    public void setHosts(List<HostInfo> hostInfos) {
        this.hostInfos = hostInfos;
    }
}
