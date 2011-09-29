package serializers;


import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import models.Commentaire;
import models.Reservation;
import models.User;

import java.lang.reflect.Type;
import java.util.List;

public class ProfilSerializer extends AbstractSerializer<User> implements JsonSerializer<User> {


    public JsonElement serialize(User user, Type type, JsonSerializationContext jsonSerializationContext) {
        JsonObject obj = new JsonObject();

        obj.addProperty("nom", user.nom);
        obj.addProperty("prenom", user.prenom);
        obj.addProperty("email", user.email);
        obj.addProperty("username", user.username);
        obj.addProperty("isAdmin", user.isAdmin);
        List<Commentaire> commentaires = user.commentaires();
        String commentairesSuffix = " Commentaire";
        if (commentaires.size() > 1) {
            commentairesSuffix += "s";
        }
        obj.addProperty("commentaires", commentaires.size() + commentairesSuffix);

        List<Reservation> ouvrages = user.ouvrages();
        String ouvragesSuffix = " Ouvrage";
        if (ouvrages.size() > 1) {
            ouvragesSuffix += "s lus";
        } else {
            ouvragesSuffix += " lu";
        }

        obj.addProperty("ouvrages", ouvrages.size() + ouvragesSuffix);

        List<Reservation> reservations = user.reservations();
        String reservationsSuffix = " Ouvrage";
        if (reservations.size() > 1) {
            reservationsSuffix += "s Réservés";
        } else {
            reservationsSuffix += " Réservé";
        }

        obj.addProperty("reservations", reservations.size() + reservationsSuffix);

        List<Reservation> ouvragesEncours = user.ouvragesEncours();
        String ouvragesEncoursSuffix = " Ouvrage";
        if (ouvragesEncours.size() > 1) {
            ouvragesEncoursSuffix += "s";
        }
        ouvragesEncoursSuffix += " en cours de lecture";

        obj.addProperty("ouvragesEncours", ouvragesEncours.size() + ouvragesEncoursSuffix);


        return obj;
    }
}
