package dev.cool.ssh.task.exec.ssh;

import com.google.gson.Gson;
import com.jcraft.jsch.Channel;
import dev.cool.ssh.task.exec.ISSH;
import dev.cool.ssh.task.exec.JschFactory;
import dev.cool.ssh.task.exec.State;
import dev.cool.ssh.task.exec.factory.TaskFactory;
import dev.cool.ssh.task.exec.wrapper.ExecuteInfoWrapper;
import dev.cool.ssh.task.model.HostInfo;
import dev.cool.ssh.task.model.JumpServerHostInfo;
import dev.cool.ssh.task.view.node.ExecutionNode;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

public class JumpServerSSH extends BasicSSH implements ISSH {
    public JumpServerSSH(HostInfo hostInfo) {
        super(hostInfo);
    }

    @Override
    public void execute(ExecuteInfoWrapper executeInfo) throws Exception {
        ExecutionNode executionNode = (ExecutionNode) executeInfo.getNode();
        executionNode.setState(State.RUNNING);
        String host = getHostInfo().getHost();
        int port = 22;
        if (host.contains(":")) {
            port = Integer.parseInt(host.split(":")[1]);
            host = host.split(":")[0];
        }
        Channel channel = JschFactory.openChannel(host, port, getHostInfo().getUsername(), getHostInfo().getPassword());
        JumpServerHostInfo jumpServerHostInfo = new Gson().fromJson(getHostInfo().getHostExtJSON(), JumpServerHostInfo.class);

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        InputStream inputStream = channel.getInputStream();
        System.out.println("等待opt");
        while (!byteArrayOutputStream.toString().contains("Opt>")) {
            byte[] bytes = new byte[2028];
            int read = inputStream.read(bytes);
            byteArrayOutputStream.write(bytes, 0, read);
            System.out.println(byteArrayOutputStream.toString());
        }
        System.out.println("<UNK>opt");
        OutputStream outputStream = channel.getOutputStream();
        outputStream.write(("p\r" + jumpServerHostInfo.getIp() + "\r" + jumpServerHostInfo.getUserId() + "\r").getBytes());
        outputStream.flush();
        try {
            TaskFactory.getTask(executeInfo).execute(executeInfo, channel);
        } finally {
            channel.disconnect();
        }
    }
}
