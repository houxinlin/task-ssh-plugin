package dev.cool.ssh.task.model;

public class ScriptParameter  extends SimpleParameter{
    private boolean executeInScriptDir;

    public boolean isExecuteInScriptDir() {
        return executeInScriptDir;
    }

    public void setExecuteInScriptDir(boolean executeInScriptDir) {
        this.executeInScriptDir = executeInScriptDir;
    }
}
