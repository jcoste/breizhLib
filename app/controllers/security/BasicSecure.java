package controllers.security;


import models.User;
import play.data.validation.Required;
import play.modules.gae.GAE;
import play.mvc.Controller;
import play.mvc.results.Redirect;

public class BasicSecure extends Controller implements ISecure {

    public static final BasicSecure INSTANCE = new BasicSecure();


    public void login() {
          basiclogin();
    }

    public void logout() {
        session.put("userEmail",null);
        session.put("secureimpl", null);
        Secure.authetification();
    }

    public boolean check(String profile) {
        if ("public".equals(profile)) {
            return true;
        }
        if ("admin".equals(profile)) {
            return GAE.isLoggedIn() && GAE.isAdmin();
        } else if ("member".equals(profile)) {
            return GAE.isLoggedIn();
        }

        return false;
    }

    public static void basiclogin() {
          render();
    }

    public static void newuser() {
          render();
    }

    public static void authenticate(@Required String username, String password, boolean remember) throws Throwable {
        User user = User.find(username);
        if(user == null){
            flash.put("error","Utilisateur inconu");
            basiclogin();
        }
        session.put("userEmail",username);
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
