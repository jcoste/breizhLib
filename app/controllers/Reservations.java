package controllers;

import controllers.security.Role;
import controllers.security.Secure;
import models.EtatLivre;
import models.Livre;
import models.Reservation;
import models.User;
import play.Logger;
import play.data.validation.Email;
import play.data.validation.Required;
import play.modules.router.Get;
import play.modules.router.Post;
import play.mvc.Controller;
import play.mvc.With;

import java.util.Date;
import java.util.List;

@With(Secure.class)
public class Reservations extends Controller {

    @Role("member")
    @Get("/book/{id}/reservations")
    public static void index(String id) {
        Livre livre = Livre.findByISBN(id);
        if (livre == null) {
            Logger.warn("l'ouvrage d'id {} n'existe pas en base", id);
            error("l'ouvrage d'id " + id + " n'existe pas en base");
        }
        List<Reservation> emprunts = livre.getHistoriqueReservation();
        for (Reservation resa : emprunts) {
            if (resa.empruntEncours != null) {
                resa.empruntEncours.get();
            }
        }
        User user = Secure.getUser();

        render(livre, emprunts, user);
    }

    @Role("admin")
    @Get("/reservations")
    public static void reservations() {
        Date d = Reservation.getDummyDate();
        List<Reservation> reservations = Reservation.all(Reservation.class).filter("dateEmprunt", d).filter("dateRetour", null).fetch();
        for (Reservation resa : reservations) {
            if (resa.empruntEncours != null) {
                resa.empruntEncours.get();
            }
        }


        List<Reservation> emprunts = Reservation.all(Reservation.class).filter("dateEmprunt>", d).filter("dateRetour", null).fetch();
        for (Reservation resa : emprunts) {
            if (resa.empruntEncours != null) {
                resa.empruntEncours.get();
            }
        }
        render(reservations, emprunts);
    }

    @Role("admin")
    public static void rendreLivre(Long id) {
        Reservation reservation = Reservation.findById(id);
        Livre livre = reservation.empruntEncours;
        livre.get();
        if (!livre.getEtat().equals(EtatLivre.INSDIPONIBLE)) {
            throw new IllegalStateException("le livre n'a pas été prêté : " + livre.getEtat());
        }
        Reservation resa = livre.reservationEncours;
        resa.get();
        resa.emprunt = livre;
        resa.empruntEncours = null;
        resa.dateRetour = new Date();
        livre.reservationEncours = null;
        resa.update();
        livre.setEtat(livre.getEtat().getNextState());
        livre.update();
        reservations();
    }

    @Role("member")
    public static void annuler(Long id) {
        Reservation reservation = Reservation.findById(id);
        Livre livre = reservation.empruntEncours;
        livre.get();
        reservation.empruntEncours = null;
        livre.reservationEncours = null;
        reservation.delete();
        livre.setEtat(EtatLivre.DISP0NIBLE);
        livre.update();
        index(livre.iSBN);
    }

    @Role("admin")
    public static void pretLivre(Long id) {
        Reservation reservation = Reservation.findById(id);
        Livre livre = reservation.empruntEncours;
        livre.get();
        if (!livre.getEtat().equals(EtatLivre.RESERVE)) {
            throw new IllegalStateException("le livre n'a pas été réservé : " + livre.getEtat());
        }
        livre.reservationEncours.get();
        livre.reservationEncours.dateEmprunt = new Date();
        livre.reservationEncours.empruntEncours.get();
        livre.reservationEncours.update();
        livre.setEtat(livre.getEtat().getNextState());
        livre.update();
        reservations();
    }

    @Role("member")
    @Get("/book/{id}/reserver")
    public static void reserver(String id) {
        if (id == null) {
            render();
        }
        Livre livre = Livre.findByISBN(id);
        if (livre.getEtat().equals(EtatLivre.DISP0NIBLE)) {
            User user = Secure.getUser();
            render(id, user);
        } else {
            error("l'ouvrage " + livre.titre + " n'est pas disponible a la reservation");
        }
    }

    @Role("member")
    @Post("/book/{id}/reserver")
    public static void postReservation(String id, @Required String nom, @Required String prenom, @Required @Email String email) {
        if (validation.hasErrors()) {
            render("Reservations/reserver.html");
        }
        if (id == null) {
            render();
        }
        Livre livre = Livre.findByISBN(id);
        if (!livre.getEtat().equals(EtatLivre.DISP0NIBLE)) {
            throw new IllegalStateException("le livre n'est pas disponible a la réservation");
        }

        User user = Secure.getUser();
        if (user.nom == null) {
            user.nom = nom;
        }
        if (user.prenom == null) {
            user.prenom = prenom;
        }
        user.update();
        Reservation reservation = new Reservation(livre, user, nom, prenom, email);
        reservation.insert();
        livre.reservationEncours = reservation;
        livre.setEtat(livre.getEtat().getNextState());
        livre.update();
        flash.success("Réservation enregistré!");
        Livres.show(id);
    }
}
