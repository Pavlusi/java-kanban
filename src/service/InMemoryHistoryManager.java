package service;

import model.Task;

import java.util.*;

public class InMemoryHistoryManager implements HistoryManager {

    private HashMap<Integer, Node> history = new HashMap<>();
    Node first;
    Node last;

    public void add(Task task) {
        if (task != null) {
            Node node = history.get(task.getId());
            if (node != null) {
                removeNode(node);
            }
            linkLast(task);
        }
    }

    @Override
    public void remove(int id) {
        Node node = history.get(id);
        if (node != null) {
            removeNode(node);
        }
    }

    @Override
    public List<Task> getHistory() {
        ArrayList<Task> list = new ArrayList<>();
        Node current = first;
        while (current != null) {
            list.add(current.element);
            current = current.next;
        }
        return list;
    }

    private void removeNode(Node node) {
        final Node next = node.next;
        final Node prev = node.prev;
        if (prev == null) {
            first = next;
        } else {
            prev.next = next;
            node.prev = null;
        }
        if (next == null) {
            last = prev;
        } else {
            next.prev = prev;
            node.next = null;
        }
        history.remove(node.element.getId());
    }

    private void linkLast(Task task) {
        final Node l = last;
        final Node newNode = new Node(l, task, null);
        last = newNode;
        if (l == null) {
            first = newNode;
        } else {
            l.next = newNode;
        }
        history.put(task.getId(), newNode);
    }

    private static class Node {
        Node prev;
        Task element;
        Node next;

        public Node(Node prev, Task element, Node next) {
            this.prev = prev;
            this.element = element;
            this.next = next;
        }
    }
}
