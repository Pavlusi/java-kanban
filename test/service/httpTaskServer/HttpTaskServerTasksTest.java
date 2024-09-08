package service.httpTaskServer;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import exeptions.TaskNotFoundException;
import model.Task;


import java.io.IOException;
import java.lang.reflect.Type;
import java.net.http.HttpClient;
import java.net.http.HttpResponse;
import java.util.List;

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

import static org.junit.jupiter.api.Assertions.*;


public class HttpTaskServerTasksTest {

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
    public void GetTasksList() throws IOException, InterruptedException {
        taskManager.saveTask(utils.getTask());

        HttpResponse<String> response = RequestsSender.sendGetRequest("http://localhost:8080/tasks");
        assertEquals(200, response.statusCode(), "Код ответа не соответсвует ожидаемому");

        Type taskListType = new TypeToken<List<Task>>() {
        }.getType();
        List<Task> tasksFromServer = gson.fromJson(response.body().toString(), taskListType);
        List<Task> tasksFromManager = taskManager.getTasksList();

        assertArrayEquals(tasksFromManager.toArray(), tasksFromServer.toArray(), "Список задач пришедший от сервера не соответствует списку добавленному в менеджер");
    }

    @Test
    public void GetTaskById() throws IOException, InterruptedException {
        Task taskFromManager = taskManager.saveTask(utils.getTask());
        shouldReturnTaskById(taskFromManager);
        shouldReturnNotFoundWhenRequestNonExistentIdGetRequest(taskFromManager);
    }


    @Test
    public void SaveNewTask() throws IOException, InterruptedException {
        Task task1 = utils.getTask();
        Task taskWithSameTimeTask1 = utils.getTaskWithSameTime(task1);

        shouldSaveNewTaskAndResponseCreated(task1);
        shouldReturnNotAcceptableWhenTaskTimeCrossSave(taskWithSameTimeTask1);
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
    public void deleteTaskById() throws IOException, InterruptedException {
        shouldDeleteTaskById();
        shouldReturnNotFoundWhenRequestNonExistentIdDeleteRequest();
    }


    private void shouldReturnTaskById(Task taskFromManager) throws IOException, InterruptedException {

        HttpResponse<String> response = RequestsSender.sendGetRequest("http://localhost:8080/tasks/" + taskFromManager.getId());
        assertEquals(200, response.statusCode(), "Код ответа не соответстует ожидаемому");

        Task taskFromServer = gson.fromJson(response.body().toString(), Task.class);
        assertEquals(taskFromManager, taskFromServer, "Задача пришедшая от сервера не соответствует задаче добавленной в менеджер");
    }

    private void shouldReturnNotFoundWhenRequestNonExistentIdGetRequest(Task taskFromManager) throws IOException, InterruptedException {
        HttpResponse<String> response = RequestsSender.sendGetRequest("http://localhost:8080/tasks/" + (taskFromManager.getId() + 1));
        assertEquals(404, response.statusCode(), "Код ответа не соответстует ожидаемому");
    }

    private void shouldSaveNewTaskAndResponseCreated(Task task) throws IOException, InterruptedException {
        String taskJson = gson.toJson(task);

        HttpResponse<String> response = RequestsSender.sendPostRequest("http://localhost:8080/tasks", taskJson);
        assertEquals(201, response.statusCode());

        List<Task> tasksFromManager = taskManager.getTasksList();

        assertNotNull(tasksFromManager, "Задачи не возвращаются");
        assertEquals(1, tasksFromManager.size(), "Некорректное количество задач");
        assertEquals(task.getName(), tasksFromManager.get(0).getName(), "Некорректное имя задачи");
    }

    private void shouldReturnNotAcceptableWhenTaskTimeCrossSave(Task task) throws IOException, InterruptedException {
        String taskJson = gson.toJson(task);
        HttpResponse<String> response = RequestsSender.sendPostRequest("http://localhost:8080/tasks", taskJson);
        assertEquals(406, response.statusCode());

        List<Task> tasksFromManager = taskManager.getTasksList();

        assertNotNull(tasksFromManager, "Задачи не возвращаются");
        assertEquals(1, tasksFromManager.size(), "Некорректное количество задач");
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

    private void shouldDeleteTaskById() throws IOException, InterruptedException {
        Task taskToDelete = taskManager.saveTask(utils.getTask());
        HttpResponse<String> response = RequestsSender.sendDeleteRequest("http://localhost:8080/tasks/"
                + taskToDelete.getId());
        assertEquals(201, response.statusCode());
        assertThrows(TaskNotFoundException.class, () -> {
            taskManager.getTaskById(taskToDelete.getId());
        });
    }

    private void shouldReturnNotFoundWhenRequestNonExistentIdDeleteRequest() throws IOException, InterruptedException {
        Task taskToDelete = taskManager.saveTask(utils.getTask());
        HttpResponse<String> response = RequestsSender.sendDeleteRequest("http://localhost:8080/tasks/"
                + (taskToDelete.getId() + 1));
        assertEquals(404, response.statusCode());
        assertNotNull(taskManager.getTaskById(taskToDelete.getId()));
    }


}
