package models;

import play.data.binding.As;
import play.data.validation.Email;
import play.data.validation.Required;
import siena.Column;
import siena.Generator;
import siena.Id;
import siena.Model;

import java.text.ParseException;
import java.text.SimpleDateFormat;
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

    public User user;

    @As("yyyy-MM-dd")
    public Date dateReservation;

    @As("yyyy-MM-dd")
    public Date dateEmprunt;

    @As("yyyy-MM-dd")
    public Date dateRetour;

    @Column("emprunt")
    public Livre emprunt;

    @Column("empruntEncours")
    public Livre empruntEncours;

    public Reservation(Livre livre,User user, String nom, String prenom, String email) {
        this.empruntEncours = livre;
        this.email = email;
        this.nom = nom;
        this.prenom = prenom;
        this.dateReservation = new Date();
        this.dateEmprunt = Reservation.getDummyDate();
        this.user = user;
    }

    public boolean isDateEmpruntNull(){
        return dateEmprunt.equals(getDummyDate());
    }

    public static Reservation findById(Long id) {
        return Model.all(Reservation.class).filter("id", id).get();
    }

    public static Date getDummyDate() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Date d = null;
        try {
            d = sdf.parse("1969-01-01");
        } catch (ParseException e) {
        }
        return d;
    }
}
