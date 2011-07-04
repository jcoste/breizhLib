package models;

import com.google.gson.JsonObject;
import play.data.binding.As;
import play.data.validation.Required;
import play.libs.Codec;
import play.mvc.Scope;
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

    public String username;

    public Boolean actif;


    public User(String email) {
        this.dateCreation = new Date();
        this.email = email;
    }


    public static User find(String email) {
        User user = User.all(User.class).filter("email", email).get();
		if (user == null) {
			Email userEmail = Email.find(email);
			if(userEmail == null){
				userEmail.user.get();
				user = userEmail.user;
			}
		}
        return user;
    }

    public static User findByUsername(String username) {
        User user = User.all(User.class).filter("username", username).get();
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

    public Livre getLastEmprunt() {
        Reservation reservation = Reservation.all(Reservation.class).filter("dateRetour>", Reservation.getDummyDate()).get();
        if (reservation != null) {
            reservation.emprunt.get();
            return reservation.emprunt;
        }
        return null;
    }

    /**
     *  facebook connect
     *
     * @param data
     */
     public static void facebookOAuthCallback(JsonObject data){
	    String email = data.get("email").getAsString();
		User user = User.find(email);
        if (user != null) {
			user.dateConnexion = new Date();
            user.update();
		}
        Scope.Session.current().put("userEmail",email );
    }

    public String gravatarhash(String gravatarId){
        if(gravatarId != null)
            return Codec.hexMD5(gravatarId.toLowerCase().trim());
         return null;

    }
}
