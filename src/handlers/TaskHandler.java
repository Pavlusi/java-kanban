package handlers;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import exeptions.TaskNotFoundException;
import exeptions.TaskTimeCrossException;
import model.Task;
import service.TaskManager;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class TaskHandler extends BaseHttpHandler implements HttpHandler {

    public TaskHandler(TaskManager taskManager, Gson gson) {
        super(taskManager, gson);
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        try {
            String method = exchange.getRequestMethod();
            String path = exchange.getRequestURI().toString();
            switch (method) {
                case "GET":
                    Integer taskId = super.getIdFromPath(path);
                    if (taskId == null) {
                        try {
                            List<Task> tasks = taskManager.getTasksList();
                            String responseJson = gson.toJson(tasks);
                            sendText(exchange, responseJson);
                        } catch (Exception e) {
                            sendServerError(exchange);
                        }
                    } else {
                        try {
                            Task task = taskManager.getTaskById(taskId);
                            String responseJson = gson.toJson(task);
                            sendText(exchange, responseJson);
                        } catch (TaskNotFoundException e) {
                            sendNotFound(exchange, e.getMessage());
                        } catch (Exception e) {
                            sendServerError(exchange);
                        }
                    }
                    break;
                case "POST":
                    try (InputStream inputStream = exchange.getRequestBody()) {
                        String body = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
                        if (!body.isEmpty()) {
                            try {
                                Task postTask = gson.fromJson(body, Task.class);
                                if (postTask.getId() == null) {
                                    try {
                                        taskManager.saveTask(postTask);
                                        writeResponse(exchange, 201);
                                    } catch (TaskTimeCrossException e) {
                                        sendHasInteractions(exchange, e.getMessage());
                                    } catch (Exception e) {
                                        sendServerError(exchange);
                                    }
                                } else {
                                    try {
                                        taskManager.updateTask(postTask);
                                        writeResponse(exchange, 201);
                                    } catch (TaskNotFoundException e) {
                                        sendNotFound(exchange, e.getMessage());
                                    } catch (TaskTimeCrossException e) {
                                        sendHasInteractions(exchange, e.getMessage());
                                    } catch (Exception e) {
                                        sendServerError(exchange);
                                    }
                                }

                            } catch (Exception e) {
                                sendServerError(exchange);
                            }

                        }
                    } catch (Exception e) {
                        sendServerError(exchange);
                    }
                    break;
                case "DELETE":
                    Integer id = super.getIdFromPath(path);
                    try {
                        taskManager.deleteTaskById(id);
                        writeResponse(exchange, 201);
                    } catch (TaskNotFoundException e) {
                        sendNotFound(exchange, e.getMessage());
                    } catch (Exception e) {
                        sendServerError(exchange);
                    }
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
