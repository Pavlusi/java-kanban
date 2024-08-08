package model;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Epic extends Task {

    private final List<Subtask> subtasks = new ArrayList<>();
    private LocalDateTime endTime;

    public Epic(String name, String description) {
        super(name, description);
        this.endTime = super.getStartTime().plus(super.getDuration());
    }

    public Epic(int id, String name, Status status, String description) {
        super(id, name, status, description);
        this.endTime = super.getStartTime().plus(super.getDuration());
    }

    public Epic(int id, String name, Status status, String description, LocalDateTime startTime, Duration duration) {
        super(id, name, status, description, startTime, duration);
        this.endTime = super.getStartTime().plus(super.getDuration());
    }

    public List<Subtask> getSubtasks() {
        return subtasks;
    }

    public void addSubtask(Subtask subtasks) {
        this.subtasks.add(subtasks);


    }

    public void removeSubtask(Subtask subtask) {
        this.subtasks.remove(subtask);

    }

    @Override
    public TaskType getTaskType() {
        return TaskType.EPIC;
    }

    @Override
    public LocalDateTime getEndTime() {
        return this.endTime;
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }

    @Override
    public String toString() {
        return "Epic{" +
                "id=" + super.getId() +
                ", name='" + super.getName() + '\'' +
                ", description='" + super.getDescription() + '\'' +
                ", status=" + super.getStatus() +
                '}';
    }
}
