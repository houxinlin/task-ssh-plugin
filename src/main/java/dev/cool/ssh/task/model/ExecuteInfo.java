package dev.cool.ssh.task.model;

public class ExecuteInfo {
    private int sort;
    private int executeType;
    private String executeExtJSON;
    private String executeName;

    public int getSort() {
        return sort;
    }

    public void setSort(int sort) {
        this.sort = sort;
    }

    public int getExecuteType() {
        return executeType;
    }

    public void setExecuteType(int executeType) {
        this.executeType = executeType;
    }

    public String getExecuteExtJSON() {
        return executeExtJSON;
    }

    public void setExecuteExtJSON(String executeExtJSON) {
        this.executeExtJSON = executeExtJSON;
    }

    public String getExecuteName() {
        return executeName;
    }

    public void setExecuteName(String executeName) {
        this.executeName = executeName;
    }
}
