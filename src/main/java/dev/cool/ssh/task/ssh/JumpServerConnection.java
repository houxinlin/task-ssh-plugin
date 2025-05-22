package dev.cool.ssh.task.ssh;

import com.intellij.openapi.application.ApplicationManager;
import dev.cool.ssh.task.model.JumpServerHostInfo;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

public class JumpServerConnection {

    private ByteArrayOutputStream waitSymbol(String symbol, InputStream inputStream) throws IOException {
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

    private String executeJumpServerLogin(InputStream input, OutputStream output, JumpServerHostInfo jumpInfo) throws Exception {
        waitSymbol("Opt> ", input);
        Thread.sleep(100);
        writeSymbol("p\r", output);

        waitSymbol("[Host]> ", input);
        Thread.sleep(100);
        writeSymbol(jumpInfo.getIp() + "\r", output);

        waitSymbol("ID> ", input);
        Thread.sleep(100);
        writeSymbol(jumpInfo.getUserId() + "\r", output);

        ByteArrayOutputStream byteArrayOutputStream = waitSymbol("$", input);
        // 构造 prompt
        byte[] byteArray = byteArrayOutputStream.toByteArray();
        StringBuilder prompt = new StringBuilder();
        for (int i = byteArray.length - 1; i >= 0; i--) {
            if (!Character.isWhitespace(byteArray[i]) || byteArray[i] == 32) {
                prompt.append((char) byteArray[i]);
            } else {
                break;
            }
        }
        if (prompt.length() >= 3) {
            prompt.delete(0, 3);
        }
        return prompt.reverse().toString();
    }

    public String tryConnectAndWait(InputStream input, OutputStream output, JumpServerHostInfo info) {
        try {
            Future<String> future = ApplicationManager.getApplication().executeOnPooledThread(() -> {
                try {
                    return executeJumpServerLogin(input, output, info);
                } catch (Throwable e) {
                    return null;
                }
            });
            return future.get(2, TimeUnit.SECONDS);
        } catch (Exception e) {
            return null;
        }
    }
}
