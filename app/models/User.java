package models;

import com.google.gson.JsonObject;
import models.socialoauth.IUser;
import play.data.binding.As;
import play.data.validation.Required;
import play.libs.Codec;
import play.mvc.Scope;
import siena.Generator;
import siena.Id;

import java.util.Date;
import java.util.List;

@siena.Table("User")
public class User extends UpdatableModel implements IUser {

    @Id(Generator.AUTO_INCREMENT)
    public Long id;

    public String nom;
    public String prenom;

    @Required
    public String email;

    public boolean isAdmin;

    public Boolean isPublic;

    public String password;

    @As("yyyy-MM-dd")
    public Date dateCreation;

    @As("yyyy-MM-dd")
    public Date dateConnexion;

    public Boolean actif;


    /**
     * Twitter username
     */
    public String username;

    public Boolean publicUsername;


    public User(String email, String username) {
        this.dateCreation = new Date();
        this.email = email;
        this.username = username;
        this.isPublic = Boolean.FALSE;
        this.publicUsername = Boolean.FALSE;
    }


    public static User find(String email) {
        User user = User.all(User.class).filter("email", email).get();
        if (user == null) {
            Email userEmail = Email.find(email);
            if (userEmail != null) {
                userEmail.user.get();
                user = userEmail.user;
            }
        }
        return user;
    }

    public List<Email> getExtraEmail() {
        return Email.findByUser(this);
    }

    public static User findByUsername(String username) {
        User user = User.all(User.class).filter("username", username).get();
        return user;
    }

    public static User findById(Long id) {
        User user = User.all(User.class).filter("id", id).get();
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

    public List<Emprunt> ouvrages() {
        return Emprunt.all(Emprunt.class).filter("user", this).filter("dateRetour>", Reservation.getDummyDate()).fetch();
    }

    public List<Emprunt> ouvragesEncours() {
        return Emprunt.all(Emprunt.class).filter("user", this).filter("dateRetour", null).fetch();
    }

    public List<Reservation> reservations() {
        return Reservation.all(Reservation.class).filter("user", this).filter("isAnnuler", false).filter("datePret", null).fetch();
    }

    public Livre getLastEmprunt() {
        Emprunt emprunt = Emprunt.all(Emprunt.class).filter("user", this).filter("dateRetour>", Reservation.getDummyDate()).get();
        if (emprunt != null) {
            emprunt.emprunt.get();
            return emprunt.emprunt;
        }
        return null;
    }

    /**
     * facebook connect
     *
     * @param data
     */
    public static void facebookOAuthCallback(JsonObject data) {
        String email = data.get("email").getAsString();
        User user = User.find(email);
        if (user != null) {
            user.dateConnexion = new Date();
            user.update();
        }
        Scope.Session.current().put("userEmail", email);
    }

    public static String gravatarhash(String gravatarId) {
        if (gravatarId != null)
            return Codec.hexMD5(gravatarId.toLowerCase().trim());
        return null;

    }

    public boolean isActif() {
        return actif;
    }

    public void setActif(boolean actif) {
        this.actif = actif;
    }

    public boolean isAdmin() {
        return isAdmin;
    }

    public void setAdmin(boolean admin) {
        this.isAdmin = admin;
    }

    public Date getDateConnexion() {
        return dateConnexion;
    }

    public void setDateConnexion(Date dateConnexion) {
        this.dateConnexion = dateConnexion;
    }

    public String getEmail() {
        return email;
    }

    @Override
    public void save() {
        if (id == null) {
            insert();
        } else {
            update();
        }
    }

    @Override
    public boolean equals(Object that) {
        if (that != null) {
            return ((User) that).id.equals(this.id);
        }
        return super.equals(that);
    }
}
