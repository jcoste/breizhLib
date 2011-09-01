package serializers;


import com.google.gson.*;
import models.Livre;
import models.Reservation;
import models.User;

import java.lang.reflect.Type;
import java.util.Date;

public class ReservationSerializer implements JsonSerializer<Reservation>,JsonDeserializer<Reservation> {


    public JsonElement serialize(Reservation reservation, Type type, JsonSerializationContext jsonSerializationContext) {
         JsonObject obj = new JsonObject();

         obj.addProperty("nom", reservation.nom);
         obj.addProperty("prenom", reservation.prenom);
         obj.add("livre",jsonSerializationContext.serialize(reservation.empruntEncours));
         obj.addProperty("user", reservation.email);
         obj.addProperty("dateEmprunt",reservation.dateEmprunt.getTime());
         obj.addProperty("dateReservation",reservation.dateReservation.getTime());
         obj.addProperty("dateRetour",reservation.dateRetour.getTime());

         // pour etre compatible avec les versions 0.1.0 et 0.1.1 de l'application android
         obj.addProperty("titre", "");
         obj.addProperty("isbn", "");
         obj.addProperty("image", "");
        return obj;
    }

    public Reservation deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
        JsonObject jsonObject = jsonElement.getAsJsonObject();
        Reservation reservation = null;
        if(reservation == null){
            reservation = new Reservation(null,null,jsonObject.get("nom").getAsString(),jsonObject.get("prenom").getAsString(),jsonObject.get("email").getAsString());
            GsonBuilder builder = new GsonBuilder();
            builder.registerTypeAdapter(Livre.class, new LivreSerializer());
            Gson gson = builder.create();
            reservation.empruntEncours = gson.fromJson(jsonObject.get("livre"), Livre.class);
            reservation.user = User.find(jsonObject.get("user").getAsString());
            if(jsonObject.get("dateEmprunt").getAsString() != null){
                Date d =  new Date();
                d.setTime(Long.valueOf(jsonObject.get("dateEmprunt").getAsString()));
                reservation.dateEmprunt = d;
            }
            if(jsonObject.get("dateReservation").getAsString() != null){
                Date d =  new Date();
                d.setTime(Long.valueOf(jsonObject.get("dateReservation").getAsString()));
                reservation.dateReservation = d;
            }
            if(jsonObject.get("dateRetour").getAsString() != null){
                Date d =  new Date();
                d.setTime(Long.valueOf(jsonObject.get("dateRetour").getAsString()));
                reservation.dateRetour = d;
            }
            reservation.save();
        }
        return reservation;
    }
}
