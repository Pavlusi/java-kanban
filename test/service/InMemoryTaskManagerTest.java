package service;

import model.Epic;
import model.Status;
import model.Subtask;
import model.Task;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import testUtils.TestUtils;

import java.util.List;

class InMemoryTaskManagerTest {

    private InMemoryTaskManager inMemoryTaskManager;
    private final TestUtils testUtils = new TestUtils();

    private Task task1;

    private Epic epic1;

    private Subtask subtask1;

    private Subtask subtask2;


    @BeforeEach
    void init() {
        inMemoryTaskManager = new InMemoryTaskManager();
        task1 = inMemoryTaskManager.saveTask(testUtils.getTaskWithNewId());
        epic1 = inMemoryTaskManager.saveEpic(testUtils.getEpicWithNewId());
        subtask1 = inMemoryTaskManager.saveSubtask(testUtils.getSubtaskWithNewId(epic1));
        subtask2 = inMemoryTaskManager.saveSubtask(testUtils.getSubtaskWithNewId(epic1));

    }

    @Test
    void shouldGetTasksList() {
        List<Task> tasks = inMemoryTaskManager.getTasksList();
        Assertions.assertEquals(1, tasks.size());
        Assertions.assertEquals(task1, tasks.get(0));
    }

    @Test
    void shouldGetEpicList() {
        List<Epic> epics = inMemoryTaskManager.getEpicList();
        Assertions.assertEquals(1, inMemoryTaskManager.getEpicList().size());
        Assertions.assertEquals(epic1, epics.get(0));
    }

    @Test
    void shouldGetSubtaskList() {
        List<Subtask> subtasks = inMemoryTaskManager.getSubtaskList();
        Assertions.assertEquals(2, inMemoryTaskManager.getSubtaskList().size());
        Assertions.assertEquals(subtask1, subtasks.get(0));
        Assertions.assertEquals(subtask2, subtasks.get(1));
    }

    @Test
    void shouldDeleteAllTasks() {
        inMemoryTaskManager.deleteAllTasks();
        Assertions.assertEquals(0, inMemoryTaskManager.getTasksList().size());
    }

    @Test
    void shouldDeleteAllEpicsAndLinkedSubtasks() {
        inMemoryTaskManager.deleteAllEpics();
        Assertions.assertEquals(0, inMemoryTaskManager.getEpicList().size());
        Assertions.assertEquals(0, inMemoryTaskManager.getSubtaskList().size());

    }

    @Test
    void shouldDeleteAllSubtask() {
        inMemoryTaskManager.deleteAllSubtask();

        Assertions.assertEquals(0, inMemoryTaskManager.getSubtaskList().size());
    }

    @Test
    void shouldGetTaskById() {
        Task fromManager = inMemoryTaskManager.getTaskById(task1.getId());

        Assertions.assertNotNull(fromManager, "Задача не найдена");
        Assertions.assertEquals(task1, fromManager, "Задачи не совпадают");
    }

    @Test
    void shouldGetEpicById() {
        Epic fromManager = inMemoryTaskManager.getEpicById(epic1.getId());

        Assertions.assertNotNull(fromManager, "Eпик не найден");
        Assertions.assertEquals(epic1, fromManager, "Епики не совпадают");
    }

    @Test
    void shouldGetSubtaskById() {
        Subtask fromManager = inMemoryTaskManager.getSubtaskById(subtask1.getId());

        Assertions.assertNotNull(fromManager, "Субтакск не найден");
        Assertions.assertEquals(subtask1, fromManager, "Субтаски не совпадают");
    }

    @Test
    void shouldSaveNewTask() {
        Task task1 = inMemoryTaskManager.saveTask(testUtils.getTask());

        Assertions.assertNotNull(inMemoryTaskManager.getTaskById(task1.getId()));
    }

    @Test
    void shouldSaveNewEpic() {
        Epic epic1 = inMemoryTaskManager.saveEpic(testUtils.getEpic());

        Assertions.assertNotNull(inMemoryTaskManager.getEpicById(epic1.getId()));
    }

    @Test
    void shouldSaveNewSubtask() {
        Subtask subtask = inMemoryTaskManager.saveSubtask(testUtils.getSubtask(epic1));

        Assertions.assertNotNull(inMemoryTaskManager.getSubtaskById(subtask.getId()));
    }

