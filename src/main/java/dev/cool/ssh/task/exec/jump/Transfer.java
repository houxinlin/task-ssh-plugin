package dev.cool.ssh.task.exec.jump;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;

public class Transfer implements Runnable {
    private static final int DEFAULT_BUFFER_SIZE = 8192;
    public InputStream szSourceInputStream;
    private final OutputStream outTo;
    private int sendTotal = 0;
    private ByteArrayOutputStream byteArrayOutputStream;
    private ProgressListener progressListener;
    private final String file;

    private Transfer(InputStream szSourceInputStream, OutputStream outTo, String file) {
        this.szSourceInputStream = szSourceInputStream;
        this.outTo = outTo;
        this.file = file;
    }

    public static Transfer create(InputStream source, OutputStream outTo, String file, ProgressListener progressListener) {
        Transfer transfer = new Transfer(source, outTo, file);
        transfer.setProgressListener(progressListener);
        return transfer;
    }

    public void setProgressListener(ProgressListener progressListener) {
        this.progressListener = progressListener;
    }

    public void transferTo() throws IOException {
        long size = Files.size(Paths.get(file));
        byte[] message = new byte[]{
                (byte) 0x2A, (byte) 0x18, (byte) 0x43, (byte) 0x0A
        };
        byte[] buffer = new byte[DEFAULT_BUFFER_SIZE];
        int read;
        while ((read = this.szSourceInputStream.read(buffer, 0, DEFAULT_BUFFER_SIZE)) >= 0) {
            int start = 0;
            if (startsWith(buffer, message) && byteArrayOutputStream == null) {
                byteArrayOutputStream = new ByteArrayOutputStream();
                start = 12;
            }
            if (byteArrayOutputStream != null && sendTotal != size) {
                byteArrayOutputStream.write(buffer, start, read);
                int chunk = size - sendTotal >= 1024 ? 1024 : (int) (size - sendTotal);
                byte[] byteArray = byteArrayOutputStream.toByteArray();
                while (byteArray.length >= chunk + 6 && chunk != 0) {
                    sendTotal += chunk;
                    byteArray = Arrays.copyOfRange(byteArray, chunk + 6, byteArray.length);
                    byteArrayOutputStream.reset();
                    byteArrayOutputStream.write(byteArray);
                    float progress = (((float) sendTotal) / size);
                    chunk = size - sendTotal >= 1024 ? 1024 : (int) (size - sendTotal);
                    progressListener.onProgress(progress);
                }
            }
            outTo.write(buffer, 0, read);
            outTo.flush();
        }
        progressListener.onComplete(null);
    }

    @Override
    public void run() {
        try {
            this.transferTo();
        } catch (Exception e) {
            progressListener.onComplete(e);
        }
    }

    public static boolean startsWith(byte[] data, byte[] prefix) {
        if (data.length < prefix.length) return false;
        for (int i = 0; i < prefix.length; i++) {
            if (data[i] != prefix[i]) return false;
        }
        return true;
    }

    public interface ProgressListener {
        void onProgress(float progress);

        void onComplete(Exception exception);
    }
}
