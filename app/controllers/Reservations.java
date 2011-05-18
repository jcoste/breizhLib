package controllers;

import models.EtatLivre;
import models.Livre;
import models.Reservation;
import play.Logger;
import play.data.validation.Email;
import play.data.validation.Required;
import play.mvc.Controller;

import java.util.Date;
import java.util.List;


public class Reservations extends Controller {

    public static void index(Long id) {
        Livre livre = Livre.findById(id);
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
        render(livre, emprunts);
    }

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

    public static void annuler(Long id) {
        Reservation reservation = Reservation.findById(id);
        Livre livre = reservation.empruntEncours;
        livre.get();
        reservation.empruntEncours = null;
        livre.reservationEncours = null;
        reservation.delete();
        livre.setEtat(EtatLivre.DISP0NIBLE);
        livre.update();
        index(livre.id);
    }


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

    public static void reserver(Long id) {
        if (id == null) {
            render();
        }
        Livre livre = Livre.findById(id);
        if (livre.getEtat().equals(EtatLivre.DISP0NIBLE)) {
            render(id);
        } else {
            error("l'ouvrage " + livre.titre + " n'est pas disponible a la reservation");
        }
    }

    public static void postReservation(Long id, @Required String nom, @Required String prenom, @Required @Email String email) {
        if (validation.hasErrors()) {
            render("Reservations/reserver.html");
        }
        if (id == null) {
            render();
        }
        Livre livre = Livre.findById(id);
        if (!livre.getEtat().equals(EtatLivre.DISP0NIBLE)) {
            throw new IllegalStateException("le livre n'est pas disponible a la réservation");
        }
        Reservation reservation = new Reservation(livre, nom, prenom, email);
        reservation.insert();
        livre.reservationEncours = reservation;
        livre.setEtat(livre.getEtat().getNextState());
        livre.update();
        flash.success("Réservation enregistré!");
        Livres.show(id);
    }
}
