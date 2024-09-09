package service.httpTaskServer;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import model.Task;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import service.HttpTaskServer;
import service.InMemoryTaskManager;
import service.TaskManager;
import testUtils.RequestsSender;
import testUtils.TestUtils;
import util.Managers;

import java.io.IOException;
import java.lang.reflect.Type;
import java.net.http.HttpClient;
import java.net.http.HttpResponse;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class HttpTaskServerPrioritizedTest {
    private TaskManager taskManager;
    private HttpTaskServer server;
    private Gson gson;
    private HttpClient client;

    TestUtils utils = new TestUtils();

    @BeforeEach
    public void setUp() throws InterruptedException {
        taskManager = new InMemoryTaskManager();
        server = new HttpTaskServer(taskManager);
        gson = Managers.getGson();
        client = HttpClient.newHttpClient();
        server.startServer();
    }

    @AfterEach
    public void shutDown() {
        taskManager.deleteAllTasks();
        taskManager.deleteAllSubtask();
        taskManager.deleteAllEpics();
        server.stopServer();
    }

    @Test
    public void GetPrioritizedList() throws IOException, InterruptedException {
        taskManager.saveTask(utils.getTask());
        taskManager.saveTask(utils.getTask());

        HttpResponse<String> response = RequestsSender.sendGetRequest("http://localhost:8080/prioritized");
        assertEquals(200, response.statusCode(), "Код ответа не соответсвует ожидаемому");

        Type taskListType = new TypeToken<List<Task>>() {
        }.getType();
        List<Task> tasksFromServer = gson.fromJson(response.body().toString(), taskListType);
        List<Task> tasksFromManager = taskManager.getTasksList();

        assertArrayEquals(tasksFromManager.toArray(), tasksFromServer.toArray(), "Список задач пришедший от сервера не соответствует списку добавленному в менеджер");
    }

}