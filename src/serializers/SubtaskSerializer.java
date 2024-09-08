package serializers;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import model.Epic;
import model.Subtask;

import java.lang.reflect.Type;
import java.time.format.DateTimeFormatter;

public class SubtaskSerializer implements JsonSerializer<Subtask> {
    private static final DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy.MM.dd HH:mm");

    @Override
    public JsonElement serialize(Subtask subtask, Type typeOfSrc, JsonSerializationContext context) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("id", subtask.getId());
        jsonObject.addProperty("name", subtask.getName());
        jsonObject.addProperty("description", subtask.getDescription());
        jsonObject.addProperty("status", subtask.getStatus().toString());
        jsonObject.addProperty("startTime", subtask.getStartTime().format(dtf));
        jsonObject.addProperty("duration", subtask.getDuration().toMinutes());
        jsonObject.addProperty("endTime", subtask.getEndTime().format(dtf));

        Epic epic = subtask.getEpic();
        if (epic != null) {
            JsonObject epicJson = new JsonObject();
            epicJson.addProperty("id", epic.getId());
            epicJson.addProperty("name", epic.getName());
            epicJson.addProperty("description", epic.getDescription());
            epicJson.addProperty("status", epic.getStatus().toString());
            jsonObject.add("epic", epicJson);
        }

        return jsonObject;
    }
}