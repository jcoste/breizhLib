package controllers;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.stream.JsonReader;
import controllers.security.Secure;
import models.*;
import models.socialoauth.Role;
import play.modules.router.Get;
import play.modules.router.Post;
import play.mvc.Controller;
import play.mvc.With;
import serializers.*;

import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

@With(Secure.class)
public class Import extends Controller {

    @Role("admin")
    @Get(value = "/import")
    public static void index() {
        Serveur serveur = Serveur.findDefaut();
        if (serveur == null) {
            serveur = new Serveur("gae_server", "http://breizh-lib.appspot.com/", null, ServerType.IMPORT);
            serveur.defaut = true;
            serveur.save();
        }
        String apicode = serveur.code;
        render(apicode);
    }

    @Role("admin")
    @Post(value = "/import/code")
    public static void code(String apicode) {
        Serveur serveur = Serveur.findDefaut();
        if (serveur != null) {
            serveur.code = apicode;
            serveur.save();
        }
        index();
    }

    @Role("admin")
    @Get(value = "/import/books.json", format = "json")
    public static void books(boolean display) {
        try {
            Serveur serveur = Serveur.findDefaut();
            Gson gson = new GsonBuilder().registerTypeAdapter(Livre.class, new LivreSerializer()).create();
            URL url = new URL(serveur.url + "export/books.json?apicode=" + serveur.code);
            JsonReader reader = new JsonReader(new InputStreamReader(url.openStream()));
            reader.beginArray();
            List<Livre> livres = new ArrayList<Livre>();
            while (reader.hasNext()) {
                livres.add(gson.<Livre>fromJson(reader, Livre.class));
            }
            reader.endArray();
            if (display) {
                int ouvragesNb = livres.size();
                 String apicode = serveur.code;
                render("Import/index.html", ouvragesNb,apicode);
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
            Serveur serveur = Serveur.findDefaut();
            Gson gson = new GsonBuilder().registerTypeAdapter(Editeur.class, new EditeurSerializer()).create();
            URL url = new URL(serveur.url + "export/editeurs.json?apicode=" + serveur.code);
            JsonReader reader = new JsonReader(new InputStreamReader(url.openStream()));
            reader.beginArray();
            List<Editeur> editeurs = new ArrayList<Editeur>();
            while (reader.hasNext()) {
                editeurs.add(gson.<Editeur>fromJson(reader, Editeur.class));
            }
            reader.endArray();
            if (display) {
                int editeursNb = editeurs.size();
                 String apicode = serveur.code;
                render("Import/index.html", editeursNb,apicode);
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
    public static void commentaires(boolean display) {
        try {
            Serveur serveur = Serveur.findDefaut();
            Gson gson = new GsonBuilder().registerTypeAdapter(Commentaire.class, new CommentaireSerializer()).create();
            URL url = new URL(serveur.url + "export/commentaires.json?apicode=" + serveur.code);
            JsonReader reader = new JsonReader(new InputStreamReader(url.openStream()));
            reader.beginArray();
            List<Commentaire> commentaires = new ArrayList<Commentaire>();
            while (reader.hasNext()) {
                commentaires.add(gson.<Commentaire>fromJson(reader, Commentaire.class));
            }
            reader.endArray();
            if (display) {
                int commentairesNb = commentaires.size();
                 String apicode = serveur.code;
                render("Import/index.html", commentairesNb,apicode);
            } else {
                renderText(commentaires.size());
            }
        } catch (Exception ex) {
            renderText("KO" + ex);
        }
        renderText("OK");
    }

    @Role("admin")
    @Get("/import/users.json")
    public static void users(boolean display) {
        try {
            Serveur serveur = Serveur.findDefaut();
            Gson gson = new GsonBuilder()
                    .registerTypeAdapter(User.class, new UserSerializer()).create();
            URL url = new URL(serveur.url + "export/users.json?apicode=" + serveur.code);
            JsonReader reader = new JsonReader(new InputStreamReader(url.openStream()));
            reader.beginArray();
            List<User> users = new ArrayList<User>();
            while (reader.hasNext()) {
                users.add(gson.<User>fromJson(reader, User.class));
            }
            reader.endArray();
            if (display) {
                int usersNb = users.size();
                String apicode = serveur.code;
                render("Import/index.html", usersNb,apicode);
            } else {
                renderText(users.size());
            }
        } catch (Exception ex) {
            renderText("KO" + ex);
        }
        renderText("OK");
    }


    @Role("admin")
    @Get(value = "/import/reservations.json", format = "json")
    public static void reservations() {
        try {
            Serveur serveur = Serveur.findDefaut();
            Gson gson = new GsonBuilder()
                    .registerTypeAdapter(Reservation.class, new ReservationSerializer()).create();
            URL url = new URL(serveur.url + "export/reservations.json?apicode=" + serveur.code);
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
