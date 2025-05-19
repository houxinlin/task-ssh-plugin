package dev.cool.ssh.task.utils;

import dev.cool.ssh.task.model.FileEntry;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LSParser {
    private static final Pattern LS_PATTERN = Pattern.compile(
            "^([\\-ldcbpsrwx]+)\\.?\\s+\\d+\\s+(\\S+)\\s+\\S+\\s+(\\S+)\\s+\\w{3}\\s+\\d+\\s+[\\d:]+\\s+(\\S+)(?:\\s+->\\s+\\S+)?$"
    );

    public static List<FileEntry> parseLsOutput(String lsOutput) {
        List<FileEntry> entries = new ArrayList<>();
        String[] lines = lsOutput.split("\\r?\\n");
        for (String line : lines) {
            if (line.startsWith("total")) continue;

            Matcher matcher = LS_PATTERN.matcher(line);
            if (matcher.find()) {
                String permissions = matcher.group(1);
                String owner = matcher.group(2);
                String size = matcher.group(3);
                String name = matcher.group(4);
                boolean isDir = permissions.charAt(0) == 'd';

                entries.add(new FileEntry(name, isDir, size, owner));
            }
        }
        return entries;
    }
}
