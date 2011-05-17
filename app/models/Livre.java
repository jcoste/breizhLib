package models;


import play.data.binding.As;
import play.data.validation.Required;
import play.db.jpa.Model;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity
public class Livre extends Model {

    @Required
    public String titre;
    @Required
    public String editeur;
    @Lob
    public String description;
    @Required
    public String image;
    @Required
    public String iSBN;

    @As("yyyy-MM-dd")
    public Date dateAjout;

    public EtatLivre etat;

    @OneToMany(mappedBy = "livre", cascade = CascadeType.ALL)
    public List<Commentaire> commentaires;

    @OneToOne(mappedBy = "empruntEncours")
    public Reservation reservationEncours;

    @OneToMany(mappedBy = "emprunt", cascade = CascadeType.ALL)
    public List<Reservation> historiqueReservation;


    public Livre() {
        this.commentaires = new ArrayList<Commentaire>();
        this.historiqueReservation = new ArrayList<Reservation>();
        this.etat = EtatLivre.DISP0NIBLE;
    }

    public Livre(String titre, String editeur, String image, String description, String iSBN) {
        this();
        this.description = description;
        this.titre = titre;
        this.editeur = editeur;
        this.image = image;
        this.iSBN = iSBN;
        this.dateAjout = new Date();
    }

    public void addComment(String nom, String content) {
        Commentaire commentaire = new Commentaire(this, nom, content);
        commentaire.create();
        this.commentaires.add(commentaire);
    }

    public void addReservation(String nom, String prenom, String email) {
        if (!etat.equals(EtatLivre.DISP0NIBLE)) {
            throw new IllegalStateException("le livre n'est pas disponible a la réservation");
        }
        Reservation reservation = new Reservation(this,nom, prenom, email);
        reservation.create();
        this.reservationEncours = reservation;
        this.etat = this.etat.getNextState();
        this.save();
    }

    /**
     * retourne 'true' si l'utilisateur à emprunté ce livre
     *
     * @return
     */
    public boolean hasRead() {
        // TODO Long userId
        return true;
    }

    public String toString() {
        return titre;
    }

    public void rendreDisponible() {
        if (!etat.equals(EtatLivre.INSDIPONIBLE)) {
            throw new IllegalStateException("le livre n'a pas été prêté");
        }
        this.historiqueReservation.add(this.reservationEncours);
        this.reservationEncours.emprunt = this;
        this.reservationEncours.empruntEncours = null;
        this.reservationEncours.dateRetour = new Date();
        this.reservationEncours.save();
        this.reservationEncours = null;
        this.etat = this.etat.getNextState();
        this.save();
    }

    public void preter() {
        if (!etat.equals(EtatLivre.RESERVE)) {
            throw new IllegalStateException("le livre n'a pas été réservé");
        }

        this.reservationEncours.dateEmprunt = new Date();
        this.reservationEncours.save();
        this.etat = this.etat.getNextState();
        this.save();
    }
}


