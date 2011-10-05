package serializers;


import com.google.gson.*;
import models.Livre;
import models.Reservation;
import models.User;
import utils.DateUtils;

import java.lang.reflect.Type;
import java.util.Date;

public class ReservationSerializer extends AbstractSerializer<Reservation> implements JsonSerializer<Reservation>, JsonDeserializer<Reservation> {


    public JsonElement serialize(Reservation reservation, Type type, JsonSerializationContext jsonSerializationContext) {
        JsonObject obj = new JsonObject();

        obj.addProperty("nom", reservation.nom);
        obj.addProperty("prenom", reservation.prenom);
        obj.addProperty("uid", reservation.getUid());
        obj.addProperty("isAnnuler", reservation.isAnnuler);
        obj.add("livre", jsonSerializationContext.serialize(reservation.livre));
        if (reservation.livre != null) {
            obj.add("livreEmprunt", jsonSerializationContext.serialize(reservation.livre));
        }
        obj.addProperty("user", reservation.email);
        if (reservation.datePret != null && !DateUtils.isSameDay(reservation.datePret, Reservation.getDummyDate())) {
            obj.addProperty("dateEmprunt", reservation.datePret.getTime());
        }
        if (reservation.dateReservation != null) {
            obj.addProperty("dateReservation", reservation.dateReservation.getTime());
        }
        if (reservation.datePret != null) {
            obj.addProperty("dateRetour", reservation.datePret.getTime());
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

        if (needUpdate(reservation, jsonObject)) {

            if (getFacultatifObject(jsonObject, "livreEmprunt") != null) {
                reservation.livre = Livre.findByISBN(getFacultatifObject(jsonObject, "livreEmprunt").get("isbn").getAsString());
            }
            if (getFacultatifObject(jsonObject, "livre") != null) {
                reservation.livre = Livre.findByISBN(getFacultatifObject(jsonObject, "livre").get("isbn").getAsString());
            }

            reservation.isAnnuler = getFacultatifBoolean(jsonObject, "isAnnuler");
            reservation.user = User.find(jsonObject.get("user").getAsString());

            if (getFacultatifString(jsonObject, "dateEmprunt") != null) {
                Date d = new Date();
                d.setTime(Long.valueOf(getFacultatifString(jsonObject, "dateEmprunt")));
                reservation.datePret = d;

            } else {
                reservation.datePret = Reservation.getDummyDate();
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

            reservation.saveImport();

        }
        return reservation;
    }
}
