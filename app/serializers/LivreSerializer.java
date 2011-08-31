package serializers;


import com.google.gson.*;
import models.EtatLivre;
import models.Livre;
import play.mvc.Router;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

public class LivreSerializer implements JsonSerializer<Livre>, JsonDeserializer<Livre> {


    public JsonElement serialize(Livre livre, Type type, JsonSerializationContext jsonSerializationContext) {
        JsonObject obj = new JsonObject();
        obj.addProperty("editeur", livre.editeur);
        obj.addProperty("titre", livre.titre);
        obj.addProperty("isbn", livre.iSBN);
        obj.addProperty("note", livre.getNote());
        obj.addProperty("aAjouter", livre.isNotPresent);
        obj.addProperty("etat", livre.getEtat().toString());

        if (livre.image == null || livre.image.contains("/shared/")) {
            Map<String, Object> param = new HashMap<String, Object>();
            param.put("file", livre.iSBN + ".jpg");
            obj.addProperty("image", Router.getFullUrl("Pictures.getPicture", param));
        } else {
            obj.addProperty("image", livre.image);
        }
        return obj;
    }

    //TODO gestion des images
    public Livre deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
        JsonObject jsonObject = jsonElement.getAsJsonObject();
        Livre livre = Livre.findByISBN(jsonObject.get("isbn").getAsString());
        if (livre == null) {
            livre = new Livre(jsonObject.get("titre").getAsString(), jsonObject.get("editeur").getAsString(), jsonObject.get("image").getAsString(), jsonObject.get("isbn").getAsString());
        }

        livre.setEtat(EtatLivre.fromString(jsonObject.get("etat").getAsString()));
         livre.save();
        return livre;
    }
}
