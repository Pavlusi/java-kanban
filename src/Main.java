import model.Epic;
import model.Subtask;
import model.Task;
import service.InMemoryTaskManager;

public class Main {


    public static void main(String[] args) {
     InMemoryTaskManager taskManager = new InMemoryTaskManager();

     Task task1 = taskManager.saveTask(new Task("testTask1", "test"));
     Task task2 = taskManager.saveTask(new Task("testTask2", "test"));

     Epic epic1 = taskManager.saveEpic(new Epic("testEpic1", "test"));

     Subtask subtask1 = taskManager.saveSubtask(new Subtask("testSubtask1", "test", epic1));
     Subtask subtask2 = taskManager.saveSubtask(new Subtask("testSubtask2", "test", epic1));
     Subtask subtask3 = taskManager.saveSubtask(new Subtask("testSubtask3", "test", epic1));

     Epic epic2 = taskManager.saveEpic(new Epic("testEpic2", "test"));

     taskManager.getTaskById(task2.getId());
     taskManager.getTaskById(task1.getId());
     taskManager.getTaskById(task1.getId());
     taskManager.getTaskById(task2.getId());
     System.out.println(taskManager.getHistory());

     taskManager.getEpicById(epic1.getId());
     taskManager.getEpicById(epic2.getId());
     taskManager.getEpicById(epic2.getId());
     taskManager.getEpicById(epic1.getId());
     System.out.println(taskManager.getHistory());

     taskManager.getSubtaskById(subtask1.getId());
     taskManager.getSubtaskById(subtask3.getId());
     taskManager.getSubtaskById(subtask2.getId());
     taskManager.getSubtaskById(subtask2.getId());
     taskManager.getSubtaskById(subtask1.getId());
     taskManager.getSubtaskById(subtask3.getId());
     System.out.println(taskManager.getHistory());

     taskManager.deleteTaskById(task1.getId());
     System.out.println(taskManager.getHistory());

     taskManager.deleteEpicById(epic1.getId());
     System.out.println(taskManager.getHistory());


    }

}
