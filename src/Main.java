import model.Epic;
import model.Status;
import model.Subtask;
import model.Task;
import service.TaskManager;
import util.Managers;

public class Main {


    public static void main(String[] args) {
        TaskManager taskManager = Managers.getDefault();

        Task task1 =taskManager.saveTask(new Task("TaskName1", "Task"));
        Task task2 = taskManager.saveTask(new Task("TaskName2", "Task"));

        Epic epic1 = taskManager.saveEpic(new Epic("EpicName1", "Epic"));

        Subtask subtask1 = taskManager.saveSubtask(new Subtask("SubtaskName1", "Subtask", epic1));
        Subtask subtask2 = taskManager.saveSubtask(new Subtask("SubtaskName2", "Subtask", epic1));

        Epic epic2 = taskManager.saveEpic(new Epic("EpicName2", "Epic"));
        Subtask subtask3 = taskManager.saveSubtask(new Subtask("SubtaskName3", "Subtask", epic2));

        System.out.println(taskManager.getTasksList());
        System.out.println(taskManager.getEpicList());
        System.out.println(taskManager.getSubtaskList());

        task1.setStatus(Status.DONE);
        taskManager.updateTask(task1);

        task2.setStatus(Status.IN_PROGRESS);
        taskManager.updateTask(task2);

        subtask1.setStatus(Status.DONE);
        taskManager.updateSubtask(subtask1);

        subtask2.setStatus(Status.IN_PROGRESS);
        taskManager.updateSubtask(subtask2);

        subtask3.setStatus(Status.DONE);
        taskManager.updateSubtask(subtask3);

        System.out.println(task1);
        System.out.println(task2);

        System.out.println(epic1);
        System.out.println(subtask1);
        System.out.println(subtask2);

        System.out.println(epic2);
        System.out.println(subtask3);

        taskManager.deleteTaskById(task1.getId());
        taskManager.deleteEpicById(epic2.getId());

    }

}
