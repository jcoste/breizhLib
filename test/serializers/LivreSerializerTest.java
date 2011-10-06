package serializers;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import models.Livre;
import org.junit.Test;
import play.test.UnitTest;

import java.lang.reflect.Type;

public class LivreSerializerTest extends UnitTest {


     @Test
    public void serialise(){
        LivreSerializer serializer = new LivreSerializer();
        Livre livre = new Livre("Apache Maven", "PEARSON", "http://www.pearson.fr/Resources/titles/27440100730370/Images/27440100730370L.gif", "978-2-7440-2337-8");
        JsonObject obj = (JsonObject) serializer.serialize(livre, Livre.class, new JsonSerializationContextStub());

        assertNotNull(obj);

        String result = "{\"editeur\":\"PEARSON\",\"titre\":\"Apache Maven\",\"isbn\":\"978-2-7440-2337-8\",\"note\":0,\"aAjouter\":false,\"etat\":\"DISP0NIBLE\",\"tags\":\"\",\"image\":\"http://www.pearson.fr/Resources/titles/27440100730370/Images/27440100730370L.gif\"}" ;
        assertEquals(result,obj.toString());
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
