package dev.cool.ssh.task.utils;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.JSchException;
import dev.cool.ssh.task.model.ExecuteInfo;
import dev.cool.ssh.task.model.ScriptParameter;

import static dev.cool.ssh.task.utils.Utils.getLinuxParentPath;

public class ExecUtils {
    public static void closeChannel(Channel channel) {
        if (channel == null) return;
        channel.disconnect();
        try {
            channel.getSession().disconnect();
        } catch (JSchException ignored) {

        }
    }

    public static String buildExecCmd(ExecuteInfo executeInfo) {
        StringBuilder command = new StringBuilder();
        ScriptParameter scriptParameter = JSONUtils.fromJSON(executeInfo.getExecuteExtJSON(), ScriptParameter.class);
        if (scriptParameter.isExecuteInScriptDir()) {
            command.append("cd ").append(getLinuxParentPath(scriptParameter.getValue())).append(" && ");
        }
        command.append("bash ").append(scriptParameter.getValue());
        return command.toString();
    }
}
