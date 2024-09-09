package service.httpTaskServer;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import exeptions.TaskNotFoundException;
import model.Epic;
import model.Subtask;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
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

public class HttpTaskServerEpicTest {

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
    public void GetEpicList() throws IOException, InterruptedException {
        Epic epic = taskManager.saveEpic(utils.getEpic());

        HttpResponse<String> response = RequestsSender.sendGetRequest("http://localhost:8080/epics");
        assertEquals(200, response.statusCode(), "Код ответа не соответсвует ожидаемому");

        Type epicListType = new TypeToken<List<Epic>>() {
        }.getType();
        List<Epic> epicsFromServer = gson.fromJson(response.body().toString(), epicListType);
        List<Epic> epicsFromManager = taskManager.getEpicList();

        assertArrayEquals(epicsFromServer.toArray(), epicsFromServer.toArray(), "Список задач пришедший от сервера не соответствует списку добавленному в менеджер");
    }

    @Test
    public void GetEpicById() throws IOException, InterruptedException {
        Epic epicFromManager = taskManager.saveEpic(utils.getEpic());
        shouldReturnEpicById(epicFromManager);
        shouldReturnNotFoundWhenRequestNonExistentIdGetRequest(epicFromManager);
    }

    @Test
    public void GetSubtasksListByEpicId() throws IOException, InterruptedException {
        Epic epic = taskManager.saveEpic(utils.getEpic());
        shouldReturnListSubtasksByEpicId(epic);
        shouldReturnNotFoundWhenRequestNonExistentIdToGetSubtasksGetRequest(epic);
    }

    @Test
    public void SaveNewEpic() throws IOException, InterruptedException {
        shouldSaveNewTaskAndResponseCreated();
    }

    @Test
    public void UpdateEpic() throws IOException, InterruptedException {
        Epic epicToUpdate = taskManager.saveEpic(utils.getEpic());
        Epic updatedEpic = utils.getUpdatedEpic(epicToUpdate);

        shouldUpdateEpic(updatedEpic);
        shouldReturnNotFoundWhenRequestNonExistentIdPostRequest(updatedEpic);
    }

    @Test
    public void deleteEpicById() throws IOException, InterruptedException {
        shouldDeleteEpicById();
        shouldReturnNotFoundWhenRequestNonExistentIdDeleteRequest();
    }

    private void shouldReturnEpicById(Epic epicFromManager) throws IOException, InterruptedException {

        HttpResponse<String> response = RequestsSender.sendGetRequest("http://localhost:8080/epics/" + epicFromManager.getId());
        assertEquals(200, response.statusCode(), "Код ответа не соответстует ожидаемому");

        Epic epicFromServer = gson.fromJson(response.body().toString(), Epic.class);
        assertEquals(epicFromManager, epicFromServer, "Задача пришедшая от сервера не соответствует задаче добавленной в менеджер");
    }

    private void shouldReturnNotFoundWhenRequestNonExistentIdGetRequest(Epic epicFromManager) throws IOException, InterruptedException {
        HttpResponse<String> response = RequestsSender.sendGetRequest("http://localhost:8080/epics/" + (epicFromManager.getId() + 1));
        assertEquals(404, response.statusCode(), "Код ответа не соответстует ожидаемому");
    }

    private void shouldSaveNewTaskAndResponseCreated() throws IOException, InterruptedException {
        String taskJson = gson.toJson(utils.getEpic());

        HttpResponse<String> response = RequestsSender.sendPostRequest("http://localhost:8080/epics", taskJson);
        assertEquals(201, response.statusCode());

        List<Epic> epicsFromManager = taskManager.getEpicList();

        assertNotNull(epicsFromManager, "Задачи не возвращаются");
        assertEquals(1, epicsFromManager.size(), "Некорректное количество задач");
    }

    private void shouldUpdateEpic(Epic updatedEpic) throws IOException, InterruptedException {
        String taskJson = gson.toJson(updatedEpic);
        HttpResponse<String> response = RequestsSender.sendPostRequest("http://localhost:8080/epics/" + updatedEpic.getId()
                , taskJson);
        assertEquals(201, response.statusCode());
        assertEquals(updatedEpic.getName(), taskManager.getEpicList().get(0).getName());
    }

    private void shouldReturnNotFoundWhenRequestNonExistentIdPostRequest(Epic updatedEpic) throws IOException, InterruptedException {
        Epic updated = updatedEpic;
        updated.setId(20);
        String taskJson = gson.toJson(updatedEpic);
        HttpResponse<String> response = RequestsSender.sendPostRequest("http://localhost:8080/epics/" + (updatedEpic.getId())
                , taskJson);
        Assertions.assertEquals(404, response.statusCode());
    }

    private void shouldDeleteEpicById() throws IOException, InterruptedException {
        Epic epicToDelete = taskManager.saveEpic(utils.getEpic());
        HttpResponse<String> response = RequestsSender.sendDeleteRequest("http://localhost:8080/epics/"
                + epicToDelete.getId());
        assertEquals(201, response.statusCode());
        assertThrows(TaskNotFoundException.class, () -> {
            taskManager.getTaskById(epicToDelete.getId());
        });
    }

    private void shouldReturnNotFoundWhenRequestNonExistentIdDeleteRequest() throws IOException, InterruptedException {
        Epic epicToDelete = taskManager.saveEpic(utils.getEpic());
        HttpResponse<String> response = RequestsSender.sendDeleteRequest("http://localhost:8080/epics/"
                + (epicToDelete.getId() + 1));
        assertEquals(404, response.statusCode());
        assertNotNull(taskManager.getEpicById(epicToDelete.getId()));
    }

    private void shouldReturnListSubtasksByEpicId(Epic epic) throws IOException, InterruptedException {
        Subtask subtask = taskManager.saveSubtask(utils.getSubtask(epic));
        HttpResponse<String> response = RequestsSender.sendGetRequest("http://localhost:8080/epics/" + epic.getId()
                + "/subtasks");

        Type epicListType = new TypeToken<List<Subtask>>() {
        }.getType();
        List<Subtask> subtasksFromServer = gson.fromJson(response.body().toString(), epicListType);
        List<Subtask> subtasksFromManager = taskManager.getSubtaskByEpicId(epic.getId());
        assertEquals(200, response.statusCode());
        assertArrayEquals(subtasksFromServer.toArray(), subtasksFromManager.toArray());
    }

    private void shouldReturnNotFoundWhenRequestNonExistentIdToGetSubtasksGetRequest(Epic epic) throws IOException, InterruptedException {
        HttpResponse<String> response = RequestsSender.sendGetRequest("http://localhost:8080/epics/" + (epic.getId() + 1)
                + "/subtasks");
        assertEquals(404, response.statusCode(), "Код ответа не соответстует ожидаемому");
    }
}
