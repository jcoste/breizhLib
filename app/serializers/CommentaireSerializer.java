package serializers;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import models.Commentaire;
import play.mvc.Router;

import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;


public class CommentaireSerializer implements JsonSerializer<Commentaire> {


    public JsonElement serialize(Commentaire commentaire, Type type, JsonSerializationContext jsonSerializationContext) {
        JsonObject obj = new JsonObject();
        obj.addProperty("avis", commentaire.commentaire);
        obj.addProperty("nom", commentaire.nom+" le "+new SimpleDateFormat("dd MMMM yyyy").format(commentaire.dateAjout));
        obj.addProperty("note", ""+commentaire.note);
        obj.addProperty("isbn", commentaire.livre.iSBN);
        obj.addProperty("titre", commentaire.livre.titre);
        Map<String,Object> param = new HashMap<String,Object>();
        param.put("file",commentaire.livre.iSBN+".jpg");
        obj.addProperty("image", Router.getFullUrl("Pictures.getPicture",param)) ;
        return obj;
    }
}
