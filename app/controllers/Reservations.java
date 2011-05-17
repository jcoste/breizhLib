package controllers;

import models.EtatLivre;
import models.Livre;
import models.Reservation;
import play.data.validation.Email;
import play.data.validation.Required;
import play.mvc.Controller;

import java.util.List;


public class Reservations extends Controller {

    public static void index(Long id) {
        Livre livre = Livre.findById(id);
        render(livre);
    }

    public static void reservations() {
        List<Reservation> reservations = Reservation.find(" dateEmprunt IS null ").fetch();

        List<Reservation> empruints = Reservation.find(" dateEmprunt IS Not null And dateRetour IS null ").fetch();
        render(reservations,empruints);
    }


    public static void rendreLivre(Long id) {
        Reservation reservation = Reservation.findById(id);

        Livre livre = reservation.empruntEncours;

        livre.rendreDisponible();
        reservations();
    }

    public static void annuler(Long id) {
        Reservation reservation = Reservation.findById(id);
        Livre livre = reservation.empruntEncours;
        reservation.empruntEncours = null;
        livre.reservationEncours = null;
        reservation.delete();
        livre.etat = EtatLivre.DISP0NIBLE;
        livre.save();
        index(livre.id);
    }


    public static void pretLivre(Long id) {
        Reservation reservation = Reservation.findById(id);

        Livre livre = reservation.empruntEncours;

        livre.preter();
        reservations();
    }

    public static void reserver(Long id) {
        if (id == null) {
            render();
        }
        Livre livre = Livre.findById(id);
        if (livre.etat.equals(EtatLivre.DISP0NIBLE)) {
            render(id);
        } else {
            // TODO erreur message
            Livres.show(id);
        }
    }

    public static void postReservation(Long id,@Required String nom,@Required String prenom,@Required @Email String email) {
        if (validation.hasErrors()) {
            render("Reservations/reserver.html");
        }
        if (id == null) {
            render();
        }
        Livre livre = Livre.findById(id);
        livre.addReservation(nom, prenom, email);
        flash.success("Réservation enregistré!");
        Livres.show(id);
    }
}
