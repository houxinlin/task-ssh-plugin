package dev.cool.ssh.task.model;

public class HostInfo {
    private String host;
    private int port;
    private String username;
    private String password;
    private int hostType;
    private String hostExtJSON;
    private int sort;

    public int getSort() {
        return sort;
    }

    public void setSort(int sort) {
        this.sort = sort;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public int getHostType() {
        return hostType;
    }

    public void setHostType(int hostType) {
        this.hostType = hostType;
    }

    public String getHostExtJSON() {
        return hostExtJSON;
    }

    public void setHostExtJSON(String hostExtJSON) {
        this.hostExtJSON = hostExtJSON;
    }
}
