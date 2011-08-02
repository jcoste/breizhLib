package controllers;

import com.google.appengine.api.datastore.Blob;
import controllers.security.Secure;
import models.*;
import models.socialoauth.Role;
import play.Logger;
import play.data.validation.Required;
import play.modules.router.Get;
import play.modules.router.Post;
import play.mvc.Controller;
import play.mvc.With;
import remote.Isbn13Extractor;
import serializers.CommentaireSerializer;
import serializers.LivreSerializer;
import siena.Query;
import utils.ImagesUtils;

import java.util.Date;
import java.util.List;

@With(Secure.class)
public class AndroidAPI extends Controller {

    @Role("admin")
    @Get("/api")
    public static void index(){
        render();
    }

    @Get("/android")
    public static void android(){
        render();
    }

    @Role("member")
    @Get("/api/profil")
    public static void userprofil() {
        User user = (User) Secure.getUser();
        if (user != null) {
           List<Commentaire> commentaires = user.commentaires();
           List<Reservation> ouvrages = Reservation.all(Reservation.class).filter("user", user).filter("dateRetour>", Reservation.getDummyDate()).fetch();
           List<Reservation> ouvragesEncours = Reservation.all(Reservation.class).filter("user", user).filter("dateEmprunt>", Reservation.getDummyDate()).filter("dateRetour", null).fetch();
           List<Reservation> reservations = Reservation.all(Reservation.class).filter("user", user).filter("dateEmprunt", Reservation.getDummyDate()).filter("dateRetour", null).fetch();
           request.format = "json";
           render(user, commentaires, ouvrages, ouvragesEncours, reservations);
        }
        renderText("ERROR");
    }


    @Role("public")
    @Post("/api/find")
    public static void findisbn(String iSBN) {
        String iSBN13 = iSBN.replaceAll("-", "");

        List<Livre> livres = Livre.findAll();
        for (Livre livre : livres) {
            if (livre.iSBN.replaceAll("-", "").equals(iSBN13)) {
                request.format = "json";
                render(livre);
            }
        }

        Livre livre = Isbn13Extractor.getLivre(iSBN);
        livre.isNotPresent = true;
        request.format = "json";
        render(livre);
    }

    @Role("public")
    @Post("/api/add")
    public static void addByisbn(String iSBN) {
        String iSBN13 = iSBN.replaceAll("-", "");
        boolean exist = false;
        List<Livre> livres = Livre.findAll();
        for (Livre livre : livres) {
            if (livre.iSBN.replaceAll("-", "").equals(iSBN13)) {
                exist = true;
            }
        }
        Livre livre = null;
        if (!exist) {
            livre = Isbn13Extractor.getLivre(iSBN);
            if(iSBN.length() == 13){
              livre.iSBN = iSBN.substring(0,3)+"-"+iSBN.substring(3,4)+"-"+iSBN.substring(4,8)+"-"+iSBN.substring(8,12)+"-"+iSBN.substring(12,13);
            }
            livre.isNotPresent = false;
            livre.dateAjout = new Date();

            if(livre.image != null && livre.image.length() > 0) {
                Logger.debug(livre.image);
                Picture imageFile = new Picture();
                imageFile.image = new Blob(ImagesUtils.getByteFromUrl(livre.image));
                imageFile.name = livre.iSBN + ".jpg";
                imageFile.path = "ouvrages/";
                imageFile.insert();
                livre.image = imageFile.getUrl();
            }

            livre.insert();
        }

        request.format = "json";
        render(livre, exist);
    }

    @Role("public")
    @Get(value = "/api/ouvrages", format = "json")
    public static void ouvrages() {
        Query<Livre> query = Livre.all(Livre.class);
        List<Livre> livres = query.order("-dateAjout").fetch();
        renderJSON(livres, new LivreSerializer());
    }

    @Role("public")
    @Get(value = "/api/commentaires", format = "json")
    public static void commentaires() {
         List<Commentaire> commentaires = Commentaire.all(Commentaire.class).order("-dateAjout").fetch();

        for (Commentaire commentaire : commentaires) {
            commentaire.livre.get();
        }
        renderJSON(commentaires, new CommentaireSerializer());
    }

    @Role("public")
    @Get(value = "/api/reservations", format = "json")
    public static void reservations() {
         Date d = Reservation.getDummyDate();
        List<Reservation> reservations = Reservation.all(Reservation.class).filter("dateEmprunt", d).filter("dateRetour", null).fetch();
        for (Reservation resa : reservations) {
            if (resa.empruntEncours != null) {
                resa.empruntEncours.get();
            }
        }
        render(reservations);
    }

    @Role("member")
    @Post("/api/book/reserver")
    public static void postReservation(String id, @Required String nom, @Required String prenom, @Required @play.data.validation.Email String email) {
        if (validation.hasErrors()) {
            renderText("ERROR");
        }
        if (id == null) {
            renderText("ERROR");
        }
        Livre livre = Livre.findByISBN(id);
        if (!livre.getEtat().equals(EtatLivre.DISP0NIBLE)) {
            throw new IllegalStateException("le livre n'est pas disponible a la réservation");
        }

        User user = (User) Secure.getUser();
        if (user.nom == null) {
            user.nom = nom;
        }
        if (user.prenom == null) {
            user.prenom = prenom;
        }
        user.update();

        // TODO contrôller le nombre de réservation déjà en cours pour l'utilisateur

        Reservation reservation = new Reservation(livre, user, nom, prenom, email);
        reservation.insert();
        livre.reservationEncours = reservation;
        livre.setEtat(livre.getEtat().getNextState());
        livre.update();

        renderText("OK");
    }

    @Role("member")
    @Post("/api/comment")
    public static void postComment(String bookId, @Required String nom, @Required String content, @Required int note) {
        Livre livre = Livre.findByISBN(bookId);
        if (validation.hasErrors()) {
            render("Livres/show.html", livre);
        }


        User user = (User) Secure.getUser();
        Commentaire commentaire = new Commentaire(livre, user, nom, content, note);
        commentaire.insert();
        livre.popularite = livre.getCommentaires().size();
        livre.update();
        renderText("OK");
    }


}
