package service;

import model.Task;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import testUtils.TestUtils;

import java.util.List;

class InMemoryHistoryManagerTest {

    private InMemoryHistoryManager inMemoryHistoryManager;

    private TestUtils testUtils;

    @BeforeEach
    public void init() {
        inMemoryHistoryManager = new InMemoryHistoryManager();
        testUtils = new TestUtils();
        for (Task task : testUtils.getListTasksWithId(10)) {
            inMemoryHistoryManager.add(task);
        }
    }

    @Test
    void shouldGetHistoryList() {
        Assertions.assertEquals(10, inMemoryHistoryManager.getHistory().size());
    }

    @Test
    void shouldAddTaskInHistoryManager() {
        Task task = testUtils.getTaskWithNewId();
        inMemoryHistoryManager.add(task);
        List<Task> tasks = inMemoryHistoryManager.getHistory();
        Assertions.assertEquals(task, tasks.get(tasks.size() - 1));

    }

    @Test
    void shouldKeepOrderOfAdding() {
        List<Task> tasksList = inMemoryHistoryManager.getHistory();
        for (int i = 0; i < tasksList.size(); i++) {
            Assertions.assertEquals(tasksList.get(i).getId(), i + 1);
        }
    }

    @Test
    void shouldRemoveTaskFromBeginningHistoryManager() {
        Task task = inMemoryHistoryManager.getHistory().get(0);
        inMemoryHistoryManager.remove(task.getId());
        Assertions.assertEquals(9, inMemoryHistoryManager.getHistory().size());
        Assertions.assertEquals(2, inMemoryHistoryManager.getHistory().get(0).getId());
    }

    @Test
    void shouldRemoveTaskFromMiddleHistoryManager() {
        Task task = inMemoryHistoryManager.getHistory().get(5);
        inMemoryHistoryManager.remove(task.getId());
        Assertions.assertEquals(9, inMemoryHistoryManager.getHistory().size());
        Assertions.assertEquals(7, inMemoryHistoryManager.getHistory().get(5).getId());
    }

    @Test
    void shouldRemoveTaskFromEndHistoryManager() {
        Task task = inMemoryHistoryManager.getHistory().get(9);
        inMemoryHistoryManager.remove(task.getId());
        Assertions.assertEquals(9, inMemoryHistoryManager.getHistory().size());
        Assertions.assertEquals(9, inMemoryHistoryManager.getHistory().get(8).getId());
    }

    @Test
    void shouldAddAnyTaskTypeInHistoryManager() {
        inMemoryHistoryManager.add(testUtils.getTaskWithNewId());
        inMemoryHistoryManager.add(testUtils.getEpicWithNewId());
        inMemoryHistoryManager.add(testUtils.getSubtaskWithNewId(testUtils.getEpic()));
        Assertions.assertEquals(13, inMemoryHistoryManager.getHistory().size());
    }

    @Test
    void shouldChangeTaskToNewInHistoryManagerIfHisIdAlreadyExists() {
        Task task1 = testUtils.getTaskWithNewId();
        Task task2 = testUtils.getTaskWithNewId();
        task2.setId(task1.getId());
        task2.setName("newName");

        inMemoryHistoryManager.add(task1);
        Assertions.assertEquals(11, inMemoryHistoryManager.getHistory().size());

        inMemoryHistoryManager.add(task2);
        Assertions.assertEquals(11, inMemoryHistoryManager.getHistory().size());
        Assertions.assertEquals(task2.getName(), inMemoryHistoryManager.getHistory().get(10).getName());

    }
}