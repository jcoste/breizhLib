package controllers;

import controllers.security.Secure;
import models.*;
import models.socialoauth.Role;
import play.modules.router.Get;
import play.modules.router.Post;
import play.mvc.Controller;
import play.mvc.With;
import serializers.*;
import siena.Query;

import java.util.Date;
import java.util.List;

@With(Secure.class)
public class Export extends Controller {

    @Role("admin")
    @Get("/export")
    public static void index() {
        Serveur serveur = Serveur.findByType(ServerType.EXPORT);
        String apicode = null;
        if (serveur != null) {
            apicode = serveur.code;
        }
        render(serveur, apicode);
    }

    @Role("admin")
    @Post("/export/init")
    public static void init(String name, String url) {
        Serveur serveur = Serveur.findByType(ServerType.EXPORT);
        if (serveur == null) {
            serveur = new Serveur(name, url, null, ServerType.EXPORT);
            serveur.save();
        }
        render("Export/index.html", serveur);
    }

    @Role("admin")
    @Post("/export/code")
    public static void code() {
        Serveur serveur = Serveur.findByType(ServerType.EXPORT);
        if (serveur != null) {
            serveur.code = serveur.generateCode();
            serveur.save();
        }
        String apicode = serveur.code;
        render("Export/index.html", serveur, apicode);
    }


    @Role("api")
    @Get(value = "/export/books.json", format = "json")
    public static void books() {
        Query<Livre> query = Livre.all(Livre.class);
        List<Livre> livres = query.order("-dateAjout").fetch();
        for (Livre livre : livres) {
            livre.popularite = livre.getCommentaires().size();
            livre.update();
        }

        renderJSON(livres, new LivreSerializer());
    }

    @Role("api")
    @Get(value = "/export/editeurs.json", format = "json")
    public static void editeurs() {
        Query<Editeur> query = Editeur.all(Editeur.class);
        List<Editeur> editeurs = query.order("-nom").fetch();
        renderJSON(editeurs, new EditeurSerializer());
    }

    @Role("api")
    @Get(value = "/export/commentaires.json", format = "json")
    public static void all() {
        List<Commentaire> commentaires = Commentaire.all(Commentaire.class).order("-dateAjout").fetch();

        for (Commentaire commentaire : commentaires) {
            commentaire.livre.get();
            commentaire.user.get();
        }
        renderJSON(commentaires, new LivreSerializer(), new CommentaireSerializer());
    }

    @Role("api")
    @Get("/export/users.json")
    public static void users() {
        List<User> users = User.findAll();
        renderJSON(users, new UserSerializer());
    }


    @Role("api")
    @Get(value = "/export/reservations.json", format = "json")
    public static void reservations() {
        Date d = Reservation.getDummyDate();
        List<Reservation> reservations = Reservation.all(Reservation.class).fetch();
        for (Reservation resa : reservations) {
            if (resa.empruntEncours != null) {
                resa.empruntEncours.get();
                resa.user.get();
            }
            if (resa.emprunt != null) {
                resa.emprunt.get();
            }
        }
        renderJSON(reservations, new LivreSerializer(), new ReservationSerializer());
    }
}
