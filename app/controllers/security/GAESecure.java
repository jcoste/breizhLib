package controllers.security;


import models.Email;
import models.User;
import play.modules.gae.GAE;

public class GAESecure implements ISecure {

    public static final GAESecure INSTANCE = new GAESecure();
    public static final String ID = "gae";


    public void login() {
        GAE.login("security.secure.authetification");
    }

    public void logout() {
        GAE.logout("security.secure.authetification");
    }

    @Override
    public void oauthCallback(String callback, String oauth_token, String oauth_verifier) throws Exception {
        throw new IllegalAccessException();
    }

    @Override
    public boolean check(String profile) {
        return false;
    }


    public User getUser() {
        User user = null;
        if (GAE.isLoggedIn()) {
            user = User.find(GAE.getUser().getEmail().toLowerCase());
            if (user == null) {
                user = new User(null);
                user.actif = true;
                user.insert();
            }
            user.actif = true;
            user.isAdmin = user.isAdmin || GAE.isAdmin();
            user.update();
        }
        return user;
    }
}
