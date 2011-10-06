package serializers;


import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import models.Livre;
import models.Reservation;
import models.User;
import org.junit.Test;
import play.test.UnitTest;

import java.lang.reflect.Type;

public class ReservationSerializerTest extends UnitTest {

    @Test
    public void serialise(){
        ReservationSerializer serializer = new ReservationSerializer();
        Livre livre = new Livre("Apache Maven", "PEARSON", "http://www.pearson.fr/Resources/titles/27440100730370/Images/27440100730370L.gif", "978-2-7440-2337-8");
        User devAdminUser = new User("admin@breizhlib.org", null);
        Reservation reservation = new Reservation(livre,devAdminUser,"AdminName","AdminLastName","admin@breizhlib.org");
        JsonObject obj = (JsonObject) serializer.serialize(reservation, Reservation.class, new JsonSerializationContextStub());

        assertNotNull(obj);

        String result = "{\"nom\":\"AdminName\",\"prenom\":\"AdminLastName\",\"uid\":\"Rnull\",\"isAnnuler\":false,\"livre\":null,\"livreEmprunt\":null,\"user\":\"admin@breizhlib.org\",\"dateReservation\":"+reservation.dateReservation.getTime()+",\"titre\":\"\",\"isbn\":\"\",\"image\":\"\"}" ;
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
