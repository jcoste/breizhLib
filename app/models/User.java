package models;

import play.data.validation.Required;
import siena.Generator;
import siena.Id;
import siena.Model;

@siena.Table("User")
public class User extends Model {

    @Id(Generator.AUTO_INCREMENT)
    public Long id;

    public String nom;
    public String prenom;

    @Required
    public String email;

    public boolean isAdmin;


    public User(String email) {
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

}
