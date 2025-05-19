package dev.cool.ssh.task.exec.ssh;

import com.google.gson.Gson;
import com.intellij.openapi.application.ApplicationManager;
import com.jcraft.jsch.ChannelShell;
import dev.cool.ssh.task.exec.ExecContext;
import dev.cool.ssh.task.exec.ISSH;
import dev.cool.ssh.task.exec.JschFactory;
import dev.cool.ssh.task.exec.factory.TaskFactory;
import dev.cool.ssh.task.exec.wrapper.ExecuteInfoWrapper;
import dev.cool.ssh.task.model.HostInfo;
import dev.cool.ssh.task.model.JumpServerHostInfo;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

public class JumpServerSSH extends BasicSSH implements ISSH {

    public JumpServerSSH(HostInfo hostInfo) {
        super(hostInfo);
    }

    private ByteArrayOutputStream waitSymbol(String symbol, InputStream inputStream) throws IOException {
        System.out.println("Waiting symbol: " + symbol);
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        byte[] buffer = new byte[2048];
        while (!output.toString().contains(symbol)) {
            int len = inputStream.read(buffer);
            if (len == -1) break;
            output.write(buffer, 0, len);
        }
        return output;
    }

    private void writeSymbol(String data, OutputStream outputStream) throws IOException {
        outputStream.write(data.getBytes());
        outputStream.flush();
    }

    private ByteArrayOutputStream executeJumpServerLogin(InputStream input, OutputStream output, JumpServerHostInfo jumpInfo) throws Exception {
        waitSymbol("Opt> ", input);
        Thread.sleep(100);
        writeSymbol("p\r", output);

        waitSymbol("[Host]> ", input);
        Thread.sleep(100);
        writeSymbol(jumpInfo.getIp() + "\r", output);

        waitSymbol("ID> ", input);
        Thread.sleep(100);
        writeSymbol(jumpInfo.getUserId() + "\r", output);

        return waitSymbol("$", input);
    }

    private ByteArrayOutputStream tryConnectAndWait(InputStream input, OutputStream output, JumpServerHostInfo info) {
        try {
            Future<ByteArrayOutputStream> future = ApplicationManager.getApplication().executeOnPooledThread(() -> {
                try {
                    return executeJumpServerLogin(input, output, info);
                } catch (Throwable e) {
                    e.printStackTrace();
                    return null;
                }
            });
            return future.get(2500, TimeUnit.SECONDS);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public void execute(ExecuteInfoWrapper executeInfo) throws Exception {
        ChannelShell channel = null;
        try {
            channel = JschFactory.openChannel(getHostInfo());
            channel.connect(3000);
            OutputStream outputStream = channel.getOutputStream();
            InputStream inputStream = channel.getInputStream();

            JumpServerHostInfo jumpServerHostInfo = new Gson().fromJson(getHostInfo().getHostExtJSON(), JumpServerHostInfo.class);
            ByteArrayOutputStream promptByteArray = null;

            for (int i = 0; i < 3; i++) {
                promptByteArray = tryConnectAndWait(inputStream, outputStream, jumpServerHostInfo);
                if (promptByteArray != null) break;
                System.out.println("重新尝试链接");
                // 重试连接
                inputStream.close();
                outputStream.close();
                channel.disconnect();
                channel.getSession().disconnect();
                channel = JschFactory.openChannel(getHostInfo());
                channel.connect(3000);
                outputStream = channel.getOutputStream();
                inputStream = channel.getInputStream();
            }

            if (promptByteArray == null) throw new IllegalArgumentException("无法连接到服务器");

            // 构造 prompt
            byte[] byteArray = promptByteArray.toByteArray();
            StringBuilder prompt = new StringBuilder();
            for (int i = byteArray.length - 1; i >= 0; i--) {
                if (!Character.isWhitespace(byteArray[i]) || byteArray[i] == 32) {
                    prompt.append((char) byteArray[i]);
                } else {
                    break;
                }
            }

            System.out.println("连接成功");
            Thread.sleep(500);
            ExecContext execContext = new ExecContext(prompt.reverse().toString(), inputStream, outputStream);
            execContext.setHostInfo(getHostInfo());
            execContext.setExecuteInfoWrapper(executeInfo);
            TaskFactory.getTask(executeInfo, this).execute(execContext);
        } finally {
            if (channel != null) {
                channel.disconnect();
                channel.getSession().disconnect();
            }
        }
    }

    private String extractPrompt(ByteArrayOutputStream output) {
        byte[] bytes = output.toByteArray();
        StringBuilder prompt = new StringBuilder();
        for (int i = bytes.length - 1; i >= 0; i--) {
            if (!Character.isWhitespace(bytes[i]) || bytes[i] == 32) {
                prompt.append((char) bytes[i]);
            } else {
                break;
            }
        }
        return prompt.reverse().toString().trim();
    }
}
