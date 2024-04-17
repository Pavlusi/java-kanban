package service;

import model.Epic;
import model.Subtask;
import model.Task;

import java.util.List;

public interface TaskManager {
    List<Task> getTasksList();

    List<Epic> getEpicList();

    List<Subtask> getSubtaskList();

    void deleteAllTasks();

    void deleteAllEpics();

    void deleteAllSubtask();

    Task getTaskById(int id);

    Epic getEpicById(int id);

    Subtask getSubtaskById(int id);

    Task saveTask(Task task);

    Epic saveEpic(Epic epic);

    Subtask saveSubtask(Subtask subtask);

    void updateTask(Task updatedTask);

    void updateEpic(Epic updatedEpic);

    void updateSubtask(Subtask updatedSubtask);

    void deleteTaskById(int id);

    void deleteEpicById(int id);

    void deleteSubtaskById(int id);

    List<Subtask> getSubtaskByEpicId(int id);

}
