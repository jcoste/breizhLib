package controllers;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import controllers.security.Secure;
import models.Commentaire;
import models.Editeur;
import models.Livre;
import models.Reservation;
import models.socialoauth.Role;
import play.modules.router.Get;
import play.mvc.Controller;
import play.mvc.With;
import serializers.CommentaireSerializer;
import serializers.EditeurSerializer;
import serializers.LivreSerializer;
import serializers.ReservationSerializer;

import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

@With(Secure.class)
public class Import extends Controller {

    //TODO a ajouter dans les propriétés
    public static final String SERVER_URL = "http://breizh-lib.appspot.com/";

    @Role("admin")
    @Get(value = "/import/books.json", format = "json")
    public static void books() {
        try {
            Type type = new TypeToken<Livre>() {
            }.getType();
            Gson gson = new GsonBuilder().registerTypeAdapter(type, new LivreSerializer()).create();
            URL url = new URL(SERVER_URL + "export/books.json");
            JsonReader reader = new JsonReader(new InputStreamReader(url.openStream()));
            reader.beginArray();
            List<Livre> livres = new ArrayList<Livre>();
            while (reader.hasNext()) {
                livres.add(gson.<Livre>fromJson(reader, type));
            }
            reader.endArray();
            renderText(livres.size());
        } catch (Exception ex) {
            renderText("KO" + ex);
        }
        renderText("OK");
    }

    @Role("admin")
    @Get(value = "/import/editeurs.json", format = "json")
    public static void editeurs() {
        try {
            Type type = new TypeToken<Editeur>() {
            }.getType();
            Gson gson = new GsonBuilder().registerTypeAdapter(type, new EditeurSerializer()).create();
            URL url = new URL(SERVER_URL + "export/editeurs.json");
            JsonReader reader = new JsonReader(new InputStreamReader(url.openStream()));
            reader.beginArray();
            List<Editeur> editeurs = new ArrayList<Editeur>();
            while (reader.hasNext()) {
                editeurs.add(gson.<Editeur>fromJson(reader, type));
            }
            reader.endArray();
            renderText(editeurs.size());
        } catch (Exception ex) {
            renderText("KO" + ex);
        }
        renderText("OK");
    }

    @Role("admin")
    @Get(value = "/import/commentaires.json", format = "json")
    public static void all() {
        try {
            Type type = new TypeToken<Commentaire>() {}.getType();
            Gson gson = new GsonBuilder().registerTypeAdapter(type, new CommentaireSerializer())
                            .registerTypeAdapter(new TypeToken<Livre>() {}.getType(), new LivreSerializer()).create();
            URL url = new URL(SERVER_URL + "export/commentaires.json");
            JsonReader reader = new JsonReader(new InputStreamReader(url.openStream()));
            reader.beginArray();
            List<Commentaire> commentaires = new ArrayList<Commentaire>();
            while (reader.hasNext()) {
                commentaires.add(gson.<Commentaire>fromJson(reader, type));
            }
            reader.endArray();
            renderText(commentaires.size());
        } catch (Exception ex) {
            renderText("KO" + ex);
        }
        renderText("OK");
    }

    @Role("admin")
    @Get("/import/users.json")
    public static void users() {
        renderText("");
    }


    @Role("admin")
    @Get(value = "/import/reservations.json", format = "json")
    public static void reservations() {
        try {
            Type type = new TypeToken<Reservation>() {}.getType();
            Gson gson = new GsonBuilder()
                            .registerTypeAdapter(type, new ReservationSerializer())
                            .registerTypeAdapter(new TypeToken<Livre>() {}.getType(), new LivreSerializer()).create();
            URL url = new URL(SERVER_URL + "export/commentaires.json");
            JsonReader reader = new JsonReader(new InputStreamReader(url.openStream()));
            reader.beginArray();
            List<Reservation> reservations = new ArrayList<Reservation>();
            while (reader.hasNext()) {
                reservations.add(gson.<Reservation>fromJson(reader, type));
            }
            reader.endArray();
            renderText(reservations.size());
        } catch (Exception ex) {
            renderText("KO" + ex);
        }
        renderText("OK");
    }
}
