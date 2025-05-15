package dev.cool.ssh.task.exec;

import com.jcraft.jsch.*;
import dev.cool.ssh.task.exec.jump.Transfer;
import dev.cool.ssh.task.utils.MD5Util;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.concurrent.CountDownLatch;

public class ShellRzExample {
//    public static void main(String[] args) {
//        String filePath = "D:\\1.txt";
//        String user = "houxinlin3219";
//        String password = "Hxl495594@@"; // Consider environment variables
//        String host = "101.42.129.143";
//        main(host, user, password, filePath, "15", "1", "/home/fyadmin");
//    }

    public static boolean main(String host, String user, String password, String filePath, String index, String order, String uploadPath) {
        int port = 2222;
        File file = new File(filePath);
        String fileMD5 = MD5Util.getFileMD5(file);
        JSch jsch = new JSch();
        Session session = null;
        ChannelShell channel = null;
        try {
            session = jsch.getSession(user, host, port);
            session.setPassword(password);
            session.setConfig("StrictHostKeyChecking", "no");
            session.connect(30000);

            channel = (ChannelShell) session.openChannel("shell");
            ChannelExec channelExec = (ChannelExec) session.openChannel("exec");
            channelExec.setCommand("md5sum " + file.getName());
            channelExec.setErrStream(System.err);


            channel.setPty(true);

            OutputStream outputStream = channel.getOutputStream();
            InputStream inputStream = channel.getInputStream();
            channel.connect();
;
            new Thread(() -> {
                try {
                    Thread.sleep(1000);
                    outputStream.write(("p\r" + index + "\r" + order + "\r").getBytes());
                    outputStream.flush();
                    Thread.sleep(1000);

                    outputStream.write(("cd " + uploadPath + " && rz -b -y\r").getBytes());
                    outputStream.flush();
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }).start();
            byte[] buffer = new byte[2048];
            while (true) {
                int read = inputStream.read(buffer);
                if (read == -1) {
                    return false;
                }
                String data = new String(buffer, 0, read);
                if (data.contains("**\u0018B0100000023be50")) {
                    startSzTransfer(outputStream, inputStream, file.getPath());
                    CountDownLatch countDownLatch = new CountDownLatch(1);
                    try {
                        outputStream.write(("md5sum " + file.getName() + "\r").getBytes());
                        outputStream.flush();

                        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                        while ((read = inputStream.read(buffer)) != -1) {
                            byteArrayOutputStream.write(buffer, 0, read);
                            if (getCRCount(byteArrayOutputStream.toByteArray()) == 2) {
                                String secondLine = getSecondLine(byteArrayOutputStream.toString());
                                channel.disconnect();
                                if (secondLine != null) {
                                    if (secondLine.contains(fileMD5)) {
                                        System.out.println("传输成功" + fileMD5);
                                        return true;
                                    } else {
                                        System.out.println("传输失败");
                                    }
                                }

                            }
                        }
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    } finally {
                        countDownLatch.countDown();
                    }
                    countDownLatch.await();

                }
            }

        } catch (Exception e) {
            System.err.println("SSH error: " + e.getMessage());
            e.printStackTrace();
        } finally {
            if (channel != null) channel.disconnect();
            if (session != null) session.disconnect();
        }
        return false;
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
        Transfer.create(szIn, sshOut, file);

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