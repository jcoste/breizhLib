package serializers;


import com.google.gson.*;
import models.Livre;
import models.Reservation;
import models.User;

import java.lang.reflect.Type;

public class ReservationSerializer implements JsonSerializer<Reservation>,JsonDeserializer<Reservation> {


    public JsonElement serialize(Reservation reservation, Type type, JsonSerializationContext jsonSerializationContext) {
         JsonObject obj = new JsonObject();

         obj.addProperty("nom", reservation.nom);
         obj.addProperty("prenom", reservation.prenom);
         obj.add("livre",jsonSerializationContext.serialize(reservation.empruntEncours));
         obj.addProperty("user", reservation.email);

         // pour etre compatible avec les versions 0.1.0 et 0.1.1 de l'application android
         obj.addProperty("titre", "");
         obj.addProperty("isbn", "");
         obj.addProperty("image", "");
        return obj;
    }

    public Reservation deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
        JsonObject jsonObject = jsonElement.getAsJsonObject();
        Reservation commentaire = null;
        if(commentaire == null){
            commentaire = new Reservation(null,null,jsonObject.get("nom").getAsString(),jsonObject.get("prenom").getAsString(),jsonObject.get("email").getAsString());
            commentaire.empruntEncours = jsonDeserializationContext.deserialize(jsonObject.get("livre"), Livre.class);
            commentaire.user = User.find(jsonObject.get("user").getAsString());
            commentaire.save();
        }
        return commentaire;
    }
}
