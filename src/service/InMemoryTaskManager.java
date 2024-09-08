package service;

import exeptions.TaskNotFoundException;
import exeptions.TaskTimeCrossException;
import model.Epic;
import model.Status;
import model.Subtask;
import model.Task;
import util.Managers;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;


public class InMemoryTaskManager implements TaskManager {

    private final HistoryManager historyManager = Managers.getDefaultHistory();

    protected int idCounter = 0;
    protected HashMap<Integer, Task> allTasks = new HashMap<>();
    protected HashMap<Integer, Epic> allEpics = new HashMap<>();
    protected HashMap<Integer, Subtask> allSubtasks = new HashMap<>();

    protected TreeSet<Task> prioritizedTasks = new TreeSet<>(Comparator.comparing(Task::getStartTime));


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
            List<Subtask> epicSubtasks = epic.getSubtasks();
            epicSubtasks.clear();
            checkEpicStatus(epic);
            checkAndSetEpicTime(epic);
        }
        allSubtasks.clear();
    }


    @Override
    public Task getTaskById(int id) {
        if (allTasks.containsKey(id)) {
            Task task = allTasks.get(id);
            historyManager.add(task);
            return task;
        }
        throw new TaskNotFoundException("Задача с id:" + id + " не найдена");

    }


    @Override
    public Epic getEpicById(int id) {
        if (allEpics.containsKey(id)) {
            Epic epic = allEpics.get(id);
            historyManager.add(epic);
            return epic;
        }
        throw new TaskNotFoundException("Задача с id:" + id + " не найдена");

    }

    @Override
    public Subtask getSubtaskById(int id) {
        if (allSubtasks.containsKey(id)) {
            Subtask subtask = allSubtasks.get(id);
            historyManager.add(subtask);
            return subtask;
        }
        throw new TaskNotFoundException("Задача с id:" + id + " не найдена");
    }


    @Override
    public Task saveTask(Task task) {
        if (isTasksTimeCross(task)) {
            throw new TaskTimeCrossException("Задача " + task.getName() + " пересекается по времени с другой задачей");
        }
        task.setId(generateId());
        allTasks.put(task.getId(), task);
        prioritizedTasks.add(task);
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
        if (isTasksTimeCross(subtask)) {
            throw new TaskTimeCrossException("Задача " + subtask.getName() + " пересекается по времени с другой задачей");
        }
        subtask.setId(generateId());
        Epic epic = getEpicById(subtask.getEpic().getId());
        epic.addSubtask(subtask);
        prioritizedTasks.add(subtask);
        allSubtasks.put(subtask.getId(), subtask);
        checkEpicStatus(epic);
        checkAndSetEpicTime(epic);
        return subtask;

    }


    @Override
    public void updateTask(Task updatedTask) {
        if (allTasks.containsKey(updatedTask.getId())) {
            if (isTasksTimeCross(updatedTask)) {
                throw new TaskTimeCrossException("Задача " + updatedTask.getName() + " пересекается по времени с другой задачей");
            }
            Task saved = allTasks.get(updatedTask.getId());
            saved.setName(updatedTask.getName());
            saved.setStatus(updatedTask.getStatus());
            saved.setDuration(updatedTask.getDuration());
            saved.setDescription(updatedTask.getDescription());
            saved.setStartTime(updatedTask.getStartTime());
        } else {
            throw new TaskNotFoundException("Задача с id:" + updatedTask.getId() + " не найдена");
        }

    }


    @Override
    public void updateEpic(Epic updatedEpic) {
        if (allEpics.containsKey(updatedEpic.getId())) {
            Epic saved = allEpics.get(updatedEpic.getId());
            saved.setName(updatedEpic.getName());
            saved.setDescription(updatedEpic.getDescription());
        } else {
            throw new TaskNotFoundException("Задача с id:" + updatedEpic.getId() + " не найдена");
        }
    }


    @Override
    public void updateSubtask(Subtask updatedSubtask) {

        if (allSubtasks.containsKey(updatedSubtask.getId())) {
            if (isTasksTimeCross(updatedSubtask)) {
                throw new TaskTimeCrossException("Задача " + updatedSubtask.getName() + " пересекается по времени с другой задачей");
            }
            Subtask oldSubtask = allSubtasks.get(updatedSubtask.getId());
            oldSubtask.setName(updatedSubtask.getName());
            oldSubtask.setStatus(updatedSubtask.getStatus());
            oldSubtask.setDescription(updatedSubtask.getDescription());
            oldSubtask.setDuration(updatedSubtask.getDuration());
            oldSubtask.setStartTime(updatedSubtask.getStartTime());
            Epic epic = getEpicById(updatedSubtask.getEpic().getId());
            oldSubtask.setEpic(epic);
            checkEpicStatus(epic);
            checkAndSetEpicTime(epic);
        } else {
            throw new TaskNotFoundException("Задача с id:" + updatedSubtask.getId() + " не найдена");
        }

    }


    @Override
    public void deleteTaskById(int id) {
        if (allTasks.containsKey(id)) {
            historyManager.remove(id);
            allTasks.remove(id);
        } else {
            throw new TaskNotFoundException("Задача с id:" + id + " не найдена");
        }
    }


    @Override
    public void deleteEpicById(int id) {
        if (allEpics.containsKey(id)) {
            Epic deletedEpic = allEpics.remove(id);
            historyManager.remove(id);
            List<Subtask> deletedEpicSubtasks = deletedEpic.getSubtasks();
            deletedEpicSubtasks.forEach(subtask -> allSubtasks.remove(subtask.getId()));
            deletedEpicSubtasks.forEach(subtask -> historyManager.remove(subtask.getId()));
        } else {
            throw new TaskNotFoundException("Задача с id: " + id + "не найдена");
        }
    }


    @Override
    public void deleteSubtaskById(int id) {
        if (allSubtasks.containsKey(id)) {
            Subtask deletedSubtask = allSubtasks.remove(id);
            historyManager.remove(id);
            Epic epic = deletedSubtask.getEpic();
            epic.removeSubtask(deletedSubtask);
            checkEpicStatus(epic);
            checkAndSetEpicTime(epic);
        } else {
            throw new TaskNotFoundException("Задача с id:" + id + "не найдена");
        }


    }


    @Override
    public List<Subtask> getSubtaskByEpicId(int id) {
        if (allEpics.containsKey(id)) {
            return allEpics.get(id).getSubtasks();
        } else {
            throw new TaskNotFoundException("Задача с id:" + id + "не найдена");
        }
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
        if (statuses.isEmpty()) {
            epic.setStatus(Status.NEW);
        } else if (statuses.size() == 1) {
            epic.setStatus(statuses.iterator().next());
        } else {
            epic.setStatus(Status.IN_PROGRESS);
        }
    }

    private void checkAndSetEpicTime(Epic epic) {
        if (epic.getSubtasks().isEmpty()) {
            epic.setStartTime(LocalDateTime.now());
            epic.setDuration(Duration.ofMinutes(10));
            epic.setEndTime(epic.getStartTime().plus(epic.getDuration()));

        } else if (epic.getSubtasks().size() == 1) {
            Subtask subtask = epic.getSubtasks().getFirst();
            epic.setStartTime(subtask.getStartTime());
            epic.setDuration(subtask.getDuration());
            epic.setEndTime(subtask.getEndTime());

        } else {
            Duration duration = Duration.ofMinutes(0);
            for (Subtask subtask : epic.getSubtasks()) {
                duration = duration.plus(subtask.getDuration());
                if (subtask.getStartTime().isBefore(epic.getStartTime())) {
                    epic.setStartTime(subtask.getStartTime());
                }
                if (subtask.getEndTime().isAfter(epic.getEndTime())) {
                    epic.setEndTime(subtask.getEndTime());
                }
            }
            epic.setDuration(duration);
        }
    }

    private boolean isTasksTimeCross(Task task) {
        return prioritizedTasks.stream()
                .filter(t -> task.getId() != t.getId())
                .anyMatch(t -> (task.getStartTime().isEqual(t.getEndTime()) || task.getStartTime().isBefore(t.getEndTime())) &&
                        (task.getEndTime().isEqual(t.getStartTime()) || task.getEndTime().isAfter(t.getStartTime())));
    }

    @Override
    public List<Task> getPrioritizedTasks() {
        return new ArrayList<>(prioritizedTasks);
    }

    private int generateId() {
        return ++idCounter;
    }


}
