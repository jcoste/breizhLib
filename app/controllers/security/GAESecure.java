package controllers.security;


import controllers.socialoauth.UserManagement;
import models.socialoauth.ISecure;
import models.socialoauth.IUser;
import play.Play;
import play.exceptions.UnexpectedException;
import play.modules.gae.GAE;
import play.mvc.Router;

public class GAESecure implements ISecure {

    public static final String ID = "gae";

    protected String callback;

    private UserManagement um;

    public GAESecure(UserManagement um) {
        init();
        this.um = um;
    }

    public void init() {
        if (!Play.configuration.containsKey("google.callback")) {
            throw new UnexpectedException("OAuth google requires that you specify google.callback in your application.conf");
        }
        callback = Router.getFullUrl(Play.configuration.getProperty("google.callback"));

    }


    public void login() {
        GAE.login("security.secure.authentification");
    }

    public void logout() {
        GAE.logout("security.secure.authentification");
    }

    public void oauthCallback(String callback, String oauth_token, String oauth_verifier) throws Exception {
        throw new IllegalAccessException();
    }

    public boolean check(String profile) {
        return false;
    }


    public IUser getUser() {
        IUser user = null;
        if (GAE.isLoggedIn()) {
            user = um.getByEmail(GAE.getUser().getEmail().toLowerCase());
            if (user == null) {
                user = um.createUser(GAE.getUser().getEmail().toLowerCase(), null);
                user.setActif(true);
                user.save();
            }
            user.setActif(true);
            user.setAdmin(user.isAdmin() || GAE.isAdmin());
            user.save();
        }
        return user;
    }
}
