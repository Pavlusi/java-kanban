package service;

import model.Task;

import java.util.LinkedList;
import java.util.List;

public class InMemoryHistoryManager implements HistoryManager {

    private LinkedList<Task> historyList = new LinkedList<>();

    public void add(Task task) {
        if (historyList.size() == 10) {
            historyList.pollFirst();
        }
        historyList.addLast(task);
    }

    @Override
    public List<Task> getHistory() {
        return historyList;
    }
}
