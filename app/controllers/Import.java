package controllers;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import controllers.security.Secure;
import models.*;
import models.socialoauth.Role;
import play.modules.router.Get;
import play.mvc.Controller;
import play.mvc.With;
import serializers.*;

import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

@With(Secure.class)
public class Import extends Controller {

    //TODO a ajouter dans les propriétés
    public static final String SERVER_URL = "http://breizh-lib.appspot.com/";

    public static final String API_CODE = "1234";

    @Role("admin")
    @Get(value = "/import")
    public static void index() {

        render();
    }

    @Role("admin")
    @Get(value = "/import/books.json", format = "json")
    public static void books(boolean display) {
        try {
            Type type = new TypeToken<Livre>() {
            }.getType();
            Gson gson = new GsonBuilder().registerTypeAdapter(type, new LivreSerializer()).create();
            URL url = new URL(SERVER_URL + "export/books.json?apicode="+API_CODE);
            JsonReader reader = new JsonReader(new InputStreamReader(url.openStream()));
            reader.beginArray();
            List<Livre> livres = new ArrayList<Livre>();
            while (reader.hasNext()) {
                livres.add(gson.<Livre>fromJson(reader, type));
            }
            reader.endArray();
            if (display) {
                int ouvragesNb = livres.size();
                render("Import/index.html", ouvragesNb);
            } else {
                renderText(livres.size());
            }
        } catch (Exception ex) {
            renderText("KO" + ex);
        }
        renderText("OK");
    }

    @Role("admin")
    @Get(value = "/import/editeurs.json", format = "json")
    public static void editeurs(boolean display) {
        try {
            Type type = new TypeToken<Editeur>() {
            }.getType();
            Gson gson = new GsonBuilder().registerTypeAdapter(type, new EditeurSerializer()).create();
            URL url = new URL(SERVER_URL + "export/editeurs.json?apicode="+API_CODE);
            JsonReader reader = new JsonReader(new InputStreamReader(url.openStream()));
            reader.beginArray();
            List<Editeur> editeurs = new ArrayList<Editeur>();
            while (reader.hasNext()) {
                editeurs.add(gson.<Editeur>fromJson(reader, type));
            }
            reader.endArray();
            if (display) {
                int editeursNb = editeurs.size();
                render("Import/index.html", editeursNb);
            } else {
                renderText(editeurs.size());
            }
        } catch (Exception ex) {
            renderText("KO" + ex);
        }
        renderText("OK");
    }

    @Role("admin")
    @Get(value = "/import/commentaires.json", format = "json")
    public static void all() {
        try {
            Gson gson = new GsonBuilder().registerTypeAdapter(Commentaire.class, new CommentaireSerializer()).create();
            URL url = new URL(SERVER_URL + "export/commentaires.json?apicode="+API_CODE);
            JsonReader reader = new JsonReader(new InputStreamReader(url.openStream()));
            reader.beginArray();
            List<Commentaire> commentaires = new ArrayList<Commentaire>();
            while (reader.hasNext()) {
                commentaires.add(gson.<Commentaire>fromJson(reader, Commentaire.class));
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
       try {
            Gson gson = new GsonBuilder()
                    .registerTypeAdapter(User.class, new UserSerializer()).create();
            URL url = new URL(SERVER_URL + "export/users.json?apicode="+API_CODE);
            JsonReader reader = new JsonReader(new InputStreamReader(url.openStream()));
            reader.beginArray();
            List<User> users = new ArrayList<User>();
            while (reader.hasNext()) {
                users.add(gson.<User>fromJson(reader, User.class));
            }
            reader.endArray();
            renderText(users.size());
        } catch (Exception ex) {
            renderText("KO" + ex);
        }
        renderText("OK");
    }


    @Role("admin")
    @Get(value = "/import/reservations.json", format = "json")
    public static void reservations() {
        try {
            Gson gson = new GsonBuilder()
                    .registerTypeAdapter(Reservation.class, new ReservationSerializer()).create();
            URL url = new URL(SERVER_URL + "export/reservations.json?apicode="+API_CODE);
            JsonReader reader = new JsonReader(new InputStreamReader(url.openStream()));
            reader.beginArray();
            List<Reservation> reservations = new ArrayList<Reservation>();
            while (reader.hasNext()) {
                reservations.add(gson.<Reservation>fromJson(reader, Reservation.class));
            }
            reader.endArray();
            renderText(reservations.size());
        } catch (Exception ex) {
            renderText("KO" + ex);
        }
        renderText("OK");
    }
}
