package model;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class TaskTest {

    @Test
    void TasksWithSameIdShouldBeEquals() {
        int idToSet = 1;
        Task task1 = new Task("name1", "dis");
        task1.setId(idToSet);

        Task task2 = new Task("name2", "dis");
        task2.setId(idToSet);

        Assertions.assertEquals(task1, task2);
    }
}