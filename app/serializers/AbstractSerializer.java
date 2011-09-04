package serializers;

import com.google.gson.JsonObject;


public class AbstractSerializer {

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


}
