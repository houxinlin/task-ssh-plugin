package dev.cool.ssh.task.exec.factory;

import com.google.gson.Gson;
import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.SftpProgressMonitor;
import dev.cool.ssh.task.exec.ExecContext;
import dev.cool.ssh.task.exec.ExecType;
import dev.cool.ssh.task.exec.ITask;
import dev.cool.ssh.task.exec.JschFactory;
import dev.cool.ssh.task.exec.wrapper.ExecuteInfoWrapper;
import dev.cool.ssh.task.model.FileExecuteInfo;
import dev.cool.ssh.task.model.HostInfo;
import dev.cool.ssh.task.model.SimpleParameter;
import dev.cool.ssh.task.utils.ExecUtils;
import dev.cool.ssh.task.view.node.ProgressExecuteNode;
import org.jetbrains.annotations.NotNull;

import java.io.FileInputStream;
import java.util.Objects;

public class SimpleTaskFactory {
    public static @NotNull ITask getTask(ExecuteInfoWrapper executeInfo) {
        if (Objects.equals(executeInfo.getExecuteType(), ExecType.UPLOAD.getExecType())) {
            return new UploadTask();
        }
        if (Objects.equals(executeInfo.getExecuteType(), ExecType.KILL_JAR.getExecType())) {
            return new KillJarTask();
        }
        if (Objects.equals(executeInfo.getExecuteType(), ExecType.COMMAND.getExecType())) {
            return new CommandTask();
        }
        if (Objects.equals(executeInfo.getExecuteType(), ExecType.SCRIPT.getExecType())) {
            return new ScriptExecuteTask();
        }
        throw new IllegalArgumentException("Unknown execute type: " + executeInfo.getExecuteType());

    }


    private static void execCommand(String command, HostInfo hostInfo, ExecContext execContext) throws Exception {
        ChannelExec channelExec = JschFactory.openExecChannel(hostInfo);
        execContext.getExecuteInfoWrapper().getNode().setChannel(channelExec);
        try {
            channelExec.setCommand(command);

            // 获取标准输出
            java.io.InputStream in = channelExec.getInputStream();
            // 获取错误输出
            java.io.InputStream err = channelExec.getErrStream();

            // 读取标准输出
            java.io.BufferedReader reader = new java.io.BufferedReader(new java.io.InputStreamReader(in));
            String line;
            while ((line = reader.readLine()) != null) {
                System.out.println(line);
            }
            // 读取错误输出
            java.io.BufferedReader errReader = new java.io.BufferedReader(new java.io.InputStreamReader(err));
            while ((line = errReader.readLine()) != null) {
                System.err.println(line);
            }

            // 等待命令执行完成
            while (channelExec.isConnected()) {
                Thread.sleep(100);
            }
        } finally {
            ExecUtils.closeChannel(channelExec);
        }
    }


    private static class CommandTask implements ITask {
        @Override
        public void execute(ExecContext execContext) throws Exception {
            String command = new Gson().fromJson(execContext.getExecuteInfoWrapper().getExecuteExtJSON(), SimpleParameter.class).getValue();
            execCommand(command, execContext.getHostInfo(), execContext);
        }
    }

    private static class ScriptExecuteTask implements ITask {
        @Override
        public void execute(ExecContext execContext) throws Exception {
            execCommand(ExecUtils.buildExecCmd(execContext.getExecuteInfoWrapper().getExecuteInfo()), execContext.getHostInfo(), execContext);
        }
    }

    private static class KillJarTask extends CommandTask {
        @Override
        public void execute(ExecContext execContext) throws Exception {
            String value = new Gson().fromJson(execContext.getExecuteInfoWrapper().getExecuteExtJSON(), SimpleParameter.class).getValue();
            String command = "jps -l | grep \" " + value + " \" | awk '{print $1}' | xargs -r kill -9\r";
            execCommand(command, execContext.getHostInfo(), execContext);
        }
    }

    private static class UploadTask implements ITask {
        @Override
        public void execute(ExecContext execContext) throws Exception {
            ChannelSftp channelSftp = JschFactory.openSFTP(execContext.getHostInfo());
            execContext.getExecuteInfoWrapper().getNode().setChannel(channelSftp);
            ExecuteInfoWrapper executeInfoWrapper = execContext.getExecuteInfoWrapper();
            FileExecuteInfo fileExecuteInfo = new Gson().fromJson(executeInfoWrapper.getExecuteExtJSON(), FileExecuteInfo.class);

            String remoteFile = fileExecuteInfo.getRemotePath() + "/" + new java.io.File(fileExecuteInfo.getLocalPath()).getName();
            FileInputStream fis = new FileInputStream(fileExecuteInfo.getLocalPath());

            channelSftp.put(fis, remoteFile, new ProgressMonitor(fileExecuteInfo.getLocalPath(), executeInfoWrapper), ChannelSftp.OVERWRITE);

            ExecUtils.closeChannel(channelSftp);
            fis.close();
        }


        // 自定义进度监听器
        public static class ProgressMonitor implements SftpProgressMonitor {
            private long transferred = 0;
            private long fileSize = 0;
            private final String fileName;

            private ExecuteInfoWrapper wrapper;

            public ProgressMonitor(String filePath, ExecuteInfoWrapper wrapper) {
                this.fileName = filePath;
                this.fileSize = new java.io.File(filePath).length();
                this.wrapper = wrapper;
            }

            @Override
            public void init(int op, String src, String dest, long max) {
                System.out.println("开始上传文件: " + fileName);
            }

            @Override
            public boolean count(long count) {
                transferred += count;
                float percent = (((float) transferred) / fileSize);
                if (wrapper.getNode() instanceof ProgressExecuteNode progressExecuteNode) {
                    progressExecuteNode.setProgress((int) (percent * 100f));
                }
                return true;
            }

            @Override
            public void end() {
                System.out.println("\n上传完成！");
            }
        }
    }
}
