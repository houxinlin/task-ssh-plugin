package dev.cool.ssh.task.exec.ssh;

import com.google.gson.Gson;
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

public class JumpServerSSH extends BasicSSH implements ISSH {
    public JumpServerSSH(HostInfo hostInfo) {
        super(hostInfo);
    }

    private ByteArrayOutputStream waitSymbol(String symbol, InputStream inputStream) throws IOException {
        System.out.println("Waiting symbol: " + symbol);
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        byte[] bytes = new byte[2028];
        while (!stream.toString().contains(symbol)) {
            int read = inputStream.read(bytes);
            stream.write(bytes, 0, read);
        }
        return stream;
    }

    private void writeSymbol(String symbol, OutputStream outputStream) throws IOException {
        outputStream.write(symbol.getBytes());
        outputStream.flush();
    }

    @Override
    public void execute(ExecuteInfoWrapper executeInfo) throws Exception {
//        String host = getHostInfo().getHost();
//        int port = 22;
//        if (host.contains(":")) {
//            port = Integer.parseInt(host.split(":")[1]);
//            host = host.split(":")[0];
//        }
//        ChannelShell channel = null;
//        try {
//            channel = JschFactory.openChannel(host, port, getHostInfo().getUsername(), getHostInfo().getPassword());
//
//            OutputStream outputStream = channel.getOutputStream();
//            InputStream inputStream = channel.getInputStream();
//            JumpServerHostInfo jumpServerHostInfo = new Gson().fromJson(getHostInfo().getHostExtJSON(), JumpServerHostInfo.class);
//            waitSymbol("Opt> ", inputStream);
//            writeSymbol("p\r", outputStream);
//
//            waitSymbol("[Host]> ", inputStream);
//            writeSymbol(jumpServerHostInfo.getIp() + "\r", outputStream);
//
//            waitSymbol("ID> ", inputStream);
//            writeSymbol(jumpServerHostInfo.getUserId() + "\r", outputStream);
//
//            waitSymbol("$", inputStream);
//
//            OutputStream outputStream1 = channel.getOutputStream();
//            outputStream1.write("ls\r".getBytes());
//            outputStream1.flush();
//
//            InputStream stream = channel.getInputStream();
//            try {
//                byte[] buffer = new byte[2048];
//                while (true) {
//                    int read = stream.read(buffer);
//                    if (read == -1) {
//                    }
//                    String data = new String(buffer, 0, read);
//                    System.out.println(data);
//
//                }
//
//            } finally {
//                System.out.println("推出");
//            }
//
//        } catch (Exception e) {
//            System.err.println("SSH error: " + e.getMessage());
//            e.printStackTrace();
//        } finally {
//            if (channel != null) channel.disconnect();
//        }
        ChannelShell channel = null;
        try {
            channel = JschFactory.openChannel(getHostInfo());
            OutputStream outputStream = channel.getOutputStream();
            InputStream inputStream = channel.getInputStream();
            JumpServerHostInfo jumpServerHostInfo = new Gson().fromJson(getHostInfo().getHostExtJSON(), JumpServerHostInfo.class);
            waitSymbol("Opt> ", inputStream);
            writeSymbol("p\r", outputStream);

            waitSymbol("[Host]> ", inputStream);
            writeSymbol(jumpServerHostInfo.getIp() + "\r", outputStream);

            waitSymbol("ID> ", inputStream);
            writeSymbol(jumpServerHostInfo.getUserId() + "\r", outputStream);

            ByteArrayOutputStream promptByteArray = waitSymbol("$", inputStream);
            byte[] byteArray = promptByteArray.toByteArray();
            StringBuilder prompt = new StringBuilder();
            for (int i = byteArray.length - 1; i >= 0; i--) {
                if (!Character.isWhitespace(byteArray[i]) || byteArray[i] == 32) {
                    prompt.append((char) byteArray[i]);
                } else {
                    break;
                }
            }
            Thread.sleep(500);
            ExecContext execContext = new ExecContext(prompt.reverse().toString(), inputStream, outputStream);
            execContext.setHostInfo(getHostInfo());
            execContext.setExecuteInfoWrapper(executeInfo);
            TaskFactory.getTask(executeInfo, this).execute(execContext);

        } finally {
            if (channel != null) {
                channel.disconnect();
            }
        }
    }
}
