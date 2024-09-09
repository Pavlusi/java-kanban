package service.httpTaskServer;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import exeptions.TaskNotFoundException;
import model.Epic;
import model.Subtask;
import model.Task;
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

public class HttpTaskServerSubtasksTest {

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
    public void GetSubtasksList() throws IOException, InterruptedException {
        Epic epic = taskManager.saveEpic(new Epic("testEpic", "test"));
        taskManager.saveSubtask(utils.getSubtask(epic));

        HttpResponse<String> response = RequestsSender.sendGetRequest("http://localhost:8080/subtasks");
        assertEquals(200, response.statusCode(), "Код ответа не соответсвует ожидаемому");

        Type taskListType = new TypeToken<List<Subtask>>() {
        }.getType();
        List<Subtask> subtasksFromServer = gson.fromJson(response.body().toString(), taskListType);
        List<Subtask> subtasksFromManager = taskManager.getSubtaskList();

        assertArrayEquals(subtasksFromManager.toArray(), subtasksFromServer.toArray(), "Список задач пришедший от сервера не соответствует списку добавленному в менеджер");
    }

    @Test
    public void GetTaskById() throws IOException, InterruptedException {
        Epic epic = taskManager.saveEpic(new Epic("testEpic", "epic"));
        Subtask subtaskFromManager = taskManager.saveSubtask(utils.getSubtask(epic));
        shouldReturnSubtaskById(subtaskFromManager);
        shouldReturnNotFoundWhenRequestNonExistentIdGetRequest(subtaskFromManager);
    }

    @Test
    public void SaveNewSubtask() throws IOException, InterruptedException {
        Epic epic = taskManager.saveEpic(new Epic("testEpic", "test"));
        Subtask subtask1 = utils.getSubtask(epic);
        Subtask subtaskWithSameTimeTask1 = utils.getSubtaskWithSameTime(subtask1);

        shouldSaveNewSubtaskAndResponseCreated(subtask1);
        shouldReturnNotAcceptableWhenSubtaskTimeCrossSave(subtaskWithSameTimeTask1);
    }

    @Test
    public void UpdateTask() throws IOException, InterruptedException {
        Task taskToUpdate = taskManager.saveTask(utils.getTask());
        Task updatedTask = utils.getUpdatedTaskWithStatusDone(taskToUpdate);

        shouldUpdateTask(updatedTask);
        shouldReturnNotAcceptableWhenTaskTimeCrossUpdate(updatedTask);
        shouldReturnNotFoundWhenRequestNonExistentIdPostRequest(updatedTask);
    }

    @Test
    public void deleteSubtaskById() throws IOException, InterruptedException {
        shouldDeleteSubtaskById();
        shouldReturnNotFoundWhenRequestNonExistentIdDeleteRequest();
    }

    private void shouldReturnSubtaskById(Subtask subtaskFromManager) throws IOException, InterruptedException {

        HttpResponse<String> response = RequestsSender.sendGetRequest("http://localhost:8080/subtasks/" + subtaskFromManager.getId());
        assertEquals(200, response.statusCode(), "Код ответа не соответстует ожидаемому");

        Subtask subtaskFromServer = gson.fromJson(response.body().toString(), Subtask.class);
        assertEquals(subtaskFromManager, subtaskFromServer, "Задача пришедшая от сервера не соответствует задаче добавленной в менеджер");
    }

    private void shouldReturnNotFoundWhenRequestNonExistentIdGetRequest(Task taskFromManager) throws IOException, InterruptedException {
        HttpResponse<String> response = RequestsSender.sendGetRequest("http://localhost:8080/subtasks/" + (taskFromManager.getId() + 1));
        assertEquals(404, response.statusCode(), "Код ответа не соответстует ожидаемому");
    }

    private void shouldSaveNewSubtaskAndResponseCreated(Subtask subtask) throws IOException, InterruptedException {
        String taskJson = gson.toJson(subtask);

        HttpResponse<String> response = RequestsSender.sendPostRequest("http://localhost:8080/subtasks", taskJson);
        assertEquals(201, response.statusCode());

        List<Subtask> subtasksFromManager = taskManager.getSubtaskList();

        assertNotNull(subtasksFromManager, "Задачи не возвращаются");
        assertEquals(1, subtasksFromManager.size(), "Некорректное количество задач");
        assertEquals(subtask.getName(), subtasksFromManager.get(0).getName(), "Некорректное имя задачи");
    }

    private void shouldReturnNotAcceptableWhenSubtaskTimeCrossSave(Subtask subtask) throws IOException, InterruptedException {
        String taskJson = gson.toJson(subtask);
        HttpResponse<String> response = RequestsSender.sendPostRequest("http://localhost:8080/subtasks", taskJson);
        assertEquals(406, response.statusCode());

        List<Subtask> subtasksFromManager = taskManager.getSubtaskList();

        assertNotNull(subtasksFromManager, "Задачи не возвращаются");
        assertEquals(1, subtasksFromManager.size(), "Некорректное количество задач");
    }

    private void shouldUpdateTask(Task updatedTask) throws IOException, InterruptedException {
        String taskJson = gson.toJson(updatedTask);
        HttpResponse<String> response = RequestsSender.sendPostRequest("http://localhost:8080/tasks/" + updatedTask.getId()
                , taskJson);
        assertEquals(201, response.statusCode());
        assertEquals(updatedTask.getStatus(), taskManager.getTasksList().get(0).getStatus());
    }

    private void shouldReturnNotAcceptableWhenTaskTimeCrossUpdate(Task updatedTask) throws IOException, InterruptedException {
        Task secondTask = taskManager.saveTask(utils.getTask());
        updatedTask.setStartTime(secondTask.getStartTime());
        updatedTask.setDuration(secondTask.getDuration());
        String taskJson = gson.toJson(updatedTask);


        HttpResponse<String> response = RequestsSender.sendPostRequest("http://localhost:8080/tasks/" + updatedTask.getId()
                , taskJson);

        assertEquals(406, response.statusCode());
    }

    private void shouldReturnNotFoundWhenRequestNonExistentIdPostRequest(Task updatedTask) throws IOException, InterruptedException {
        Task updated = updatedTask;
        updated.setId(20);
        String taskJson = gson.toJson(updatedTask);
        HttpResponse<String> response = RequestsSender.sendPostRequest("http://localhost:8080/tasks/" + (updatedTask.getId() + 5)
                , taskJson);
        Assertions.assertEquals(404, response.statusCode());
    }

    private void shouldDeleteSubtaskById() throws IOException, InterruptedException {
        Epic epic = taskManager.saveEpic(new Epic("testEpic", "test"));
        Subtask subtaskToDelete = taskManager.saveSubtask(utils.getSubtask(epic));
        HttpResponse<String> response = RequestsSender.sendDeleteRequest("http://localhost:8080/subtasks/"
                + subtaskToDelete.getId());
        assertEquals(201, response.statusCode());
        assertThrows(TaskNotFoundException.class, () -> {
            taskManager.getTaskById(subtaskToDelete.getId());
        });
    }

    private void shouldReturnNotFoundWhenRequestNonExistentIdDeleteRequest() throws IOException, InterruptedException {
        Epic epic = taskManager.saveEpic(new Epic("testEpic", "test"));
        Subtask subtaskToDelete = taskManager.saveSubtask(utils.getSubtask(epic));
        HttpResponse<String> response = RequestsSender.sendDeleteRequest("http://localhost:8080/subtasks/"
                + (subtaskToDelete.getId() + 1));
        assertEquals(404, response.statusCode());
        assertNotNull(taskManager.getSubtaskById(subtaskToDelete.getId()));
    }
}
