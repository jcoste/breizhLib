package serializers;

import com.google.gson.*;
import models.UpdatableModel;

import java.lang.reflect.Type;
import java.util.Date;


public class    AbstractSerializer<T> implements JsonSerializer<T>, JsonDeserializer<T> {

    public String getFacultatifString(JsonObject jsonObject, String property) {
        try {
            return jsonObject.get(property).getAsString();
        } catch (Exception e) {
            return null;
        }
    }

    public boolean getFacultatifBoolean(JsonObject jsonObject, String property) {
        try {
            return jsonObject.get(property).getAsBoolean();
        } catch (Exception e) {
            return false;
        }
    }

    public JsonObject getFacultatifObject(JsonObject jsonObject, String property) {
        try {
            return jsonObject.get(property).getAsJsonObject();
        } catch (Exception e) {
            return null;
        }
    }


    public T deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public JsonElement serialize(UpdatableModel t, Type type, JsonSerializationContext jsonSerializationContext) {
        JsonObject jsonObj = new JsonObject();
        if(t.getLastMaj() != null ){
            jsonObj.addProperty("lastMaj",t.getLastMaj().getTime());
        }else{
           jsonObj.addProperty("lastMaj",new Date().getTime());
        }
        return jsonObj;
    }

    public boolean needUpdate(UpdatableModel model,JsonObject jsonObject ){
          return model.getLastMaj() == null || model.getLastMaj().getTime() < jsonObject.get("lastMaj").getAsInt();
    }

    public JsonElement serialize(Object t, Type type, JsonSerializationContext jsonSerializationContext) {
        return new JsonObject();
    }
}
