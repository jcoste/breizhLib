package serializers;

import com.google.gson.*;
import models.Commentaire;
import models.Livre;
import models.User;

import java.lang.reflect.Type;
import java.text.SimpleDateFormat;


public class CommentaireSerializer implements JsonSerializer<Commentaire>,JsonDeserializer<Commentaire> {


    public JsonElement serialize(Commentaire commentaire, Type type, JsonSerializationContext jsonSerializationContext) {
        JsonObject obj = new JsonObject();
        obj.addProperty("avis", commentaire.commentaire);
        obj.addProperty("nom", commentaire.nom);
        obj.addProperty("titre", commentaire.nom+" le "+new SimpleDateFormat("dd MMMM yyyy").format(commentaire.dateAjout));
        obj.addProperty("note", commentaire.note);
        obj.addProperty("uid", commentaire.getUid());
        obj.add("livre",jsonSerializationContext.serialize(commentaire.livre));
        return obj;
    }

    public Commentaire deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
        JsonObject jsonObject = jsonElement.getAsJsonObject();
        Commentaire commentaire = Commentaire.findByUID(jsonObject.get("uid").getAsString());
        if(commentaire == null){
            commentaire = new Commentaire(null,null,jsonObject.get("nom").getAsString(),jsonObject.get("avis").getAsString(),jsonObject.get("note").getAsInt());
            commentaire.uid = jsonObject.get("uid").getAsString();
            commentaire.livre = jsonDeserializationContext.deserialize(jsonObject.get("livre"), Livre.class);
            commentaire.user = User.find(jsonObject.get("user").getAsString());
            commentaire.save();
        }
        return commentaire;
    }
}
