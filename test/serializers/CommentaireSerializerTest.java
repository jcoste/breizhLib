package serializers;


import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import models.Commentaire;
import models.Livre;
import models.User;
import org.junit.Test;
import play.test.UnitTest;

import java.lang.reflect.Type;
import java.util.Date;

public class CommentaireSerializerTest extends UnitTest {

    @Test
    public void serialise(){
        CommentaireSerializer serializer = new CommentaireSerializer();
        Livre livre = new Livre("Apache Maven", "PEARSON", "http://www.pearson.fr/Resources/titles/27440100730370/Images/27440100730370L.gif", "978-2-7440-2337-8");
        User devAdminUser = new User("admin@breizhlib.org", null);
        Commentaire  commentaire = new Commentaire(livre,devAdminUser,"test","content",4);
        commentaire.lastmaj = new Date();
        JsonObject obj = (JsonObject) serializer.serialize(commentaire, Commentaire.class, new JsonSerializationContextStub());

        //assertNotNull(obj);

        String result = "{\"lastMaj\":"+commentaire.lastmaj.getTime()+",\"avis\":\"content\",\"nom\":\"test\",\"titre\":\"test le 06 Octobre 2011\",\"note\":4,\"uid\":\"Cnull\",\"user\":\"admin@breizhlib.org\",\"livre\":null}" ;
        //assertEquals(result,obj.toString());
    }


    class JsonSerializationContextStub implements JsonSerializationContext {

        public JsonElement serialize(Object o) {
            return null;
        }

        public JsonElement serialize(Object o, Type type) {
            return null;
        }
    }
}
