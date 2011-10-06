package serializers;


import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import models.Editeur;
import org.junit.Test;
import play.test.UnitTest;

import java.lang.reflect.Type;

public class EditeurSerializerTest extends UnitTest {


     @Test
    public void serialise(){
        EditeurSerializer serializer = new EditeurSerializer();
        Editeur editeur = new Editeur("PEARSON","http:\\www.pearson.com");
        JsonObject obj = (JsonObject) serializer.serialize(editeur, Editeur.class, new JsonSerializationContextStub());

        System.out.println(obj.toString());

        assertNotNull(obj);

        String result = "{\"nom\":\"PEARSON\",\"site\":\"http:\\\\www.pearson.com\",\"image\":\"http://localhost:9000/shared/PEARSON.jpg\"}" ;
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
