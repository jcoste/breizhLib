package serializers;


import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import models.Reservation;

import java.lang.reflect.Type;

public class ReservationSerializer implements JsonSerializer<Reservation> {


    public JsonElement serialize(Reservation reservation, Type type, JsonSerializationContext jsonSerializationContext) {
         JsonObject obj = new JsonObject();

         obj.addProperty("nom", reservation.nom);
         obj.addProperty("prenom", reservation.prenom);
         obj.add("livre",jsonSerializationContext.serialize(reservation.empruntEncours));

         // pour etre compatible avec les versions 0.1.0 et 0.1.1 de l'application android
         obj.addProperty("titre", "");
         obj.addProperty("isbn", "");
         obj.addProperty("image", "");
        return obj;
    }
}
