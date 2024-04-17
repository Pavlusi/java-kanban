package util;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import service.InMemoryTaskManager;
import service.TaskManager;

class ManagersTest {

    @Test
    void shouldGetDefaultTaskManager() {
        Assertions.assertNotNull(Managers.getDefault());
    }

    @Test
    void shouldGetDefaultHistoryManager() {
        Assertions.assertNotNull(Managers.getDefaultHistory());
    }
}