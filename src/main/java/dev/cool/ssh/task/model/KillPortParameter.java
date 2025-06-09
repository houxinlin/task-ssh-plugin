package dev.cool.ssh.task.model;

public class KillPortParameter extends SimpleParameter {
    private boolean isSudo;

    public boolean isSudo() {
        return isSudo;
    }

    public void setSudo(boolean sudo) {
        isSudo = sudo;
    }
}
