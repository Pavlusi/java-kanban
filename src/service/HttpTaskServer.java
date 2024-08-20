package service;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpsServer;
import handlers.ErrorHandler;
import util.Managers;

import java.io.IOException;
import java.net.InetSocketAddress;

public class HttpTaskServer {

    private static final int PORT = 8080;
    private final TaskManager taskManager;

    private final HttpsServer httpsServer;

    private ErrorHandler errorHandler;

    private final Gson gson;

    public HttpTaskServer(TaskManager taskManager) {
        this.taskManager = taskManager;
        try {
            this.httpsServer = HttpsServer.create(new InetSocketAddress(PORT), 0);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        this.gson = Managers.getGson();
    }

    public HttpTaskServer() {
        this.taskManager = Managers.getDefault();
        this.gson = Managers.getGson();
        try {
            this.httpsServer = HttpsServer.create(new InetSocketAddress(PORT), 0);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    public static void main(String[] args) {

    }
}
