package dev.cool.ssh.task.exec.ssh;

import com.google.gson.Gson;
import com.jcraft.jsch.ChannelShell;
import dev.cool.ssh.task.exec.ExecContext;
import dev.cool.ssh.task.exec.ExecListener;
import dev.cool.ssh.task.exec.ISSH;
import dev.cool.ssh.task.exec.JschFactory;
import dev.cool.ssh.task.exec.factory.TaskFactory;
import dev.cool.ssh.task.exec.wrapper.ExecuteInfoWrapper;
import dev.cool.ssh.task.model.HostInfo;
import dev.cool.ssh.task.model.JumpServerHostInfo;
import dev.cool.ssh.task.ssh.JumpServerConnection;
import dev.cool.ssh.task.utils.ExecUtils;

import java.io.InputStream;
import java.io.OutputStream;

public class JumpServerSSH extends BasicSSH implements ISSH {

    public JumpServerSSH(HostInfo hostInfo) {
        super(hostInfo);
    }

    @Override
    public void execute(ExecuteInfoWrapper executeInfo, ExecListener execListener) throws Exception {
        ChannelShell channel = null;
        try {
            JumpServerConnection connection = new JumpServerConnection();
            channel = JschFactory.openChannel(getHostInfo());
            OutputStream outputStream = channel.getOutputStream();
            InputStream inputStream = channel.getInputStream();

            JumpServerHostInfo jumpServerHostInfo = new Gson().fromJson(getHostInfo().getHostExtJSON(), JumpServerHostInfo.class);
            String prompt = null;

            for (int i = 0; i < 3; i++) {
                if (executeInfo.getNode().isCancelled()) break;
                execListener.execOutput(executeInfo, "尝试第" + (i + 1) + "次连接服务器");
                prompt = connection.tryConnectAndWait(inputStream, outputStream, jumpServerHostInfo);
                if (prompt != null) break;
                // 重试连接
                inputStream.close();
                outputStream.close();
                ExecUtils.closeChannel(channel);
                channel = JschFactory.openChannel(getHostInfo());
                outputStream = channel.getOutputStream();
                inputStream = channel.getInputStream();
            }

            if (prompt == null) throw new IllegalArgumentException("无法连接到服务器");
            execListener.execOutput(executeInfo, "连接服务器成功");
            executeInfo.getNode().setChannel(channel);
            Thread.sleep(500);
            ExecContext execContext = new ExecContext(prompt, inputStream, outputStream);
            execContext.setHostInfo(getHostInfo());
            execContext.setExecuteInfoWrapper(executeInfo);
            execContext.setExecListener(execListener);
            TaskFactory.getTask(executeInfo, this).execute(execContext);
        } finally {
            ExecUtils.closeChannel(channel);
        }
    }

}
