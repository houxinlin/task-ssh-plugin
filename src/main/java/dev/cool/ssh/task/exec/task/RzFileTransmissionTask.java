package dev.cool.ssh.task.exec.task;

import com.google.gson.Gson;
import com.intellij.openapi.application.ApplicationManager;
import com.jcraft.jsch.Channel;
import dev.cool.ssh.task.common.ExecuteException;
import dev.cool.ssh.task.exec.jump.Transfer;
import dev.cool.ssh.task.exec.wrapper.ExecuteInfoWrapper;
import dev.cool.ssh.task.model.FileExecuteInfo;
import dev.cool.ssh.task.utils.MD5Util;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.LockSupport;

public class RzFileTransmissionTask extends BasicTask implements Transfer.ProgressListener {

    @Override
    public void doExecute(ExecuteInfoWrapper executeInfo, Channel channel) throws Exception {
        AtomicInteger tryCount = new AtomicInteger(3);
        OutputStream outputStream = null;
        InputStream inputStream = null;

        StatusListener statusListener = () -> tryCount.set(-1);
        while (tryCount.intValue() > 0) {
            System.out.println(tryCount.intValue());
            inputStream = channel.getInputStream();
            outputStream = channel.getOutputStream();
            Stream stream = new Stream(inputStream, outputStream);
            Runnable runnable = () -> {
                try {
                    tryCallRzProcess(stream, executeInfo, statusListener);
                    System.out.println("成功");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            };
            ApplicationManager.getApplication().executeOnPooledThread(runnable);
            LockSupport.parkUntil(System.currentTimeMillis() + 2500);
            stream.close();
            tryCount.decrementAndGet();
        }

    }

    private void tryCallRzProcess(Stream stream,
                                  ExecuteInfoWrapper executeInfo,
                                  StatusListener statusListener) throws Exception {
        FileExecuteInfo fileExecuteInfo = new Gson().fromJson(executeInfo.getExecuteExtJSON(), FileExecuteInfo.class);
        String remotePath = fileExecuteInfo.getRemotePath();
        String command = String.format("\rmkdir -p %s && cd %s && rz -b -y\r", remotePath, remotePath);
        stream.getOutputStream().write(command.getBytes(StandardCharsets.UTF_8));
        stream.getOutputStream().flush();
        System.out.println("写写rz");
        boolean b = doExecute(fileExecuteInfo, stream, statusListener);
        if (!b) throw new ExecuteException("文件上传失败");
    }

    private boolean doExecute(FileExecuteInfo executeInfo, Stream stream, StatusListener statusListener) throws Exception {
        InputStream inputStream = stream.getInputStream();
        OutputStream outputStream = stream.getOutputStream();
        byte[] buffer = new byte[4096];
        while (true) {
            int read = inputStream.read(buffer);
            if (read == -1) {
                return false;
            }
            String data = new String(buffer, 0, read);
            System.out.println(data);
            if (data.contains("**\u0018B0100000023be50")) {
                statusListener.rzCallFinished();
                startSzTransfer(outputStream, inputStream, executeInfo.getLocalPath());
                return checkResult(inputStream, outputStream, executeInfo);
            }
        }

    }

    public static String getSecondLine(String input) {
        if (input == null) {
            return null;
        }
        String[] lines = input.split("\\r?\\n");
        return lines.length >= 2 ? lines[1] : null;
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
            if (getCRCount(byteArrayOutputStream.toByteArray()) == 2) {
                String secondLine = getSecondLine(byteArrayOutputStream.toString());
                if (secondLine != null) {
                    return secondLine.contains(fileMD5);
                }

            }
        }
        return false;
    }

    @Override
    public void onProgress(float progress) {
    }

    @Override
    public void onComplete(Exception exception) {
        System.out.println(exception);
    }

    private void startSzTransfer(OutputStream sshOut, InputStream sshIn, String file) throws Exception {
        File file1 = new File(file);
        String name = file1.getName();
        String[] command = {"D:\\app\\lrzsz_0.12.21rc_windows_x86_64\\lrzsz_0.12.21rc_windows_x86_64\\sz.exe", "-b", name};

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

    private static interface StatusListener {
        public void rzCallFinished();
    }

    private static class Stream {
        private InputStream inputStream;
        private OutputStream outputStream;

        public Stream(InputStream inputStream, OutputStream outputStream) {
            this.inputStream = inputStream;
            this.outputStream = outputStream;
        }

        public InputStream getInputStream() {
            return inputStream;
        }

        public void setInputStream(InputStream inputStream) {
            this.inputStream = inputStream;
        }

        public OutputStream getOutputStream() {
            return outputStream;
        }

        public void setOutputStream(OutputStream outputStream) {
            this.outputStream = outputStream;
        }

        public void close() {
            try {
                inputStream.close();
            } catch (Exception ignored) {
            }
            try {
                outputStream.close();
            } catch (Exception ignored) {
            }
        }
    }
}
