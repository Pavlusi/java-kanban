package testUtils;

import model.Epic;
import model.Status;
import model.Subtask;
import model.Task;

import java.util.ArrayList;
import java.util.List;

public class TestUtils {

    private int counter = 0;


    public Task getTask() {
        return new Task("testTaskName", "testDescription");
    }

    public Task getTaskWithNewId() {
        Task task = new Task("testName", "test");
        task.setId(++counter);
        return task;
    }

    public Task getUpdatedTaskWithStatusDone(Task task) {
        Task taskToReturn = new Task("newTaskName", "newDescription");
        task.setStatus(Status.DONE);
        taskToReturn.setId(task.getId());
        return taskToReturn;
    }

    public Epic getEpic() {
        return new Epic("testEpicName", "testDescription");
    }

    public Epic getEpicWithNewId() {
        Epic epic = new Epic("testName", "test");
        epic.setId(++counter);
        return epic;
    }

    public Epic getUpdatedEpic(Epic epic) {
        Epic epicToReturn = new Epic("newEpicName", "newDescription");
        epicToReturn.setId(epic.getId());
        epicToReturn.getSubtasks().addAll(epic.getSubtasks());
        return epicToReturn;
    }


    public Subtask getSubtask(Epic epic) {
        return new Subtask("testSubtaskName", "testDescription", epic);
    }

    public Subtask getSubtaskWithNewId(Epic epic) {
        Subtask subtask = new Subtask("testName", "test", epic);
        subtask.setId(++counter);
        return subtask;
    }

    public Subtask getUpdatedSubtaskWithStatusDone(Subtask subtask) {
        Subtask subtaskToReturn = new Subtask("newSubtaskName", "newDescription", null);
        subtaskToReturn.setId(subtask.getId());
        subtaskToReturn.setEpic(subtask.getEpic());
        subtaskToReturn.setStatus(Status.DONE);
        return subtaskToReturn;
    }

    public Subtask getUpdatedSubtaskWithStatusInProgress(Subtask subtask) {
        Subtask subtaskToReturn = new Subtask("newSubtaskName", "newDescription", null);
        subtaskToReturn.setId(subtask.getId());
        subtaskToReturn.setEpic(subtask.getEpic());
        subtaskToReturn.setStatus(Status.IN_PROGRESS);
        return subtaskToReturn;
    }

    public List<Task> getListTasksWithId(int numberOfTasks) {
        List<Task> tasks = new ArrayList<>(numberOfTasks);
        for (int i = 0; i < numberOfTasks; i++) {
            Task task = new Task("testTask" + (++counter), "testDescription");
            task.setId(i + 1);
            tasks.add(task);
        }
        return tasks;

    }

}


