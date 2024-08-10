package util;

import model.*;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class TaskConverter {

    DateTimeFormatter writeFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy, HH:mm");

    public static String toString(Task task) {
        return task.getId() + "," + task.getTaskType() + "," + task.getName() + "," + task.getStatus()
                + "," + task.getDescription() + "," + (task.getEpic() != null ? task.getEpic().getId() : null) + "," + task.getDuration().toMinutes() + "," + task.getStartTime().toString() + "\n";
    }

    public static Task toTask(String str) {
        String[] fields = str.split(",");
        int id = Integer.parseInt(fields[0]);
        TaskType type = TaskType.valueOf(fields[1]);
        String name = fields[2];
        Status status = Status.valueOf(fields[3]);
        String description = fields[4];
        Duration duration = Duration.ofMinutes(Integer.parseInt(fields[6]));
        LocalDateTime startTime = LocalDateTime.parse(fields[7]);
        switch (type) {
            case TaskType.TASK:
                return new Task(id, name, status, description, startTime, duration);

            case TaskType.EPIC:
                return new Epic(id, name, status, description, startTime, duration);

            case TaskType.SUBTASK:
                return new Subtask(id, name, status, description, new Epic(
                        Integer.parseInt(fields[5]), null, null, null), startTime, duration);
        }
        return null;
    }


}
