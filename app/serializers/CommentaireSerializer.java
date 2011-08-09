package serializers;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import models.Commentaire;

import java.lang.reflect.Type;
import java.text.SimpleDateFormat;


public class CommentaireSerializer implements JsonSerializer<Commentaire> {


    public JsonElement serialize(Commentaire commentaire, Type type, JsonSerializationContext jsonSerializationContext) {
        JsonObject obj = new JsonObject();
        obj.addProperty("avis", commentaire.commentaire);
        obj.addProperty("nom", commentaire.nom+" le "+new SimpleDateFormat("dd MMMM yyyy").format(commentaire.dateAjout));
        obj.addProperty("note", commentaire.note);
        obj.add("livre",jsonSerializationContext.serialize(commentaire.livre));

        // pour etre compatible avec les versions 0.1.0 et 0.1.1 de l'application android
        obj.addProperty("isbn", commentaire.livre.iSBN);
        obj.addProperty("titre", commentaire.livre.titre);
        return obj;
    }
}
