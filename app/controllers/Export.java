package controllers;

import controllers.security.Secure;
import models.*;
import models.socialoauth.Role;
import play.modules.router.Get;
import play.mvc.Controller;
import play.mvc.With;
import serializers.*;
import siena.Query;

import java.util.Date;
import java.util.List;

@With(Secure.class)
public class Export extends Controller{


    @Role("api")
    @Get(value = "/export/books.json", format = "json")
    public static void books() {
        Query<Livre> query = Livre.all(Livre.class);
        List<Livre> livres = query.order("-dateAjout").fetch();
        for (Livre livre : livres) {
            livre.popularite = livre.getCommentaires().size();
            livre.update();
        }

        renderJSON(livres,new LivreSerializer());
    }

    @Role("api")
    @Get(value = "/export/editeurs.json", format = "json")
    public static void editeurs() {
        Query<Editeur> query = Editeur.all(Editeur.class);
        List<Editeur> editeurs = query.order("-nom").fetch();
        renderJSON(editeurs,new EditeurSerializer());
    }

    @Role("api")
    @Get(value = "/export/commentaires.json", format = "json")
    public static void all() {
        List<Commentaire> commentaires = Commentaire.all(Commentaire.class).order("-dateAjout").fetch();

        for (Commentaire commentaire : commentaires) {
            commentaire.livre.get();
            commentaire.user.get();
        }
        renderJSON(commentaires,new LivreSerializer(),new CommentaireSerializer());
    }

    @Role("api")
    @Get("/export/users.json")
    public static void users() {
        List<User> users = User.findAll();
        renderJSON(users,new UserSerializer());
    }


    @Role("api")
    @Get(value = "/export/reservations.json", format = "json")
    public static void reservations() {
        Date d = Reservation.getDummyDate();
        List<Reservation> reservations = Reservation.all(Reservation.class).filter("dateEmprunt", d).filter("dateRetour", null).fetch();
        for (Reservation resa : reservations) {
            if (resa.empruntEncours != null) {
                resa.empruntEncours.get();
                resa.user.get();
            }
        }
        renderJSON(reservations,new LivreSerializer(),new ReservationSerializer());
    }
}
