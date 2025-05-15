package dev.cool.ssh.task.exec.jump;

import org.jetbrains.annotations.NotNull;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;

public class Transfer implements Runnable {
    public InputStream szSourceInputStream;
    private OutputStream outTo;

    private int sendTotal = 0;
    private ByteArrayOutputStream byteArrayOutputStream;

    private String file;

    private Transfer(InputStream szSourceInputStream, OutputStream outTo, String file) {
        this.szSourceInputStream = szSourceInputStream;
        this.outTo = outTo;
        this.file = file;
    }

    public static void create(InputStream source, OutputStream outTo, String file) {
        Transfer transfer = null;
        transfer = new Transfer(source, outTo, file);
        new Thread(transfer).start();

    }

    @Override
    public void run() {
        try {
            long size = Files.size(Paths.get(file));
            byte[] message = new byte[]{
                    (byte) 0x2A, (byte) 0x18, (byte) 0x43, (byte) 0x0A
            };
            szSourceInputStream.transferTo(new OutputStream() {
                @Override
                public void write(int b) {
                }

                @Override
                public void write(@NotNull byte[] b, int off, int len) throws IOException {
//                    if (len == 2) {
//                        return;
//                    }
                    int start = 0;
                    if (startsWith(b, message) && byteArrayOutputStream == null) {
                        byteArrayOutputStream = new ByteArrayOutputStream();
                        start = 12;
                    }
                    if (byteArrayOutputStream != null && sendTotal != size) {
                        byteArrayOutputStream.write(b, start, len);
                        int chunk = size - sendTotal >= 1024 ? 1024 : (int) (size - sendTotal);
                        byte[] byteArray = byteArrayOutputStream.toByteArray();
                        while (byteArray.length >= chunk + 6 && chunk != 0) {
                            sendTotal += chunk;
                            byteArray = Arrays.copyOfRange(byteArray, chunk + 6, byteArray.length);
                            byteArrayOutputStream.reset();
                            byteArrayOutputStream.write(byteArray);
                            float progress = (((float) sendTotal) / size);
                            chunk = size - sendTotal >= 1024 ? 1024 : (int) (size - sendTotal);
//                            System.out.println(progress);
                            printProgressBar(progress, 100);
                        }
                    }

//                    System.out.println("sz->ssh" + bytesToHex(newByte(b, len)));
                    outTo.write(b, off, len);
                    outTo.flush();
                }
            });
        } catch (Exception ignored) {
            ignored.printStackTrace();
        }
    }

    public static void printProgressBar(float progress, int barWidth) {
        int filled = (int) (progress * barWidth);
        StringBuilder bar = new StringBuilder();
        bar.append('\r').append('[');
        for (int i = 0; i < barWidth; i++) {
            if (i < filled) bar.append('=');
            else if (i == filled) bar.append('>');
            else bar.append(' ');
        }
        bar.append(']').append(String.format(" %.2f%%", progress * 100));
        System.out.print(bar.toString());

        if (progress >= 1.0f) {
            System.out.println(" Done!");
        }
    }

    public static boolean startsWith(byte[] data, byte[] prefix) {
        if (data.length < prefix.length) return false;
        for (int i = 0; i < prefix.length; i++) {
            if (data[i] != prefix[i]) return false;
        }
        return true;
    }
}
