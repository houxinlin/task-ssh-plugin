package dev.cool.ssh.task.exec.task;

import com.google.gson.Gson;
import com.intellij.openapi.application.ApplicationManager;
import dev.cool.ssh.task.common.ExecuteException;
import dev.cool.ssh.task.exec.ExecContext;
import dev.cool.ssh.task.exec.jump.Transfer;
import dev.cool.ssh.task.exec.wrapper.ExecuteInfoWrapper;
import dev.cool.ssh.task.model.FileExecuteInfo;
import dev.cool.ssh.task.utils.LrzszUtils;
import dev.cool.ssh.task.utils.MD5Util;
import dev.cool.ssh.task.view.node.ExecutionNode;
import dev.cool.ssh.task.view.node.ProgressExecuteNode;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class RzFileTransmissionTask extends BasicTask implements Transfer.ProgressListener {
    private static final int BEGIN_TRANSFER = -1000;
    private static final Lock lock = new ReentrantLock();
    private static final Condition condition = lock.newCondition();

    public RzFileTransmissionTask(ExecuteInfoWrapper executeInfoWrapper) {
        super(executeInfoWrapper);
    }

    @Override
    public void doExecute(ExecContext execContext) throws Exception {
        ExecuteInfoWrapper executeInfo = getExecuteInfoWrapper();
        StatusListener statusListener = () -> {
            try {
                lock.lock();
                condition.signal();
            } finally {
                lock.unlock();
            }
        };
        Callable<Exception> listenerRz = () -> {
            FileExecuteInfo fileExecuteInfo = new Gson().fromJson(executeInfo.getExecuteExtJSON(), FileExecuteInfo.class);
            try {
                if (!Files.exists(Paths.get(fileExecuteInfo.getLocalPath()))) return null;
                if (fileExecuteInfo.getRemotePath().isEmpty()) return null;
                if (doExecute(fileExecuteInfo, execContext, statusListener)) return null;
                throw new ExecuteException("上传失败");
            } catch (Exception e) {
                return e;
            }
        };
        Future<Exception> exceptionFuture = ApplicationManager.getApplication().executeOnPooledThread(listenerRz);
        tryCallRzProcess(execContext, executeInfo);
        try {
            lock.lock();
            if (condition.await(3, TimeUnit.SECONDS)) {
                Exception exception = exceptionFuture.get();
                if (exception != null) throw exception;
                return;
            }
            throw new ExecuteException("执行RZ超时");
        } finally {
            lock.unlock();
        }

    }

    private void tryCallRzProcess(ExecContext stream,
                                  ExecuteInfoWrapper executeInfo) throws Exception {
        FileExecuteInfo fileExecuteInfo = new Gson().fromJson(executeInfo.getExecuteExtJSON(), FileExecuteInfo.class);
        String remotePath = fileExecuteInfo.getRemotePath();
        String command = String.format("mkdir -p %s && cd %s && rz -b -y\r", remotePath, remotePath);
        stream.getOutputStream().write(command.getBytes(StandardCharsets.UTF_8));
        stream.getOutputStream().flush();
    }

    private boolean doExecute(FileExecuteInfo executeInfo, ExecContext execContext, StatusListener statusListener) throws Exception {
        InputStream inputStream = execContext.getInputStream();
        OutputStream outputStream = execContext.getOutputStream();
        byte[] buffer = new byte[4096];
        while (true) {
            int read = inputStream.read(buffer);
            if (read == -1) {
                return false;
            }
            String data = new String(buffer, 0, read);
            if (data.contains("**\u0018B0100000023be50")) {
                statusListener.rzCallFinished();
                startSzTransfer(outputStream, inputStream, executeInfo.getLocalPath());
                return checkResult(inputStream, outputStream, executeInfo);
            }
        }

    }

    @Override
    public void onProgress(float progress) {
        ExecutionNode node = getExecuteInfoWrapper().getNode();
        if (node instanceof ProgressExecuteNode progressExecuteNode) {
            progressExecuteNode.setProgress((int) (progress * 100f));
        }
    }

    @Override
    public void onComplete(Exception exception) {
    }

    private static int getCRCount(byte[] bytes) {
        int count = 0;
        for (int i = 0; i < bytes.length - 1; i++) {
            if ((bytes[i] & 0xFF) == 0x0D && (bytes[i + 1] & 0xFF) == 0x0A) {
                count++;
                i++;
            }
        }
        return count;
    }

    private boolean checkResult(InputStream inputStream, OutputStream outputStream, FileExecuteInfo fileExecuteInfo) throws Exception {
        byte[] buffer = new byte[4096];
        File file = new File(fileExecuteInfo.getLocalPath());
        String fileMD5 = MD5Util.getFileMD5(file);
        outputStream.write(("md5sum " + file.getName() + "\r").getBytes());
        outputStream.flush();
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        int read;
        while ((read = inputStream.read(buffer)) != -1) {
            byteArrayOutputStream.write(buffer, 0, read);
            if (byteArrayOutputStream.toString().contains(file.getName()) && getCRCount(byteArrayOutputStream.toByteArray()) == 2) {
                return byteArrayOutputStream.toString().contains(fileMD5);
            }
        }
        return false;
    }

    private String[] buildSzCommand(String name) {
        String executePath = LrzszUtils.getExecutePath();
        if (executePath == null) throw new IllegalArgumentException("找不到sz程序，请先安装");
        return new String[]{executePath, "-b", name};
    }

    private void startSzTransfer(OutputStream sshOut, InputStream sshIn, String file) throws Exception {
        File file1 = new File(file);
        String name = file1.getName();
        String[] command = null;
        try {
            command = buildSzCommand(name);
        } catch (Exception e) {
            throw e;
        }
        Process szProcess = new ProcessBuilder().command(command).directory(file1.getParentFile()).start();
        InputStream szIn = szProcess.getInputStream();
        OutputStream szOut = szProcess.getOutputStream();
        Transfer transfer = Transfer.create(szIn, sshOut, file, this);
        ApplicationManager.getApplication().executeOnPooledThread(transfer);

        try {
            byte[] buffer = new byte[1024];
            int read;
            while ((read = sshIn.read(buffer, 0, 1024)) >= 0) {
                szOut.write(buffer, 0, read);
                szOut.flush();
            }
        } catch (Exception ignored) {
        }
    }

    private interface StatusListener {
        public void rzCallFinished();
    }

}
