package adapters;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;
import java.time.Duration;

public class DurationAdapter extends TypeAdapter<Duration> {

    @Override
    public void write(final JsonWriter jsonWriter, final Duration duration) throws IOException {
        if (duration == null) {
            jsonWriter.value("null");
        }
        jsonWriter.value(duration.toMinutes());

    }

    @Override
    public Duration read(final JsonReader jsonReader) throws IOException {
        if (jsonReader.nextString().equals("null"))  {
            return null;
        }
        return Duration.ofMinutes(Long.parseLong(jsonReader.nextString()));
    }
}

