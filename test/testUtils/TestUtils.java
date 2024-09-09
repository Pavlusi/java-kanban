package testUtils;

import model.Epic;
import model.Status;
import model.Subtask;
import model.Task;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class TestUtils {

    private int counter = 0;
    private int minutesCounter = 0;


    public Task getTask() {
        Task task = new Task("testTaskName", "testDescription");
        task.setStartTime(LocalDateTime.now().plusMinutes(minutesCounter));
        minutesCounter = minutesCounter + 120;
        return task;
    }

    public Task getTaskWithSameTime(Task task) {
        Task taskToReturn = new Task("taskWithSameTime", "testWithSameTime");
        taskToReturn.setStartTime(task.getStartTime());
        taskToReturn.setDuration(task.getDuration());
        return taskToReturn;
    }


    public Task getTaskWithNewId() {
        Task task = new Task("testName", "test");
        task.setId(++counter);
        task.setStartTime(LocalDateTime.now().plusMinutes(minutesCounter));
        minutesCounter = minutesCounter + 120;
        return task;
    }

    public Task getUpdatedTaskWithStatusDone(Task task) {
        Task taskToReturn = new Task("newTaskName", "newDescription");
        taskToReturn.setStatus(Status.DONE);
        taskToReturn.setId(task.getId());
        taskToReturn.setStartTime(LocalDateTime.now().plusMinutes(minutesCounter));
        minutesCounter = minutesCounter + 120;
        return taskToReturn;
    }

    public Epic getEpic() {
        Epic epic = new Epic("testEpicName", "testDescription");
        epic.setEndTime(epic.getStartTime().plus(epic.getDuration()));
        return epic;
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
        epicToReturn.setEndTime(epic.getStartTime().plus(epic.getDuration()));
        return epicToReturn;
    }


    public Subtask getSubtask(Epic epic) {
        Subtask subtask = new Subtask("testSubtaskName", "testDescription", epic);
        subtask.setStartTime(LocalDateTime.now().plusMinutes(minutesCounter));
        minutesCounter = minutesCounter + 120;
        return subtask;
    }

    public Subtask getSubtaskWithSameTime(Subtask subtask) {
        Subtask subtaskToReturn = new Subtask("testSubtask", "withSameTime", subtask.getEpic());
        subtaskToReturn.setStartTime(subtask.getStartTime());
        subtaskToReturn.setDuration(subtask.getDuration());
        return subtaskToReturn;
    }

    public Subtask getSubtaskWithNewId(Epic epic) {
        Subtask subtask = new Subtask("testName", "test", epic);
        subtask.setId(++counter);
        subtask.setStartTime(LocalDateTime.now().plusMinutes(minutesCounter));
        minutesCounter = minutesCounter + 120;
        return subtask;
    }

    public Subtask getUpdatedSubtaskWithStatusDone(Subtask subtask) {
        Subtask subtaskToReturn = new Subtask("newSubtaskName", "newDescription", null);
        subtaskToReturn.setId(subtask.getId());
        subtaskToReturn.setEpic(subtask.getEpic());
        subtaskToReturn.setStatus(Status.DONE);
        subtaskToReturn.setStartTime(subtask.getStartTime());
        return subtaskToReturn;
    }

    public Subtask getUpdatedSubtaskWithStatusInProgress(Subtask subtask) {
        Subtask subtaskToReturn = new Subtask("newSubtaskName", "newDescription", null);
        subtaskToReturn.setId(subtask.getId());
        subtaskToReturn.setEpic(subtask.getEpic());
        subtaskToReturn.setStatus(Status.IN_PROGRESS);
        subtaskToReturn.setStartTime(subtask.getStartTime());
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


