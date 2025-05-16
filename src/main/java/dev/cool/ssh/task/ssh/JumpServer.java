package dev.cool.ssh.task.ssh;

import com.intellij.openapi.application.ApplicationManager;
import com.jcraft.jsch.ChannelShell;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.CountDownLatch;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class JumpServer {
    public static void main(String[] args) {
        String filePath = "D:\\1.txt";
        String user = "houxinlin3219";
        String password = "Hxl495594@@"; // Consider environment variables
        String host = "101.42.129.143";
//        main(host, user, password, filePath, "15", "1", "/home/fyadmin");
    }

    public void beginListHost(String host, String user, String password, JumpServerHostFinder jumpServerHostFinder) {
        ApplicationManager.getApplication().executeOnPooledThread(() -> {
            doBeginListHost(host, user, password, jumpServerHostFinder);
        });
    }

    private void doBeginListHost(String host, String user, String password, JumpServerHostFinder jumpServerHostFinder) {
        int port = 2222;
        JSch jsch = new JSch();
        Session session = null;
        ChannelShell channel = null;
        try {
            session = jsch.getSession(user, host, port);
            session.setPassword(password);
            session.setConfig("StrictHostKeyChecking", "no");
            session.connect(30000);
            channel = (ChannelShell) session.openChannel("shell");
            channel.setPty(true);
            OutputStream outputStream = channel.getOutputStream();
            InputStream inputStream = channel.getInputStream();
            channel.connect();
            outputStream.write("p\r".getBytes());
            outputStream.flush();
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
            String line;
            Pattern pattern = Pattern.compile("\\s*(\\d+)\\s*\\|\\s*([^|]+?)\\s*\\|\\s*([\\d.]+)\\s*\\|\\s*(.*?)\\s*$");

            while ((line = reader.readLine()) != null) {
                Matcher matcher = pattern.matcher(line);
                if (matcher.matches()) {
                    String id = matcher.group(1);
                    String hostname = matcher.group(2);
                    String ip = matcher.group(3);
                    jumpServerHostFinder.findJumpServerHost(id, hostname, ip, "");
                }
            }

        } catch (Exception ignored) {
        } finally {
            if (channel != null) channel.disconnect();
            if (session != null) session.disconnect();
        }
    }



    private static int getCRCount(byte[] bytes) {
        int count = 0;
        for (int i = 0; i < bytes.length - 1; i++) {
            if ((bytes[i] & 0xFF) == 0x0D && (bytes[i + 1] & 0xFF) == 0x0A) {
                count++;
                i++; // 跳过 0A，避免重复统计
            }
        }
        return count;
    }

    public static boolean startsWith(byte[] data, byte[] prefix) {
        if (data.length < prefix.length) return false;
        for (int i = 0; i < prefix.length; i++) {
            if (data[i] != prefix[i]) return false;
        }
        return true;
    }

    private static void startSzTransfer(OutputStream sshOut, InputStream sshIn, String file) throws Exception {
        File file1 = new File(file);
        String name = file1.getName();

        String[] command = {"D:\\app\\lrzsz_0.12.21rc_windows_x86_64\\lrzsz_0.12.21rc_windows_x86_64\\sz.exe", "-b", name};

        Process exec1 = new ProcessBuilder().command(command).directory(file1.getParentFile()).start();
        InputStream szIn = exec1.getInputStream();
        OutputStream szOut = exec1.getOutputStream();

        CountDownLatch countDownLatch = new CountDownLatch(1);
        new Thread(() -> {
            try {
                byte[] buffer = new byte[1024];
                int read;
                byte[] prefix = new byte[]{
                        0x2A, 0x2A, 0x18, 0x42, 0x30, 0x38
                };
//2A 2A 18 42 30 38 30 30 30 30 30 30 30 30 30 32 32 64 0D 8A
                byte[] end = new byte[]{0x4f, 0x4f};
                while ((read = sshIn.read(buffer, 0, 1024)) >= 0) {
//                    System.out.println("ssh->sz" + bytesToHex(newByte(buffer, read)));
                    szOut.write(buffer, 0, read);
                    szOut.flush();
//                    if (startsWith(buffer, prefix)) {
//                        sshOut.write(end, 0, 2);
//                        sshOut.flush();
//                        return;
//                    }
                }
            } catch (Exception ignored) {
            } finally {
                countDownLatch.countDown();
            }
        }).start();
        countDownLatch.await();
    }


}