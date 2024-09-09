package serializers;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import model.Epic;

import java.lang.reflect.Type;
import java.time.format.DateTimeFormatter;

public class EpicSerializer implements JsonSerializer<Epic> {
    private static final DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy.MM.dd HH:mm");

    @Override
    public JsonElement serialize(Epic epic, Type typeOfSrc, JsonSerializationContext context) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("id", epic.getId());
        jsonObject.addProperty("name", epic.getName());
        jsonObject.addProperty("description", epic.getDescription());
        jsonObject.addProperty("status", epic.getStatus().toString());
        jsonObject.addProperty("startTime", epic.getStartTime().format(dtf));
        jsonObject.addProperty("duration", epic.getDuration().toMinutes());
        jsonObject.addProperty("endTime", epic.getEndTime().format(dtf));

        JsonElement subtasks = context.serialize(epic.getSubtasks());
        jsonObject.add("subtasks", subtasks);

        return jsonObject;
    }
}
