package dev.cool.ssh.task.exec.task;

import dev.cool.ssh.task.exec.ExecContext;
import dev.cool.ssh.task.exec.ITask;
import dev.cool.ssh.task.exec.wrapper.ExecuteInfoWrapper;

import java.io.ByteArrayOutputStream;

public abstract class BasicTask implements ITask {
    private final ExecuteInfoWrapper executeInfoWrapper;

    public BasicTask(ExecuteInfoWrapper executeInfoWrapper) {
        this.executeInfoWrapper = executeInfoWrapper;
    }

    public abstract void doExecute(ExecContext execContext) throws Exception;

    @Override
    public void execute(ExecContext execContext) throws Exception {
        doExecute(execContext);
    }

    public ExecuteInfoWrapper getExecuteInfoWrapper() {
        return executeInfoWrapper;
    }

    public ByteArrayOutputStream waitUntilFinished(ExecContext execContext) throws Exception {
        byte[] buffer = new byte[2048];
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        while (true) {
            int read = execContext.getInputStream().read(buffer);
            if (read == -1) break;
            byteArrayOutputStream.write(buffer, 0, read);
            String data = new String(buffer, 0, read);
            if (data.contains(execContext.getSshPrompt())) break;
        }
        System.out.println(byteArrayOutputStream.toString());
        return byteArrayOutputStream;
    }
}
