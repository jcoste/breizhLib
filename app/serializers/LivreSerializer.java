package serializers;


import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import models.Livre;
import play.mvc.Router;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

public class LivreSerializer implements JsonSerializer<Livre> {


    public JsonElement serialize(Livre livre, Type type, JsonSerializationContext jsonSerializationContext) {
         JsonObject obj = new JsonObject();
         obj.addProperty("editeur", livre.editeur);
         obj.addProperty("titre", livre.titre);
         obj.addProperty("isbn", livre.iSBN);
         obj.addProperty("note", livre.getNote());
         obj.addProperty("aAjouter", livre.isNotPresent);
         obj.addProperty("etat", livre.getEtat().toString());
         Map<String,Object> param = new HashMap<String,Object>();
         param.put("file",livre.iSBN+".jpg");
         obj.addProperty("image", Router.getFullUrl("Pictures.getPicture",param)) ;
        return obj;
    }
}
