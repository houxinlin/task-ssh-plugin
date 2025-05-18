package dev.cool.ssh.task.exec;

public enum ExecType {
    UPLOAD(1),COMMAND(2),SCRIPT(3),KILL_JAR(4);
    private int execType;

    ExecType(int execType) {
        this.execType = execType;
    }

    public int getExecType() {
        return execType;
    }

    public void setExecType(int execType) {
        this.execType = execType;
    }
}
