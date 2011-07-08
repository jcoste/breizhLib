package controllers.security;


import controllers.Application;
import controllers.Users;
import models.User;
import models.oauthclient.Credentials;
import play.Play;
import play.exceptions.UnexpectedException;
import play.mvc.Router;
import play.mvc.Scope;

import java.util.HashMap;
import java.util.Map;

public class GoogleSecure extends OAuthSecure implements ISecure {

    public static final GoogleSecure INSTANCE = new GoogleSecure();
    public static final String ID = "google";

    private GoogleSecure() {
        super("https://www.google.com/accounts/OAuthGetRequestToken",
                "https://www.google.com/accounts/OAuthAuthorizeToken",
                " https://www.google.com/accounts/OAuthGetAccessToken");
        init();
    }

    public void init() {
        if (!Play.configuration.containsKey("google.consumerKey")) {
            throw new UnexpectedException("OAuth Google requires that you specify google.consumerKey in your application.conf");
        }
        if (!Play.configuration.containsKey("google.consumerSecret")) {
            throw new UnexpectedException("OAuth Google requires that you specify google.consumerSecret in your application.conf");
        }
        if (!Play.configuration.containsKey("google.callback")) {
            throw new UnexpectedException("OAuth Google requires that you specify google.callback in your application.conf");
        }
        consumerKey = Play.configuration.getProperty("google.consumerKey");
        consumerSecret = Play.configuration.getProperty("google.consumerSecret");
        callback = Router.getFullUrl(Play.configuration.getProperty("google.callback"));

    }


    public void authenticate(String callback) throws Exception {
        // 1: get the request token
        Map<String, Object> args = new HashMap<String, Object>();
        args.put("callback", callback);
        String callbackURL = Router.getFullUrl(request.controller + ".oauthCallback", args);
        INSTANCE.getConnector().authenticate(getCredentials(), callbackURL);
    }

    public void oauthCallback(String callback, String oauth_token, String oauth_verifier) throws Exception {
        // 2: get the access token
        INSTANCE.getConnector().retrieveAccessToken(getCredentials(), oauth_verifier);
        session().put(SESSION_EMAIL_KEY, INSTANCE.getConnector().getProvider().getResponseParameters().get("email"));
        redirect(callback);
    }

    @Override
    public IUser getUser() {
       User user = null;
        if (session().get(SESSION_EMAIL_KEY) != null) {
            user = User.findByUsername(session().get(SESSION_EMAIL_KEY));
            if (user == null) {
                user = new User(null);
                user.username = session().get(SESSION_EMAIL_KEY);
                user.actif = true;
                user.insert();
            }
            user.actif = true;
            user.update();
        }
        return user;
    }

    private static ThreadLocal<Credentials> _session = new ThreadLocal<Credentials>();

    public static Credentials getCredentials() {
        if (_session.get() == null) {
            _session.set(new Credentials());
        }
        return _session.get();
    }

    protected Scope.Session session() {
        return Scope.Session.current();
    }

    public static void informations() {
        if (((User) Secure.getUser()).email == null) {
            Users.edit();
        } else {
            Application.index();
        }
    }

}
