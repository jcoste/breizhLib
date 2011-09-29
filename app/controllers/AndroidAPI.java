package controllers;

import controllers.security.Secure;
import models.*;
import models.socialoauth.Role;
import play.Logger;
import play.data.validation.Required;
import play.modules.router.Any;
import play.modules.router.Get;
import play.modules.router.Post;
import play.mvc.Controller;
import play.mvc.With;
import remote.IsbnNicebooksExtractor;
import serializers.CommentaireSerializer;
import serializers.LivreSerializer;
import serializers.ProfilSerializer;
import serializers.ReservationSerializer;
import siena.Query;
import utils.ImagesUtils;

import java.util.Date;
import java.util.List;

@With(Secure.class)
public class AndroidAPI extends Controller {

    @Role("member")
    @Get("/api")
    public static void index() {
        render();
    }

    @Get("/android")
    public static void android() {
        List<Version> versions=Version.all(Version.class).order("-versionCode").fetch(3);
        render(versions);

    }

    @Get("/android/version")
    public static void version() {
        Version version = Version.find();
        if (version == null) {
            version = new Version("0.1.0", 1000);
            version.insert();
        }
        renderText(version.versionCode + "\n" + version.version);
    }

    @Role("member")
    @Get("/api/profil")
    public static void userprofil() {
        User user = (User) Secure.getUser();
        if (user != null) {
            if (user.nom == null) {
                user.nom = "";
            }
            if (user.prenom == null) {
                user.prenom = "";
            }
            if (user.username == null) {
                user.username = "";
            }
            renderJSON(user, new ProfilSerializer());
        }
        renderJSON(new Result("Utilisateur inconnu", "UNKNOW_USER"));
    }


    @Role("public")
    @Any("/api/find")
    public static void findisbn(String iSBN) {
        try {
            String iSBN13 = iSBN.replaceAll("-", "");

            List<Livre> livres = Livre.findAll();
            for (Livre livre : livres) {
                if (livre.iSBN.replaceAll("-", "").equals(iSBN13)) {
                    renderJSON(livre, new LivreSerializer());
                }
            }

            Livre livre = IsbnNicebooksExtractor.getLivre(iSBN13);

            if (livre != null) {
                livre.isNotPresent = true;
                livre.setEtat(EtatLivre.NON_PRESENT);
                renderJSON(livre, new LivreSerializer());
            } else {
                renderJSON(new Result("isbn non trouvé sur les sites d'ouvrages", "UNKNOW_ISBN"));
            }
        } catch (Exception e) {
            renderJSON(new Result("erreur inconnue", "ERROR"));
        }
    }

    @Role("public")
    @Post("/api/add")
    public static void addByisbn(String iSBN)  throws Exception {
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
            livre = IsbnNicebooksExtractor.getLivre(iSBN13);
            if (iSBN13.length() == 13) {
                Logger.info("rename isbn");
                livre.iSBN = iSBN13.substring(0, 3) + "-" + iSBN13.substring(3, 4) + "-" + iSBN13.substring(4, 8) + "-" + iSBN13.substring(8, 12) + "-" + iSBN13.substring(12, 13);
            }
            livre.isNotPresent = false;
            livre.dateAjout = new Date();


                    String image = null;
            if(livre.image != null && livre.image.length() > 0) {
                Logger.debug(livre.image);
                Picture picture = Pictures.createImage(ImagesUtils.getByteFromUrl(livre.image), "ouvrages/", iSBN, true);
                image = picture.getUrl();
                livre.image = image;
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
        renderJSON(commentaires, new CommentaireSerializer(), new LivreSerializer());
    }

    @Role("public")
    @Get(value = "/api/reservations", format = "json")
    public static void reservations() {
        Date d = Reservation.getDummyDate();
        List<Reservation> reservations = Reservation.all(Reservation.class).filter("dateEmprunt", d).filter("dateRetour", null).filter("isAnnuler", false).fetch();
        for (Reservation resa : reservations) {
            if (resa.empruntEncours != null) {
                resa.empruntEncours.get();
            }
        }
        renderJSON(reservations, new ReservationSerializer(), new LivreSerializer());
    }

    @Role("member")
    @Post("/api/book/reserver")
    public static void postReservation(String id, @Required String nom, @Required String prenom, @Required @play.data.validation.Email String email) {
        if (validation.hasErrors()) {
            renderJSON(new Result("informations manquantes", "ERROR"));
        }
        if (id == null) {
            renderJSON(new Result("Ouvrages inconnu", "ERROR"));
        }
        Livre livre = Livre.findByISBN(id);
        if (!livre.getEtat().equals(EtatLivre.DISP0NIBLE)) {
            renderJSON(new Result("le livre n'est pas disponible a la réservation", "NON_DISPONIBLE"));
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
            renderJSON(new Result("paramètres incorrect", "DATA_INVALID"));
        }


        User user = (User) Secure.getUser();
        Commentaire commentaire = new Commentaire(livre, user, nom, content, note);
        commentaire.insert();
        livre.popularite = livre.getCommentaires().size();
        livre.update();

        renderJSON(commentaire, new CommentaireSerializer(), new LivreSerializer());
    }


}
