package handlers;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import exeptions.TaskNotFoundException;
import model.Epic;
import service.TaskManager;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class EpicHandler extends BaseHttpHandler implements HttpHandler {

    public EpicHandler(TaskManager taskManager, Gson gson) {
        super(taskManager, gson);
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        try {
            String method = exchange.getRequestMethod();
            String path = exchange.getRequestURI().toString();
            switch (method) {
                case "GET":
                    Integer epicId = super.getIdFromPath(path);
                    if (epicId != null) {
                        String[] param = path.split("/");
                        if (param[param.length - 1].equals("subtasks")) {
                            try {
                                Epic epic = taskManager.getEpicById(epicId);
                                String responseJson = gson.toJson(epic.getSubtasks());
                                sendText(exchange, responseJson);
                            } catch (TaskNotFoundException e) {
                                sendNotFound(exchange, e.getMessage());
                            } catch (Exception e) {
                                sendServerError(exchange);
                            }
                        } else {
                            try {
                                Epic epic = taskManager.getEpicById(epicId);
                                String responseJson = gson.toJson(epic);
                                sendText(exchange, responseJson);
                            } catch (TaskNotFoundException e) {
                                sendNotFound(exchange, e.getMessage());
                            } catch (Exception e) {
                                sendServerError(exchange);
                            }
                        }
                    } else {
                        try {
                            List<Epic> epics = taskManager.getEpicList();
                            String responseJson = gson.toJson(epics);
                            sendText(exchange, responseJson);
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
                                Epic postEpic = gson.fromJson(body, Epic.class);
                                if (postEpic.getId() == null) {
                                    try {
                                        taskManager.saveEpic(postEpic);
                                        writeResponse(exchange, 201);
                                    } catch (Exception e) {
                                        sendServerError(exchange);
                                    }
                                } else {
                                    try {
                                        taskManager.updateEpic(postEpic);
                                        writeResponse(exchange, 201);
                                    } catch (TaskNotFoundException e) {
                                        sendNotFound(exchange, e.getMessage());
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
                        taskManager.deleteEpicById(id);
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

