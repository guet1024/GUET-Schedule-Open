package com.telephone.coursetable.Gson.Adapters;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import java.lang.reflect.Type;

public class NoNullStringAdapter implements JsonSerializer<String>, JsonDeserializer<String> {
    @Override
    public String deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        if (json == null){
            return "";
        }
        try {
            return json.getAsString();
        }catch (ClassCastException e){
            return "TELEPHONE_GSON_STRING_ADAPTER_ERROR_NOT_A_STRING";
        }catch (IllegalStateException e){
            return "TELEPHONE_GSON_STRING_ADAPTER_ERROR_IS_AN_ARRAY";
        }
    }

    @Override
    public JsonElement serialize(String src, Type typeOfSrc, JsonSerializationContext context) {
        if (src == null){
            return new JsonPrimitive("");
        }
        return new JsonPrimitive(src);
    }
}
