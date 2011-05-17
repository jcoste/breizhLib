package models;

import play.data.binding.As;
import play.data.validation.Email;
import play.data.validation.Required;
import play.db.jpa.Model;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import java.util.Date;

@Entity
public class Reservation extends Model {

    @Required
    public String nom;

    @Required
    public String prenom;

    @Required
    @Email
    public String email;

    @As("yyyy-MM-dd")
    public Date dateReservation;

    @As("yyyy-MM-dd")
    public Date dateEmprunt;

    @As("yyyy-MM-dd")
    public Date dateRetour;

    @ManyToOne
    public Livre emprunt;

    @OneToOne
    public Livre empruntEncours;

    public Reservation(Livre livre ,String nom, String prenom, String email) {
        this.empruntEncours = livre;
        this.email = email;
        this.nom = nom;
        this.prenom = prenom;
        this.dateReservation = new Date();
    }
}
