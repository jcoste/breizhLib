package models;

import play.data.binding.As;
import play.data.validation.Required;
import siena.Generator;
import siena.Id;
import siena.Model;

import java.util.Date;
import java.util.List;

@siena.Table("User")
public class User extends Model {

    @Id(Generator.AUTO_INCREMENT)
    public Long id;

    public String nom;
    public String prenom;

    @Required
    public String email;

    public boolean isAdmin;

    public String password;

    @As("yyyy-MM-dd")
    public Date dateCreation;

    @As("yyyy-MM-dd")
    public Date dateConnexion;


    public User(String email) {
        this.dateCreation = new Date();
        this.email = email;
    }


    public static User find(String email) {
        User user = User.all(User.class).filter("email", email).get();
        return user;
    }

    public String toString() {
        if (prenom != null && nom != null) {
            return nom + " " + prenom;
        } else {
            return email;
        }
    }

    public static List<User> findAll() {
        return User.all(User.class).fetch();
    }

    public List<Commentaire> commentaires() {
        return Commentaire.all(Commentaire.class).filter("user", this).fetch();
    }

    public Livre getLastEmprunt(){
        Reservation reservation = Reservation.all(Reservation.class).filter("dateRetour>",Reservation.getDummyDate()).get();
        if(reservation != null){
            reservation.emprunt.get();
            return reservation.emprunt;
        }
        return null;
    }
}
