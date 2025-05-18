package dev.cool.ssh.task.model;

public interface ConnectionTestCallback {
    void onSuccess();
    void onError(String errorMessage);
} 