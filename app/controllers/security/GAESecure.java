package controllers.security;


import models.User;
import play.modules.gae.GAE;

public class GAESecure implements ISecure {

    public static final GAESecure INSTANCE = new GAESecure();


    public void login() {
        GAE.login("security.Secure.authetification");
    }

    public void logout() {
        GAE.logout("security.Secure.authetification");
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


    public User getUser() {
        User user = null;
        if (GAE.isLoggedIn()) {
            user = User.find(GAE.getUser().getEmail());
            if (user == null) {
                user = new User(GAE.getUser().getEmail());
                user.insert();
            }
            user.isAdmin = GAE.isAdmin();
            user.update();
        }
        return user;
    }
}
