package service;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpServer;
import handlers.*;
import util.Managers;

import java.io.IOException;
import java.net.InetSocketAddress;


public class HttpTaskServer {

    private static final int PORT = 8080;
    private final TaskManager taskManager;

    private final HttpServer httpServer;

    private final Gson gson;

    public HttpTaskServer(TaskManager taskManager) {
        this.taskManager = taskManager;
        this.gson = Managers.getGson();
        try {
            this.httpServer = HttpServer.create(new InetSocketAddress(PORT), 0);
            this.httpServer.createContext("/tasks", new TaskHandler(taskManager, gson));
            this.httpServer.createContext("/subtasks", new SubtaskHandler(taskManager, gson));
            this.httpServer.createContext("/epics", new EpicHandler(taskManager, gson));
            this.httpServer.createContext("/history", new HistoryHandler(taskManager, gson));
            this.httpServer.createContext("/prioritized", new PrioritizedHandler(taskManager, gson));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public HttpTaskServer() {
        this.taskManager = Managers.getDefault();
        this.gson = Managers.getGson();
        try {
            this.httpServer = HttpServer.create(new InetSocketAddress(PORT), 0);
            this.httpServer.createContext("/tasks", new TaskHandler(taskManager, gson));
            this.httpServer.createContext("/subtasks", new SubtaskHandler(taskManager, gson));
            this.httpServer.createContext("/epics", new EpicHandler(taskManager, gson));
            this.httpServer.createContext("/history", new HistoryHandler(taskManager, gson));
            this.httpServer.createContext("/prioritized", new PrioritizedHandler(taskManager, gson));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    public static void main(String[] args) {
        HttpTaskServer taskServer = new HttpTaskServer();
        taskServer.startServer();

    }

    public void startServer() {
        this.httpServer.start();
    }

    public void stopServer() {
        this.httpServer.stop(0);
    }

}
