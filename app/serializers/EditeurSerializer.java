package serializers;


import com.google.gson.*;
import models.Editeur;
import play.mvc.Router;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

public class EditeurSerializer extends AbstractSerializer implements JsonSerializer<Editeur>,JsonDeserializer<Editeur> {



    public Editeur deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
       JsonObject jsonObject = jsonElement.getAsJsonObject();
        Editeur editeur = Editeur.findByNom(jsonObject.get("nom").getAsString());
        if (editeur == null) {
            editeur = new Editeur(jsonObject.get("nom").getAsString(), jsonObject.get("site").getAsString());
        }
        //TODO saveImage
        editeur.image = jsonObject.get("image").getAsString();
        editeur.site = jsonObject.get("site").getAsString();
        editeur.save();
        return editeur;
    }

    public JsonElement serialize(Editeur editeur, Type type, JsonSerializationContext jsonSerializationContext) {
        JsonObject obj = new JsonObject();
        obj.addProperty("nom", editeur.nom);
        obj.addProperty("site", editeur.site);
        if (editeur.image == null || editeur.image.contains("/shared/")) {
            Map<String, Object> param = new HashMap<String, Object>();
            param.put("file", editeur.nom + ".jpg");
            obj.addProperty("image", Router.getFullUrl("Pictures.getPicture", param));
        } else {
            obj.addProperty("image", editeur.image);
        }
        return obj;
    }
}
