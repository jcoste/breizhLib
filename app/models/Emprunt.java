package models;

import play.data.binding.As;
import play.data.validation.Required;
import siena.Column;
import siena.Generator;
import siena.Id;
import siena.Model;

import java.util.Calendar;
import java.util.Date;


@siena.Table("Emprunt")
public class Emprunt extends UpdatableModel {

    @Id(Generator.AUTO_INCREMENT)
    public Long id;

    @Required
    public String nom;

    @Required
    public String prenom;

    @Required
    @play.data.validation.Email
    public String email;

    public String uid;

    @Column("user")
    public User user;

    @As("yyyy-MM-dd")
    public Date dateEmprunt;

    @As("yyyy-MM-dd")
    public Date dateRetour;

    @Column("emprunt")
    public Livre emprunt;

    public Emprunt(Livre livre, User user, String nom, String prenom, String email) {
        this.emprunt = livre;
        this.email = email;
        this.nom = nom;
        this.prenom = prenom;
        this.dateEmprunt = new Date();
        this.user = user;
    }

    public Emprunt(Reservation reservation) {
        this(reservation.livre,reservation.user,reservation.nom,reservation.prenom,reservation.email);
    }

    public String getUid() {
        if (uid == null) {
            uid = "R" + id;
        }
        return uid;
    }

    public Date getDateRetourIdeal() {
        Calendar c1 = Calendar.getInstance();
        c1.setTime(dateEmprunt);
        c1.add(Calendar.MONTH, 2);
        return c1.getTime();
    }

    public static Emprunt findById(Long id) {
        return Model.all(Emprunt.class).filter("id", id).get();
    }

    public static Emprunt findByUID(String uid) {
        return Emprunt.all(Emprunt.class).filter("uid", uid).get();
    }

}
