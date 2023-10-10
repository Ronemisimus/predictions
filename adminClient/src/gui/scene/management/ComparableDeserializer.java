package gui.scene.management;

import com.google.gson.*;
import java.lang.reflect.Type;

public class ComparableDeserializer implements JsonDeserializer<Comparable<?>> {
    @Override
    public Comparable<?> deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        if (json.isJsonPrimitive()) {
            JsonPrimitive jsonPrimitive = json.getAsJsonPrimitive();

            if (jsonPrimitive.isNumber()) {
                if (jsonPrimitive.getAsNumber() instanceof Integer) {
                    return jsonPrimitive.getAsInt();
                } else if (jsonPrimitive.getAsNumber() instanceof Float) {
                    return jsonPrimitive.getAsFloat();
                }
            } else if (jsonPrimitive.isBoolean()) {
                return jsonPrimitive.getAsBoolean();
            } else if (jsonPrimitive.isString()) {
                return jsonPrimitive.getAsString();
            }
        }

        // If the JSON element doesn't match any of the expected types, you can handle the error or return null as needed.
        // For example, you might throw an exception or return a default value.
        throw new JsonParseException("Invalid JSON format for Comparable<?>: " + json);
    }
}