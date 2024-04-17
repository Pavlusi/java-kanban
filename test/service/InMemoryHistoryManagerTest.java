package service;

import model.Epic;
import model.Subtask;
import model.Task;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import testUtils.TestUtils;
import util.Managers;

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
    void shouldAddTaskInEndOfList() {
        Task task = testUtils.getTask();
        inMemoryHistoryManager.add(task);
        List<Task> tasks = inMemoryHistoryManager.getHistory();

        Assertions.assertEquals(task, tasks.get(9));

    }

    @Test
    void shouldDeleteTaskFromBeginningListWithSize10WhenAddNewTask() {
        Task taskFromBeginningBeforeCapacityExceeds10 = inMemoryHistoryManager.getHistory().get(0);

        inMemoryHistoryManager.add(testUtils.getTask());

        Task taskFromBeginningAfterCapacityExceeds10 = inMemoryHistoryManager.getHistory().get(0);

        Assertions.assertNotEquals(taskFromBeginningBeforeCapacityExceeds10, taskFromBeginningAfterCapacityExceeds10
                , "Таск из начала списка не удалтился");
    }

    @Test
    void shouldKeepCapacity10WhenAddMoreThen10Tasks() {
        for (Task task : testUtils.getListTasksWithId(20)) {
            inMemoryHistoryManager.add(task);
        }

        Assertions.assertEquals(10, inMemoryHistoryManager.getHistory().size());
    }


    @Test
    void shouldAddAnyTaskTypeInHistoryList() {
        inMemoryHistoryManager.getHistory().clear();
        inMemoryHistoryManager.add(testUtils.getTask());
        inMemoryHistoryManager.add(testUtils.getEpic());
        inMemoryHistoryManager.add(testUtils.getSubtask(testUtils.getEpic()));
        Assertions.assertEquals(3, inMemoryHistoryManager.getHistory().size());
    }
}