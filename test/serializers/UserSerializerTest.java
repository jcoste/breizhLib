package serializers;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import models.User;
import org.junit.Test;
import play.test.UnitTest;

import java.lang.reflect.Type;


public class UserSerializerTest  extends UnitTest {

    @Test
    public void serialise(){
        UserSerializer serializer = new UserSerializer();
        User devAdminUser = new User("admin@breizhlib.org", null);
        devAdminUser.nom = "AdminName";
        devAdminUser.prenom = "AdminLastName";
        devAdminUser.username = "AdminUsername";
        JsonObject obj = (JsonObject) serializer.serialize(devAdminUser, User.class, new JsonSerializationContextStub());

        assertNotNull(obj);

        String result = "{\"nom\":\"AdminName\",\"prenom\":\"AdminLastName\",\"email\":\"admin@breizhlib.org\",\"actif\":null,\"isAdmin\":false,\"isPublic\":false,\"username\":\"AdminUsername\",\"publicUsername\":false,\"password\":null,\"dateCreation\":"+devAdminUser.dateCreation.getTime()+"}" ;
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