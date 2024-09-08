package model;


import java.time.Duration;
import java.time.LocalDateTime;

public class Subtask extends Task {

    private Epic epic;

    public Subtask() {
    }

    public Subtask(String name, String description, Epic epic) {
        super(name, description);
        this.epic = epic;
    }

    public Subtask(int id, String name, Status status, String description, Epic epic) {
        super(id, name, status, description);
        this.epic = epic;
    }

    public Subtask(int id, String name, Status status, String description, Epic epic, LocalDateTime startTime, Duration duration) {
        super(id, name, status, description, startTime, duration);
        this.epic = epic;
    }

    @Override
    public Epic getEpic() {
        return epic;
    }

    public void setEpic(Epic epic) {
        this.epic = epic;
    }

    @Override
    public TaskType getTaskType() {
        return TaskType.SUBTASK;
    }

    @Override
    public String toString() {
        return "Subtask{" +
                "id=" + super.getId() +
                ", name='" + super.getName() + '\'' +
                ", description='" + super.getDescription() + '\'' +
                ", status=" + super.getStatus() +
                ", epic=" + epic +
                '}';
    }
}

