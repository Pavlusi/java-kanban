package util;

import service.FileBackedTaskManager;
import service.HistoryManager;
import service.InMemoryHistoryManager;
import service.TaskManager;

public class Managers {

    private static final String pathToSaveFile = "resources/save.csv";


    public static TaskManager getDefault() {
        return new FileBackedTaskManager(pathToSaveFile);
    }

    public static HistoryManager getDefaultHistory() {
        return new InMemoryHistoryManager();
    }
}
