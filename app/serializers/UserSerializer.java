package serializers;

import com.google.gson.*;
import models.User;

import java.lang.reflect.Type;
import java.util.Date;


public class UserSerializer extends AbstractSerializer implements JsonSerializer<User>, JsonDeserializer<User> {

    public User deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
        JsonObject jsonObject = jsonElement.getAsJsonObject();
        User user = User.find(jsonObject.get("email").getAsString());
        if (user == null) {
            user = new User(jsonObject.get("email").getAsString(), getFacultatifString(jsonObject, "username"));
        }
        user.password = getFacultatifString(jsonObject, "password");
        user.prenom = getFacultatifString(jsonObject, "prenom");
        user.nom = getFacultatifString(jsonObject, "nom");
        user.actif = getFacultatifBoolean(jsonObject, "actif");
        user.isAdmin = getFacultatifBoolean(jsonObject, "isAdmin");
        user.isPublic = getFacultatifBoolean(jsonObject, "isPublic");
        user.publicUsername = getFacultatifBoolean(jsonObject, "publicUsername");
        if (getFacultatifString(jsonObject, "dateConnexion") != null) {
            Date d = new Date();
            d.setTime(Long.valueOf(getFacultatifString(jsonObject, "dateConnexion")));
            user.dateConnexion = d;
        }

        if (getFacultatifString(jsonObject, "dateCreation") != null) {
            Date d = new Date();
            d.setTime(Long.valueOf(getFacultatifString(jsonObject, "dateCreation")));
            user.dateCreation = d;
        }
        user.save();

        return user;
    }


    public JsonElement serialize(User user, Type type, JsonSerializationContext jsonSerializationContext) {
        JsonObject obj = new JsonObject();

        obj.addProperty("nom", user.nom);
        obj.addProperty("prenom", user.prenom);
        obj.addProperty("email", user.email);
        obj.addProperty("actif", user.actif);
        obj.addProperty("isAdmin", user.isAdmin);
        obj.addProperty("isPublic", user.isPublic);
        obj.addProperty("username", user.username);
        obj.addProperty("publicUsername", user.publicUsername);
        obj.addProperty("password", user.password);
        if (user.dateConnexion != null) {
            obj.addProperty("dateConnexion", user.dateConnexion.getTime());
        }
        if (user.dateCreation != null) {
            obj.addProperty("dateCreation", user.dateCreation.getTime());
        }
        return obj;
    }
}
