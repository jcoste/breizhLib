package serializers;


import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import models.Commentaire;
import models.Livre;
import models.User;
import org.junit.Test;

import java.lang.reflect.Type;
import java.util.Date;

public class CommentaireSerializerTest {

    @Test
    public void serialise(){
        CommentaireSerializer serializer = new CommentaireSerializer();
        Livre livre = new Livre("Apache Maven", "PEARSON", "http://www.pearson.fr/Resources/titles/27440100730370/Images/27440100730370L.gif", "978-2-7440-2337-8");
        User devAdminUser = new User("admin@breizhlib.org", null);
        Commentaire  commentaire = new Commentaire(livre,devAdminUser,"test","content",4);
        commentaire.lastmaj = new Date();
        JsonObject obj = (JsonObject) serializer.serialize(commentaire, Commentaire.class, new JsonSerializationContextStub());

        System.out.println(obj.get("lastMaj"));
    }


    class JsonSerializationContextStub implements JsonSerializationContext{

        public JsonElement serialize(Object o) {
            return null;  //To change body of implemented methods use File | Settings | File Templates.
        }

        public JsonElement serialize(Object o, Type type) {
            return null;  //To change body of implemented methods use File | Settings | File Templates.
        }
    }
}
