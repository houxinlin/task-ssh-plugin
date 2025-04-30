package dev.cool.ssh.task.view;

import com.intellij.openapi.ui.VerticalFlowLayout;

import javax.swing.*;

public class SSHTaskView extends JPanel {

    public SSHTaskView() {
        initView();
    }

    private void  initView(){
        setLayout(new VerticalFlowLayout());
    }
}
