package service;

import exeptions.ManagerLoadException;
import exeptions.ManagerSaveException;
import model.Epic;
import model.Subtask;
import model.Task;
import util.TaskConverter;

import java.io.*;
import java.util.List;

public class FileBackedTaskManager extends InMemoryTaskManager {
    private final String pathToSaveFile;

    public FileBackedTaskManager(String pathToSaveFile) {
        this.pathToSaveFile = pathToSaveFile;
    }


    @Override
    public Task saveTask(Task task) {
        Task task1 = super.saveTask(task);
        save();
        return task1;
    }

    @Override
    public Epic saveEpic(Epic epic) {
        Epic epic1 = super.saveEpic(epic);
        save();
        return epic1;
    }

    @Override
    public Subtask saveSubtask(Subtask subtask) {
        Subtask subtask1 = super.saveSubtask(subtask);
        save();
        return subtask1;
    }

    @Override
    public void updateTask(Task updatedTask) {
        super.updateTask(updatedTask);
        save();
    }

    @Override
    public void updateEpic(Epic updatedEpic) {
        super.updateEpic(updatedEpic);
        save();
    }

    @Override
    public void updateSubtask(Subtask updatedSubtask) {
        super.updateSubtask(updatedSubtask);
        save();
    }

    @Override
    public void deleteTaskById(int id) {
        super.deleteTaskById(id);
        save();
    }

    @Override
    public void deleteEpicById(int id) {
        super.deleteEpicById(id);
        save();
    }

    @Override
    public void deleteSubtaskById(int id) {
        super.deleteSubtaskById(id);
        save();
    }

    @Override
    public void deleteAllTasks() {
        super.deleteAllTasks();
        save();
    }

    @Override
    public void deleteAllEpics() {
        super.deleteAllEpics();
        save();
    }

    @Override
    public void deleteAllSubtask() {
        super.deleteAllSubtask();
        save();
    }


    private void save() throws ManagerSaveException {
        try (FileWriter fileWriter = new FileWriter(pathToSaveFile)) {

            List<Task> allTasks = super.getTasksList();

            allTasks.addAll(super.getEpicList());
            allTasks.addAll(super.getSubtaskList());

            fileWriter.write("id,type,name,status,description,epic, duration, startTime \n");

            for (Task task : allTasks) {
                fileWriter.write(TaskConverter.toString(task));
            }
        } catch (IOException e) {
            throw new ManagerSaveException("Ошибка сохранения состояния менеджера в файл");
        }
    }

    public static FileBackedTaskManager loadFromFile(File file) {
        FileBackedTaskManager taskManager = new FileBackedTaskManager(file.getPath());
        int maxId = 0;
        try (BufferedReader bufferedReader = new BufferedReader(new FileReader(file.getAbsolutePath()))) {
            bufferedReader.readLine();

            while (bufferedReader.ready()) {
                Task task = TaskConverter.toTask(bufferedReader.readLine());

                if (task.getId() > maxId) {
                    maxId = task.getId();
                }

                if (task instanceof Epic) {
                    taskManager.allEpics.put(task.getId(), (Epic) task);
                } else if (task instanceof Subtask subtask) {
                    Epic epic = taskManager.allEpics.get(subtask.getEpic().getId());
                    subtask.setEpic(epic);
                    epic.getSubtasks().add(subtask);

                    taskManager.allSubtasks.put(subtask.getId(), subtask);
                    taskManager.prioritizedTasks.add(task);
                } else {
                    taskManager.allTasks.put(task.getId(), task);
                    taskManager.prioritizedTasks.add(task);
                }
            }
            taskManager.idCounter = maxId;

        } catch (IOException e) {
            throw new ManagerLoadException("Ошибка загрузки состояния менеджера из файла");
        }
        return taskManager;

    }

}
