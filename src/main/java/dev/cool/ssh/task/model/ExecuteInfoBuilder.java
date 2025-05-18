package dev.cool.ssh.task.model;

import com.google.gson.Gson;

public class ExecuteInfoBuilder {
    private final ExecuteInfo executeInfo;

    public ExecuteInfoBuilder() {
        this.executeInfo = new ExecuteInfo();
    }

    public ExecuteInfoBuilder sort(int sort) {
        executeInfo.setSort(sort);
        return this;
    }

    public ExecuteInfoBuilder executeType(int executeType) {
        executeInfo.setExecuteType(executeType);
        return this;
    }

    public ExecuteInfoBuilder executeExtJSON(Object executeExtJSON) {
        executeInfo.setExecuteExtJSON(new Gson().toJson(executeExtJSON));
        return this;
    }

    public ExecuteInfoBuilder executeName(String executeName) {
        executeInfo.setExecuteName(executeName);
        return this;
    }

    public ExecuteInfo build() {
        return executeInfo;
    }
}
