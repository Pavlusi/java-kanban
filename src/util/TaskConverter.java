package util;

import model.*;

public class TaskConverter {

    static public String toString(Task task) {
        return task.getId() + "," + task.getTaskType() + "," + task.getName() + "," + task.getStatus()
                + "," + task.getDescription() + "," + (task.getEpic() != null ? task.getEpic().getId() : null) + "\n";
    }

    static public Task toTask(String str) {
        String[] fields = str.split(",");
        int id = Integer.parseInt(fields[0]);
        TaskType type = TaskType.valueOf(fields[1]);
        String name = fields[2];
        Status status = Status.valueOf(fields[3]);
        String description = fields[4];
        switch (type) {
            case TaskType.TASK:
                return new Task(id, name, status, description);

            case TaskType.EPIC:
                return new Epic(id, name, status, description);

            case TaskType.SUBTASK:
                return new Subtask(id, name, status, description, new Epic(
                        Integer.parseInt(fields[5]), null, null, null));
        }
        return null;
    }


}
