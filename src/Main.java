import model.Epic;
import model.Subtask;
import model.Task;
import service.FileBackedTaskManager;

import java.io.File;
import java.util.List;

public class Main {


    public static void main(String[] args) {

        FileBackedTaskManager taskManager1 = new FileBackedTaskManager("resources/save.csv");

        Task task = taskManager1.saveTask(new Task("testTask", "test"));
        Epic epic = taskManager1.saveEpic(new Epic("testEpic", "test"));
        Subtask subtask = taskManager1.saveSubtask(new Subtask("testSubtask", "test", epic));

        FileBackedTaskManager manager2 = FileBackedTaskManager.loadFromFile(new File("resources/save.csv"));

        List<Task> allTasks = manager2.getTasksList();
        allTasks.addAll(manager2.getEpicList());
        allTasks.addAll(manager2.getSubtaskList());

        allTasks.forEach(System.out::println);


    }

}
