package handlers;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import exeptions.TaskNotFoundException;
import exeptions.TaskTimeCrossException;
import model.Subtask;
import service.TaskManager;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class SubtaskHandler extends BaseHttpHandler implements HttpHandler {

    public SubtaskHandler(TaskManager taskManager, Gson gson) {
        super(taskManager, gson);
    }


    @Override
    public void handle(HttpExchange exchange) throws IOException {
        try {
            String method = exchange.getRequestMethod();
            String path = exchange.getRequestURI().toString();
            switch (method) {
                case "GET":
                    Integer subtaskId = super.getIdFromPath(path);
                    if (subtaskId == null) {
                        try {
                            List<Subtask> subtasks = taskManager.getSubtaskList();
                            String responseJson = gson.toJson(subtasks);
                            sendText(exchange, responseJson);
                        } catch (Exception e) {
                            sendServerError(exchange);
                        }
                    } else {
                        try {
                            Subtask subtask = taskManager.getSubtaskById(subtaskId);
                            String responseJson = gson.toJson(subtask);
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
                                Subtask postSubtask = gson.fromJson(body, Subtask.class);
                                if (postSubtask.getId() == null) {
                                    try {
                                        taskManager.saveSubtask(postSubtask);
                                        writeResponse(exchange, 201);
                                    } catch (TaskTimeCrossException e) {
                                        sendHasInteractions(exchange, e.getMessage());
                                    } catch (Exception e) {
                                        sendServerError(exchange);
                                    }
                                } else {
                                    try {
                                        taskManager.updateSubtask(postSubtask);
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
                        taskManager.deleteSubtaskById(id);
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
