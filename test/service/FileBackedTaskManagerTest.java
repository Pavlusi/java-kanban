package service;

import exeptions.TaskNotFoundException;
import model.Epic;
import model.Status;
import model.Subtask;
import model.Task;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import testUtils.TestUtils;
import util.TaskConverter;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class FileBackedTaskManagerTest {
    private FileBackedTaskManager fileBackedTaskManager;
    private final TestUtils testUtils = new TestUtils();

    private static String testFile;

    private Task task1;

    private Epic epic1;

    private Subtask subtask1;

    private Subtask subtask2;

    private List<Task> allTasksFromManager;

    @BeforeEach
    void init() {
        testFile = "resources/test.cvs";
        fileBackedTaskManager = new FileBackedTaskManager(testFile);
        task1 = fileBackedTaskManager.saveTask(testUtils.getTaskWithNewId());
        epic1 = fileBackedTaskManager.saveEpic(testUtils.getEpicWithNewId());
        subtask1 = fileBackedTaskManager.saveSubtask(testUtils.getSubtaskWithNewId(epic1));
        subtask2 = fileBackedTaskManager.saveSubtask(testUtils.getSubtaskWithNewId(epic1));
        allTasksFromManager = fileBackedTaskManager.getTasksList();
        allTasksFromManager.addAll(fileBackedTaskManager.getEpicList());
        allTasksFromManager.addAll(fileBackedTaskManager.getSubtaskList());

    }

    @AfterAll
    static void reset() {
        try {
            new FileWriter(testFile, false).close();
        } catch (IOException e) {
            System.out.println("Ошибка очистки тестевого файла: " + e.getMessage());
        }
    }

    @Test
    void shouldGetTasksList() {
        List<Task> tasks = fileBackedTaskManager.getTasksList();
        Assertions.assertEquals(1, tasks.size());
        Assertions.assertEquals(task1, tasks.get(0));
    }

    @Test
    void shouldGetEpicList() {
        List<Epic> epics = fileBackedTaskManager.getEpicList();
        Assertions.assertEquals(1, fileBackedTaskManager.getEpicList().size());
        Assertions.assertEquals(epic1, epics.get(0));
    }

    @Test
    void shouldGetSubtaskList() {
        List<Subtask> subtasks = fileBackedTaskManager.getSubtaskList();
        Assertions.assertEquals(2, fileBackedTaskManager.getSubtaskList().size());
        Assertions.assertEquals(subtask1, subtasks.get(0));
        Assertions.assertEquals(subtask2, subtasks.get(1));
    }

    @Test
    void shouldDeleteAllTasks() {
        fileBackedTaskManager.deleteAllTasks();
        Assertions.assertEquals(0, fileBackedTaskManager.getTasksList().size());
    }

    @Test
    void shouldDeleteAllEpicsAndLinkedSubtasks() {
        fileBackedTaskManager.deleteAllEpics();
        Assertions.assertEquals(0, fileBackedTaskManager.getEpicList().size());
        Assertions.assertEquals(0, fileBackedTaskManager.getSubtaskList().size());
    }

    @Test
    void shouldDeleteAllSubtask() {
        fileBackedTaskManager.deleteAllSubtask();

        Assertions.assertEquals(0, fileBackedTaskManager.getSubtaskList().size());
    }

    @Test
    void shouldGetTaskById() {
        Task fromManager = fileBackedTaskManager.getTaskById(task1.getId());

        Assertions.assertNotNull(fromManager, "Задача не найдена");
        Assertions.assertEquals(task1, fromManager, "Задачи не совпадают");
    }

    @Test
    void shouldGetEpicById() {
        Epic fromManager = fileBackedTaskManager.getEpicById(epic1.getId());

        Assertions.assertNotNull(fromManager, "Eпик не найден");
        Assertions.assertEquals(epic1, fromManager, "Епики не совпадают");
    }

    @Test
    void shouldGetSubtaskById() {
        Subtask fromManager = fileBackedTaskManager.getSubtaskById(subtask1.getId());

        Assertions.assertNotNull(fromManager, "Субтакск не найден");
        Assertions.assertEquals(subtask1, fromManager, "Субтаски не совпадают");
    }

    @Test
    void shouldSaveNewTask() {
        Task task1 = fileBackedTaskManager.saveTask(testUtils.getTask());

        Assertions.assertNotNull(fileBackedTaskManager.getTaskById(task1.getId()));
    }

    @Test
    void shouldSaveNewEpic() {
        Epic epic1 = fileBackedTaskManager.saveEpic(testUtils.getEpic());

        Assertions.assertNotNull(fileBackedTaskManager.getEpicById(epic1.getId()));
    }

    @Test
    void shouldSaveNewSubtask() {
        Subtask subtask = fileBackedTaskManager.saveSubtask(testUtils.getSubtask(epic1));

        Assertions.assertNotNull(fileBackedTaskManager.getSubtaskById(subtask.getId()));
    }

    @Test
    void shouldUpdateTask() {
        Task taskFromManager = fileBackedTaskManager.getTaskById(task1.getId());
        Task updatedTask = testUtils.getUpdatedTaskWithStatusDone(taskFromManager);
        fileBackedTaskManager.updateTask(updatedTask);
        taskFromManager = fileBackedTaskManager.getTaskById(updatedTask.getId());

        Assertions.assertEquals(updatedTask.getStatus(), taskFromManager.getStatus());
        Assertions.assertEquals(updatedTask.getName(), taskFromManager.getName());
        Assertions.assertEquals(updatedTask.getDescription(), taskFromManager.getDescription());
    }

    @Test
    void shouldUpdateEpic() {
        Epic epicFromManager = fileBackedTaskManager.getEpicById(epic1.getId());
        Epic updatedEpic = testUtils.getUpdatedEpic(epicFromManager);
        fileBackedTaskManager.updateEpic(updatedEpic);
        epicFromManager = fileBackedTaskManager.getEpicById(updatedEpic.getId());

        Assertions.assertEquals(epicFromManager.getName(), updatedEpic.getName());
        Assertions.assertEquals(epicFromManager.getDescription(), updatedEpic.getDescription());
        Assertions.assertArrayEquals(epicFromManager.getSubtasks().toArray(), updatedEpic.getSubtasks().toArray());
        Assertions.assertEquals(epicFromManager.getStatus(), updatedEpic.getStatus());
    }

    @Test
    void shouldUpdateSubtask() {
        Subtask subtaskFromManager = fileBackedTaskManager.getSubtaskById(subtask1.getId());
        Subtask updatedSubtask = testUtils.getUpdatedSubtaskWithStatusDone(subtaskFromManager);
        fileBackedTaskManager.updateSubtask(updatedSubtask);
        subtaskFromManager = fileBackedTaskManager.getSubtaskById(updatedSubtask.getId());

        Assertions.assertEquals(updatedSubtask.getName(), subtaskFromManager.getName());
        Assertions.assertEquals(updatedSubtask.getDescription(), subtaskFromManager.getDescription());
        Assertions.assertEquals(updatedSubtask.getEpic(), subtaskFromManager.getEpic());
        Assertions.assertEquals(updatedSubtask.getStatus(), subtaskFromManager.getStatus());
    }

    @Test
    void shouldDeleteTaskById() {
        fileBackedTaskManager.deleteTaskById(task1.getId());

        Assertions.assertThrows(TaskNotFoundException.class, () -> {
            fileBackedTaskManager.getTaskById(task1.getId());
        });
    }

    @Test
    void shouldDeleteEpicById() {
        fileBackedTaskManager.deleteEpicById(epic1.getId());

        Assertions.assertThrows(TaskNotFoundException.class, () -> {
            fileBackedTaskManager.getEpicById(epic1.getId());
        });
    }

    @Test
    void shouldDeleteSubtaskById() {
        fileBackedTaskManager.deleteSubtaskById(subtask1.getId());

        Assertions.assertThrows(TaskNotFoundException.class, () -> {
            fileBackedTaskManager.getSubtaskById(subtask1.getId());
        });
    }

    @Test
    void shouldGetSubtaskListByEpicId() {
        List<Subtask> subtasks = fileBackedTaskManager.getSubtaskByEpicId(epic1.getId());
        Assertions.assertEquals(2, subtasks.size());
        Assertions.assertEquals(subtask1, subtasks.get(0));
        Assertions.assertEquals(subtask2, subtasks.get(1));
    }

    @Test
    public void checkEpicStatus() {
        epicShouldHasStatusNewWhenAllHisSubtaskHaveStatusNew();
        epicShouldHasStatusInProgressWhenOneOfHisSubtaskHasStatusInProgressOthersNew();
        epicShouldHasStatusInProgressWhenOneOfHisSubtaskHasStatusDoneOtherNew();
        epicShouldHasStatusDoneWhenAllSubtaskHaveStatusDone();
        epicShouldChangeStatusFromDoneToNewAfterDeleteListHisSubtaskWithStatusDone();
    }


    private void epicShouldHasStatusNewWhenAllHisSubtaskHaveStatusNew() {
        Assertions.assertEquals(Status.NEW, epic1.getStatus());
    }


    private void epicShouldHasStatusInProgressWhenOneOfHisSubtaskHasStatusInProgressOthersNew() {
        fileBackedTaskManager.updateSubtask(testUtils.getUpdatedSubtaskWithStatusInProgress(subtask1));
        Assertions.assertEquals(Status.IN_PROGRESS, epic1.getStatus());
    }


    private void epicShouldHasStatusInProgressWhenOneOfHisSubtaskHasStatusDoneOtherNew() {
        fileBackedTaskManager.updateSubtask(testUtils.getUpdatedSubtaskWithStatusDone(subtask1));
        Assertions.assertEquals(Status.IN_PROGRESS, epic1.getStatus());
    }


    private void epicShouldHasStatusDoneWhenAllSubtaskHaveStatusDone() {
        fileBackedTaskManager.updateSubtask(testUtils.getUpdatedSubtaskWithStatusDone(subtask1));
        fileBackedTaskManager.updateSubtask(testUtils.getUpdatedSubtaskWithStatusDone(subtask2));
        Assertions.assertEquals(Status.DONE, epic1.getStatus());
    }


    private void epicShouldChangeStatusFromDoneToNewAfterDeleteListHisSubtaskWithStatusDone() {
        fileBackedTaskManager.updateSubtask(testUtils.getUpdatedSubtaskWithStatusDone(subtask1));
        fileBackedTaskManager.updateSubtask(testUtils.getUpdatedSubtaskWithStatusDone(subtask2));

        fileBackedTaskManager.deleteAllSubtask();

        Assertions.assertEquals(Status.NEW, epic1.getStatus());
    }

    @Test
    void shouldIncreaseCounterIdWhenSaveNewTask() {
        Task task = fileBackedTaskManager.saveTask(testUtils.getTask());

        Assertions.assertEquals(5, task.getId());
    }

    @Test
    void shouldSaveStateIntroFile() {
        List<Task> tasksFromFile = new ArrayList<>();

        try (BufferedReader bf = new BufferedReader(new FileReader(testFile))) {
            bf.readLine();
            while (bf.ready()) {
                tasksFromFile.add(TaskConverter.toTask(bf.readLine()));
            }
        } catch (IOException e) {
            System.out.println("Ошибка чтения из тестового файла: " + e.getMessage());
        }
        Assertions.assertArrayEquals(allTasksFromManager.toArray(), tasksFromFile.toArray());
    }

    @Test
    void shouldLoadStateFromFile() {
        FileBackedTaskManager testManager = FileBackedTaskManager.loadFromFile(new File(testFile));

        List<Task> allTasksFromTestManager = testManager.getTasksList();

        allTasksFromTestManager.addAll(fileBackedTaskManager.getEpicList());
        allTasksFromTestManager.addAll(fileBackedTaskManager.getSubtaskList());

        Assertions.assertArrayEquals(allTasksFromTestManager.toArray(), allTasksFromManager.toArray());
    }

    @Test
    void shouldSetIdCounterOfLoadedManagerDependsQuantityOfLoadedTasks() {
        FileBackedTaskManager testManager = FileBackedTaskManager.loadFromFile(new File(testFile));
        Task task = testManager.saveTask(testUtils.getTask());
        Assertions.assertEquals(5, task.getId());
    }
}
