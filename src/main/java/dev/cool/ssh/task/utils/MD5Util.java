package dev.cool.ssh.task.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.security.MessageDigest;

public class MD5Util {

    public static String getFileMD5(File file) {
        try (InputStream fis = new FileInputStream(file)) {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] buffer = new byte[8192];
            int bytesRead;

            while ((bytesRead = fis.read(buffer)) != -1) {
                md.update(buffer, 0, bytesRead);
            }

            byte[] md5Bytes = md.digest();

            // 转成十六进制字符串
            StringBuilder hexString = new StringBuilder();
            for (byte b : md5Bytes) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }

            return hexString.toString();
        } catch (Exception e) {
            throw new RuntimeException("无法计算文件的MD5", e);
        }
    }

    public static void main(String[] args) {
        File file = new File("/home/LinuxWork/project/go/github/zssh/main.go");
        String md5 = getFileMD5(file);
        System.out.println("文件MD5: " + md5);
    }
}
