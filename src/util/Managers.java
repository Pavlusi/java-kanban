package util;

import adapters.DurationAdapter;
import serializers.EpicSerializer;
import adapters.LocalDateTimeAdapter;
import serializers.SubtaskSerializer;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import model.Epic;
import model.Subtask;
import service.FileBackedTaskManager;
import service.HistoryManager;
import service.InMemoryHistoryManager;
import service.TaskManager;

import java.time.Duration;
import java.time.LocalDateTime;

public class Managers {

    private static final String pathToSaveFile = "resources/save.csv";


    public static TaskManager getDefault() {
        return new FileBackedTaskManager(pathToSaveFile);
    }

    public static HistoryManager getDefaultHistory() {
        return new InMemoryHistoryManager();
    }

    public static Gson getGson() {
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter());
        gsonBuilder.registerTypeAdapter(Duration.class, new DurationAdapter());
        gsonBuilder.registerTypeAdapter(Subtask.class, new SubtaskSerializer());
        gsonBuilder.registerTypeAdapter(Epic.class, new EpicSerializer());
        return gsonBuilder.create();
    }
}
