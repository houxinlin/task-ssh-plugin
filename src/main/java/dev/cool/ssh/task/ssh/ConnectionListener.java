package dev.cool.ssh.task.ssh;

public interface ConnectionListener {
    public void connectionSuccess();

    public void connectionFailed();
}