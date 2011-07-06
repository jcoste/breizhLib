package controllers.security;


import models.User;
import play.Play;
import play.exceptions.UnexpectedException;
import play.modules.gae.GAE;
import play.mvc.Router;

public class GAESecure implements ISecure {

    public static final GAESecure INSTANCE = new GAESecure();
    public static final String ID = "gae";

    protected String callback;


    private GAESecure() {
        init();
    }

    public void init() {
        if (!Play.configuration.containsKey("google.callback")) {
            throw new UnexpectedException("OAuth google requires that you specify google.callback in your application.conf");
        }
        callback = Router.getFullUrl(Play.configuration.getProperty("google.callback"));

    }


    public void login() {
        GAE.login(callback);
    }

    public void logout() {
        GAE.logout(callback);
    }

    @Override
    public void oauthCallback(String callback, String oauth_token, String oauth_verifier) throws Exception {
        throw new IllegalAccessException();
    }

    @Override
    public boolean check(String profile) {
        return false;
    }


    public IUser getUser() {
        IUser user = null;
        if (GAE.isLoggedIn()) {
            user = User.find(GAE.getUser().getEmail().toLowerCase());
            if (user == null) {
                user = new User(null);
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
