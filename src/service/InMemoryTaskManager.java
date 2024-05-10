package service;

import model.Epic;
import model.Subtask;
import model.Task;
import model.Status;
import util.Managers;

import java.util.*;


public class InMemoryTaskManager implements TaskManager {

    private HistoryManager historyManager = Managers.getDefaultHistory();

    private int idCounter = 0;

    private HashMap<Integer, Task> allTasks = new HashMap<>();
    private HashMap<Integer, Epic> allEpics = new HashMap<>();
    private HashMap<Integer, Subtask> allSubtasks = new HashMap<>();


    @Override
    public List<Task> getTasksList() {
        return new ArrayList<>(allTasks.values());

    }


    @Override
    public List<Epic> getEpicList() {
        return new ArrayList<>(allEpics.values());

    }


    @Override
    public List<Subtask> getSubtaskList() {
        return new ArrayList<>(allSubtasks.values());

    }


    @Override
    public void deleteAllTasks() {
        allTasks.keySet().forEach(id -> historyManager.remove(id));
        allTasks.clear();
    }


    @Override
    public void deleteAllEpics() {
        allEpics.keySet().forEach(id -> historyManager.remove(id));
        allSubtasks.keySet().forEach(id -> historyManager.remove(id));
        allEpics.clear();
        allSubtasks.clear();
    }


    @Override
    public void deleteAllSubtask() {
        allSubtasks.keySet().forEach(id -> historyManager.remove(id));
        for (Subtask subtask : allSubtasks.values()) {
            Epic epic = subtask.getEpic();
            List<Subtask> EpicSubtasks = epic.getSubtasks();
            EpicSubtasks.clear();
            checkEpicStatus(epic);
        }
        allSubtasks.clear();
    }


    @Override
    public Task getTaskById(int id) {
        Task task = allTasks.get(id);
        historyManager.add(task);
        return task;
    }


    @Override
    public Epic getEpicById(int id) {
        Epic epic = allEpics.get(id);
        historyManager.add(epic);
        return epic;
    }

    @Override
    public Subtask getSubtaskById(int id) {
        Subtask subtask = allSubtasks.get(id);
        historyManager.add(subtask);
        return subtask;
    }


    @Override
    public Task saveTask(Task task) {
        task.setId(generateId());
        allTasks.put(task.getId(), task);
        return task;

    }


    @Override
    public Epic saveEpic(Epic epic) {
        epic.setId(generateId());
        allEpics.put(epic.getId(), epic);
        return epic;
    }


    @Override
    public Subtask saveSubtask(Subtask subtask) {
        subtask.setId(generateId());
        subtask.getEpic().getSubtasks().add(subtask);
        allSubtasks.put(subtask.getId(), subtask);
        checkEpicStatus(subtask.getEpic());
        return subtask;
    }


    @Override
    public void updateTask(Task updatedTask) {
        if (allTasks.containsKey(updatedTask.getId())) {
            allTasks.put(updatedTask.getId(), updatedTask);
        }
    }


    @Override
    public void updateEpic(Epic updatedEpic) {
        if (allEpics.containsKey(updatedEpic.getId())) {
            Epic saved = allEpics.get(updatedEpic.getId());
            saved.setName(updatedEpic.getName());
            saved.setDescription(updatedEpic.getDescription());
        }
    }


    @Override
    public void updateSubtask(Subtask updatedSubtask) {
        if (allSubtasks.containsKey(updatedSubtask.getId())) {
            Subtask oldSubtask = allSubtasks.put(updatedSubtask.getId(), updatedSubtask);
            Epic epic = updatedSubtask.getEpic();
            epic.getSubtasks().remove(oldSubtask);
            epic.getSubtasks().add(updatedSubtask);
            checkEpicStatus(epic);
        }
    }


    @Override
    public void deleteTaskById(int id) {
        historyManager.remove(id);
        allTasks.remove(id);
    }


    @Override
    public void deleteEpicById(int id) {
        Epic deletedEpic = allEpics.remove(id);
        historyManager.remove(id);
        List<Subtask> deletedEpicSubtasks = deletedEpic.getSubtasks();
        deletedEpicSubtasks.forEach(subtask -> allSubtasks.remove(subtask.getId()));
        deletedEpicSubtasks.forEach(subtask -> historyManager.remove(subtask.getId()));
    }


    @Override
    public void deleteSubtaskById(int id) {
        Subtask deletedSubtask = allSubtasks.remove(id);
        historyManager.remove(id);
        Epic epic = deletedSubtask.getEpic();
        List<Subtask> epicsSubtasks = epic.getSubtasks();
        epicsSubtasks.remove(deletedSubtask);
        checkEpicStatus(epic);
    }


    @Override
    public List<Subtask> getSubtaskByEpicId(int id) {
        return allEpics.get(id).getSubtasks();
    }

    @Override
    public List<Task> getHistory() {
        return historyManager.getHistory();
    }

    private void checkEpicStatus(Epic epic) {
        Set<Status> statuses = new HashSet<>();
        for (Subtask subtask : epic.getSubtasks()) {
            statuses.add(subtask.getStatus());
        }
        if (statuses.size() == 0) {
            epic.setStatus(Status.NEW);
        } else if (statuses.size() == 1) {
            epic.setStatus(statuses.iterator().next());
        } else {
            epic.setStatus(Status.IN_PROGRESS);
        }
    }

    private int generateId() {
        return ++idCounter;
    }


}
