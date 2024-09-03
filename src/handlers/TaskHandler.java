package handlers;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import model.Task;
import service.TaskManager;

import java.io.IOException;
import java.util.List;

public class TaskHandler extends BaseHttpHandler  implements HttpHandler  {

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
                  if(super.getIdFromPath(path) == null){
                      try{
                          List<Task> tasks = taskManager.getTasksList();
                          String responseJson = gson.toJson(tasks);
                          sendText(exchange, responseJson);
                      } catch (Exception e) {
                          sendServerError(exchange);
                      }
                  }

          }


      } catch (Exception e) {
          e.printStackTrace();
      }
    }
}
