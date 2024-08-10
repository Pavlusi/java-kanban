package model;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import testUtils.TestUtils;

public class EpicTest {

    private final TestUtils testUtils = new TestUtils();

    @Test
    void shouldAddSubtaskToList() {
        Epic epic = testUtils.getEpic();
        Subtask subtask1 = testUtils.getSubtask(epic);
        epic.addSubtask(subtask1);

        Assertions.assertEquals(1, epic.getSubtasks().size());
    }
}
