package models;

import play.data.binding.As;
import play.data.validation.Email;
import play.data.validation.Required;
import siena.Generator;
import siena.Id;
import siena.Model;

import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import java.util.Date;

@siena.Table("Reservation")
public class Reservation extends Model {

    @Id(Generator.AUTO_INCREMENT)
    public Long id;

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

    public static Reservation findById(Long id) {
         return Model.all(Reservation.class).filter("id",id).get();
    }
}
