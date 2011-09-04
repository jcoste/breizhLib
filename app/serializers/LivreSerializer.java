package serializers;


import com.google.gson.*;
import models.EtatLivre;
import models.Livre;
import models.tag.Tag;
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
        String tags = "";
        for (Tag tag : livre.getTags()) {
            tags += ";" + tag.name;
        }
        obj.addProperty("tags", tags);
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

            String editeur = getFacultatifString(jsonObject, "editeur");
            livre = new Livre(jsonObject.get("titre").getAsString(), editeur, jsonObject.get("image").getAsString(), jsonObject.get("isbn").getAsString());
        }

        String tags = jsonObject.get("tags").getAsString();
        String[] tagsListe = tags.split(";");

        for (String tag : tagsListe) {
            if (tag != null && tag.length() > 0) {
                livre.addTag(tag);
            }
        }

        livre.setEtat(EtatLivre.fromString(jsonObject.get("etat").getAsString()));
        livre.save();
        return livre;
    }

    private String getFacultatifString(JsonObject item, String name) {
        try {
            return item.get(name).getAsString();
        } catch (Exception e) {
            return null;
        }
    }
}
