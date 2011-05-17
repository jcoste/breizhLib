package models;


import play.Logger;
import play.data.binding.As;
import play.data.validation.Required;
import siena.*;
import siena.Column;
import siena.Id;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@siena.Table("Livre")
public class Livre extends Model {

    @Id(Generator.AUTO_INCREMENT)
    public Long id;

    @Required
    @Column("titre")
    public String titre;
    @Required
    @Column("editeur")
    public String editeur;
    @Lob
    public String description;
    @Required
    @Column("image")
    public String image;
    @Required

    @Column("iSBN")
    public String iSBN;

    @As("yyyy-MM-dd")
    public Date dateAjout;

    public String etat;

    @OneToMany(mappedBy = "livre", cascade = CascadeType.ALL)
    public List<Commentaire> commentaires;

    @OneToOne(mappedBy = "empruntEncours")
    public Reservation reservationEncours;

    @OneToMany(mappedBy = "emprunt", cascade = CascadeType.ALL)
    public List<Reservation> historiqueReservation;


    public Livre() {
        this.commentaires = new ArrayList<Commentaire>();
        this.historiqueReservation = new ArrayList<Reservation>();
        setEtat(EtatLivre.DISP0NIBLE);
    }

    //TODO
    public void setEtat(EtatLivre etat) {
        Logger.info("setEtat "+etat);
        this.etat = etat.getClasseCss();
    }

    public EtatLivre getEtat() {
        return EtatLivre.fromCss(this.etat);
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
        commentaire.insert();
        this.commentaires.add(commentaire);
    }

    public void addReservation(String nom, String prenom, String email) {
        if (!getEtat().equals(EtatLivre.DISP0NIBLE)) {
            throw new IllegalStateException("le livre n'est pas disponible a la réservation");
        }
        Reservation reservation = new Reservation(this, nom, prenom, email);
        reservation.insert();
        this.reservationEncours = reservation;
        setEtat(this.getEtat().getNextState());
        this.update();
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
        if (!getEtat().equals(EtatLivre.INSDIPONIBLE)) {
            throw new IllegalStateException("le livre n'a pas été prêté : "+getEtat());
        }
        this.historiqueReservation.add(this.reservationEncours);
        this.reservationEncours.emprunt = this;
        this.reservationEncours.empruntEncours = null;
        this.reservationEncours.dateRetour = new Date();
        this.reservationEncours.update();
        this.reservationEncours = null;
        setEtat(this.getEtat().getNextState());
        this.update();
    }

    public void preter() {
        if (!getEtat().equals(EtatLivre.RESERVE)) {
            throw new IllegalStateException("le livre n'a pas été réservé : "+getEtat());
        }

        this.reservationEncours.dateEmprunt = new Date();
        this.reservationEncours.update();
        setEtat(this.getEtat().getNextState());
        this.update();
    }

    public Long getId() {
        return id;
    }

    public static Livre findById(Long bookId) {
        return Livre.all(Livre.class).filter("id", bookId).get();
    }

    public static List<Livre> findAll() {
        return Livre.all(Livre.class).fetch();
    }
}


