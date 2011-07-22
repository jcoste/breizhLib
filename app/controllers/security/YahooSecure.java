package controllers.security;


import controllers.socialoauth.OAuthSecure;
import controllers.socialoauth.UserManagement;
import models.User;
import models.socialoauth.Credentials;
import models.socialoauth.ISecure;
import models.socialoauth.IUser;
import play.Logger;
import play.Play;
import play.exceptions.UnexpectedException;
import play.mvc.Router;
import play.mvc.Scope;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class YahooSecure extends OAuthSecure implements ISecure {

    public static final String ID = "yahoo";

    UserManagement um;

    public YahooSecure(UserManagement um) {
        super("https://api.login.yahoo.com/oauth/v2/get_request_token",
                "https://api.login.yahoo.com/oauth/v2/request_auth",
                "https://api.login.yahoo.com/oauth/v2/get_token");
        this.um = um;
        init();
    }

    public void init() {
        if (!Play.configuration.containsKey("yahoo.consumerKey")) {
            throw new UnexpectedException("OAuth yahoo requires that you specify yahoo.consumerKey in your application.conf");
        }
        if (!Play.configuration.containsKey("yahoo.consumerSecret")) {
            throw new UnexpectedException("OAuth yahoo requires that you specify yahoo.consumerSecret in your application.conf");
        }
        if (!Play.configuration.containsKey("yahoo.callback")) {
            throw new UnexpectedException("OAuth yahoo requires that you specify yahoo.callback in your application.conf");
        }
        consumerKey = Play.configuration.getProperty("yahoo.consumerKey");
        consumerSecret = Play.configuration.getProperty("yahoo.consumerSecret");
        callback = Router.getFullUrl(Play.configuration.getProperty("yahoo.callback"));

    }

    public void authenticate(String callback) throws Exception {
        // 1: get the request token
        Map<String, Object> args = new HashMap<String, Object>();
        args.put("callback", callback);
        String callbackURL = Router.getFullUrl(request.controller + ".oauthCallback", args);
        getConnector().authenticate(getCredentials(), callbackURL);
    }

    public Scope.Session session() {
        return Scope.Session.current();
    }

    public void oauthCallback(String callback, String oauth_token, String oauth_verifier) throws Exception {
        // 2: get the access token
        Logger.info("token :" + oauth_token);
        getConnector().retrieveAccessToken(getCredentials(), oauth_verifier);
        String email = getConnector().getProvider().getResponseParameters().get("screen_name").toLowerCase();
        User user = User.find(email);
        if (user != null) {
            user.dateConnexion = new Date();
            user.update();
        }
        session().put(SESSION_EMAIL_KEY, email);
        redirect(callback);
    }

    @Override
    public boolean check(String profile) {
        return false;
    }

    private static ThreadLocal<Credentials> _session = new ThreadLocal<Credentials>();

    public static Credentials getCredentials() {
        if (_session.get() == null) {
            _session.set(new Credentials());
        }
        return _session.get();
    }

    @Override
    public IUser getUser() {
        IUser user = null;
        if (session().get(SESSION_EMAIL_KEY) != null) {
            user = um.getByUsername(session().get(SESSION_EMAIL_KEY));
            if (user == null) {
                user = um.createUser(session().get(SESSION_EMAIL_KEY),null);
                user.setActif(true);
                user.save();
            }
        }
        return user;
    }

}
