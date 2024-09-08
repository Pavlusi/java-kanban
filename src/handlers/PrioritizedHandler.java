package handlers;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import model.Task;
import service.TaskManager;

import java.io.IOException;
import java.util.List;

public class PrioritizedHandler extends BaseHttpHandler implements HttpHandler {

    public PrioritizedHandler(TaskManager taskManager, Gson gson) {
        super(taskManager, gson);
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        try {
            List<Task> prioritizedTasks = taskManager.getPrioritizedTasks();
            String response = gson.toJson(prioritizedTasks);
            sendText(exchange, response);
        } catch (Exception e) {
            sendServerError(exchange);
        }
    }
}
