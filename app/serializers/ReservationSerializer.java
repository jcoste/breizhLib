package serializers;


import com.google.gson.*;
import models.Livre;
import models.Reservation;
import models.User;
import utils.DateUtils;

import java.lang.reflect.Type;
import java.util.Date;

public class ReservationSerializer extends AbstractSerializer implements JsonSerializer<Reservation>, JsonDeserializer<Reservation> {


    public JsonElement serialize(Reservation reservation, Type type, JsonSerializationContext jsonSerializationContext) {
        JsonObject obj = new JsonObject();

        obj.addProperty("nom", reservation.nom);
        obj.addProperty("prenom", reservation.prenom);
        obj.addProperty("uid", reservation.getUid());
        obj.add("livre", jsonSerializationContext.serialize(reservation.empruntEncours));
        if (reservation.emprunt != null) {
            obj.add("livreEmprunt", jsonSerializationContext.serialize(reservation.emprunt));
        }
        obj.addProperty("user", reservation.email);
        if (reservation.dateEmprunt != null && !DateUtils.isSameDay(reservation.dateEmprunt, Reservation.getDummyDate())) {
            obj.addProperty("dateEmprunt", reservation.dateEmprunt.getTime());
        }
        if (reservation.dateReservation != null) {
            obj.addProperty("dateReservation", reservation.dateReservation.getTime());
        }
        if (reservation.dateRetour != null) {
            obj.addProperty("dateRetour", reservation.dateRetour.getTime());
        }

        // pour etre compatible avec les versions 0.1.0 et 0.1.1 de l'application android
        obj.addProperty("titre", "");
        obj.addProperty("isbn", "");
        obj.addProperty("image", "");
        return obj;
    }

    public Reservation deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
        JsonObject jsonObject = jsonElement.getAsJsonObject();
        Reservation reservation = Reservation.findByUID(jsonObject.get("uid").getAsString());
        if (reservation == null) {
            reservation = new Reservation(null, null, jsonObject.get("nom").getAsString(), jsonObject.get("prenom").getAsString(), jsonObject.get("user").getAsString());
            reservation.uid = jsonObject.get("uid").getAsString();
        }
        if (getFacultatifObject(jsonObject, "livreEmprunt") != null) {
            reservation.emprunt = Livre.findByISBN(getFacultatifObject(jsonObject, "livreEmprunt").get("isbn").getAsString());
        }
        if (getFacultatifObject(jsonObject, "livre") != null) {
            reservation.empruntEncours = Livre.findByISBN(getFacultatifObject(jsonObject, "livre").get("isbn").getAsString());
        }
        reservation.user = User.find(jsonObject.get("user").getAsString());

        if (getFacultatifString(jsonObject, "dateEmprunt") != null) {
            Date d = new Date();
            d.setTime(Long.valueOf(getFacultatifString(jsonObject, "dateEmprunt")));
            reservation.dateEmprunt = d;

        } else {
            reservation.dateEmprunt = Reservation.getDummyDate();
        }
        if (jsonObject.get("dateReservation").getAsString() != null) {
            Date d = new Date();
            d.setTime(Long.valueOf(jsonObject.get("dateReservation").getAsString()));
            if (!DateUtils.isSameDay(d, Reservation.getDummyDate())) {
                reservation.dateReservation = d;
            } else {
                reservation.dateReservation = null;
            }
        }
        if (getFacultatifString(jsonObject, "dateRetour") != null) {
            Date d = new Date();
            d.setTime(Long.valueOf(getFacultatifString(jsonObject, "dateRetour")));
            if (!DateUtils.isSameDay(d, Reservation.getDummyDate())) {
                reservation.dateRetour = d;
            } else {
                reservation.dateRetour = null;
            }
        }
        reservation.save();
        return reservation;
    }
}
