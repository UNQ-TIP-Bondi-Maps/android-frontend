package tpi.unq.bondimaps;

import android.app.Application;

public class Global extends Application{

    private String ipServer;

    public String getIpServer() {
        return ipServer;
    }

    public void setIpServer(String ipServer) {
        this.ipServer = ipServer;
    }
}

