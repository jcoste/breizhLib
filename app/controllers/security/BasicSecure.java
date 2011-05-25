package controllers.security;


import models.User;
import play.Play;
import play.data.validation.Required;
import play.libs.Crypto;
import play.mvc.Controller;

public class BasicSecure extends Controller implements ISecure {

    public static final BasicSecure INSTANCE = new BasicSecure();


    public void login() {
        basiclogin();
    }

    public void logout() {
        session.put("userEmail", null);
        session.put("secureimpl", null);
        Secure.authetification();
    }

    public boolean check(String profile) {
        if ("public".equals(profile)) {
            return true;
        }
        if ("admin".equals(profile)) {
            if (session.get("userEmail") != null) {
                User user = User.find(session.get("userEmail"));
                return user.isAdmin;
            }
            return false;
        } else if ("member".equals(profile)) {
            return session.get("userEmail") != null;
        }

        return false;
    }

    public static void basiclogin() {
        render();
    }

    public static void newuser(String email, String nom, String prenom) {
        render(email,nom,prenom);
    }

    public static void postNewuser(@Required String email,String nom,String prenom,@Required String password,@Required String passwordconfirm,String captcha) throws Throwable {
        User user = User.find(email);
        if (user != null) {
            flash.put("error", "email déjà enregistré");
            newuser(null,nom,prenom);
        }
        if (!password.equals(passwordconfirm)) {
            flash.put("error", "mot de passe incorrect");
            newuser(email,nom,prenom);
        }

        user =  new User(email);
        user.nom = nom;
        user.prenom = prenom;
        user.password = Crypto.passwordHash(password);
        user.insert();

        authenticate(email, password, false);
    }


    public static void authenticate(@Required String username, String password, boolean remember) throws Throwable {
        User user = User.find(username);
        if (user == null) {
            flash.put("error", "Utilisateur inconu");
            basiclogin();
        }
        if (!user.password.equals(Crypto.passwordHash(password))) {
            flash.put("error", "mot de passe incorrect");
            //TODO compter le nobre d'echec, afin de pouvoir bloquer le compte
            basiclogin();
        }


        session.put("userEmail", username);
        session.put("secureimpl", "basic");
        Secure.authetification();
    }


    public User getUser() {
        User user = null;
        if (session.get("userEmail") != null) {
            user = User.find(session.get("userEmail"));
        }
        return user;
    }
}