    @Test
    void shouldUpdateTask() {
        Task taskFromManager = inMemoryTaskManager.getTaskById(task1.getId());
        Task updatedTask = testUtils.getUpdatedTaskWithStatusDone(taskFromManager);
        inMemoryTaskManager.updateTask(updatedTask);
        taskFromManager = inMemoryTaskManager.getTaskById(updatedTask.getId());

        Assertions.assertEquals(updatedTask.getStatus(), taskFromManager.getStatus());
        Assertions.assertEquals(updatedTask.getName(), taskFromManager.getName());
        Assertions.assertEquals(updatedTask.getDescription(), taskFromManager.getDescription());
    }

    @Test
    void shouldUpdateEpic() {
        Epic epicFromManager = inMemoryTaskManager.getEpicById(epic1.getId());
        Epic updatedEpic = testUtils.getUpdatedEpic(epicFromManager);
        inMemoryTaskManager.updateEpic(updatedEpic);
        epicFromManager = inMemoryTaskManager.getEpicById(updatedEpic.getId());

        Assertions.assertEquals(epicFromManager.getName(), updatedEpic.getName());
        Assertions.assertEquals(epicFromManager.getDescription(), updatedEpic.getDescription());
        Assertions.assertArrayEquals(epicFromManager.getSubtasks().toArray(), updatedEpic.getSubtasks().toArray());
        Assertions.assertEquals(epicFromManager.getStatus(), updatedEpic.getStatus());
    }

    @Test
    void shouldUpdateSubtask() {
        Subtask subtaskFromManager = inMemoryTaskManager.getSubtaskById(subtask1.getId());
        Subtask updatedSubtask = testUtils.getUpdatedSubtaskWithStatusDone(subtaskFromManager);
        inMemoryTaskManager.updateSubtask(updatedSubtask);
        subtaskFromManager = inMemoryTaskManager.getSubtaskById(updatedSubtask.getId());

        Assertions.assertEquals(updatedSubtask.getName(), subtaskFromManager.getName());
        Assertions.assertEquals(updatedSubtask.getDescription(), subtaskFromManager.getDescription());
        Assertions.assertEquals(updatedSubtask.getEpic(), subtaskFromManager.getEpic());
        Assertions.assertEquals(updatedSubtask.getStatus(), subtaskFromManager.getStatus());

    }

    @Test
    void shouldDeleteTaskById() {
        inMemoryTaskManager.deleteTaskById(task1.getId());

        Assertions.assertNull(inMemoryTaskManager.getTaskById(task1.getId()));
    }

    @Test
    void shouldDeleteEpicById() {
        inMemoryTaskManager.deleteEpicById(epic1.getId());

        Assertions.assertNull(inMemoryTaskManager.getEpicById(epic1.getId()));
    }

    @Test
    void shouldDeleteSubtaskById() {
        inMemoryTaskManager.deleteSubtaskById(subtask1.getId());

        Assertions.assertNull(inMemoryTaskManager.getSubtaskById(subtask1.getId()));
    }

    @Test
    void shouldGetSubtaskListByEpicId() {
        List<Subtask> subtasks = inMemoryTaskManager.getSubtaskByEpicId(epic1.getId());
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
        inMemoryTaskManager.updateSubtask(testUtils.getUpdatedSubtaskWithStatusInProgress(subtask1));
        Assertions.assertEquals(Status.IN_PROGRESS, epic1.getStatus());
    }


    private void epicShouldHasStatusInProgressWhenOneOfHisSubtaskHasStatusDoneOtherNew() {
        inMemoryTaskManager.updateSubtask(testUtils.getUpdatedSubtaskWithStatusDone(subtask1));
        Assertions.assertEquals(Status.IN_PROGRESS, epic1.getStatus());
    }


    private void epicShouldHasStatusDoneWhenAllSubtaskHaveStatusDone() {
        inMemoryTaskManager.updateSubtask(testUtils.getUpdatedSubtaskWithStatusDone(subtask1));
        inMemoryTaskManager.updateSubtask(testUtils.getUpdatedSubtaskWithStatusDone(subtask2));
        Assertions.assertEquals(Status.DONE, epic1.getStatus());
    }


    private void epicShouldChangeStatusFromDoneToNewAfterDeleteListHisSubtaskWithStatusDone() {
        inMemoryTaskManager.updateSubtask(testUtils.getUpdatedSubtaskWithStatusDone(subtask1));
        inMemoryTaskManager.updateSubtask(testUtils.getUpdatedSubtaskWithStatusDone(subtask2));

        inMemoryTaskManager.deleteAllSubtask();

        Assertions.assertEquals(Status.NEW, epic1.getStatus());
    }

    @Test
    void shouldIncreaseCounterIdWhenSaveNewTask() {
        Task task = inMemoryTaskManager.saveTask(testUtils.getTask());

        Assertions.assertEquals(5, task.getId());
    }


}