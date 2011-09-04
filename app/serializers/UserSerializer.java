package serializers;

import com.google.gson.*;
import models.User;

import java.lang.reflect.Type;


public class UserSerializer implements JsonSerializer<User>,JsonDeserializer<User> {
    public User deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
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
        if( user.dateConnexion != null){
            obj.addProperty("dateConnexion", user.dateConnexion.getTime());
        }
        if( user.dateCreation != null){
            obj.addProperty("dateCreation", user.dateCreation.getTime());
        }
        return obj;
    }
}
