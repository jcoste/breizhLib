package serializers;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import models.User;
import org.junit.Test;
import play.test.UnitTest;

import java.lang.reflect.Type;


public class ProfilSerializerTest extends UnitTest {

    @Test
    public void serialise(){
        ProfilSerializer serializer = new ProfilSerializer();
        User devAdminUser = new User("admin@breizhlib.org", null);
        devAdminUser.nom = "AdminName";
        devAdminUser.prenom = "AdminLastName";
        devAdminUser.username = "AdminUsername";
        JsonObject obj = (JsonObject) serializer.serialize(devAdminUser, User.class, new JsonSerializationContextStub());

        assertNotNull(obj);

        String result = "{\"nom\":\"AdminName\",\"prenom\":\"AdminLastName\",\"email\":\"admin@breizhlib.org\",\"username\":\"AdminUsername\",\"isAdmin\":false,\"commentaires\":\"0 Commentaire\",\"ouvrages\":\"0 Ouvrage lu\",\"reservations\":\"0 Ouvrage Réservé\",\"ouvragesEncours\":\"0 Ouvrage en cours de lecture\"}" ;
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